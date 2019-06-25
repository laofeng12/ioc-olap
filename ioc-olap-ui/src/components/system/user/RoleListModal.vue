
<!-- 角色列表弹框 -->
<template>
  <div>
    <el-dialog
      width="50%"
      title="添加角色"
      :visible.sync="innerRoleListVisible"
      center
      append-to-body>

      <header class="searchBar">
        <el-form ref="form" :model="roleParams" :inline="true" label-width="80px">
          <el-form-item label="角色名称">
            <el-input style="width: 200px" size="small" v-model="roleParams.like_rolename" placeholder=""></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" size="small" icon="el-icon-search" @click.native="searchFetch">搜索</el-button>
            <el-button type="primary" size="small" icon="el-icon-refresh" @click.native="resetFetch">重置</el-button>
          </el-form-item>
        </el-form>
      </header>

      <el-table
        v-loading="roleListLoading"
        :data="roleList"
        empty-text="没有数据"
        element-loading-text="Loading"
        fit
        highlight-current-row
        >

        <el-table-column
          prop="alias"
          label="别名"
          width="200"
          align="center">
        </el-table-column>

        <el-table-column
          prop="rolename"
          label="角色名称"
          align="center">
        </el-table-column>

        <el-table-column
          prop="enabledName"
          label="是否启用"
          width="150"
          align="center">
        </el-table-column>

        <el-table-column align="center" label="操作" width="80">
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="confirmAddRole(scope.row)">添加</el-button>
          </template>
        </el-table-column>
      </el-table>

      <element-pagination
        :pageSize="roleParams.size"
        :currentPage="roleParams.page"
        :total="roleListTotal"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange"
      >
      </element-pagination>
      <div slot="footer" class="dialog-footer">
        <el-button @click="innerRoleListVisible = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import ElementPagination from '@/components/ElementPagination'

export default {
  components: { ElementPagination },
  data () {
    return {
      innerRoleListVisible: false,
      roleListLoading: false,
      roleList: [],
      roleListTotal: 0,
      roleParams: {
        size: 20,
        page: 1,
        eq_enabled: 1
      },
      userId: ''
    }
  },
  methods: {
    modal (userId) {
      this.innerRoleListVisible = true
      this.userId = userId
      this.resetFetch()
    },
    /**
     * 获取角色列表
     */
    fetchData (params) {
      this.roleListLoading = true
      let newParms = { ...params }
      newParms.page -= 1

      this.$api.getRoleList(newParms).then(res => {
        this.roleList = res.rows
        this.roleListTotal = res.total
      }).finally(() => {
        this.roleListLoading = false
      })
    },
    resetFetch () {
      this.roleParams = {
        size: 20,
        page: 1,
        like_rolename: '',
        eq_enabled: 1
      }
      this.fetchData(this.roleParams)
    },
    handleSizeChange (size) {
      this.roleParams.size = size

      this.fetchData(this.roleParams)
    },
    handleCurrentChange (page) {
      this.roleParams.page = page
      this.fetchData(this.roleParams)
    },
    confirmAddRole (row) {
      if (row.enabled !== 1) {
        return this.$message.warning('该角色未启动，不能添加该角色')
      }

      this.$confirm(
        '确认添加',
        '警告',
        {
          confirmButtonText: '添加',
          cancleButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        this.addRole(row.roleid)
      }).catch(() => {
        console.log('取消')
      })
    },
    addRole (roleId) {
      this.roleListLoading = true
      let params = { userid: this.userId, roleid: roleId }

      this.$api.addSysUserRole(params).then(res => {
        this.$message.success('添加成功')
        this.$emit('getUserRoleList')
        this.$store.dispatch('GetMenuList')
      }).finally(() => {
        this.roleListLoading = false
      })
    },
    searchFetch () {
      this.roleParams.page = 1
      this.fetchData(this.roleParams)
    },
    roleSelectionChange () {}
  }
}
</script>

<style>

</style>
