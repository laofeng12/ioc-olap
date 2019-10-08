<template>
  <div class="con">
    <div class="showCon allScreen" :style="`width: ${isPop ? '100%' :tableBoxWidth + 'px'}; max-height: ${tableBoxHeight}px`">
      <table border='1' cellpadding="0" cellspacing="0">
        <tbody>
          <tr v-for="(item, index) in tableData" :key="index" :class="`${index%2 && 'dark'}`">
            <td v-for="(tdItem, tdIndex) in item" :colspan="tdItem.colspan"
                :class="`${ (tdItem.type === 'th' || (tdItem.type !== 4 && tdItem.type !== 'td')) && 'table-header' } ${(tdItem.type === 4 && canClick) && 'cur-pointer'}`"
                :rowspan="tdItem.rowspan" :key="`${index}-${tdIndex}`" @click="`${(tdItem.type === 4 && canClick) && tdClick(tdItem, canClick)}`">
              <div class="cell">
                {{tdItem.value}}
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <div class="page dis-flex" v-if="pageData.pageSize && tableData.length > 0">
      <el-pagination class="pagination" background layout="prev, pager, next" :total="pageData.totalRows"
                     :page-size="pageData.pageSize" @current-change="handleCurrentChange" :current-page.sync="page"></el-pagination>
      <div class="row dis-flex">
        <div>查询行数：</div>
        <el-input class="input center-input" v-model="input" size="mini"></el-input>
      </div>
      <el-button type="primary" @click="sure" size="mini">确认</el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    tableData: {
      type: Array,
      required: true
    },
    canClick: {
      default: false
    },
    diffWidth: {
      type: Number,
      default: 312
    },
    isPop: {
      type: Boolean,
      default: false
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
  data () {
    return {
      search: '',
      tableBoxWidth: 800,
      tableBoxHeight: 700,
      input: '20',
      currentPage: 1
    }
  },
  watch: {
    input () {
      this.currentPage = 1
    }
  },
  created () {
    this.tableBoxWidth = document.body.offsetWidth - this.diffWidth
    this.tableBoxHeight = this.isPop ? document.body.offsetHeight - 320 : document.body.offsetHeight - 300
  },
  methods: {
    tdClick (item, type) {
      this.$emit('tdClick', item, type)
    },
    handleCurrentChange (val) {
      if (!Number(this.input) && this.input !== '0') {
        return this.$message.error('查询行数只能为数字')
      } else if (Number(this.input) <= 0) {
        return this.$message.error('查询行数最小为1')
      }
      this.currentPage = val
      this.$emit('handlePage', val, this.input)
    },
    sure () {
      if (!Number(this.input) && this.input !== '0') {
        return this.$message.error('查询行数只能为数字')
      } else if (Number(this.input) <= 0) {
        return this.$message.error('查询行数最小为1')
      }
      this.$emit('handlePage', this.currentPage, this.input)
    }
  }
}
</script>

<style lang="scss" scoped>
  .con {
    width: 100%;
    .showCon {
      overflow: auto;
      background-color: #ffffff;
      table {
        font-size: 14px;
        color: #606266;
        border: 1px solid #e0ebf7;
        border-collapse: collapse;
        table-layout: fixed;
        word-break: break-all;
        min-width: 100%;
        text-align: center;
        .dark {
          background-color: #F5F7FA;
        }
        td {
          min-width: 150px;
          height: 50px;
        }
        .table-header {
          background-color: #ECEEF5;
          font-weight: bold;
        }
        .cur-pointer {
          cursor: pointer;
        }
      }
    }
    .page {
      justify-content: center;
      margin: 15px auto;
      .pagination {
        display: inline-block;
      }
      .row {
        align-items: center;
        margin: 0 25px;
        .input {
          width: 70px;
        }
      }
    }
  }
  .mar-center {
    margin: 0 auto;
  }
</style>
