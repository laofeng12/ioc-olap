<template>
  <div class="dis-flex">
    <OlapAside></OlapAside>
    <div class="olapTable">
      <div class="top">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="100px" class="demo-ruleForm">
          <el-form-item label="活动名称" prop="name">
            <el-input v-model="ruleForm.name"></el-input>
          </el-form-item>
          <el-form-item label="活动区域" prop="folder">
            <el-select v-model="ruleForm.folder" placeholder="请选择活动区域">
              <el-option label="区域一" value="shanghai"></el-option>
              <el-option label="区域二" value="beijing"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
      </div>
      <ResultBox v-if="tableData.length > 0" :theadData="theadData" :tableData="tableData" :diffWidth="736"></ResultBox>
    </div>
  </div>
</template>

<script>
import OlapAside from '../../components/olapComponent/olapAside'
import ResultBox from '../../components/olapComponent/common/ResultBox'

export default {
  components: { OlapAside, ResultBox },
  data () {
    return {
      ruleForm: {
        name: '',
        folder: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入活动名称', trigger: 'blur' },
          { min: 1, max: 20, message: '长度在 1 到 20 个字', trigger: 'blur' }
        ],
        folder: [
          { required: true, message: '请选择活动区域', trigger: 'change' }
        ]
      },
      theadData: [
        { prop: 'column1', label: '日期' },
        { prop: 'column2', label: '姓名' },
        { prop: 'column3', label: '地址' },
        { prop: 'column4', label: '日期' },
        { prop: 'column5', label: '姓名' },
        { prop: 'column6', label: '地址' }
      ],
      tableData: [
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
      ],
    }
  },
  watch: {},
  mounted () {
    // this.setSortTable()
  },
  methods: {}
}
</script>
<style lang="scss" scoped>
  /*.olapTable {*/
    /*width: calc(100vh - 400px);*/
  /*}*/
</style>
