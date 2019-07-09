<template>
  <div class="setMeasure">
    <el-form>
      <el-table
          :data="tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          style="margin-top: 10px;">
          <el-table-column type="index" prop="序号" align="center"></el-table-column>
          <el-table-column prop="apiName" label="度量名称" align="center"> </el-table-column>
          <el-table-column prop="type" label="计算方式" align="center"> </el-table-column>
          <el-table-column prop="apiPaths" label="计算值" align="center">
            <template slot-scope="scope">
              <span style="margin-right:15px;">值：{{scope.row.apiPaths[0].a}}</span>
              <span>类型：{{scope.row.apiPaths[0].b}}</span>
            </template>
          </el-table-column>
          <el-table-column prop="catalogName" label="计算类型" align="center"> </el-table-column>
          <el-table-column
            label="操作"
            align="center">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" @click="addMeasure(tableData)" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="addMeasure">添加度量</el-button>
    </el-form>
    <add-measure ref="dialog"></add-measure>
    <steps class="steps" :step="4" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
import addMeasure from '@/components/olapComponent/dialog/addMeasure'
export default {
  components: {
    steps, addMeasure
  },
  data () {
    return {
      tableData: [
        { apiName: '111', type: '递归', catalogName: 'string', apiPaths: [ { a: 123123, b: 'aaaaaa' } ] },
        { apiName: '222', type: '递归', catalogName: 'string', apiPaths: [ { a: 123123, b: 'aaaaaa' } ] },
        { apiName: '333', type: '递归', catalogName: 'string', apiPaths: [ { a: 123123, b: 'aaaaaa' } ] },
        { apiName: '444', type: '递归', catalogName: 'string', apiPaths: [ { a: 123123, b: 'aaaaaa' } ] },
        { apiName: '555', type: '递归', catalogName: 'string', apiPaths: [ { a: 123123, b: 'aaaaaa' } ] }
      ]
    }
  },
  methods: {
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
    addMeasure () {
      this.$refs.dialog.dialog()
    }
  }
}
</script>

<style lang="stylus" scoped>
.setMeasure{

}
</style>
