<template>
  <div class="ele-pagination">
    <div class="page-detail">
      共 {{total}} 条记录 第{{currentPage}} / {{` ${total === 0  ? 1 : Math.ceil(total/pageSize)} 页`}}
    </div>
    <el-pagination
      class="page-wrap"
      background
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="currentPage"
      :page-sizes="[5, 10, 20, 30, 40, 50]"
      :page-size="pageSize"
      layout="prev, pager, next, sizes, jumper"
      :total="total"
    >
    </el-pagination>
  </div>
</template>
<script>
export default {
  props: {
    total: {
      type: Number,
      default: 40
    },
    pageSize: {
      type: Number,
      default: 5
    },
    currentPage: {
      type: Number,
      default: 1
    }
  },
  methods: {
    handleCurrentChange (page) {
      this.$emit('handleCurrentChange', page)
    },
    handleSizeChange (pageSize) {
      this.$emit('handleSizeChange', pageSize)
    }
  }
}
</script>

<style lang="stylus" scoped>
.ele-pagination{
  margin-top:15px;
  // text-align: right;
  overflow hidden;
  .page-detail {
    display inline-block;
    line-height 32px;
    font-size 14px;
    color #000000;
    opacity 0.65;
  }
  .page-wrap {
    float right;
  }
}
</style>
