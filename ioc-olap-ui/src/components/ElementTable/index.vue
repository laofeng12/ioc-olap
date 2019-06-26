<template>
  <el-table class="my-table" :data="tableData" :height="height">
    <template v-for="(colConfig, key) in colConfigs">
      <slot v-if="colConfig.slot" :name="colConfig.slot"></slot>
      <el-table-column v-bind="colConfig" :key="key" align="center" show-overflow-tooltip></el-table-column>
    </template>
  </el-table>
</template>

<script>
export default {
  props: {
    colConfigs: Array,
    tableData: Array,
    // border: {
    //   type: Boolean,
    //   default: false
    // }
    height: {
      type: [String, Number],
      default: 'auto'
    }
  },
  mounted () {
  },
  methods: {
    indexMethod (index) {
      return index
    }
  }
}
</script>

<style lang="stylus" scoped>
  .my-table >>>
    .el-table::before
      height 0!important
    .el-table--group::after, .el-table--border::after, .el-table::before
      height 0
    td, th
      padding 9px 0
    thead
      color: #222222;
      font-size 14px
      th
        background-color: #f4f9fb
    td,th.is-leaf
      border-bottom 1px solid #e0ebf7
    td, th, .el-table__body-wrapper .el-table--border.is-scrolling-left~.el-table__fixed
      // border-right 1px solid #e0ebf7
    // hover
    tr:hover>td
      background-color: #f0fcff
    .cell, th div
      // padding 0 20px
</style>
<style lang="stylus" scoped>
  .my-table >>>
    >>>.el-table::before
      height 0!important
    .el-table--group::after, .el-table--border::after, .el-table::before
      height 0
</style>
