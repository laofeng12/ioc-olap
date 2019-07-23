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
          <el-button class="button" type="primary" size="mini" @click="showTipVisible('save')">保存结果</el-button>
          <el-button class="button" type="primary" size="mini" @click="showTipVisible('reset')">重置</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">查询</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'" @click="handleAutoSearch">
            {{autoSearch ? '关闭' : '开启'}}自动查询
          </el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">行列转换</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">横行下钻</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">上钻</el-button>
          <el-button class="button" type="primary" size="mini" v-if="showType === 'isAnalysis'">求和</el-button>
          <el-button class="button" type="primary" size="mini">导出结果</el-button>
          <el-button class="button" type="primary" size="mini" @click="fullscreenToggle">全屏</el-button>
        </div>
      </div>
      <DynamicTable class="allScreen" :tableData="tableData" :diffWidth="diffWidth"></DynamicTable>
    </div>
    <el-dialog title="提示" :visible.sync="tipVisible" width="30%">
      <span>确认{{translate(word)}}数据吗？</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="tipVisible = false">取 消</el-button>
        <el-button type="primary" @click="tipVisible = false">确 定</el-button>
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
      word: '',
      autoSearch: false,
      isFullscreen: false
    }
  },
  beforeUpdate () {},
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
