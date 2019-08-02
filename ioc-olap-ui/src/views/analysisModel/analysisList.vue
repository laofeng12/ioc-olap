<template>
  <el-container class="cus-box">
    <el-aside width="200px" class="cus-left">
      <el-row class="left-tabs">
        <el-tabs class="cus-tabs" v-model="activeTab" :stretch="true">
          <el-tab-pane label="我的" name="my">
            <FolderAside :menuList="myMenuList" :menuDefault="menuDefault" vueType="myOlap"
                         :menuListLoading="myLoading" @clickItem="getTableById"></FolderAside>
          </el-tab-pane>
          <el-tab-pane label="分享" name="share">
            <FolderAside :menuList="shareMenuList" :menuDefault="menuDefault" vueType="shareOlap"
                         :menuListLoading="shareLoading" @clickItem="getTableById"></FolderAside>
          </el-tab-pane>
        </el-tabs>
      </el-row>
    </el-aside>
    <div class="cus-right" v-loading="loading">
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" showType="needNew" :folderData="folderData"
                 :duration="duration" :shareList="shareList"></ResultBox>
    </div>
  </el-container>
</template>

<script>
import FolderAside from '../../components/analysisComponent/common/FolderAside'
import ResultBox from '../../components/analysisComponent/common/ResultBox'
// import ShareDialog from '../../components/ShareDialog'
// import MoveDialog from '../../components/MoveDialog'
// import StatementTable from '@/components/BITemp/StatementTable'
// import TableFilter from '../../components/TableFilter'
// import {
//   getStatementTree, editStatementTree, renameStatementTree, getFolderList, moveStatement,
//   delFolder, delStatement, newFile, getDepartmentTree, getDepartMember, shareStament, getTableData
// } from '../../api/statement'
import { getFolderWithQueryApi, getQueryShareApi, getQueryTableApi } from '../../api/olapAnalysisList'

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
      folderData: {},
      // exportData: {},
      duration: '',
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
    async getTableById (folderData, type) {
      this.loading = true
      this.folderData = folderData
      const params = {
        analyzeId: folderData.attrs.analyzeId,
        cubeId: folderData.attrs.cubeId
      }
      try {
        const { results = [], duration } = await getQueryTableApi(params)
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
