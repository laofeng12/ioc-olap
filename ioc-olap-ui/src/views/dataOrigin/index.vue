<template>
  <div class="container">
    <div class="header-button">
      <el-button type="primary" size="small" @click="goAdd">新增数据库</el-button>
    </div>
    <el-table class="m-t-16" :data="tableData" v-loading="getLoading">
      <el-table-column min-width="100%" prop="databaseName" label="数据库名称" show-overflow-tooltip> </el-table-column>
      <el-table-column min-width="100%" prop="databaseTypeName" label="数据库类型" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" prop="url" label="链接地址" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" prop="description" label="数据库描述" show-overflow-tooltip></el-table-column>
      <!--<el-table-column min-width="100%" prop="databaseUseName" label="数据库用途" show-overflow-tooltip></el-table-column>-->
      <el-table-column min-width="100%" prop="createTime" label="创建时间" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" label="连通状态" prop="linkStatusName" show-overflow-tooltip></el-table-column>
    </el-table>
  </div>
</template>

<script>
import { searchApi } from '@/api/dataOrigin'
export default {
  components: {
    // modelDetail, clones, construct, reloads, merge, sharedTable
  },
  data () {
    return {
      tableData: []
    }
  },
  mounted () {
    this.getTable()
  },
  methods: {
    async getTable () {
      const { data = [] } = await searchApi()
      this.tableData = data
    },
    goAdd () {
      this.$router.push('/addDataOrigin')
    }
  }
}
</script>

<style lang="scss" scoped>
  .container {
    padding: 16px;
    box-sizing: border-box;
    background-color: #FFFFFF;
  }
  .header-button {
    display: flex;
    flex-direction: row-reverse;
    width: 100%;
    padding-right: 16px;
  }
</style>
