<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="saveFolderList" :menuDefault="menuDefault" @clickItem="getTableById" @editFunc="editSave"
                 vueType="saveResult" @deleteFunc="deleteFolder" :menuListLoading="menuListLoading"></FolderAside>
    <div class="content" v-loading="loading">
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" @exportFunc="exportFile"
                 :shareList="shareList"></ResultBox>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import FolderAside from './common/FolderAside'
import ResultBox from './common/ResultBox'
import { deleteOlapApi, searchOlapByIdApi, exportExcelApi } from '../../api/instantInquiry'

export default {
  components: { FolderAside, ResultBox },
  data () {
    return {
      search: '',
      textarea: '',
      lineNumber: '',
      checked: false,
      tableData: [],
      dialogFormVisible: false,
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      },
      formLabelWidth: '120px',
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
      menuListLoading: false,
      exportData: {},
      loading: false,
      shareList: []
    }
  },
  computed: {
    ...mapGetters({ saveFolderList: 'saveFolderList' })
  },
  mounted () {
    this.getAsideList()
  },
  methods: {
    async getAsideList () {
      this.menuListLoading = true
      await this.$store.dispatch('getSaveFolderListAction')
      this.menuListLoading = false
    },
    async getTableById (folderData, type) {
      this.loading = true
      try {
        const { columnMetas, results, shareList } = await searchOlapByIdApi({ id: folderData.attrs.realQueryId })
        const columnMetasList = columnMetas.map(v => {
          return (
            { colspan: 1, rowspan: 1, value: v.label, type: 'th' }
          )
        })
        const resultsList = results.map(item => {
          let list = []
          item.forEach(v => {
            const obj = { colspan: 1, rowspan: 1, value: v, type: 'td' }
            list.push(obj)
          })
          return list
        })
        this.shareList = shareList
        this.exportData = { sql: folderData.attrs.sql, limit: folderData.attrs.limit }
        this.tableData = [...[columnMetasList], ...resultsList]
        if (type !== 'share') {
          this.$message.success('查询完成')
        }
      } catch (e) {
        this.$message.error('查询失败')
      }
      this.loading = false
    },
    async deleteFolder (id) {
      const res = await deleteOlapApi({id})
      this.$message.success('删除成功')
      await this.$store.dispatch('getSaveFolderListAction')
    },
    async editSave (data) {
      this.$emit('changeActive', '1', data)
    },
    async exportFile () {
      const res = await exportExcelApi(this.exportData)
      const blob = new Blob([res], { type: 'application/vnd.ms-excel' })
      const fileName = '即席查询文件.xlsx'
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
  .queries {
    align-items: stretch;
    .content {
      width: 100%;
      flex-grow: 1;
      .editSql {
        .editor {
          display: flex;
          .number {
            width: 40px;
            text-align: center;
            height: 30px;
            line-height: 30px;
            margin-top: 7px;
          }
          .textarea {
            overflow: hidden;
            /deep/ textarea {
              line-height: 30px;
              border: none;
            }
          }
        }
        .bottom {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-top: 20px;
          .checkbox {
            margin-left: 20px;
          }
          .lineNumber {
            width: 60px;
          }
        }
      }
    }
    .visible {
      .visibleInput {
        width: 250px;
      }
    }
  }
  .cur-poi {
    cursor: pointer;
  }
</style>
