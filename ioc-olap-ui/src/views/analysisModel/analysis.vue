<template>
  <div class="dis-flex" v-loading="loading">
    <OlapAside :changeRowAndCol="changeRowAndCol" :auto="auto" :editData="editData"
               @searchFunc="searchFunc" ref="olapAside"></OlapAside>
    <div class="olapTable">
      <ResultBox :tableData="showSum ? realTableData: tableData" :diffWidth="580" :formData="formData"
                 showType="isAnalysis" @searchFunc="searchFunc" :resetShow="true" @saveFunc="saveOlap"
                 @reset="reset" @showSum="showSumFunc" @changeRowAndColFunc="changeRowAndColFunc"
                 @autoFunc="autoFunc" @tdClick="tdClick" @exportFunc="exportFile" :pageData="pageData" :page="page"
                 @handlePage="handlePage" :saveFolderListByProp="saveFolderList"></ResultBox>
    </div>
    <el-dialog title="下钻设置" :visible.sync="treeVisible" width="30%">
      <div class="treeContent" v-if="treeVisible">
        <div class="box">
          <div class="title">维度</div>
          <el-tree class="tree" :data="cubeData.dimensures" show-checkbox :props="treeDefault" default-expand-all :check-on-click-node="true"
                   @check-change="handleDimensuresChange"></el-tree>
        </div>
        <div class="box">
          <div class="title">指标</div>
          <el-tree class="tree" :data="cubeData.measures" show-checkbox :props="treeDefault" default-expand-all :check-on-click-node="true"
                   @check-change="handleMeasuresChange"></el-tree>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="treeVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitTree">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="下钻数据展示" :visible.sync="tableVisible" width="70%">
      <DynamicTable class="mar-center" :tableData="visibleTableData" :isPop="true" :pageData="downPageData"
                    @handlePage="handleDownPage" :page="page"></DynamicTable>
      <div slot="footer" class="dialog-footer">
        <el-button @click="tableVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import OlapAside from '../../components/analysisComponent/olapAside'
import ResultBox from '../../components/analysisComponent/common/ResultBoxMini'
import DynamicTable from '../../components/analysisComponent/common/DynamicTable'
import {
  getOlapAnalyzeApi, getFolderWithQueryApi, saveOlapAnalyzeApi, getOlapAnalyzeDetailsApi, olapAnalyzeExportApi
} from '../../api/olapAnalysisList'

