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
            <FolderAside :menuList="shareMenuList" :menuDefault="menuDefault" vueType="shareOlap"
                         :menuListLoading="shareLoading" @clickItem="getTableById" :needNewFolder="false"></FolderAside>
          </el-tab-pane>
        </el-tabs>
      </el-row>
    </el-aside>
    <div class="cus-right" v-loading="loading">
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" showType="needNew"
                 :shareList="shareList" @exportFunc="exportFile"></ResultBox>
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
      tableData: [],
      loading: false,
      fileData: {},
      shareList: []
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
        analyzeId: fileData.attrs.analyzeId,
        cubeId: fileData.attrs.cubeId
      }
      try {
        const { results = [] } = await getQueryTableApi(params)
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
        // this.shareList = shareList
        // this.exportData = { sql: fileData.attrs.sql, limit: fileData.attrs.limit }
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
        cubeId: this.fileData.attrs.cubeId
      }
      const res = await olapAnalyzeExportExistApi(data)
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
      const fileName = 'olap分析文件'
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
