<template>
  <div class="setFiled">
    <div class="containers">
      <filed-table></filed-table>
      <div class="dimension" style="margin-left:240px;">
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
              style="margin-top: 10px;">
              <el-table-column type="selection" prop="全选" align="center"></el-table-column>
              <el-table-column prop="comment" label="字段名称" align="center"> </el-table-column>
              <el-table-column prop="dataType" label="字段类型" align="center"> </el-table-column>
              <el-table-column prop="apiPaths" label="显示名称" align="center">
                <template slot-scope="scope">
                  <el-form-item :prop="'tableData.' + scope.$index + '.apiPaths'">
                    <el-input type="text" v-model="scope.row.apiPaths" @blur="iptChange(scope.row, scope.$index)"></el-input>
                  </el-form-item>
                </template>
              </el-table-column>
              <el-table-column
                label="维度组合名称"
                align="center">
                <template slot-scope="scope">
                  <div class="play">
                    <el-radio-group v-model="scope.row.mode" @change="radioChange(scope.$index)">
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
import filedTable from '@/components/olapComponent/common/filedTable'
import steps from '@/components/olapComponent/common/steps'
import selectFiled from '@/components/olapComponent/dialog/selectFiled'
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
      tableData: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.selectTableTotal.length < 1 && this.$router.push('/olap/createolap/selectStep')
      this.$root.eventBus.$on('filedTable', (res, code) => {
        this.loading = true
        if (code === 200) {
          this.tableData = res
          setTimeout(() => {
            this.loading = false
            let arr = []
            this.tableData.forEach((item, i) => {
              this.saveSelectFiled && this.saveSelectFiled.forEach(val => {
                if (val.id === item.id) {
                  arr.push(item)
                }
              })
              this.saveList && this.saveList.forEach((val, index) => {
                if (val.id === item.id) {
                  this.tableData[i].apiPaths = val.apiPaths
                  this.tableData[i].mode = val.mode
                }
              })
            })
            this.toggleSelection(arr)
          }, 300)
        }
      })
    },
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
      // if (this.saveSelectFiled.length === 0) return this.$message.warning('请选择维度字段')
      // let flag
      // this.saveSelectFiled && this.saveSelectFiled.forEach(item => {
      //   flag = item.filed !== 1 ? 1 : 0
      // })
      // flag === '1' ? this.$message.warning('请选择事实表维度字段') : (this.$router.push('/olap/createolap/setMeasure') && this.$parent.getStepCountAdd(val))
      this.$router.push('/olap/createolap/setMeasure') && this.$parent.getStepCountAdd(val)
    },
    prevModel (val) {
      this.$router.push('/olap/createolap/createTableRelation')
      this.$parent.getStepCountReduce(val)
    },
    selectcheck (rows, row) {
      let selected = rows.length && rows.indexOf(row) !== -1
      selected ? this.$store.dispatch('saveSelectFiled', row) : this.$store.dispatch('removeSelectFiled', row)
      this.$store.dispatch('SaveNewSortList', this.saveSelectFiled)
      // 若点击 左侧对应父级菜单高亮
      this.$root.eventBus.$emit('tableNameActive')
    },
    selectFiled () {
      this.$store.dispatch('SaveNewSortList', this.saveSelectFiled)
      this.$refs.dialog.dialog()
    },
    // 输入框监听
    iptChange (val, index) {
      this.$store.dispatch('changePushSelectFiled', index)
    },
    // 单选框触发
    radioChange (index) {
      this.$store.dispatch('changePushSelectFiled', index)
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectFiled: 'saveSelectFiled',
      saveNewSortList: 'saveNewSortList',
      saveList: 'saveList'
    })
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
