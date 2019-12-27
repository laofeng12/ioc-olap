<template>
  <div class="setFiled">
    <div class="containers">
      <filed-table ref="filedTable"></filed-table>
      <div class="dimension">
        <p>
          <span>维度选择</span>
          <span @click="selectFiled">已选维度</span>
        </p>
        <el-form>
          <el-table :data="tableData" v-loading="loading" ref="multipleTable" tooltip-effect="dark" class="statusDiv"
                    @select="selectcheck" @select-all="selectAllCheck" style="margin-top: 10px;">
              <el-table-column type="selection" :selectable="isSelectable" width="50" prop="全选" align="center"></el-table-column>
              <el-table-column prop="titName" label="字段名称" align="center"></el-table-column>
              <el-table-column prop="type" label="字段类型" align="center"></el-table-column>
              <el-table-column prop="name" label="显示名称" align="center">
                <template slot-scope="scope">
                  <el-form-item :prop="'tableData.' + scope.$index + '.name'">
                    <el-input type="text" v-model="scope.row.name" @change="iptChange(scope.row)"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column label="维度组合名称" align="center">
                <template slot-scope="scope">
                  <div class="play">
                    <!--<el-radio-group v-model="scope.row.filed === '1' ? '1' : scope.row.mode"-->
                    <el-radio-group v-model="scope.row.mode" @change="radioChange(scope.row)"
                                    :disabled="scope.row.filed === '1' || scope.row.defaultVal === 'n'">
                      <el-radio label="1">正常模式</el-radio>
                      <el-radio label="2">衍生模式</el-radio>
                    </el-radio-group>
                  </div>
                </template>
              </el-table-column>
            </el-table>
        </el-form>
      </div>
    </div>
    <select-filed ref="dialog"></select-filed>
    <steps class="steps" :step="3" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import filedTable from '@/components/analysisComponent/modelCommon/filedTable'
