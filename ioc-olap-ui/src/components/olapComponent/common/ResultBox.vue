<template>
  <div class="result">
    <div class="resultBox">
      <div class="f-w-b f-s-16" v-if="titleShow">查询结果</div>
      <div class="top m-t-10">
        <div class="left dis-flex">
          <div class="item dis-flex">
            <div class="tit">查询状态：</div>
            <div class="word">成功</div>
          </div>
          <div class="item dis-flex">
            <div class="tit">查询时间：</div>
            <div class="word">2019-07-03</div>
          </div>
          <div class="item dis-flex">
            <div class="tit">耗时：</div>
            <div class="word">12.12min</div>
          </div>
          <div class="item dis-flex">
            <div class="tit">查询类型：</div>
            <div class="word">东莞交通数据分析模型</div>
          </div>
        </div>
        <div class="right dis-flex">
          <el-button class="button" type="primary" size="mini" v-if="showType === 'needNew'" @click="goNewOlap">
            新建OLAP分析
          </el-button>
          <el-button class="button" type="primary" size="mini" @click="showNewFormVisible()">保存结果</el-button>
          <el-button class="button" type="primary" size="mini" @click="showTipVisible('reset')">重置</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">查询</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'" @click="handleAutoSearch">
            {{autoSearch ? '关闭' : '开启'}}自动查询
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">行列转换</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">横行下钻</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">上钻</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">求和</el-button>
          <el-button class="button" type="primary" size="mini" @click="exportTable">导出结果</el-button>
          <el-button class="button" type="primary" size="mini" @click="fullscreenToggle">全屏</el-button>
        </div>
      </div>
      <DynamicTable class="allScreen" :tableData="tableData" :diffWidth="diffWidth"></DynamicTable>
    </div>
    <el-dialog title="保存查询结果" :visible.sync="newFormVisible" width="30%">
      <el-form :model="newForm" :rules="newFormRules" ref="newForm">
        <el-form-item label="文件夹" label-width="100px" prop="folder">
          <el-select class="w-100" v-model="newForm.folder" placeholder="请选择文件夹">
            <el-option v-for="(item, index) in folderList" :key="index" :label="item.name"
                       :value="item.folderId"></el-option>
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
    <el-dialog title="提示" :visible.sync="tipVisible" width="30%">
      <span>确认{{translateWord}}数据吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="tipVisible = false">取 消</el-button>
        <el-button v-if="word === 'save'" type="primary" @click="save">确 定</el-button>
        <el-button v-else type="primary" @click="reset">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
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
    showType: {
      type: String,
      default: ''
    },
    folderList: {
      type: Array,
      default: []
    },
    exportDataId: {
      type: String,
      default: ''
    },
    diffWidth: {
      type: Number,
      default: 536
    }
  },
  components: { DynamicTable },
  data () {
    return {
      search: '',
      tipVisible: false,
      newFormVisible: false,
      word: '',
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
      }
    }
  },
  computed: {
    translateWord: function () {
      return this.translate(this.word)
    }
  },
  methods: {
    translate (word) {
      switch (word) {
        case 'save':
          return '保存'
        case 'reset':
          return '重置'
      }
    },
    showTipVisible (word) {
      this.word = word
      this.tipVisible = true
    },
    showNewFormVisible () {
      this.newFormVisible = true
    },
    submitNewForm () {
      this.$refs.newForm.validate((valid) => {
        if (valid) {
          this.showTipVisible('save')
        }
      })
    },
    goNewOlap () {
      this.$router.push('/olapAnalysis/newOlapAnalysis')
    },
    handleAutoSearch () {
      this.autoSearch = !this.autoSearch
      this.$message.success(`已${this.autoSearch ? '开启' : '关闭'}自动查询`)
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
    save () {
      const data = {
        isNew: this.word === 'save',
        folderId: this.newForm.folder,
        name: this.newForm.resultName
      }
      this.$emit('saveFunc', data)
      this.newFormVisible = false
      this.tipVisible = false
    },
    reset () {
      this.$emit('reset')
      this.tipVisible = false
    },
    async exportTable () {
      // window.open('http://www.baidu.com')
      window.open(`http://${window.location.host}/olapweb/olap/apis/olapRealQuery/export?id=${this.exportDataId}`)
      // window.location.href = `${window.location.host}/olapweb/olap/apis/olapRealQuery/export?id=${this.exportDataId}`
      // const res = await exportApi({id: this.exportDataId})
      // this.$message.success('下载成功')
    }
  }
}
</script>

<style lang="scss" scoped>
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
</style>
