<template>
  <div class="container">
    <div class="header-button">
      <el-button type="primary" size="small" @click="goAdd">新增数据库</el-button>
    </div>
    <el-table class="m-t-16" :data="tableData" v-loading="loading">
      <el-table-column min-width="100%" prop="databaseName" label="数据库名称" show-overflow-tooltip> </el-table-column>
      <el-table-column min-width="100%" prop="databaseTypeName" label="数据库类型" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" prop="url" label="链接地址" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" prop="description" label="数据库描述" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" prop="createName" label="创建人" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" prop="orgName" label="所属部门/单位" show-overflow-tooltip></el-table-column>
      <!--<el-table-column min-width="100%" prop="databaseUseName" label="数据库用途" show-overflow-tooltip></el-table-column>-->
      <el-table-column min-width="100%" prop="createTime" label="创建时间" show-overflow-tooltip></el-table-column>
      <el-table-column min-width="100%" label="连通状态" prop="linkStatusName" show-overflow-tooltip></el-table-column>
    </el-table>
    <div class="page dis-flex w-100">
      <el-pagination background @current-change="handleCurrentChange" :current-page="currentPage" :page-size="size"
                     :total="total" :hide-on-single-page="size > total" layout="prev, pager, next">
      </el-pagination>
    </div>
  </div>
</template>

<script>
import { searchApi } from '@/api/dataOrigin'
export default {
  data () {
    return {
      tableData: [],
      total: 0,
      currentPage: 1,
      size: 20,
      loading: false
    }
  },
  mounted () {
    this.getTable()
  },
  methods: {
    async getTable () {
      this.loading = true
      const params = { size: this.size, page: this.currentPage - 1 }
      const { data: { rows, total } } = await searchApi(params)
      this.tableData = rows
      this.total = total
      this.loading = false
    },
    goAdd () {
      this.$router.push('/addDataOrigin')
    },
    handleCurrentChange (val) {
      this.currentPage = val
      this.getTable()
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
  .page {
    justify-content: center;
  }
</style>
