<template>
  <div>
    <el-dialog
      width="50%"
      :title="`${roleInfo.rolename} 人员分配`"
      :visible.sync="dialogVisible"
      center>
      <header class="searchbar">
        <el-form size="small" :inline="true" :model="params" class="demo-form-inline" label-width="70px">
          <el-form-item label="登录账号">
            <el-input style="width: 200px" v-model="params.like_account" placeholder="登录账号"></el-input>
          </el-form-item>

          <el-form-item label="名称">
            <el-input style="width: 200px" v-model="params.like_fullname" placeholder="名称"></el-input>
          </el-form-item>

          <el-form-item label="是否过期">
            <el-select style="width: 200px" v-model="params.eq_isexpired" placeholder="是否过期">
              <el-option label="是" :value="1"></el-option>
              <el-option label="否" :value="0"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="是否锁定">
            <el-select style="width: 200px" v-model="params.eq_islock" placeholder="是否锁定">
              <el-option label="是" :value="1"></el-option>
              <el-option label="否" :value="0"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="状态">
            <el-select style="width: 200px" v-model="params.eq_status" placeholder="状态">
              <el-option label="有效" :value="1"></el-option>
              <el-option label="无效" :value="0"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" size="small" icon="el-icon-search" @click="onSearch">查询</el-button>
            <el-button type="primary" size="small" @click="resetSearchParams" icon="el-icon-refresh">重置</el-button>
          </el-form-item>
        </el-form>
      </header>

      <el-table
        v-loading="listLoading"
        :data="list"
        empty-text="没有数据"
        element-loading-text="Loading"
        fit
        highlight-current-row>
        <el-table-column align="center" label="账号" prop="account"></el-table-column>

        <el-table-column align="center" label="名称" prop="fullname"></el-table-column>

        <el-table-column align="center" label="是否过期" prop="isexpiredName"></el-table-column>

        <el-table-column align="center" label="是否锁定" prop="islockName"></el-table-column>

        <el-table-column align="center" label="状态" prop="statusName"></el-table-column>

        <el-table-column align="center" label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="addUser(scope.row.userid)">分配</el-button>
          </template>
        </el-table-column>
      </el-table>

      <element-pagination
        :pageSize="params.size"
        :currentPage="params.page"
        :total="total"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange">
      </element-pagination>

      <span slot="footer">
        <el-button @click="dialogVisible = false">关 闭</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import elementPagination from '@/components/ElementPagination'

export default {
  components: {
    elementPagination
  },
  data () {
    return {
      params: {
        page: 1,
        pageSize: 20
      },
      listLoading: false,
      list: [],
      dialogVisible: false,
      total: 0,
      roleInfo: {}
    }
  },
  methods: {
    dialog (row) {
      this.dialogVisible = true
      this.roleInfo = row
      this.resetSearchData()
      this.getUserList(this.params)
    },
    resetSearchData () {
      this.params = {
        page: 1,
        pageSize: 20
      }
    },
    handleSizeChange (size) {
      this.params.size = size

      this.getUserList(this.params)
    },
    handleCurrentChange (page) {
      this.params.page = page
      this.getUserList(this.params)
    },
    getUserList (params) {
      this.listLoading = true
      let param = { ...params }
      param.page -= 1

      this.$api.getUserList(param).then(res => {
        this.list = res.rows
        this.total = res.total
      }).finally(res => {
        this.listLoading = false
      })
    },
    onSearch () {
      this.getUserList(this.params)
    },
    resetSearchParams () {
      let resetParams = {
        like_account: '',
        like_fullname: '',
        eq_isexpired: '',
        eq_islock: '',
        eq_status: ''
      }
      this.params = { ...this.params, ...resetParams }

      this.getUserList(this.params)
    },
    addUser (userid) {
      let params = { roleid: this.roleInfo.roleid, userid: userid }
      this.listLoading = true
      this.$api.addRoleUser(params).then(res => {
        this.$message.success('分配成功')
        this.$store.dispatch('GetMenuList')
      }).finally(() => {
        this.listLoading = false
      })
    }
  }
}
</script>

<style>

</style>
