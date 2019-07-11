<template>
  <div class="setMeasure">
    <el-form>
      <el-table
          :data="tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          style="margin-top: 10px;">
          <el-table-column type="index" width="50" prop="序号" align="center"></el-table-column>
          <el-table-column prop="measureName" label="度量名称" align="center"> </el-table-column>
          <el-table-column prop="computeMode" label="计算方式" align="center"> </el-table-column>
          <el-table-column prop="computeValue" label="计算值" align="center">
            <template slot-scope="scope">
              <span style="margin-right:15px;">值：{{'LEAF_CATEG_ID'}}</span>
              <span>类型：{{scope.row.computeType}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="computeType" label="计算类型" align="center"> </el-table-column>
          <el-table-column
            label="操作"
            width="100"
            align="center">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" @click="addMeasure(scope.row)" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete" @click="handleChange(scope)"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="addMeasure()">添加度量</el-button>
    </el-form>
    <add-measure ref="dialog"></add-measure>
    <steps class="steps" :step="4" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
import addMeasure from '@/components/olapComponent/dialog/addMeasure'
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
      console.log(this.tableData)
    },
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      this.$router.push('/olap/createolap/reloadSet')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/setFiled')
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

}
</style>
