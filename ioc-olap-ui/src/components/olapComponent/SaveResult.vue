<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="menuList" :menuDefault="menuDefault" @clickItem="getTableById"
                 vueType="saveResult" @deleteFunc="deleteFolder"></FolderAside>
    <div class="content" v-loading="loading">
      <ResultBox v-if="tableData.length > 0" :tableData="tableData" :exportDataId="exportDataId"></ResultBox>
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
import { getFolderWithQueryApi, getTableByIdApi, searchOlapApi } from '../../api/instantInquiry'

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
      menuList: [],
      menuDefault: {
        children: 'leafs', // 子集的属性
        label: 'name', // 标题的属性
        disabled: function (resData) {
          if (resData.isShare === 0) {
            return false
          } else {
            return true
          }
        }
      },
      exportDataId: {},
      loading: false
    }
  },
  mounted () {
    this.getAsideList()
  },
  methods: {
    async getAsideList () {
      const menuList = await getFolderWithQueryApi()
      // const menuList = res.map(v => {
      //   return (
      //     { leafs: v.leafs, folderId: v.folderId, name: v.name, sortNum: v.sortNum }
      //   )
      // })
      console.info('menuList', menuList)
      this.menuList = menuList
    },
    async getTableById (sql, limit, id) {
      this.loading = true
      try {
        const { columnMetas, results } = await searchOlapApi({ sql, limit })
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
        this.exportDataId = id
        this.tableData = [...[columnMetasList], ...resultsList]
        this.$message.success('查询完成')
      } catch (e) {
        this.$message.error('查询失败')
      }
      this.loading = false
    }
    // async deleteFolder () {
    //   const res = await
    // }
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
