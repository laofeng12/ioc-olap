<template>
  <div>
    <!-- 用户所属的角色列表 -->
    <el-dialog
      center
      title="用户角色列表"
      :visible.sync="userRoleModal"
      width="55%"
    >
      <el-button type="primary" icon="el-icon-plus" size="small" @click="openRoleList">添加</el-button>
      <el-table
        v-loading="listLoading"
        :data="list"
        empty-text="没有数据"
        element-loading-text="Loading"
        fit
        highlight-current-row
        >
        <el-table-column align="center" label="角色ID" width="150" prop="roleid"></el-table-column>

        <el-table-column align="center" label="角色名称" prop="rolename"></el-table-column>

        <el-table-column align="center" label="操作" width="80">
          <template slot-scope="scope">
            <el-button size="small" type="text" @click="confirmDeleteUserRole(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <element-pagination
        :pageSize="userRoleParams.size"
        :currentPage="userRoleParams.page"
        :total="userRoleTotal"
        @handleSizeChange="handleSizeChange"
        @handleCurrentChange="handleCurrentChange"
      >
      </element-pagination>

      <role-list-modal
        ref="roleListModal"
        @getUserRoleList="getUserRoleList(userRoleParams)"
      >
      </role-list-modal>
    </el-dialog>
  </div>
</template>

<script>
import ElementPagination from '@/components/ElementPagination'
import RoleListModal from './RoleListModal'

export default {
  components: { ElementPagination, RoleListModal },
  data () {
    return {
      userRoleModal: false,
      listLoading: false,
      list: [],
      innerRoleListVisible: false,
      userRoleParams: {
        size: 20,
        page: 1
      },
      userRoleTotal: 0
    }
  },
  methods: {
    dialog (userId) {
      this.userRoleModal = true
      this.userRoleParams.eq_userid = userId
      this.getUserRoleList(this.userRoleParams)
    },

    /**
     * 用户角色列表
     */
    getUserRoleList (params) {
      this.listLoading = true

      let newParam = { ...params }
      newParam.page -= 1
      this.$api.getUserRoleList(newParam).then(res => {
        this.list = res.rows
        this.userRoleTotal = res.total
      }).finally(err => {
        this.listLoading = false
        console.log(err)
      })
    },
    confirmDeleteUserRole (roleInfo) {
      console.log('roleInfo', roleInfo)

      this.$confirm(
        '是否确认删除',
        '警告',
        {
          confirmButtonText: '删除',
          cancleButtonText: '取消',
          type: 'warning'
        }
      ).then(() => {
        this.deleteUserRole(roleInfo)
      }).catch(() => {
        console.log('取消')
      })
    },
    deleteUserRole (roleInfo) {
      let params = { id: roleInfo.userroleid }
      this.listLoading = true
      this.$api.deleteUserRole(params).then(res => {
        this.getUserRoleList(this.userRoleParams)
        this.$store.dispatch('GetMenuList')
      })
    },
    openRoleList () {
      this.$refs.roleListModal.modal(this.userRoleParams.eq_userid)
    },
    handleSizeChange (size) {
      this.userRoleParams.size = size

      this.getUserRoleList(this.userRoleParams)
    },
    handleCurrentChange (page) {
      this.userRoleParams.page = page
      this.getUserRoleList(this.userRoleParams)
    }
  }
}
</script>
