<template>
  <div class="setMeasure">
    <el-form>
      <el-table
          :data="tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          :header-cell-class-name="tableHead"
          stripe
          style="margin-top: 10px;">
          <el-table-column type="index" label="序号" width="140px"> </el-table-column>
          <el-table-column prop="name" label="度量名称"> </el-table-column>
          <el-table-column prop="function.expression" label="计算方式"> </el-table-column>
          <el-table-column prop="function.parameter.value" label="计算值" class="moreContent" width="690px">
            <template slot-scope="scope">
              <div style="text-align: left;"><span>值：</span>{{scope.row.function.parameter.value}}&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<span>类型：</span>{{scope.row.function.returntype}}</div>
            </template>
          </el-table-column>
          <el-table-column prop="function.parameter.type" label="计算类型"> </el-table-column>
          <el-table-column
            label="操作"
            width="100">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" @click="addMeasure(scope.row)" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete" @click="handleChange(scope)"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" style="float:right;margin-top:20px;margin-right:20px;" @click="addMeasure()">添加度量</el-button>
    </el-form>
    <steps class="steps" :step="4" @nextModel="nextModel" @prevModel="prevModel"></steps>
    <add-measure ref="dialog"></add-measure>
  </div>
</template>

<script>
import steps from '@/components/analysisComponent/modelCommon/steps'
import addMeasure from '@/components/analysisComponent/dialog/addMeasure'
import { mapGetters } from 'vuex'
export default {
  components: {
    steps, addMeasure
  },
  data () {
    return {
      tableData: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.tableData = [...this.measureTableList]
    },
    nextModel (val) {
      if (this.measureTableList.length > 0) {
        this.$router.push('/analysisModel/createolap/reloadSet')
        this.$parent.getStepCountAdd(val)
      } else {
        this.$message.warning('至少添加一条度量')
      }
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/analysisModel/createolap/setFiled')
    },
    handleSelectionChange (val) {

    },
    handleChange (val) {
      let idx = val.$index
      this.$confirm('是否删除这条数据？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('deleteMeasureTableList', val.row.id)
        setTimeout(() => {
          this.$message.success('删除成功')
          this.tableData.splice(idx, 1)
        }, 500)
      })
    },
    addMeasure (data) {
      data ? this.$refs.dialog.dialog(data) : this.$refs.dialog.dialog()
    },
    tableHead (row, column, rowIndex, columnIndex) {
      return 'tableHead'
    }
  },
  computed: {
    ...mapGetters({
      measureTableList: 'measureTableList'
    })
  }
}
</script>

<style lang="stylus" scoped>
.setMeasure{
  height calc(100vh - 40px)
  margin-top 30px
  margin-bottom 76px
  background #ffffff
  >>>.el-table__body, >>>.el-table__header{
    width auto!important
  }
  >>>.el-table__body tr:nth-child(even){
      background #F5F7FA
    }
  >>>.el-table__body td{
    font-size: 14px;
    padding: 10px 0 !important;
  }
  >>>.el-table__header th{
      background #444444
      padding 10px 0
      color #ffffff
      font-family: PingFangSC-Regular;
      font-size: 14px;
    }
}
</style>
