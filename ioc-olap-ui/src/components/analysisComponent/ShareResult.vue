<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="menuList" :menuDefault="menuDefault" vueType="shareResult" @clickItem="getTableById"
                 :menuListLoading="menuListLoading" :showDo="false" :needNewFolder="false"></FolderAside>
    <div class="content" v-loading="loading">
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" :shareList="shareList"
                 @exportFunc="exportFile"></ResultBox>
    </div>
  </div>
</template>

<script>

import ResultBox from '@/components/analysisComponent/common/ResultBox'
import FolderAside from '@/components/analysisComponent/common/FolderAside'
import { getQueryShareApi, exportExcelApi, searchOlapByIdApi } from '../../api/instantInquiry'

export default {
  components: { ResultBox, FolderAside },
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
      menuList: [],
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
      loading: false,
      shareList: [],
      exportData: {}
    }
  },
  mounted () {
    this.getAsideList()
  },
  methods: {
    async getAsideList () {
      this.menuListLoading = true
      const res = await getQueryShareApi()
      const menuList = res.map(item => {
        return (
          { name: item.name, id: item.realQueryId, attrs: item, children: [] }
        )
      })
      this.menuList = menuList
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
      .result {
        overflow: auto;
        .resultBox {
          .top {
            .left {
              .item {
                margin-right: 10px;
                .tit {
                  margin-right: 2px;
                }
              }
            }
            .right {
              margin: 20px 0;
            }
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
