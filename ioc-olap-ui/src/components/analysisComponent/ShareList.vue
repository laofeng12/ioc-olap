<template>
  <div class="modelList">
    <header>
      <el-input v-model="searchData.cubeName" size="small" placeholder="请输入服务名称" clearable>
      </el-input>
      <div class="nhc-elbtnwarp">
        <el-button type="primary" size="small" @click.native="searchFetch(searchData)">搜索</el-button>
      </div>
      <el-button type="primary" size="small"  @click="createolap">新建模型</el-button>
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
        @selection-change="handleSelectionChange"
        :header-cell-class-name="tableHead"
        stripe
       >
        <el-table-column  min-width="100%" show-overflow-tooltip type="expand">
          <template>
            <el-popover>
              <model-detail @closeExpands="closeExpands" :jsonData="jsonData"></model-detail>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="模型名称"  min-width="100%" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="status" label="模型状态"  min-width="100%" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
              <el-button :type="scope.row.status === 'DISABLED' ? 'type' : 'success'">{{scope.row.status === 'DISABLED' ? '禁用' : '准备中'}}</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="size_kb" label="模型大小"  min-width="100%" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>{{scope.row.size_kb}}.00kb</div>
          </template>
        </el-table-column>
        <el-table-column prop="input_records_count" label="资源记录"  min-width="100%" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="last_modified" label="上次构建的时间"  min-width="100%" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
               {{scope.row.last_modified | formatDate}}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="partitionDateStart" label="创建时间"  min-width="100%" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
               {{scope.row.create_time_utc | formatDate}}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="模型来源"  min-width="100%" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>共享</div>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
           min-width="100%">
          <template slot-scope="scope">
            <div class="play">
              <el-dropdown trigger="click" @command="handleCommand">
                <el-button type="text" size="small">操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="{type: 'lookShare', params: scope.row}">查看</el-dropdown-item>
                  <!--<el-dropdown-item :command="{type: 'dels', params: scope.row}">删除</el-dropdown-item>-->
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    <div class="more" v-if="moreShow && tableData.length >= 15" @click="moreData">更多数据</div>
    <!--<el-dialog title="查看共享人" :visible.sync="shareVisible">-->
      <!--<div class="shareBox dis-flex" v-loading="shareLoading">-->

      <!--</div>-->
      <!--<div slot="footer" class="dialog-footer">-->
        <!--<el-button @click="shareVisible = false">取 消</el-button>-->
        <!--<el-button type="primary" @click="share">确 定</el-button>-->
      <!--</div>-->
    <!--</el-dialog>-->
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
      jsonData: {},
      offset: 0,
      moreShow: true
    //   shareVisible: false,
    //   shareLoading: false
    }
  },
  filters: {
    formatDate (time) {
      // var date = new Date(time)
      return filterTime(time)
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    async init (val) {
      this.getLoading = true
      const params = {
        limit: 15,
        offset: this.offset,
        dateType: 0,
        ...val
      }
      const res = await getModelDataList(params)
      if (res.length > 0) {
        this.tableData = [...this.tableData, ...res]
      } else {
        this.moreShow = false
        // this.$message.success('已加载所有数据')
      }
      this.getLoading = false
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
        case 'lookShare':

          break
        case 'dels':

          break
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
    },
    handleSelectionChange () {

    },
    moreData () {
      this.offset += 15
      this.init()
    },
    tableHead(row, column, rowIndex, columnIndex){
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
      opacity 0
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
    height 40px
    line-height 40px
    background-color #409EFF
    color #ffffff
    margin-top 30px
    text-align center
    cursor pointer
  }
}
</style>