export default {
  components: { OlapAside, ResultBox, DynamicTable },
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
      analyzeId: '',
      headLimit: {
        cItems: [],
        rItems: []
      },
      reqDataList: [],
      showSum: false,
      changeRowAndCol: false,
      auto: false,
      editData: {},
      formData: {},
      treeVisible: false,
      tableVisible: false,
      treeDefault: {
        children: 'children',
        label: 'name'
      },
      selectDimensuresList: [],
      selectMeasuresList: [],
      clickDataList: [],
      visibleTableData: [],
      tdClickType: '',
      page: 1,
      size: 20,
      pageData: {
        totalRows: 1,
        pageSize: 20
      },
      downPage: 1,
      downSize: 20,
      downPageData: {
        totalRows: 1,
        pageSize: 20
      }
    }
  },
  computed: {
    ...mapGetters([
      'cubeData'
    ])
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
      const { olapAnalyzeAxes, name, cubeId, flags, folderId, isSummation, analyzeId } = await getOlapAnalyzeDetailsApi({ id: this.$route.query.dataId })
      this.editData = {
        olapAnalyzeAxes,
        cubeId,
        flags
      }
      this.formData = {
        folder: folderId.toString(),
        resultName: name
      }
      this.showSum = !!isSummation
      this.analyzeId = analyzeId
    },
    async searchFunc (list, cubeId, headLimit = { cItems: [], rItems: [] }) {
      this.loading = true
      this.realTableData = []
      this.tableData = []
      this.cubeId = cubeId
      this.reqDataList = list
      this.headLimit = headLimit
      try {
        const { results = [], totalRows } = await getOlapAnalyzeApi({
          cubeId,
          pageIndex: this.page,
          pageSize: this.size
        }, list)
        this.pageData = {
          totalRows,
          pageSize: this.size
        }
        let rowList = []
        let colList = []
        let colLength = results[headLimit.cItems.length + 1].length
        let againList = []
        let againNum = []
        let isAgainNext = 0
        results.forEach((item, index) => {
          let rowItem = []
          let colItem = []
          if (index < headLimit.cItems.length) {
            item.forEach((v, i) => {
              if (i !== 0) {
                for (let j = 0; j < v.colspan; j++) {
                  rowItem.push({ name: item[0].value, filter: v.value })
                }
              } else {
                for (let j = 0; j < v.colspan; j++) {
                  rowItem.push({})
                }
              }
            })
          } else if (headLimit.cItems.length < index) {
            item.forEach((v, i) => {
              if (i < headLimit.rItems.length && v !== null) {
                if (isAgainNext) {
                  colItem.push({ name: results[headLimit.cItems.length][isAgainNext + i].value, filter: v.value })
                } else {
                  colItem.push({ name: results[headLimit.cItems.length][i].value, filter: v.value })
                }
                if (v.rowspan > 1) {
                  againList.push({ name: results[headLimit.cItems.length][i].value, filter: v.value })
                  againNum.push(v.rowspan - 1)
                }
              }
            })
            if (againList.length > 0) {
              colItem = [...colItem, ...againList]
              const list = againNum.filter(v => v > 0) || []
              againList = againList.slice(0, list.length)
              list.forEach(v => {
                v -= 1
              })
              isAgainNext = againList.length
              againNum = [...list]
            }
          }
          if (rowItem.length > 0) rowList.push(rowItem)
          colList.push(colItem)
        })
        const tableData = results.map((item, index) => {
          const addIndex = colLength - item.length
          return (
            item.map((itemTd, indexTd) => {
              if (!itemTd) {
                return { colspan: 1, rowspan: 1, value: '-', type: 'td', attrs: null }
              } else {
                if (itemTd.type === 4) {
                  let col = []
                  let row = []
                  rowList.forEach(itemList => {
                    headLimit.cItems.forEach(v => {
                      if (itemList[indexTd + addIndex].name === v.columnName) {
                        col.push(Object.assign({}, v, { selectValues: itemList[indexTd + addIndex].filter }))
                      }
                    })
                  })
                  colList[index].forEach(value => {
                    headLimit.rItems.forEach(v => {
                      if (value.name === v.columnName) {
                        row.push(Object.assign({}, v, { selectValues: value.filter }))
                      }
                    })
                  })
                  Object.assign(itemTd, { attrs: { col, row } })
                }
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
        this.$message.success('查询完成')
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    async saveOlap (data) {
      if (!this.cubeId || !this.reqDataList) {
        return this.$message.error('请先查询数据')
      }
      const reqData = {
        cubeId: this.cubeId,
        analyzeId: this.analyzeId,
        flags: 0,
        folderId: data.folderId,
        isNew: !this.$route.query.dataId,
        name: data.name,
        olapAnalyzeAxes: this.reqDataList,
        isSummation: this.showSum ? 1 : 0
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
      this.realTableData = []
      this.tableData = []
      this.pageData = {
        totalRows: 1,
        pageSize: 20
      }
      this.downPageData = {
        totalRows: 1,
        pageSize: 20
      }
      this.$refs.olapAside.cleanAllList()
    },
    showSumFunc () {
      this.showSum = !this.showSum
    },
    changeRowAndColFunc () {
      this.changeRowAndCol = !this.changeRowAndCol
    },
    autoFunc (boo) {
      this.auto = boo
    },
    tdClick (data, type) {
      this.selectDimensuresList = []
      this.selectMeasuresList = []
      this.tdClickType = type
      const list = [...data.attrs.col, ...data.attrs.row]
      const obj = {
        type: 4,
        isInclude: 1
      }
      const clickDataList = list.map(v => {
        return Object.assign({}, v, obj)
      })
      this.clickDataList = clickDataList
      this.treeVisible = true
    },
    handleDimensuresChange (data, checked) {
      if (checked && data.attrs) {
        this.selectDimensuresList.push(data.attrs)
      } else {
        const index = this.selectDimensuresList.findIndex(v => v.columnId === data.columnId)
        this.selectDimensuresList.slice(index, 1)
      }
    },
    handleMeasuresChange (data, checked) {
      if (checked && data.attrs) {
        this.selectMeasuresList.push(data.attrs)
      } else {
        const index = this.selectMeasuresList.findIndex(v => v.columnId === data.columnId)
        this.selectMeasuresList.slice(index, 1)
      }
    },
    async submitTree () {
      if (this.selectDimensuresList.length <= 0) return this.$message.error('请选择维度')
      if (this.selectMeasuresList.length <= 0) return this.$message.error('请选择指标')
      this.treeVisible = false
      this.loading = true
      const row = this.selectDimensuresList.map(v => {
        return Object.assign({}, v, { type: 1 })
      })
      const col = this.selectMeasuresList.map(v => {
        return Object.assign({}, v, { type: 3 })
      })
      const list = [...row, ...col, ...this.clickDataList]
      if (this.tdClickType === 'transversedrillDown') {
        this.$store.dispatch('getNewValueListAction', [])
        this.$store.dispatch('getNewFilterListAction', [])
        this.$store.dispatch('getNewRowListAction', [])
        await this.$store.dispatch('getNewColListAction', [])
        this.editData = {
          olapAnalyzeAxes: list,
          cubeId: this.cubeId
        }
      } else {
        try {
          const { results = [], totalRows } = await getOlapAnalyzeApi({
            cubeId: this.cubeId,
            pageIndex: this.downPage,
            pageSize: this.downSize
          }, list)
          this.downPageData = {
            totalRows,
            pageSize: this.downSize
          }
          const tableData = results.map(item => {
            return (
              item.map(itemTd => {
                if (!itemTd) {
                  return { colspan: 1, rowspan: 1, value: '-', type: 'td', attrs: null }
                } else {
                  return itemTd
                }
              })
            )
          })
          this.$message.success('下钻查询成功')
          this.visibleTableData = tableData
          this.tableVisible = true
          this.loading = false
        } catch (e) {
          console.error(e)
          this.loading = false
        }
      }
    },
    async exportFile () {
      if (this.reqDataList.length <= 0) return this.$message.error('请先查询数据')
      const res = await olapAnalyzeExportApi({
        cubeId: this.cubeId,
        pageIndex: this.page,
        pageSize: this.size
      }, this.reqDataList)
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
      const fileName = 'olap分析文件.xlsx'
      if ('download' in document.createElement('a')) {
        let link = document.createElement('a')
        link.download = fileName
        link.style.display = 'none'
        link.href = URL.createObjectURL(blob)
        document.body.appendChild(link)
        link.click()
        URL.revokeObjectURL(link.href) // 释放URL 对象
        document.body.removeChild(link)
        this.$message.success('导出成功')
      } else {
        navigator.msSaveBlob(blob, fileName)
      }
    },
    handlePage (page, size) {
      this.page = page
      this.size = size
      this.searchFunc(this.reqDataList, this.cubeId, this.headLimit)
    },
    handleDownPage (page, size) {
      this.downPage = page
      this.downSize = size
      this.submitTree()
    }
  }
}
</script>
<style lang="scss" scoped>
  .olapTable {
    width: calc(100% - 480px);
    background-color: white;
    margin-left: 16px;
    .top {
      .form-item {
        width: 300px;
        margin-right: 30px;
      }
    }
  }
  .treeContent {
    border: 1px #ddd solid;
    padding: 0 15px 15px 15px;
    box-sizing: border-box;
    max-height: 300px;
    overflow: auto;
    .box {
      .title {
        margin: 10px 0;
      }
    }
  }
</style>
