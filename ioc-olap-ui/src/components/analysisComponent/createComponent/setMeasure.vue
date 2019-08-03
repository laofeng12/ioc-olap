<template>
  <div class="setMeasure">
    <el-form>
      <el-table
          :data="tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          style="margin-top: 10px;">
          <el-table-column type="index" width="50" label="序号" align="center"></el-table-column>
          <el-table-column prop="name" label="度量名称" align="center"> </el-table-column>
          <el-table-column prop="function.expression" label="计算方式" align="center"> </el-table-column>
          <el-table-column prop="function.parameter.value" label="计算值" align="center" class="moreContent">
            <template slot-scope="scope">
              <div style="text-align: left;"><span>值：</span>{{'LEAF_CATEG_ID'}}</div>
              <div style="text-align: left;"><span>类型：</span>{{scope.row.function.parameter.value}}</div>
            </template>
          </el-table-column>
          <el-table-column prop="function.parameter.type" label="计算类型" align="center"> </el-table-column>
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
        <el-button type="primary" style="float:right;margin-top:20px;" @click="addMeasure()">添加度量</el-button>
    </el-form>
    <add-measure ref="dialog"></add-measure>
    <steps class="steps" :step="4" @nextModel="nextModel" @prevModel="prevModel"></steps>
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
      console.log(this.measureTableList.length)
      this.measureTableList.length > 0
        ? this.$router.push('/analysisModel/createolap/reloadSet') && this.$parent.getStepCountAdd(val)
        : this.$message.warning('至少添加一条度量')
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
