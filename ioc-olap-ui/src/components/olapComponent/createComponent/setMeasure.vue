<template>
  <div class="setMeasure">
    <el-form>
      <el-table
          :data="tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          style="margin-top: 10px;">
          <el-table-column type="selection" prop="全选" align="center"></el-table-column>
          <el-table-column prop="apiName" label="度量名称" align="center"> </el-table-column>
          <el-table-column prop="type" label="计算方式" align="center"> </el-table-column>
          <el-table-column prop="catalogName" label="计算值" align="center"> </el-table-column>
          <el-table-column prop="apiPaths" label="返回类型" align="center">
            <template slot-scope="scope">
              <div>
                <el-input type="text" v-model="scope.row.apiPaths"></el-input>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary">添加度量</el-button>
    </el-form>
    <steps class="steps" :step="4" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
export default {
  components: {
    steps
  },
  data () {
    return {
      tableData: [
        { apiName: '111', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '222', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '333', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '444', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '555', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' }
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

    }
  }
}
</script>

<style lang="stylus" scoped>
.setMeasure{

}
</style>
