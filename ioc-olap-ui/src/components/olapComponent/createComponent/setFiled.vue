<template>
  <div class="setFiled">
    <div class="containers">
      <serch-table></serch-table>
      <div class="dimension" style="margin-left:240px;">
        <p>
          <span>维度选择</span>
          <span style="color:green;margin-left:10px;">已选维度</span>
        </p>
        <el-form>
          <el-table
              :data="tableData"
              ref="multipleTable"
              tooltip-effect="dark"
              @selection-change="handleSelectionChange"
              style="margin-top: 10px;">
              <el-table-column type="selection" prop="全选" align="center"></el-table-column>
              <el-table-column prop="apiName" label="字段名称" align="center"> </el-table-column>
              <el-table-column prop="catalogName" label="字段类型" align="center"> </el-table-column>
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
    <steps :step="3" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import serchTable from '@/components/olapComponent/common/serchTable'
import steps from '@/components/olapComponent/common/steps'
export default {
  components: {
    serchTable,
    steps
  },
  data () {
    return {
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      tableData: [
        { apiName: '111', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '222', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '333', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '444', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '555', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' }
      ]
    }
  },
  mounted () {
  },
  methods: {
    nextModel (val) {
      this.$router.push('/olap/createolap/setMeasure')
      this.$parent.getStepCountAdd(val)
    },
    prevModel (val) {
      this.$router.push('/olap/createolap/createTableRelation')
      this.$parent.getStepCountReduce(val)
    },
    handleSelectionChange (val) {

    }
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
