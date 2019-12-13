<template>
  <div class="modelList">
    <header>
      <el-input suffix-icon="el-icon-search" @keyup.enter="searchFetch(searchData)" v-model="searchData.cubeName" size="small" placeholder="请输入关键字" clearable></el-input>
      <div class="nhc-elbtnwarp">
        <el-button type="primary" size="small" @click.native="searchFetch(searchData)">搜索</el-button>
      </div>
      <el-button type="primary" size="small" @click="createolap">新建模型</el-button>
    </header>
    <el-table
        :data="tableData"
        v-loading="getLoading"
        ref="multipleTable"
        :row-key="getRowKeys"
        :expand-row-keys="expands"
         class="statusDiv"
        tooltip-effect="dark"
        @row-click="clickTable"
        :header-cell-class-name="tableHead"
        stripe>
        <el-table-column show-overflow-tooltip type="expand">
          <template>
            <el-popover>
              <model-detail @closeExpands="closeExpands" :jsonData="jsonData"></model-detail>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column min-width="100%" prop="name" label="模型名称" show-overflow-tooltip> </el-table-column>
        <el-table-column min-width="100%" prop="flagsName" label="模型状态" show-overflow-tooltip>
          <template slot-scope="scope">
            <!-- 0:不可用,1:可用,2:就绪,3:数据同步中,4:同步失败,5:构建中,6:构建失败 -->
            <div :style="{color: statusReviewFilter((scope.row.flags === 0 || scope.row.flags === 4 || scope.row.flags === 6) ? 'DISABLED' : 'READY', 4)}">{{scope.row.flagsName}}</div>
          </template>
        </el-table-column>
        <el-table-column min-width="100%" prop="size_kb" label="模型大小" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>{{scope.row.size_kb}}.00kb</div>
          </template>
        </el-table-column>
        <el-table-column min-width="100%" prop="input_records_count" label="资源记录" show-overflow-tooltip> </el-table-column>
        <el-table-column min-width="100%" prop="last_modified" label="上次构建的时间" show-overflow-tooltip>
          <template slot-scope="scope">
            <div v-if="scope.row.segments.length > 0 && scope.row.segments[scope.row.segments.length - 1].last_build_time > 0">
               {{scope.row.segments[scope.row.segments.length - 1].last_build_time | formatDate}}
            </div>
            <div v-else></div>
          </template>
        </el-table-column>
        <el-table-column min-width="100%" prop="partitionDateStart" label="创建时间" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
               {{scope.row.create_time_utc | formatDate}}
            </div>
          </template>
        </el-table-column>
        <el-table-column min-width="100%" label="模型来源" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>自建</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150px">
          <template slot-scope="scope">
            <div class="play">
              <el-dropdown trigger="click" @command="handleCommand">
                <el-button type="text" size="small">操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="{type: 'lookDetail', params: scope.row}">查看</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row}">编辑</el-dropdown-item>
                  <!-- 1：成功，-1：失败，0：进行中 :disabled="scope.row.syncStatus !== 1" -->
                  <el-dropdown-item :command="{type: 'construct', params: scope.row}" >构建</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'reloads', params: scope.row}">刷新</el-dropdown-item>
                  <el-dropdown-item v-if="scope.row.status === 'DISABLED'"
                                    :command="{type: 'enable', params: scope.row}">启用</el-dropdown-item>
                  <el-dropdown-item v-else :command="{type: 'disableds', params: scope.row}">禁用</el-dropdown-item>
                  <el-dropdown-item v-if="scope.row.status !== 'DISABLED'"
                                    :command="{type: 'sharedTable', params: scope.row}">共享</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'clones', params: scope.row}">复制</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'dels', params: scope.row}">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    <div class="more" v-if="moreShow && tableData.length >= 15" @click="moreData">更多数据</div>
    <clones ref="clones"></clones>
    <construct ref="construct" :dateType="dateType"></construct>
    <reloads ref="reloads"></reloads>
    <merge ref="merge"></merge>
    <!-- 共享 -->
    <sharedTable ref="sharedTable"></sharedTable>
  </div>
</template>

