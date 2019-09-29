<template>
  <div class="result">
    <div class="resultBox">
      <div class="f-w-b f-s-16" v-if="titleShow">查询结果</div>
      <div class="top m-t-10">
        <div class="right dis-flex">
          <el-button class="button" type="primary" size="mini" v-if="showType === 'needNew'" @click="goNewOlap">
            新建OLAP分析
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="resetShow" @click="newFormVisible = true">
            保存结果
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="resetShow" @click="reset">重置</el-button>
          <el-button class="button" type="primary" size="mini" v-if="shareList.length > 0"
                     @click="showShareListVisible = true">
            查看分享人
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'" @click="analysisSearch">
            查询
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'"
                     @click="handleAutoSearch">
            {{autoSearch ? '关闭' : '开启'}}自动查询
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'"
                     @click="changeRowAndColFunc">
            行列转换
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'" @click="toggleTransverseDrillDown">
            横行下钻
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'" @click="toggleDrillDown">
            下钻
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'" @click="showSum">求和</el-button>
          <el-button class="button" type="primary" size="mini" @click="exportTable">导出结果</el-button>
          <el-button class="button" type="primary" size="mini" @click="fullscreenToggle">全屏</el-button>
        </div>
      </div>
      <DynamicTable :tableData="tableData" :diffWidth="diffWidth" @handlePage="handlePage"
                    :canClick="(drillDown && 'drillDown') || (transversedrillDown && 'transversedrillDown')"
                    @tdClick="tdClick" :pageData="pageData" :page="page"></DynamicTable>
    </div>
    <el-dialog title="保存查询结果" :visible.sync="newFormVisible" width="30%">
      <el-form :model="newForm" :rules="newFormRules" ref="newForm">
        <el-form-item label="文件夹" label-width="100px" prop="folder">
          <el-select v-if="saveFolderListByProp && saveFolderListByProp.length > 0" class="w-100" v-model="newForm.folder" placeholder="请选择文件夹">
            <el-option v-for="(item, index) in saveFolderListByProp" :key="index" :label="item.name"
                       :value="item.id"></el-option>
          </el-select>
          <el-select v-else class="w-100" v-model="newForm.folder" placeholder="请选择文件夹">
            <el-option v-for="(item, index) in saveFolderList" :key="index" :label="item.name"
                       :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item class="w-100" label="结果名称" label-width="100px" prop="resultName">
          <el-input v-model="newForm.resultName" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="newFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitNewForm()">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="查看分享人" :visible.sync="showShareListVisible" width="30%">
      <el-table :data="shareList" max-height="250" border style="width: 100%">
        <el-table-column prop="shareUserName" label="分享人姓名" align="center"></el-table-column>
        <el-table-column prop="createTime" label="分享时间" align="center"></el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import DynamicTable from '../common/DynamicTable'

