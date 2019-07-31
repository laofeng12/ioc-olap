<template>
  <div class="dis-flex">
    <OlapAside></OlapAside>
    <div class="olapTable">
      <div class="top">
        <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="80px" :inline="true">
          <el-form-item class="form-item" label="文件夹" prop="folder">
            <el-select v-model="ruleForm.folder" placeholder="请选择文件夹">
              <el-option label="文件夹1" value="1"></el-option>
              <el-option label="文件夹2" value="2"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item class="form-item" label="文件名称" prop="name">
            <el-input v-model="ruleForm.name" placeholder="请输入文件名称"></el-input>
          </el-form-item>
        </el-form>
      </div>
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" :diffWidth="736"
                 showType="isAnalysis"></ResultBox>
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
      tableData: [
        [
          { colspan: 1, rowspan: 1, value: '标题1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ]
      ]
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
  .olapTable {
    .top {
      .form-item {
        width: 300px;
        margin-right: 30px;
      }
    }
  }
</style>