<script>
import { descDataList, getModelDataList, buildModeling, disableModeling, deleteCubeModeling, enableModeling } from '@/api/modelList'
import { modelDetail, clones, construct, reloads, merge, sharedTable } from '@/components/analysisComponent/modelListComponent'
import { filterTime, removeAllStorage, statusReviewFilter } from '@/utils/index'
export default {
  components: {
    modelDetail, clones, construct, reloads, merge, sharedTable
  },
  data () {
    return {
      dateType: '',
      searchData: {
        cubeName: ''
      },
      type: 'all',
      getLoading: false,
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      getRowKeys (row) {
        return row.uuid
      },
      // 要展开的行，数值的元素是row的key值
      expands: [],
      tableData: [],
      jsonData: {},
      offset: 0,
      moreShow: true,
      setIntervalNum: null,
      statusReviewFilter: statusReviewFilter // 调用方法
    }
  },
  filters: {
    formatDate (time) {
      // var date = new Date(time)
      return filterTime(time)
    }
  },
  created () {
    this.init()
  },
  mounted () {
  },
  beforeDestroy () {
    clearInterval(this.setIntervalNum)
  },
  methods: {
    init (val, type) {
      this.getModelList(val, type)
    },
    // 模型列表
    async getModelList (val, type) {
      if (type === 'search') {
        this.offset = 0
      }
      clearInterval(this.setIntervalNum)
      try {
        this.getLoading = true
        const params = {
          limit: 15,
          offset: this.offset,
          dateType: 1, // 1：模型列表 2：构建列表
          ...val
        }
        // if (!params.cubeName) {
        //  delete params.cubeName
        // }
        // 为空查询条件删掉，这个是get 拼接在后面
        !params.cubeName && delete params.cubeName
        const { cubeMappers: res, next } = await getModelDataList(params)
        if (type === 'search') {
          this.tableData = res
        } else if (res && res.length > 0) {
          this.tableData = [...this.tableData, ...res].sort((a, b) => b.create_time_utc - a.create_time_utc)
        }
        this.moreShow = next
        this.setIntervalNum = setInterval(this.update, 1000 * 30)
      } catch(e) {
      } finally {
        this.getLoading = false
      }
    },
    async update () {
      // this.getLoading = true
      const params = {
        limit: this.type === 'search' ? 15 : this.tableData.length > 15 ? this.tableData.length : 15,
        offset: 0,
        dateType: 1,
        cubeName: this.searchData.cubeName
      }
      // 为空查询条件删掉，这个是get 拼接在后面
      !params.cubeName && delete params.cubeName

      const { cubeMappers } = await getModelDataList(params)
      if (cubeMappers.length > 0) {
        this.tableData = cubeMappers.sort((a, b) => b.create_time_utc - a.create_time_utc)
      } else {
        this.moreShow = false
        // this.$message.success('已加载所有数据')
      }
      // this.getLoading = false
    },
    searchFetch () {
      // this.init(val, 'search')
      this.type = 'search'
      this.update()
    },
    createolap () {
      removeAllStorage() // 新增的时候清除本地存储
      this.$router.push('/analysisModel/createolap/selectStep')
    },
    // 展开详情
    clickTable (val) {
      // console.log(val)
    },
    returnSuccess () {
      this.$message({
        type: 'success',
        message: '成功!'
      })
      this.dialogFormVisible = false
    },
    handleCommand (val) {
      const { type, params } = val
      this.expands = []
      let texts = ''
      switch (type) {
        case 'disableds':
          texts = '确定禁用此模型？禁用后，引用了该模型的功能将无法使用！'
          break
        case 'enable':
          texts = '确定启用该模型？'
          break
        case 'clones':
          texts = '确定复制该模型？'
          break
        case 'dels':
          texts = '确定删除该模型？'
          break
      }
      if (type === 'lookDetail') {
        this.expands.push(params.uuid)
        this.jsonData = { cubeName: params.name, models: params.model }
        return
      }
      // 禁用/删除/启用
      if (['disableds', 'enable', 'dels'].includes(type)) {
        return this.$confirm(texts, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          this.getLoading = true
          if (type === 'disableds') {
            params.status === 'DISABLED'
              ? this.$message.warning('该模型已禁用~')
              : await disableModeling({ cubeName: params.name }).then(res => {
                this.$message.success('已禁用')
                this.init()
              }).catch(_ => {
                this.getLoading = false
              })
          }
          if (type === 'enable') {
            if (params.segments.length < 1) {
              this.$message.warning('请先构建模型~')
              this.getLoading = false
              return
            }
            params.status === 'READY'
              ? this.$message.warning('该模型已启用~')
              : await enableModeling({ cubeName: params.name }).then(res => {
                this.$message.success('已启用')
                this.init()
              }).catch(_ => {
                this.getLoading = false
              })
          }
          if (type === 'dels') {
            await deleteCubeModeling({ cubeName: params.name }).then(res => {
              this.$message.success('删除成功~')
              this.init()
            }).catch(_ => {
              this.getLoading = false
            })
          }
          this.getLoading = false
        })
        this.$refs[type].dialog(params)
      }
      // 构建
      if (type === 'construct') {
        if (params.flags === 5) {
          this.$message.error('模型构建中，请等待构建结果')
        } else {
          const info = {
            cubeName: params.name,
            models: params.model
          }
          descDataList(info).then((res) => {
            // this.dateType = res.
            this.dateType = res.ModesList.partition_desc.partition_date_format
            // console.log(this.dateType)
          })
          this.$refs['construct'].dialog(params)
        }
        
        // if (params.segments.length > 0 && params.partitionDateColumn) {
        // } else {
        //   return this.$confirm('是否构建该模型', {
        //     confirmButtonText: '确定',
        //     cancelButtonText: '取消',
        //     type: 'warning'
        //   }).then(async () => {
        //     this.getLoading = true
        //     // this.$throttle(() => {
        //     await buildModeling({ cubeName: params.name, start: 0, end: 0 }).then(res => {
        //       this.$message.success('构建成功~')
        //       this.init()
        //     }).catch(_ => {
        //       this.getLoading = false
        //     })
        //     // }, 1000)
        //   })
        // }
      }
      // 编辑
      if (type === 'lookUserModal') {
        if (params.segments && params.segments.length) {
          this.$message.error('该模型不能被编辑，请先删除已经构建的块')
          return
        }
        if (params.flags === 5) {
          this.$message.error('模型构建中，请等待构建结果')
          return
        } else {
          return this.$router.push({
            path: '/analysisModel/createolap/selectStep',
            query: {
              cubeName: params.name, models: params.model
            }
          })
        }
      }
      // 共享
      if (this.$refs[type] || type === 'sharedTable') {
        this.$refs[type].dialog(params)
      }
    },
    closeExpands () {
      this.expands = []
    },
    // 【已废弃】
    handleCurrentChange (val) {
      this.currentPage = val
      this.init()
    },
    handleSizeChange (val) {
      this.pageSize = val
      this.init()
    },
    changeLoading () {
      this.getLoading = true
    },
    closeChangeLoading () {
      this.tableData = []
      this.getLoading = false
      this.init()
    },
    closeChangeLoadingLoser () {
      this.getLoading = false
    },
    moreData () {
      this.offset += 15
      this.init()
    },
    tableHead (row, column, rowIndex, columnIndex) {
      return 'tableHead'
    }
  }
}
</script>

<style lang="stylus" scoped>
.modelList{
  header{
    overflow:hidden;
    background:#fff;
    >>>.el-input{
     width 240px
     float left
     padding 16px 0 0 16px
    }
    >>>.el-input__suffix{
      height 32px
      margin-top 16px
    }
    >>>.el-button{
      float right
      margin 16px 16px 0 0
    }
    .nhc-elbtnwarp{
      overflow: hidden;
      position: absolute;
      left: 256PX;
    }
  }
  >>>.el-table__row{
    .el-table__expand-column{
      .cell{
        visibility: hidden;
      }
    }
  }

  >>>.el-table__expanded-cell{
    height 500px
    overflow auto
    width 100%
    padding 10px
    padding-bottom 50px
    .el-popover{
      display block!important
      position absolute
      top 0
      left 0
      width 100%
    }
  }
  .more {
    font-size 14px;
    height 40px
    line-height 40px
    background-color #0486FE
    color #ffffff
    margin-top 30px
    text-align center
    cursor pointer
  }
}
</style>
