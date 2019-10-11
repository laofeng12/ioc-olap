<template>
  <div class="setFiled">
    <div class="containers">
      <filed-table></filed-table>
      <div class="dimension">
        <p>
          <span>维度选择</span>
          <span @click="selectFiled">已选维度</span>
        </p>
        <el-form>
          <el-table
              :data="tableData"
              v-loading="loading"
              ref="multipleTable"
              tooltip-effect="dark"
              class="statusDiv"
              @select="selectcheck"
              @select-all="selectAllCheck"
              :header-cell-class-name="tableHead"
              stripe
              style="padding: 30px 16px 0px 16px !important;">
              <el-table-column type="selection" label="全选" width="100px"></el-table-column>
              <el-table-column prop="titName" label="字段名称" width="330px"> </el-table-column>
              <el-table-column prop="dataType" label="字段类型" width="175px"> </el-table-column>
              <el-table-column prop="name" label="显示名称" width="300px">
                <template slot-scope="scope">
                  <el-form-item :prop="'tableData.' + scope.$index + '.name'">
                    <el-input type="text" v-model="scope.row.name" @change="iptChange(scope.row)"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                label="维度组合名称">
                <template slot-scope="scope">
                  <div class="play">
                    <el-radio-group v-model="scope.row.filed === '1' ? '1' : scope.row.mode" @change="radioChange(scope.row)" :disabled="scope.row.filed === '1' ? true : false">
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
  mounted () {
    this.init()
  },
  methods: {
    init () {
      /**
       * 接受左侧列表通过兄弟通信传递过来的数据 ${data}
       */
      this.$root.eventBus.$on('filedTable', (data, code) => {
        // this.loading = true
        /**
         * 获取第一步保存的选择的表对应的所有字段
         * 遍历所有字段
         * 根据从左侧菜单带过来的${data.id}来进行匹配${item.resourceId} 获取对应的所有字段
         */
        // 这个需要判断是否是第一次进入。。 如果是第一次进入的话就需要调用已勾选的方法。。 不然就不需要调用此方法
        // if (this.tableData.length < 1) this.processData(code, data.alias)
        this.saveSelectAllListFiled.forEach((item, index) => {
          let items = JSON.parse(item)
          if (items.resourceId === data.id) {
            items.data.columns && items.data.columns.map((n, i) => {
              n.mode = n.mode ? n.mode : '2'
              n.derived = n.name
              n.titName = n.name
              n.tableName = data.alias ? data.alias : ''
              // n.id = `${data.alias}${i}`
              n.id = `${data.alias}.${n.name}`
              n.filed = data.alias === code ? '1' : '0'
            })
            // 获取对应的字段赋值到列表
            this.tableData = items.data.columns
            // 调用获取默认勾选的方法
            this.processData(code, data.alias)
            let arr = []
            setTimeout(() => {
              /**
               * 遍历获取tableData列表
               * 遍历 ${saveSelectFiled} 已经勾选的字段
               * 匹配两者相同的id对应的数据放到${arr}
               * 执行toggleSelection 存放匹配到的数据
               */
              this.tableData && this.tableData.forEach((item, i) => {
                this.saveSelectFiled && this.saveSelectFiled.forEach(val => {
                  if (val.id === item.id) {
                    this.tableData[i].name = String(val.name)
                    this.tableData[i].mode = String(val.mode)
                    arr.push(item)
                  }
                })
              })
              this.toggleSelection(arr)
            }, 500)
          }
        })
      })
      // 判断有乜选择事实表
      this.saveSelectFiled && this.saveSelectFiled.forEach(item => {
        this.flags = item.filed === '1' ? 0 : ''
      })
    },
    // 处理第二步建立的模型对应的字段
    processData (code, alias) {
      // 处理所有表对应的字段
      let values = []
      this.saveSelectAllListFiled.map((item, index) => {
        let items = JSON.parse(item)
        items.data.columns.map((res, i) => {
          values.push({
            tableName: items.name,
            name: res.name,
            titName: res.name,
            id: `${items.name}.${res.name}`,
            resid: items.resourceId,
            mode: items.code ? items.code : '2',
            derived: res.name,
            dataType: res.dataType,
            filed: items.name === code ? '1' : '0'
          })
        })
      })
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
        val.foreign_key.map((n, i) => {
          foreign_keys.push({
            name: n,
            id: `${n.split('.')[0]}.${n.split('.')[1]}`
          })
        })
        /*
          判断这个表是否设置了别名，如果设置了别名需要把最初的表名筛选出来
        */
        val.primary_key.map((n, i) => {
          if (item.alias !== item.table) {
            foreign_keys.push({
              name: `${item.table.split('.')[1]}.${n.split('.')[1]}`,
              id: `${item.table.split('.')[1]}.${n.split('.')[1]}`,
              titid: `${n.split('.')[0]}.${n.split('.')[1]}`
            })
          }
        })
      })
      // 组合第二步设置完的表名
      let result = [ ...foreign_keys, ...primary_keys ]

      // 遍历拿到的第二步数据 与 最终存储的字段盒子进行筛选 取到对应的数据
      values.map(res => {
        result.map(n => {
          if (res.id === n.id || res.id === n.titid) {
            res.id = n.titid ? n.titid : n.id
            resultData = [...resultData, res]
            selectRows.push(res)
          }
        })
      })
      setTimeout(() => {
        // 调用默认选中的数据
        this.toggleSelection(resultData)
        this.tableData && this.tableData.forEach((item, i) => {
          this.saveSelectFiled && this.saveSelectFiled.forEach(val => {
            if (val.id === item.id) {
              this.tableData[i].name = String(val.name)
              this.tableData[i].mode = String(val.mode)
            }
          })
        })
        // // 存放到store
        this.$store.dispatch('SaveSelectFiled', selectRows)
        // this.$store.dispatch('SaveNewSortList', selectRows) // 更新已选的框（如果返回上一步修改了别名）
        this.$store.dispatch('SaveFiledData')
      }, 500)
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
      if (this.saveSelectFiled.length === 0) {
        this.$message.warning('请选择维度字段')
      } else {
        this.$router.push('/analysisModel/createolap/setMeasure')
        this.$parent.getStepCountAdd(val)
      }
    },
    // 判断选择的衍生模式能否找到对应的fk
    iscubeMatch () {
      let dimensionsVal = []
      let factVal = []
      this.reloadNeedData.map(res => {
        if (res.value.split('.')[0] === this.jointResultData.fact_table.split('.')[1] && res.modeType === '1') {
          // 获取事实表的value
          dimensionsVal.push(res.value)
        }
        if (res.value.split('.')[0] === this.jointResultData.fact_table.split('.')[1] && res.modeType === '2') {
          // 获取非事实表选择的数据
          factVal.push(res.value)
        }
      })
      let isRowkey = this.factVal && this.factVal.length ? this.factVal.some(_ => dimensionsVal.includes(_)) : false
      // 是否选择延伸模式
      // 如果为 true 的话，说明不能下一步，提示用户。必须选择指定字段才允许下一步。
      return (isRowkey && factVal.length) || factVal.length
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
    radioChange (val) {
      this.$store.dispatch('changePushSelectFiled', val)
    },
    tableHead (row, column, rowIndex, columnIndex) {
      return 'tableHead'
    }
  },
  computed: {
    ...mapGetters({
      saveSelectFiled: 'saveSelectFiled',
      dimensions: 'dimensions',
      reloadNeedData: 'reloadNeedData',
      foreignKeyData: 'foreignKeyData',
      saveNewSortListstructure: 'saveNewSortListstructure',
      jointResultData: 'jointResultData',
      saveNewSortList: 'saveNewSortList',
      saveSelectAllListFiled: 'saveSelectAllListFiled'
    }),
    strings (val) {
      return String(val)
    }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('tableNameActive')
  }
}
</script>

