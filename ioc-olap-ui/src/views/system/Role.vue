<template>
  <div class="app-container role-wrapper">
    <header class="searchBar">
       <el-form ref="form" size="small" :model="params" label-width="80px">
         <el-row :gutter="20">
           <el-col :xs="24" :sm="8">
            <el-form-item label="角色名称">
              <el-input v-model="params.like_rolename" placeholder=""></el-input>
            </el-form-item>
           </el-col>

           <el-col :xs="24" :sm="8">
            <el-form-item label="是否启用">
              <el-select style="width: 100%" v-model="params.eq_enabled" placeholder="请选择">
                <el-option label="是" value="1"></el-option>
                <el-option label="否" value="0"></el-option>
              </el-select>
            </el-form-item>
           </el-col>

           <el-col :xs="24" :sm="8">
            <el-form-item>
              <el-button type="primary" size="small" icon="el-icon-search" @click.native="searchFetch(params)">搜索</el-button>
              <el-button type="primary" size="small" icon="el-icon-refresh" @click.native="resetFetch">重置</el-button>
            </el-form-item>
           </el-col>
         </el-row>
       </el-form>
    </header>
    <main>
      <el-button-group style="float:right;margin-bottom:20px;">
        <el-button type="primary" size="small" icon="el-icon-plus" @click="changeModal('add')">新增</el-button>
        <!-- <el-button type="primary" size="small" icon="el-icon-delete" @click="handDelete('all')">批量删除</el-button> -->
      </el-button-group>
      <el-table
        :data="tableData"
        v-loading="loadings"
        ref="multipleTable"
        tooltip-effect="dark"
        @selection-change="handleSelectionChange"
        style="width: 100%;margin-top: 10px;">
        <!-- <el-table-column
          type="selection"
          width="50"
          align="center">
        </el-table-column> -->
        <el-table-column
          prop="roleid"
          label="角色id"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <el-table-column
          prop="alias"
          label="别名"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <el-table-column
          prop="rolename"
          label="角色名称"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <el-table-column
          prop="memo"
          label="备注"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <!-- <el-table-column
          prop="allowdelName"
          label="是否允许删除"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <el-table-column
          prop="alloweditName"
          label="是否允许编辑"
          show-overflow-tooltip
          align="center">
        </el-table-column>
        <el-table-column
          prop="enabledName"
          label="是否启用"
          show-overflow-tooltip
          align="center">
        </el-table-column> -->
        <el-table-column
          label="操作"
          fixed="right"
          align="center">
          <template slot-scope="scope">
            <el-button @click="changeModal('looks', scope.row)" type="text" size="small">查看</el-button>
            <el-button type="text" size="small" @click="changeModal('edit', scope.row)">编辑</el-button>
            <el-button type="text" size="small" @click="setLimits(scope.row)">设置权限</el-button>
            <el-button type="text" size="small" @click="distribution(scope.row)">用户分配</el-button>
            <el-button type="text" size="small" @click="handDelete('single', scope.row.roleid)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <element-pagination :total="totalCount" :pageSize="pageSize" :currentPage="currentPage" @handleCurrentChange="handleCurrentChange" @handleSizeChange="handleSizeChange"></element-pagination>
      <edit-modal ref="editModal"></edit-modal>
      <limits-modal ref="limitsModal"></limits-modal>
      <distribution-modal ref="distributionModal"></distribution-modal>
    </main>
  </div>
</template>

<script>
import elementPagination from '@/components/ElementPagination'
import EditModal from '@/components/system/Rolecomponent/EditModal'
import LimitsModal from '@/components/system/Rolecomponent/LimitsModal'
import DistributionModal from '@/components/system/Rolecomponent/DistributionModal'

export default {
  model: {
    prop: 'value',
    event: 'input'
  },
  components: {
    elementPagination, EditModal, LimitsModal, DistributionModal
  },
  data () {
    return {
      params: {
        like_rolename: '',
        eq_enabled: ''
      },
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      loadings: false,
      isvisbld: false,
      selectTableId: [],
      tableData: []
    }
  },
  methods: {
    fetchData (val) {
      this.loadings = true
      try {
        const params = {
          size: this.pageSize,
          page: this.currentPage - 1,
          ...val
        }
        this.$api.getRoleList(params).then(res => {
          console.log(res)
          if (res && res.code === 200) {
            this.tableData = res.rows
            this.totalCount = res.total
            this.loadings = false
          }
        })
      } catch (error) {
        this.$message.error(error || '服务异常')
      }
    },
    handleCurrentChange (val) {
      this.currentPage = val
      this.fetchData()
    },
    handleSizeChange (val) {
      this.pageSize = val
      this.fetchData()
    },
    changeModal (type, val) {
      type === 'add' ? this.$refs.editModal.dialog(type) : this.$refs.editModal.dialog(type, val)
    },
    // 权限弹框控制
    setLimits (val) {
      this.$refs.limitsModal.dialog(val)
    },
    // 删除角色
    handDelete (type, id) {
      if (type === 'all') {
        if (this.selectTableId.length === 0) {
          this.$message.error('请选择角色')
        } else {
          this.deleteRole(this.selectTableId.join(','))
        }
      } else {
        this.deleteRole(id)
      }
    },
    // 删除角色方法
    deleteRole (id) {
      this.$confirm('是否删除该角色?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        try {
          const params = {
            id: id
          }
          this.$api.deleteRole(params).then(res => {
            if (res.code === 200) {
              this.$message.success('删除成功')
              this.fetchData()
            }
          })
        } catch (error) {
          this.$message.error(error || '服务异常')
        }
      })
    },
    handleSelectionChange (val) {
      this.selectTableId = val.map(item => { return item.roleid })
      // console.log(this.selectTableId)
    },
    searchFetch (val) {
      this.fetchData(val)
    },
    resetFetch () {
      this.fetchData()
      this.params = {}
    },
    distribution (row) {
      this.$refs.distributionModal.dialog(row)
    }
  },
  created () {
    this.fetchData()
  }
}
</script>
<style lang="stylus" scoped>
.container-wrapper {
  padding: 25px;
  background: #ffffff!important;
}
</style>
<style lang="stylus">
main{
  margin-top 30px
  .el-button-group{
  }
}
</style>