export default {
  props: {
    tableData: {
      type: Array,
      required: true
    },
    titleShow: {
      type: Boolean,
      default: false
    },
    resetShow: {
      type: Boolean,
      default: false
    },
    showType: {
      type: String,
      default: ''
    },
    formData: {
      type: Object,
      default: () => ({})
    },
    diffWidth: {
      type: Number,
      default: 284
    },
    shareList: {
      type: Array,
      default: () => ([])
    },
    saveFolderListByProp: {
      type: Array,
      default: () => ([])
    },
    pageData: {
      type: Object,
      default: () => ({})
    },
    page: {
      type: Number,
      default: 1
    }
  },
  components: { DynamicTable },
  data () {
    return {
      search: '',
      newFormVisible: false,
      isNew: true,
      autoSearch: false,
      isFullscreen: false,
      newForm: {
        folder: '',
        resultName: ''
      },
      newFormRules: {
        resultName: [
          { required: true, message: '请输入查询结果名称', trigger: 'blur' }
        ],
        folder: [
          { required: true, message: '请选择文件夹', trigger: 'change' }
        ]
      },
      showShareListVisible: false,
      // treeVisible: false,
      // treeDefault: {
      //   children: 'children',
      //   label: 'name'
      // },
      drillDown: false,
      transversedrillDown: false
    }
  },
  computed: {
    ...mapGetters([
      'saveFolderList',
      'cubeData',
      'newValueList',
      'newFilterList',
      'newRowList',
      'newColList'
    ])
  },
  watch: {
    formData (val) {
      this.newForm = val
    },
    saveFolderListByProp (val) {
      if (this.$route.query.folderId) {
        this.newForm.folder = this.$route.query.folderId
      }
      this.newForm = this.formData
    }
  },
  methods: {
    analysisSearch () {
      if (this.newColList.length <= 0 && this.newRowList.length <= 0) return this.$message.error('至少填入一个维度值')
      if (this.newValueList.length <= 0) return this.$message.error('至少填入一个数值')
      const newValueList = this.newValueList.length > 0 ? this.newValueList.map(v => Object.assign({}, v, { type: 3 })) : []
      const newFilterList = this.newFilterList.length > 0 ? this.newFilterList.map(v => Object.assign({}, v, { type: 4 })) : []
      const newRowList = this.newRowList.length > 0 ? this.newRowList.map(v => Object.assign({}, v, { type: 1 })) : []
      const newColList = this.newColList.length > 0 ? this.newColList.map(v => Object.assign({}, v, { type: 2 })) : []
      const list = [...newValueList, ...newFilterList, ...newRowList, ...newColList]
      this.$emit('searchFunc', list, this.cubeData.cubeId, { rItems: this.newRowList, cItems: this.newColList })
    },
    submitNewForm () {
      this.$refs.newForm.validate(async (valid) => {
        if (valid) {
          const data = {
            isNew: this.isNew,
            folderId: this.newForm.folder,
            name: this.newForm.resultName
          }
          try {
            await this.$confirm(`确认保存数据吗？`, '提示', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning'
            })
            this.$emit('saveFunc', data)
            this.newFormVisible = false
          } catch (e) {
            this.newFormVisible = false
            this.$message('已取消')
          }
        }
      })
    },
    goNewOlap () {
      this.$router.push('/newOlapAnalysis')
    },
    handleAutoSearch () {
      this.autoSearch = !this.autoSearch
      this.$message.success(`已${this.autoSearch ? '开启' : '关闭'}自动查询`)
      this.$emit('autoFunc')
    },
    fullscreenToggle () {
      this.$fullscreen.toggle(this.$el.querySelector('.allScreen'), {
        wrap: false,
        callback: this.fullscreenChange
      })
    },
    fullscreenChange (fullscreen) {
      this.isFullscreen = fullscreen
    },
    async reset () {
      try {
        await this.$confirm(`确认重置吗？`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        this.$emit('reset')
        this.$message.success('重置成功!')
      } catch (e) {
        this.$message('已取消')
      }
    },
    async exportTable () {
      this.$emit('exportFunc')
    },
    showSum () {
      this.$emit('showSum')
    },
    changeRowAndColFunc () {
      this.$emit('changeRowAndColFunc')
    },
    toggleDrillDown () {
      this.transversedrillDown = false
      this.drillDown = !this.drillDown
      this.$message.success(`已${this.drillDown ? '开启' : '关闭'}下钻`)
    },
    toggleTransverseDrillDown () {
      this.drillDown = false
      this.transversedrillDown = !this.transversedrillDown
      this.$message.success(`已${this.transversedrillDown ? '开启' : '关闭'}横向下钻`)
    },
    tdClick (item, type) {
      this.$emit('tdClick', item, type)
    },
    handlePage (page, size) {
      this.$emit('handlePage', page, size)
    }
  }
}
</script>

<style lang="scss" scoped>
  .result {
    overflow: auto;
    .resultBox {
      .top {
        .right {
          flex-wrap: wrap;
          margin: 20px 0;
          .button {
            margin-bottom: 10px;
            margin-right: 10px;
          }
          .button + .button {
            margin-left: 0;
          }
        }
      }
    }
  }
  /*.treeContent {*/
    /*border: 1px #ddd solid;*/
    /*padding: 0 15px 15px 15px;*/
    /*box-sizing: border-box;*/
    /*max-height: 300px;*/
    /*overflow: auto;*/
    /*.box {*/
      /*.title {*/
        /*margin: 10px 0;*/
      /*}*/
    /*}*/
  /*}*/
</style>
