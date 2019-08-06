<template>
  <div class="dis-flex">
    <OlapAside></OlapAside>
    <div class="olapTable" v-loading="loading">
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
      <ResultBox :tableData="tableData" :diffWidth="736"
                 showType="isAnalysis" @searchFunc="searchFunc" :resetShow="true"
                 @reset="reset" :duration="duration"></ResultBox>
    </div>
  </div>
</template>

<script>
import OlapAside from '../../components/analysisComponent/olapAside'
import ResultBox from '../../components/analysisComponent/common/ResultBox'
import { getOlapAnalyzeApi } from '../../api/olapAnalysisList'

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
      tableData: [],
      duration: '',
      loading: false
    }
  },
  watch: {},
  mounted () {
    // this.setSortTable()
  },
  methods: {
    async searchFunc (list, cubeId) {
      this.loading = true
      try {
        const { results = [], duration } = await getOlapAnalyzeApi({ cubeId }, list)
        const tableData = results.map(item => {
          return (
            item.map(itemTd => {
              if (!itemTd) {
                return { colspan: 1, rowspan: 1, value: '-', type: 'td' }
              } else {
                return itemTd
              }
            })
          )
        })
        this.tableData = tableData
        this.duration = duration
        // this.shareList = shareList
        // this.exportData = { sql: folderData.attrs.sql, limit: folderData.attrs.limit }
        if (type !== 'share') {
          this.$message.success('查询完成')
        }
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    reset () {
      alert('8888888')
    }
  }
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
