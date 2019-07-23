<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="menuList" :menuDefault="menuDefault" @deleteFunc="deleteOlap"
                 :needNewFolder="false"></FolderAside>
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
          <el-button type="primary" size="mini" @click="searchOlap">查询</el-button>
          <el-checkbox class="checkbox" v-model="checked">限制查询行数</el-checkbox>
          <el-input class="lineNumber" v-model="lineNumber" :disabled="!checked" size="mini"></el-input>
        </div>
      </div>
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" :titleShow="true" @saveFunc="saveOlap"
                 @reset="reset" :folderList="folderList"></ResultBox>
    </div>
    <el-dialog class="visible" title="保存查询结果" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="form">
        <el-form-item label="选择文件夹" :label-width="formLabelWidth">
          <el-select class="visibleInput" v-model="form.region" placeholder="请选择文件夹">
            <el-option label="文件夹1" value="shanghai"></el-option>
            <el-option label="文件夹2" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="查询结果名称" :label-width="formLabelWidth">
          <el-input class="visibleInput" v-model="form.name" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import FolderAside from './common/FolderAside'
import ResultBox from './common/ResultBox'
import { getCubeTreeApi, saveOlapApi, deleteOlapApi, searchOlapApi, getFolderWithQueryApi } from '../../api/instantInquiry'

export default {
  components: { FolderAside, ResultBox },
  data () {
    return {
      search: '',
      textarea: '',
      lineNumber: '100',
      checked: true,
      tableData: [
        [
          { colspan: 1, rowspan: 1, value: '标题1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ]
      ],
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
      menuList: [
        {
          catalogList: [],
          dataId: 779035117190185,
          dataName: '测试嵌套报表'
        },
        {
          catalogList: [ {
            dataId: 795406468250198,
            dataName: '东莞中小学成绩报告',
            dataType: 3,
            isShare: 1
          } ],
          dataId: 796247848830160,
          dataName: '东莞'
        },
        {
          catalogList: [ {
            dataId: 795247414460141,
            dataName: '测试汇总88',
            dataType: 1,
            isShare: 1
          } ],
          dataId: 776468771050089,
          dataName: '测试通用报表'
        },
        {
          catalogList: [ {
            dataId: 795385794900198,
            dataName: '测试11',
            dataType: 2,
            isShare: 1
          } ],
          dataId: 777364408760098,
          dataName: '测试主从报表'
        }
      ],
      menuDefault: {
        children: 'catalogList', // 子集的属性
        label: 'dataName', // 标题的属性
        disabled: function (resData) {
          if (resData.isShare === 0) {
            return false
          } else {
            return true
          }
        }
      },
      folderList: []
    }
  },
  mounted () {
    this.getAsideList()
  },
  methods: {
    async getAsideList () {
      const res = await getFolderWithQueryApi()
      const folderList = res.map(v => {
        return (
          { catalogList: v.leafs, dataId: v.folderId, dataName: v.name }
        )
      })
      console.info('folderList', folderList)
      this.folderList = folderList
    },
    async getAsideList () {
      const res = await getCubeTreeApi()
      console.info('res', res)
    },
    async deleteOlap (id) {
      const res = await deleteOlapApi({id})
      console.info('res', res)
    },
    async searchOlap () {
      const data = {
        limit: this.checked ? this.lineNumber : -1,
        sql: this.textarea
      }
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
        this.$message.error('查询失败')
      }
    },
    async saveOlap (callbackData) {
      const data = {
        limit: this.checked ? this.lineNumber : '',
        sql: this.textarea,
        flags: 0 // 标志 0：正常 1：共享
      }
      console.info('Object.assign({}, data, callbackData)', Object.assign({}, data, callbackData))
      const res = saveOlapApi(Object.assign({}, data, callbackData))
      console.info('res', res)
    },
    reset () {
      this.textarea = ''
      this.checked = false
      this.lineNumber = ''
      this.tableData = []
    }
  }
}
</script>
<style lang="scss" scoped>
  .queries {
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
