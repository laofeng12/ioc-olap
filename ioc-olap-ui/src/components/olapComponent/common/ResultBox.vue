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
        <div class="right">
          <el-button type="primary" size="mini" @click="dialogFormVisible = true">保存结果</el-button>
          <el-button type="primary" size="mini">重新查询</el-button>
          <el-button type="primary" size="mini">导出结果</el-button>
          <el-button type="primary" size="mini">全屏</el-button>
        </div>
      </div>
      <DynamicTable :theadData="theadData" :tableData="tableData"></DynamicTable>
    </div>
  </div>
</template>

<script>
import DynamicTable from '@/components/olapComponent/common/DynamicTable'

export default {
  props: {
    tableData: {
      type: Array,
      required: true
    },
    theadData: {
      type: Array,
      required: true
    },
    titleShow: {
      type: Boolean,
      default: false
    }
  },
  components: { DynamicTable },
  data () {
    return {
      search: '',
      dialogFormVisible: false
    }
  },
  methods: {
    handleOpen (key, keyPath) {
      console.log('open', key, keyPath)
    },
    handleClose (key, keyPath) {
      console.log('close', key, keyPath)
    },
    handleSelect (key, keyPath) {
      console.log('select', key, keyPath)
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
          margin: 20px 0;
        }
      }
    }
  }
</style>
