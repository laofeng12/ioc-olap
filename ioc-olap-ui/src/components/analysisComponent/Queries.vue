<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="menuList" :menuDefault="menuDefault" iconType="cube"
                 :needNewFolder="false" vueType="queries" :menuListLoading="menuListLoading"
                 :showDo="false" emptyText="无可用的OLAP模型，请先创建/构建可用的模型"></FolderAside>
    <div class="content">
      <div class="editSql">
        <div class="editor">
          <!-- <div class="number">
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
          </div> -->
          <el-input id="codeTextarea" class="textarea" type="textarea" :rows="10" placeholder="请输入内容" v-model="textarea"></el-input>
        </div>
        <div class="bottom">
          <el-checkbox class="checkbox" v-model="checked">限制查询行数</el-checkbox>
          <el-input-number class="lineNumber" v-model="lineNumber" :disabled="!checked" size="mini" controls-position="right" :min="0"></el-input-number>
          <el-button class="querybutton" type="primary" size="mini" @click="searchOlap" :loading="loading">查询</el-button>
        </div>
      </div>
      <div v-loading="loading">
        <ResultBox v-if="tableData.length > 0 && isSearch" :tableData="tableData" :titleShow="true" @saveFunc="saveOlap"
                   @reset="reset" @exportFunc="exportFile" :resetShow="true" :formData="formData" :noFolderPop="noFolderPop">
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
var lineObjOffsetTop = 5;
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
      defaultLines: 1,
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
      formData: {},
      isSearch: false,
      noFolderPop: false
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
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
        this.noFolderPop = true
        this.searchOlap()
      }
    },
    textarea: function () {
      this.isSearch = false
    }
  },
  mounted () {
    this.getAsideList()
    this.$nextTick(() => {
      this.createTextAreaWithLines('codeTextarea')
    }, 500)
  },
  methods: {
    positionLineObj(obj,ta) {
      var string = '';
      this.defaultLines = ta.value.split('\n').length;
      for(var num = 1; num <= this.defaultLines; num++) {
        if(string.length>0) string = string + '<br>';
        string = string + num;
      }
      obj.style.top = (lineObjOffsetTop - ta.scrollTop) + 'px'; 
      obj.innerHTML = string;
    },
    // 动态生成左边代码行数
    createTextAreaWithLines(id) {
      var _this = this;
      var el = document.createElement('DIV');
      var ta = document.getElementById(id);
      ta.parentNode.insertBefore(el,ta);
      el.appendChild(ta);
      el.className='textAreaWithLines';
      el.style.width = (ta.offsetWidth) + 'px';
      ta.style.position = 'absolute';
      ta.style.width = '100%';
      el.style.backgroundColor = '#EBEBEB';
      ta.style.left = '30px';
      el.style.height = (ta.offsetHeight + 2) + 'px';
      el.style.overflow='hidden';
      el.style.position = 'relative';
      el.style.width = (ta.offsetWidth) + 'px';
      var lineObj = document.createElement('DIV');
      lineObj.style.position = 'absolute';
      lineObj.style.top = lineObjOffsetTop + 'px';
      lineObj.style.left = '0px';
      lineObj.style.width = '30px';
      el.insertBefore(lineObj,ta);
      lineObj.style.textAlign = 'center';
      lineObj.className='lineObj';
      lineObj.style.lineHeight = '30px'
      _this.positionLineObj(lineObj,ta);
      ta.onkeydown = function() { _this.positionLineObj(lineObj,ta); };
      ta.onkeyup = function() { _this.positionLineObj(lineObj,ta); };

      ta.onscroll = function() { _this.positionLineObj(lineObj,ta); };
    },
    async getAsideList () {
      this.menuListLoading = true
      const res = await getCubeTreeApi()
      this.menuList = res
      this.menuListLoading = false
    },
    async searchOlap () {
      if (this.textarea.length <= 0) return this.$message.error('请先填写sql语句')
      if (!/^[1-9]|[1-9][0-9]*$/.test(this.lineNumber)) return this.$message.error('请正确填写限制行数')
      this.loading = true
      const data = {
        limit: this.checked ? this.lineNumber : -1,
        sql: this.textarea,
        project: this.userInfo.userId
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
        this.isSearch = true
        this.$message.success('查询完成')
      } catch (e) {
        console.error('您的查询语句有问题')
      }
      this.loading = false
    },
    async saveOlap (callbackData) {
      const data = {
        limit: this.checked ? this.lineNumber : '',
        flags: 0 // 标志 0：正常 1：共享
      }
      let reqData = {}
      if (this.editInfo && this.editInfo.sql) {
        reqData = Object.assign({}, data, this.editInfo, callbackData, { isNew: false }, { sql: this.textarea })
      } else {
        reqData = Object.assign({}, data, callbackData, { sql: this.textarea })
      }
      const res = await saveOlapApi(reqData)
      if (res.createId) {
        await this.$store.dispatch('getSaveFolderListAction')
        this.$message.success('保存成功')
        this.$emit('changeActive', '2')
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
          background: #FFFFFF;
          border: 1px solid #D9D9D9;
          margin: 24px 24px 21px 12px;
          .number {
            width: 40px;
            height: 320px;
            text-align: center;
            line-height: 30px;
            background: #EBEBEB;
            padding-top: 5px;
          }
          .textarea {
            overflow: hidden;
            width: calc(100% - 30px);
            /deep/ textarea {
              line-height: 30px;
              border: none;
            }
          }
        }
        .bottom {
          display: flex;
          align-items: center;
          justify-content: flex-end;
          padding-right: 24px;
          margin: 20px 0 24px 0px;
          .checkbox {
          }
          .querybutton{
            margin-left: 17px;
          }
          .lineNumber {
            width: 115px;
            margin-left: 8px;
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
