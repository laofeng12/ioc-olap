<template>
  <div class="modelList">
    <header>
      <el-input v-model="searchData.cubeName" placeholder="请输入服务名称" clearable>
        <template slot="append">
          <el-button type="primary" size="small" icon="el-icon-search" @click.native="searchFetch(searchData)">搜索</el-button>
        </template>
      </el-input>
      <el-button type="success" size="small" icon="el-icon-plus" @click="createolap">新建模型</el-button>
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
        style="width: 100%;margin-top: 10px;">
        <el-table-column align="center" show-overflow-tooltip type="expand">
          <template>
            <el-popover>
              <model-detail @closeExpands="closeExpands" :jsonData="jsonData"></model-detail>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="模型名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="status" label="模型状态" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
              <el-button size="mini" :type="scope.row.status === 'DISABLED' ? 'type' : 'success'">{{scope.row.status === 'DISABLED' ? '禁用' : '准备中'}}</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="size_kb" label="模型大小" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>{{scope.row.size_kb}}.00kb</div>
          </template>
        </el-table-column>
        <el-table-column prop="input_records_count" label="资源记录" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="last_modified" label="上次构建的时间" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
               {{scope.row.last_modified | formatDate}}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="partitionDateStart" label="创建时间" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
               {{scope.row.create_time_utc | formatDate}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="模型来源" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
               {{scope.row.modelSource ? '共享' : '自建'}}
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          align="center">
          <template slot-scope="scope">
            <div class="play">
              <el-dropdown trigger="click" @command="handleCommand">
                <el-button type="text" size="small">操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="{type: 'lookDetail', params: scope.row}">查看</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row}">编辑</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'construct', params: scope.row}">构建</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'reloads', params: scope.row}">刷新</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'disableds', params: scope.row}">禁用</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'enable', params: scope.row}">启用</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'sharedTable', params: scope.row}">共享</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'clones', params: scope.row}">复制</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'dels', params: scope.row}">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- <element-pagination :total="totalCount" :pageSize="pageSize" :currentPage="currentPage" @handleCurrentChange="handleCurrentChange" @handleSizeChange="handleSizeChange"></element-pagination> -->
      <clones ref="clones"></clones>
      <construct ref="construct"></construct>
      <reloads ref="reloads"></reloads>
      <merge ref="merge"></merge>
      <sharedTable ref="sharedTable"></sharedTable>
  </div>
</template>

<script>
import { getModelDataList, buildModeling, disableModeling, deleteCubeModeling, enableModeling, descDataList } from '@/api/modelList'
import { modelDetail, clones, construct, reloads, merge, sharedTable } from '@/components/analysisComponent/modelListComponent'
import elementPagination from '@/components/ElementPagination'
import { filterTime } from '@/utils/index'
export default {
  components: {
    modelDetail, clones, construct, reloads, merge, sharedTable, elementPagination
  },
  data () {
    return {
      searchData: {
        cubeName: ''
      },
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
      jsonData: {}
    }
  },
  filters: {
    formatDate (time) {
      // var date = new Date(time)
      return filterTime(time)
    }
  },
  mounted () {
    // this.init()
  },
  methods: {
    async init (val) {
      this.getLoading = true
      const params = {
        limit: 50,
        offset: 0,
        ...val
      }
      // getModelDataList(params).then(res => {
      //   this.tableData = res
      //   this.getLoading = false
      // })
      // await getModelDataList(params).then(res => {
      //   this.tableData = res
      //   this.getLoading = false
      // }).catch(_ => {
      //   this.getLoading = false
      // })
    },
    searchFetch (val) {
      this.init(val)
      console.log(val)
    },
    createolap () {
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
        descDataList({ cubeName: params.name, models: params.model }).then(res => {
          if (res) {
            this.jsonData = res
            // console.log(JSON.stringify(res.ModesList.lookups), '==============')
          }
        })
        return
      }
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
      }
      if (type === 'construct') {
        if (params.segments.length > 0 && params.partitionDateColumn) {
          this.$refs['construct'].dialog(params)
        } else {
          return this.$confirm('是否构建该模型', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(async () => {
            this.getLoading = true
            // this.$throttle(() => {
            await buildModeling({ cubeName: params.name, start: 0, end: 0 }).then(res => {
              this.$message.success('构建成功~')
              this.init()
            }).catch(_ => {
              this.getLoading = false
            })
            // }, 1000)
          })
        }
      }
      this.$refs[type].dialog(params)
    },
    closeExpands () {
      this.expands = []
    },
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
      this.getLoading = false
      this.init()
    }
  }
}
</script>

<style lang="stylus" scoped>
.modelList{
  header{
    height 60px
    background #C9E8FF
    padding 15px
    >>>.el-input{
      width 250px
      float left
    }
    >>>.el-button{
      float right
    }
  }
  >>>.el-table__row{
    .el-table__expand-column{
      opacity 0
    }
  }
  >>>.el-table__body-wrapper{
    .el-button{
      width 80px!important
      text-align center
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
}
</style>
