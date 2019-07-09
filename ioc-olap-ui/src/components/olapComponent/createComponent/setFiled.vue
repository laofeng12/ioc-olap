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
               @selection-change="handleSelectionChange"
              style="margin-top: 10px;">
              <el-table-column type="selection" prop="全选" align="center"></el-table-column>
              <el-table-column prop="comment" label="字段名称" align="center"> </el-table-column>
              <el-table-column prop="dataType" label="字段类型" align="center"> </el-table-column>
              <el-table-column prop="apiPaths" label="显示名称" align="center">
                <template slot-scope="scope">
                  <div>
                    <el-input type="text" v-model="scope.row.apiPaths"></el-input>
                  </div>
                </template>
              </el-table-column>
              <el-table-column
                label="维度组合名称"
                align="center">
                <template slot-scope="scope">
                  <div class="play">
                    <el-radio v-model="scope.row.radio" label="1">正常模式</el-radio>
                    <el-radio v-model="scope.row.radio" label="2">衍生模式</el-radio>
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
import { reduceObj } from '@/utils/index'
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
    this.$root.eventBus.$emit('createTable', this.selectTableCount)
    this.$root.eventBus.$on('filedTable', (res, code) => {
      let reduceData = this.saveSelectFiled
      this.loading = true
      if (code === 200) {
        this.tableData = res
        setTimeout(() => {
          this.loading = false
          let arr = []
          this.tableData.forEach(item => {
            reduceData && reduceData.forEach(val => {
              if (val.columnName === item.columnName) {
                arr.push(item)
              }
            })
          })
          this.toggleSelection(arr)
        }, 300)
      }
    })
  },
  methods: {
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
      this.$router.push('/olap/createolap/setMeasure')
      this.$parent.getStepCountAdd(val)
    },
    prevModel (val) {
      this.$router.push('/olap/createolap/createTableRelation')
      this.$parent.getStepCountReduce(val)
    },
    handleSelectionChange (val) {
      this.$store.dispatch('saveSelectFiled', val)
    },
    selectcheck (rows, row) {
      let selected = rows.length && rows.indexOf(row) !== -1
      !selected && this.$store.dispatch('removeSelectFiled', row)
      // 若点击 左侧对应父级菜单高亮
      this.$root.eventBus.$emit('tableNameActive')
    },
    selectFiled () {
      let data = this.saveSelectFiled// 去重后的选择项
      this.$refs.dialog.dialog(data)
    }
  },
  computed: {
    ...mapGetters({
      selectTableCount: 'selectTableCount',
      saveSelectFiled: 'saveSelectFiled'
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