import steps from '@/components/analysisComponent/modelCommon/steps'
import selectFiled from '@/components/analysisComponent/dialog/selectFiled'
import { mapGetters } from 'vuex'
import { setTimeout } from 'timers'
export default {
  components: {
    filedTable,
    steps,
    selectFiled
  },
  data () {
    return {
      pageSize: 20,
      currentPage: 1,
      loading: false,
      totalCount: 1,
      tableData: [],
      flags: 0
    }
  },
  computed: {
    ...mapGetters([
      'saveSelectFiled',
      'reloadNeedData',
      'saveNewSortListstructure',
      'jointResultData',
      'saveNewSortList',
      'saveSelectAllListFiled',
      'ModelAllList'
    ])
  },
  watch: {
    // '$route' () {
    //   this.$refs.filedTable.init()
    // }
  },
  activated () {
    // this.$refs.filedTable.init()
    this.init()
  },
  methods: {
    init () {
      /**
       * 接受左侧列表通过兄弟通信传递过来的数据 ${data}
       */
      this.$root.eventBus.$on('filedTable', (data, code) => {
        /**
         * 获取第一步保存的选择的表对应的所有字段
         * 遍历所有字段
         * 根据从左侧菜单带过来的${data.id}来进行匹配${item.resourceId} 获取对应的所有字段
         */
        // 判断是否为一张事实表
        if (!data.id) {
          this.loading = true
          setTimeout(() => {
            this.saveSelectAllListFiled.forEach((item, index) => {
              // let items = JSON.parse(item)
              item.column && item.column.map((n, i) => {
                n.mode = n.mode ? n.mode : '2'
                n.derived = n.definition
                n.titName = n.definition // 字段名称
                n.tableName = data.alias ? data.alias : ''
                n.id = `${data.alias}.${n.definition}`
                n.filed = data.alias === code ? '1' : '0'
              })
              // 获取对应的字段赋值到列表
              // this.tableData = items.data.columns
              this.tableData = item.column
              let arr = []
              setTimeout(() => {
                this.tableData && this.tableData.forEach((item, i) => {
                  this.saveSelectFiled && this.saveSelectFiled.forEach(val => {
                    if (val.id === item.id) {
                      this.tableData[i].name = String(val.name)
                      this.tableData[i].mode = String(val.mode)
                      this.tableData[i].defaultVal = ''
                      arr.push(item)
                    }
                  })
                })
                this.toggleSelection(arr)
              }, 500)
            })
            this.loading = false
          }, 1000)
        } else {
          this.saveSelectAllListFiled.forEach((item, index) => {
            // let items = JSON.parse(item)
            if (item.resourceId === data.id) { // 根据id获取对应数据
            // if (items.name === data.joinTable) { // 根据name获取对应数据
              item.column && item.column.map((n, i) => {
                n.mode = n.mode ? n.mode : '2' // 默认衍生模式
                n.derived = n.definition
                n.titName = n.definition // 字段名称
                n.tableName = data.alias ? data.alias : ''
                // n.id = `${data.alias}${i}`
                n.id = `${data.alias}.${n.definition}`
                n.filed = data.alias === code ? '1' : '0'
              })
              // 获取对应的字段赋值到列表
              this.tableData = item.column
              // 调用获取默认勾选的方法
              this.processData(code, data.alias)
              let arr = []
              // 搞不懂明明 processData 里面调用了以此勾选, 为什么还要做一次
              setTimeout(() => {
                /**
                 * 遍历获取tableData列表
                 * 遍历 ${saveSelectFiled} 已经勾选的字段
                 * 匹配两者相同的id对应的数据放到${arr}
                 * 执行toggleSelection 存放匹配到的数据
                 */
                if (this.ModelAllList.CubeList) {
                  this.tableData && this.tableData.forEach((item, i) => {
                    this.ModelAllList && this.ModelAllList.CubeList.dimensions.forEach(val => {
                      if (val.id === item.id) {
                        this.tableData[i].name = String(val.name)
                        this.tableData[i].mode = String(val.derived) === 'null' ? '1' : '2'
                        arr.push(item)
                      }
                    })
                  })
                } else {
                  this.tableData && this.tableData.forEach((item, i) => {
                    this.saveSelectFiled && this.saveSelectFiled.forEach(val => {
                      if (val.id === item.id) {
                        this.tableData[i].name = String(val.name)
                        this.tableData[i].mode = String(val.mode)
                        arr.push(item)
                      }
                    })
                  })
                }
                this.toggleSelection(arr)
              }, 500)
            }
          })
        }
      })
      // 判断有乜选择事实表
      this.saveSelectFiled && this.saveSelectFiled.forEach(item => {
        this.flags = item.filed === '1' ? 0 : ''
      })
    },
    // 处理第二步建立的模型对应的字段
    async processData (code, alias) {
      // 处理所有表对应的字段
      let values = []
      // this.saveSelectAllListFiled.map((item, index) => {
      //   let items = JSON.parse(item)
      //   items.data.columns.map((res, i) => {
      //     values.push({
      //       tableName: items.name,
      //       name: res.name,
      //       titName: res.name,
      //       id: `${items.name}.${res.name}`,
      //       resid: items.resourceId,
      //       mode: items.code ? items.code : '2',
      //       derived: res.name,
      //       dataType: res.dataType,
      //       filed: items.name === code ? '1' : '0'
      //     })
      //   })
      // })
      this.saveSelectAllListFiled.forEach((item, index) => {
        item.column.map((res, i) => {
          values.push({
            tableName: item.resourceTableName,
            name: res.columnAlias,
            titName: res.columnAlias,
            id: `${item.resourceTableName}.${res.columnAlias}`,
            resid: item.resourceId,
            mode: item.code ? item.code : '2',
            derived: res.columnAlias,
            type: res.type,
            filed: item.name === code ? '1' : '0'
          })
        })
      })
      // 初始化已处理过的选择维度
      this.saveNewSortListstructure.forEach((res, i) => {
        this.saveNewSortListstructure.splice(i)
      })
      let result = []
      // 获取第二步建表的数据
      const data = JSON.parse(JSON.stringify(this.jointResultData))
      // 创建一个存储对应的主表字段的盒子
      let foreign_keys = []
      // 创建一个存储对应的子表字段的盒子
      let primary_keys = []
      // 最终的数据要存放的盒子
      let resultData = []
      // 创建一个要存储建表的数据
      let selectRows = []
      // 遍历第二步生成的数据， 拿到对应的字段存放到对应的盒子中
      data.lookups.map(item => {
        let val = item.join
        // 主表的判断
        val.foreign_key.map((n, i) => {
          if (item.joinAlias !== item.joinTable) {
            foreign_keys.push({
              name: `${item.joinTable}.${n.split('.')[1]}`,
              // id: `${item.joinTable}.${n.split('.')[1]}`,
              id: n,
              titid: n
            })
          } else {
            foreign_keys.push({ name: n, id: n })
          }
        })
        /*
          判断这个表是否设置了别名，如果设置了别名需要把最初的表名筛选出来
        */
        val.primary_key.map((n, i) => {
          if (item.alias !== item.table.split('.')[1]) {
            primary_keys.push({
              name: `${item.table.split('.')[1]}.${n.split('.')[1]}`,
              // id: `${item.table.split('.')[1]}.${n.split('.')[1]}`,
              id: n,
              titid: n
            })
          } else {
            primary_keys.push({ name: n, id: n })
          }
        })
      })
      // 组合第二步设置完的表名
      result = [ ...foreign_keys, ...primary_keys ]
      // 遍历拿到的第二步数据 与 ModelAllList最终存储的字段盒子进行筛选 取到对应的数据
      values.map((res, i) => {
        result.map(n => {
          // 找出设置为别名的数据push到总的数据中 替换对应的id
          if (n.titid && n.name === res.id) {
            const newRes = Object.assign({}, res, { id: n.titid })
            values.push(newRes)
          }
        })
      })
      values.map((res, i) => {
        result.map(n => {
          if (res.id === n.id) {
            resultData = [...resultData, res]
            foreign_keys.map(val => {
              if (val.id.toUpperCase() === res.id.toUpperCase()) {
                Object.assign(res, { mode: '1', fuck: '1' })
              } else {
                Object.assign(res, { mode: '2' })
              }
            })
            selectRows.push(res)
          }
        })
      })
      selectRows.map(res => { if (res.fuck) res.mode = '1' })
      this.tableData && this.tableData.map((item, i) => {
        // 筛选出是否为外键 如果是外键就要加上唯一标识${defaultVal} === 'n'
        foreign_keys.map(val => {
          if (val.id === item.id || val.titid === item.id) {
            Object.assign(item, { defaultVal: 'n' })
          }
        })
        // 筛选出是否为主键或者外键
        result.map(x => {
          if (item.id === x.id || item.id === x.titid) {
            Object.assign(item, { primary: '1' })
          }
        })
      })
      setTimeout(() => {
        // 调用默认选中的数据
        this.toggleSelection(resultData)
        // 存放到store
        this.$store.dispatch('SaveSelectFiled', selectRows)
        this.$store.dispatch('SaveFiledData')
      }, 300)


        // // 存放到store
        // await this.$store.dispatch('SaveSelectFiled', selectRows)
        // await this.$store.dispatch('SaveFiledData')
        // // 调用默认选中的数据
        // this.toggleSelection(resultData)
    },
    // 判断是否为主键或者外键
    isSelectable (row, index) {
      if (row.primary) {
        return 0
      } else {
        return 1
      }
    },
    // 接收已选择的id 根据id展示对应的复选框
    toggleSelection (rows) {
      if (rows) {
        rows.forEach(row => {
          this.$refs.multipleTable.toggleRowSelection(row, true)
        })
      } else {
        this.$refs.multipleTable.clearSelection()
      }
    },
    nextModel (val) {
      if (!this.saveSelectFiled.length) {
        this.$message.warning('请选择维度字段')
      } else {
        this.$parent.getStepCountAdd(val)
        this.$router.push('/analysisModel/createolap/setMeasure')
      }
    },
    prevModel (val) {
      this.$router.push('/analysisModel/createolap/createTableRelation')
      this.$parent.getStepCountReduce(val)
    },
    /**
     * 点击复选框
     * 根据rows的长度来判断是选择还是取消
     */
    selectcheck (rows, row) {
      let selected = rows.length && rows.indexOf(row) !== -1
      selected ? this.$store.dispatch('SaveSelectFiled', row) : this.$store.dispatch('RemoveSelectFiled', row)
      this.$store.dispatch('SaveNewSortList', this.saveSelectFiled)
      this.$store.dispatch('SaveFiledData')
      // 若点击 左侧对应父级菜单高亮
      this.$root.eventBus.$emit('tableNameActive')
    },
    /**
     * 全选功能 rows的长度> 0 = 全选
     * 取消全选的时候需要把rows以及tableName带过去过滤
     */
    selectAllCheck (rows) {
      let list = {
        list: rows,
        id: this.tableData[0].tableName
      }
      rows.length > 0 ? this.$store.dispatch('SaveSelectFiled', rows) : this.$store.dispatch('RemoveSelectFiled', list)
      this.$store.dispatch('SaveNewSortList', this.saveSelectFiled)
      this.$store.dispatch('SaveFiledData')
    },
    selectFiled () {
      /*
      * 判断新增还是编辑
        判断是否存在已选择的维度
        如果没有的话就是新增，就要勾选列后筛选出对应的维度
      */
      this.saveNewSortListstructure.length < 1 ? this.$store.dispatch('SaveNewSortList', this.saveSelectFiled) : this.saveNewSortListstructure
      this.$refs.dialog.dialog()
    },
    // 输入框监听
    iptChange (val) {
      this.$store.dispatch('changePushalias', val)
    },
    // 单选框触发
    async radioChange (val) {
      await this.$store.dispatch('changePushSelectFiled', val)
      this.$root.eventBus.$emit('filedTable', this.$refs.filedTable.catchData.data, this.$refs.filedTable.catchData.code)
    }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('tableNameActive')
    this.$root.eventBus.$off('filedTable')
  }
}
</script>

