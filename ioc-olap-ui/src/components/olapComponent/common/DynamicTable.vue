<template>
  <div class="con" :style="`width: ${tableBoxWidth}px; height: ${tableBoxHeight}px`">
    <div class="showCon">
      <el-table :data="tableData">
        <el-table-column v-for="(item, index) in theadData" :key="index" :property="`column${index+1}`"
                         :label="item.label" min-width="150" align="center"></el-table-column>
      </el-table>
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
    theadData: {
      type: Array,
      required: true
    },
    diffWidth: {
      type: Number,
      default: 536
    }
  },
  data () {
    return {
      search: '',
      tableBoxWidth: 800,
      tableBoxHeight: 700,
      tableWidth: 150,
      tableHeight: 48
    }
  },
  watch: {
    theadData (val) {
      this.tableWidth = val.length * 150
    }
  },
  created () {
    this.tableBoxWidth = document.body.offsetWidth - this.diffWidth
    this.tableBoxHeight = document.body.offsetHeight - 141
    this.tableWidth = this.theadData.length * 150
    this.tableHeight = this.tableData.length * 48
  },
  methods: {}
}
</script>

<style lang="scss" scoped>
  .con {
    width: 100%;
    overflow: auto;
    background-color: #ffffff;
    .showCon {
      /deep/ td, th {
        min-width: 150px;
        height: 50px;
      }
    }
  }
</style>
