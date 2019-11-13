<template>
  <el-container class="cus-box">
    <el-aside width="200px" class="cus-left">
      <el-row class="left-tabs">
        <el-tabs class="cus-tabs" v-model="activeTab" :stretch="true">
          <el-tab-pane label="我的" name="my">
            <FolderAside 
            :menuList="myMenuList" 
            :menuDefault="menuDefault" 
            vueType="myOlap" 
            @editFunc="edit"
            @deleteFunc="deleteAnalysis" 
            :menuListLoading="myLoading" 
            @clickItem="searchCube"
            @getAnalysisList="getFolderWithQuery">
            </FolderAside>
          </el-tab-pane>

          <el-tab-pane label="分享" name="share">
            <FolderAside :menuList="shareMenuList" :menuDefault="menuDefault" vueType="shareOlap" :showDo="false"
                         :menuListLoading="shareLoading" @clickItem="searchCube" :needNewFolder="false"></FolderAside>
          </el-tab-pane>
        </el-tabs>
      </el-row>
    </el-aside>
    <div class="cus-right dis-flex" v-loading="loading">
      <ResultBox v-if="activeTab === 'my' ? tableDataByMy.length > 0 : tableDataByShare.length > 0"
                 publishType="olapAnalyze"
                 :analyzeId="analyzeId"
                 :tableData="activeTab === 'my' ? tableDataByMy : tableDataByShare" showType="needNew" @handlePage="handlePage"
                 :shareList="shareList" @exportFunc="exportFile" :pageData="pageData" :page="page">
                 </ResultBox>
      <div v-else class="replace-table">
        <img src="@/assets/img/replace_table.png" />
      </div>
    </div>
  </el-container>
</template>

<script>
import FolderAside from '@/components/analysisComponent/common/FolderAside'
import ResultBox from '@/components/analysisComponent/common/ResultBox'
import {
  getFolderWithQueryApi, getQueryShareApi, getQueryTableApi,olapAnalyzeExportExistApi, olapAnalyzeDeleteApi,
  searchCubeApi
} from '@/api/olapAnalysisList'

export default {
  components: { FolderAside, ResultBox },
  data () {
    return {
      analyzeId: '', // analyzeId
      activeTab: 'my',
      myMenuList: [],
      shareMenuList: [],
      myLoading: false,
      shareLoading: false,
      menuDefault: {
        children: 'children', // 子集的属性
        label: 'name', // 标题的属性
        disabled: function (resData) {
          if (resData.isShare === 0) {
            return false
          } else {
            return true
          }
        }
      },
      tableDataByMy: [],
      tableDataByShare: [],
      loading: false,
      fileData: {},
      shareList: [],
      page: 1,
      size: 20,
      pageData: {
        totalRows: 1,
        pageSize: 20
      }
    }
  },
  mounted () {
    this.getFolderWithQuery()
    this.getQueryShare()
  },
  methods: {
    async getFolderWithQuery () {
      this.myLoading = true
      const res = await getFolderWithQueryApi()
      this.myMenuList = res
      this.myLoading = false
    },
    async getQueryShare () {
      this.shareLoading = true
      const res = await getQueryShareApi()
      this.shareMenuList = res
      this.shareLoading = false
    },
    async searchCube (fileData, type) {
      const params = { id: fileData.attrs.cubeId }
      this.analyzeId = fileData.attrs.analyzeId
      const res = await searchCubeApi(params)
      if (res.flags) {
        this.getTableById(fileData, type)
      } else {
        this.$message.error('该立方体已禁用')
      }
    },
    async getTableById (fileData, type) {
      const isSum = fileData.attrs.isSummation
      this.fileData = fileData
      this.loading = true
      const params = {
        analyzeId: this.fileData.attrs.analyzeId,
        cubeId: this.fileData.attrs.cubeId,
        pageIndex: this.page,
        pageSize: this.size
      }
      try {
        const { results = [], totalRows } = await getQueryTableApi(params)
        this.pageData = {
          totalRows,
          pageSize: this.size
        }
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


        if (this.activeTab === 'my') {
          this.tableDataByMy = isSum ? tableData : delSumTable
        } else {
          this.tableDataByShare = isSum ? tableData : delSumTable
        }
        if (type !== 'share') {
          this.$message.success('查询完成')
        }
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    async edit (data) {
      const params = { id: data.attrs.cubeId }
      const res = await searchCubeApi(params)
      if (res.flags) {
        this.$router.push(`/newOlapAnalysis?dataId=${data.attrs.analyzeId}`)
      } else {
        this.$message.error('该立方体已禁用')
      }
    },
    async exportFile () {
      const data = {
        analyzeId: this.fileData.attrs.analyzeId,
        cubeId: this.fileData.attrs.cubeId,
        pageIndex: this.page,
        pageSize: this.size
      }
      const res = await olapAnalyzeExportExistApi(data)
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
      this.getTableById(this.fileData)
    },
    async deleteAnalysis (id) {
      const data = { id }
      try {
        await olapAnalyzeDeleteApi(data)
        this.$message.success('删除成功')
        this.getFolderWithQuery()
      } catch (e) {
        console.error(e)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
  .cus-box {
    .cus-left {
      padding: 15px 0;
      .el-tabs__content {
        padding: 0;
        margin-top: 10px;
        background: #ffffff;
      }
      .left-tabs {
        position: relative;
        .cus-tabs {
          .no-tree-data {
            height: calc(100vh - 267px);
            text-align: center;
          }
          .cus-empty {
            text-align: center;
          }
        }
      }
    }
    .cus-right {
      flex-grow: 1;
      margin-left: 20px;
      background-color: #fff;
    }
  }
</style>