<style lang="stylus" scoped>
.setFiled{
  padding-bottom 60px
  .containers{
    // height calc(100vh - 150px)
    padding 20px 5px
    .dimension{
      background #ffffff
      margin-left:245px;
      padding-top:10px;
      p{
        margin-left:10px;
        span:nth-child(1){
          font-family: PingFangSC-Medium;
          font-size: 16px;
          color: #262626;
          letter-spacing: 0;
        }
        span:nth-child(2){
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: #0486FE;
          letter-spacing: 0;
          text-align: center;
          line-height: 14px;
          margin-left:15px;
          cursor pointer
        }
      }
    }
    >>>.el-table::before{
    content: ''!important
    height 0!important
    }
    >>>.el-table__body-wrapper{
      height calc(100vh - 150px)
      padding-bottom 100px
      overflow auto
    }
    >>>.el-form-item{
      margin-bottom 0
      .el-input__inner{
        height 35px
      }
    }
    >>>.el-radio-group{
      label:nth-child(1) {
        margin-right 18px
      }
    }
    >>>.el-table__body td{
      border none!important
      padding 5px 0!important
    }
    >>>.el-table__body tr:nth-child(even){
      background #F5F7FA
    }
    >>>.el-table__header th{
      background #444444
      padding 8px 0
      color #ffffff
      font-family: PingFangSC-Regular;
      font-size: 14px;
    }
    >>>.el-table--group::after, >>>.el-table--border::after, >>>.el-table::before{
      content: ''
      height 0!improtant
    }
    >>>.is-disabled{
      .el-checkbox__inner{
        background #1877F1
        border-color #1877F1
      }
      .el-checkbox__inner::after{
      }
    }
    >>>.el-table .cell {
      overflow -webkit-paged-x
    }
  }
}
</style>
