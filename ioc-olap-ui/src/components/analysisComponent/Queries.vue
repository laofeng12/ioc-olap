<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="menuList" :menuDefault="menuDefault"
                 :needNewFolder="false" vueType="queries" :menuListLoading="menuListLoading"
                 :showDo="false"></FolderAside>
    <div class="content">
      <div class="editSql">
        <div class="editor">
          <div class="number">
            <div>1</div>
            <div>2</div>
            <div>3</div>
            <div>4</div>
            <div>5</div>
            <div>6</div>
            <div>7</div>
            <div>8</div>
            <div>9</div>
            <div>10</div>
          </div>
          <el-input class="textarea" type="textarea" :rows="10" placeholder="请输入内容" v-model="textarea"></el-input>
        </div>
        <div class="bottom">
          <el-button type="primary" size="mini" @click="searchOlap" :loading="loading">查询</el-button>
          <el-checkbox class="checkbox" v-model="checked">限制查询行数</el-checkbox>
          <el-input class="lineNumber" v-model="lineNumber" :disabled="!checked" size="mini"></el-input>
        </div>
      </div>
      <div v-loading="loading">
        <ResultBox v-if="tableData.length > 0" :tableData="tableData" :titleShow="true" @saveFunc="saveOlap"
                   @reset="reset" @exportFunc="exportFile" :resetShow="true" :formData="formData">
        </ResultBox>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import FolderAside from './common/FolderAside'
import ResultBox from './common/ResultBox'
import { getCubeTreeApi, saveOlapApi, searchOlapApi, exportExcelApi } from '../../api/instantInquiry'

export default {
  components: { FolderAside, ResultBox },
  props: {
    editInfo: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      search: '',
      textarea: '',
      lineNumber: '100',
      checked: true,
      tableData: [],
      dialogFormVisible: false,
      formLabelWidth: '120px',
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
      exportData: {},
      formData: {}
    }
  },
  computed: {
    ...mapGetters({ editInstant: 'editInstant' })
  },
  watch: {
    editInfo: function (val) {
      if (val && val.sql) {
        this.textarea = val.sql
        this.lineNumber = val.limit
        this.formData = {
          folder: val.folderId.toString(),
          resultName: val.name
        }
        this.searchOlap()
      }
    }
  },
  mounted () {
    this.getAsideList()
  },
  methods: {
    async getAsideList () {
      this.menuListLoading = true
      const res = await getCubeTreeApi()
      this.menuList = res
      this.menuListLoading = false
    },
    async searchOlap () {
      this.loading = true
      const data = {
        limit: this.checked ? this.lineNumber : -1,
        sql: this.textarea
      }
      this.exportData = data
      try {
        const { columnMetas, results } = await searchOlapApi(data)
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
        this.tableData = [...[columnMetasList], ...resultsList]
        this.$message.success('查询完成')
      } catch (e) {
        console.error(e)
      }
      this.loading = false
    },
    async saveOlap (callbackData) {
      const data = {
        limit: this.checked ? this.lineNumber : '',
        sql: this.textarea,
        flags: 0 // 标志 0：正常 1：共享
      }
      let reqData = {}
      if (this.editInfo && this.editInfo.sql) {
        reqData = Object.assign({}, data, this.editInfo, callbackData, { isNew: false })
      } else {
        reqData = Object.assign({}, data, callbackData)
      }
      const res = await saveOlapApi(reqData)
      if (res.createId) {
        await this.$store.dispatch('getSaveFolderListAction')
        this.$message.success('保存成功')
      }
    },
    reset () {
      this.textarea = ''
      this.checked = true
      this.lineNumber = '100'
      this.tableData = []
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
      /*.result {*/
        /*overflow: auto;*/
        /*.resultBox {*/
          /*.top {*/
            /*.left {*/
              /*.item {*/
                /*margin-right: 10px;*/
                /*.tit {*/
                  /*margin-right: 2px;*/
                /*}*/
              /*}*/
            /*}*/
            /*.right {*/
              /*margin: 20px 0;*/
            /*}*/
          /*}*/
        /*}*/
      /*}*/
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
