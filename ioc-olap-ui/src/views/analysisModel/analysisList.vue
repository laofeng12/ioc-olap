<template>
  <el-container class="cus-box">
    <el-aside width="200px" class="cus-left">
      <el-row class="left-tabs">
        <el-tabs class="cus-tabs" v-model="activeTab" :stretch="true">
          <el-tab-pane label="我的" name="my">
            <FolderAside :menuList="myMenuList" :menuDefault="menuDefault" vueType="myOlap" @editFunc="edit"
                         :menuListLoading="myLoading" @clickItem="getTableById" @getAnalysisList="getFolderWithQuery"></FolderAside>
          </el-tab-pane>
          <el-tab-pane label="分享" name="share">
            <FolderAside :menuList="shareMenuList" :menuDefault="menuDefault" vueType="shareOlap" :showDo="false"
                         :menuListLoading="shareLoading" @clickItem="getTableById" :needNewFolder="false"></FolderAside>
          </el-tab-pane>
        </el-tabs>
      </el-row>
    </el-aside>
    <div class="cus-right" v-loading="loading">
      <ResultBox v-if="activeTab === 'my' ? tableDataByMy.length > 0 : tableDataByShare.length > 0"
                 :tableData="activeTab === 'my' ? tableDataByMy : tableDataByShare" showType="needNew" @handlePage="handlePage"
                 :shareList="shareList" @exportFunc="exportFile" :pageData="pageData" :page="page"></ResultBox>
    </div>
  </el-container>
</template>

<script>
import FolderAside from '../../components/analysisComponent/common/FolderAside'
import ResultBox from '../../components/analysisComponent/common/ResultBox'
import { getFolderWithQueryApi, getQueryShareApi, getQueryTableApi, olapAnalyzeExportExistApi } from '../../api/olapAnalysisList'

export default {
  components: { FolderAside, ResultBox },
  data () {
    return {
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
    async getTableById (fileData, type) {
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
        if (this.activeTab === 'my') {
          this.tableDataByMy = tableData
        } else {
          this.tableDataByShare = tableData
        }
        if (type !== 'share') {
          this.$message.success('查询完成')
        }
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    edit (data) {
      this.$router.push(`/newOlapAnalysis?dataId=${data.attrs.analyzeId}`)
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
    }
  }
</style>
