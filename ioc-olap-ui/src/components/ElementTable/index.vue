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
.my-table{
  >>>.el-table::before{
    content: ''!important
    height 0!important
  }
  >>>.el-table__body td{
    border none!important
    padding 5px 0!important
  }
  >>>.el-table__body tr:nth-child(even){
    background #F2F2F2
  }
  >>>.el-table__header th{
    background #F2F2F2
    padding 8px 0
  }
  >>>.el-table--group::after, >>>.el-table--border::after, >>>.el-table::before{
    content: ''
    height 0!improtant
  }
}
</style>
