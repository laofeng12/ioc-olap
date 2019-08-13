<template>
  <div class="dis-flex" v-loading="loading">
    <OlapAside v-if="showOlapAside" :changeRowAndCol="changeRowAndCol" :auto="auto" :editData="editData"
               @searchFunc="searchFunc"></OlapAside>
    <div class="olapTable">
      <!--<div class="top">-->
        <!--<el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-width="80px" :inline="true">-->
          <!--<el-form-item class="form-item" label="文件夹" prop="folder">-->
            <!--<el-select v-model="ruleForm.folder" placeholder="请选择文件夹">-->
              <!--<el-option label="文件夹1" value="1"></el-option>-->
              <!--<el-option label="文件夹2" value="2"></el-option>-->
            <!--</el-select>-->
          <!--</el-form-item>-->
          <!--<el-form-item class="form-item" label="文件名称" prop="name">-->
            <!--<el-input v-model="ruleForm.name" placeholder="请输入文件名称"></el-input>-->
          <!--</el-form-item>-->
        <!--</el-form>-->
      <!--</div>-->
      <ResultBox :tableData="showSum ? realTableData: tableData" :diffWidth="736" :saveFolderListByProp="saveFolderList"
                 showType="isAnalysis" @searchFunc="searchFunc" :resetShow="true" @saveFunc="saveOlap"
                 @reset="reset" @showSum="showSumFunc" @changeRowAndColFunc="changeRowAndColFunc" :formData="formData"
                 @autoFunc="autoFunc"></ResultBox>
    </div>
  </div>
</template>

<script>
import OlapAside from '../../components/analysisComponent/olapAside'
import ResultBox from '../../components/analysisComponent/common/ResultBox'
import { getOlapAnalyzeApi, getFolderWithQueryApi, saveOlapAnalyzeApi, getOlapAnalyzeDetailsApi } from '../../api/olapAnalysisList'

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
      realTableData: [],
      tableData: [],
      loading: false,
      saveFolderList: [],
      cubeId: '',
      reqDataList: [],
      showSum: false,
      showOlapAside: true,
      changeRowAndCol: false,
      auto: false,
      editData: {},
      formData: {}
    }
  },
  mounted () {
    this.getFolderWithQuery()
    if (this.$route.query.dataId) this.getOlapAnalyzeDetails()
  },
  methods: {
    async getFolderWithQuery () {
      const res = await getFolderWithQueryApi()
      this.saveFolderList = res
    },
    async getOlapAnalyzeDetails () {
      const { olapAnalyzeAxes, name, cubeId, flags, folderId } = await getOlapAnalyzeDetailsApi({ id: this.$route.query.dataId })
      this.editData = {
        olapAnalyzeAxes,
        cubeId,
        flags
      }
      this.formData = {
        folder: folderId.toString(),
        resultName: name
      }

      // const newValueList = this.newValueList.length > 0 ? this.newValueList.map(v => Object.assign({}, v, { type: 3 })) : []
      // const newFilterList = this.newFilterList.length > 0 ? this.newFilterList.map(v => Object.assign({}, v, { type: 4 })) : []
      // const newRowList = this.newRowList.length > 0 ? this.newRowList.map(v => Object.assign({}, v, { type: 1 })) : []
      // const newColList = this.newColList.length > 0 ? this.newColList.map(v => Object.assign({}, v, { type: 2 })) : []
      // const list = [...newValueList, ...newFilterList, ...newRowList, ...newColList]
    },
    async searchFunc (list, cubeId) {
      this.loading = true
      this.cubeId = cubeId
      this.reqDataList = list
      try {
        const { results = [] } = await getOlapAnalyzeApi({ cubeId }, list)
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
        const delRowSumTable = tableData.slice(0, tableData.length - 1)
        const limit = delRowSumTable[0][delRowSumTable[0].length - 1].rowspan
        const delSumTable = delRowSumTable.map((v, i) => {
          if (i !== 0 && i < limit) {
            return v
          } else {
            return v.slice(0, v.length - 1)
          }
        })
        this.realTableData = tableData
        this.tableData = delSumTable
        // this.shareList = shareList
        // this.exportData = { sql: folderData.attrs.sql, limit: folderData.attrs.limit }
        this.$message.success('查询完成')
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    async saveOlap (data) {
      if (!this.cubeId || !this.reqDataList) {
        this.$message.error('请先查询数据')
      }
      const reqData = {
        cubeId: this.cubeId,
        flags: 0,
        folderId: data.folderId,
        isNew: true,
        name: data.name,
        olapAnalyzeAxes: this.reqDataList
      }
      const { analyzeId } = await saveOlapAnalyzeApi(reqData)
      if (analyzeId) {
        this.$message.success('保存成功')
        this.$router.replace('/analysisList')
      } else {
        this.$message.error('保存失败')
      }
    },
    reset () {
      this.showOlapAside = false
      setTimeout(() => this.showOlapAside = true, 0)
    },
    showSumFunc () {
      this.showSum = !this.showSum
    },
    changeRowAndColFunc () {
      this.changeRowAndCol = !this.changeRowAndCol
    },
    autoFunc () {
      this.auto = !this.auto
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
