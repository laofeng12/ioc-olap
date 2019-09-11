<template>
  <div class="setFiled">
    <div class="containers">
      <filed-table></filed-table>
      <div class="dimension" style="margin-left:270px;">
        <p>
          <span>维度选择</span>
          <span style="color:green;margin-left:10px;cursor:pointer" @click="selectFiled">已选维度</span>
        </p>
        <el-form>
          <el-table
              :data="tableData"
              v-loading="loading"
              ref="multipleTable"
              tooltip-effect="dark"
              @select="selectcheck"
              @select-all="selectAllCheck"
              style="margin-top: 10px;">
              <el-table-column type="selection" width="30" prop="全选" align="center"></el-table-column>
              <el-table-column prop="titName" label="字段名称" align="center"> </el-table-column>
              <el-table-column prop="dataType" label="字段类型" align="center"> </el-table-column>
              <el-table-column prop="name" label="显示名称" align="center">
                <template slot-scope="scope">
                  <el-form-item :prop="'tableData.' + scope.$index + '.name'">
                    <el-input type="text" v-model="scope.row.name" @change="iptChange(scope.row)"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                label="维度组合名称"
                align="center">
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
        this.saveSelectAllListFiled.forEach((item, index) => {
          let items = JSON.parse(item)
          if (items.resourceId === data.id) {
            items.data.columns && items.data.columns.map((n, i) => {
              n.mode = n.mode ? n.mode : '2'
              n.derived = n.name
              n.titName = n.name
              n.tableName = data.alias ? data.alias.substring(data.alias.indexOf('.') + 1) : ''
              n.id = `${data.alias}${i}`
              n.filed = data.alias === code ? '1' : '0'
            })
            // 获取对应的字段赋值到列表
            this.tableData = items.data.columns
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
                    this.tableData[i].name = val.name
                    this.tableData[i].mode = val.mode
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
      console.log(this.saveSelectFiled)
      this.saveSelectFiled && this.saveSelectFiled.forEach(item => {
        this.flags = item.filed === '1' ? 0 : ''
      })
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
      // 如果返回 true ，说明用户勾选了延伸模式，但并没有选择指定字段，所以需要提示错误信息。
      // if (this.iscubeMatch()) {
      //   return this.$message.warning('对应的foreign_key找不到~')
      // }
      // if (this.flags !== 0) return this.$message.warning('事实表字段必选~')
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
      // console.log('获取的', this.reloadNeedData)
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
      // console.log('获取的', factVal, '李帆', dimensionsVal)
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
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('tableNameActive')
  }
}
</script>

<style lang="stylus" scoped>
.setFiled{
  .containers{
    height calc(100vh - 150px)
    padding 20px 5px
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
      background #F2F2F2
    }
    >>>.el-table__header th{
      background #F2F2F2
      padding 8px 0
    }
    >>>.el-table--group::after, >>>.el-table--border::after, >>>.el-table::before{
      content: ''
      height 0!improtant
  }
  }
}
</style>