<style lang="stylus" scoped>
.setFiled{
  .containers{
    // height calc(100vh - 150px)
    padding 16px 5px 76px 5px
    .dimension{
      background #ffffff
      margin-left:256px;
      padding-top:10px;
      p{
        margin-left:16px;
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
          margin-left:16px;
          cursor pointer
        }
      }
    }
    >>>.el-table::before{
    content: ''!important
    height 0!important
    }
    >>>.el-checkbox .el-checkbox__inner{
      width 16px
      height 16px
    }
    >>>.el-table__body-wrapper{
      height calc(100vh - 150px)
      padding-bottom 100px
      overflow auto
    }
    >>>.el-form-item{
      width 210px
      margin-bottom 0
      .el-input__inner{
        height 36px
        border: 1px solid #D9D9D9;
        background: #FFFFFF !important;
      }
    }
    >>>.el-radio-group{
      label:nth-child(1) {
        margin-right 18px
      }
    }
    >>>.el-radio .el-radio__inner{
      background  #FFFFFF
      border: 1px solid #D9D9D9 !important
      width 16px
      height 16px
    }
    >>>.el-radio .el-radio__label{
      color #5A5A5A !important
    }
    >>>.el-table__body td{
      border none!important
      padding 3px 0!important
      font-size 14px
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
    >>>.el-table__header .el-table-column--selection .cell .el-checkbox:after {
      content: " 全选";
    }
    >>>.el-table--group::after, >>>.el-table--border::after, >>>.el-table::before{
      content: ''
      height 0!improtant
  }
  }
}
</style>
