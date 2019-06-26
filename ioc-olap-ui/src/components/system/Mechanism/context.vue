<template>
  <div class="editModal" v-if="orgid" v-loading="loadingData">
    <header>
      <span v-for="(item, index) in navTitleList" :key="index" v-text="item+' >'"></span>
      <span style="color:#409EFF;">查看详情</span>
    </header>
    <el-form :model="formData" ref="formData">
      <!-- <el-form-item label="组织名称：" :label-width="formLabelWidth" prop="resname">{{formData.orgname}}</el-form-item> -->
      <el-form-item label="组织名称：" :label-width="formLabelWidth" prop="resname">
        <template>
          <span style="line-height:20px;display:inline-block;vertical-align:middle;">{{formData.orgname}}</span>
        </template>
      </el-form-item>
      <el-form-item label="组织类型：" :label-width="formLabelWidth" prop="alias">{{formData.orgtype}}</el-form-item>
      <el-form-item label="OA-机构编码：" :label-width="formLabelWidth" prop="alias">{{formData.deptcode}}</el-form-item>
      <!-- <el-form-item label="OA-组织机构代码" :label-width="formLabelWidth" prop="alias">{{formData.orgcode}}</el-form-item>
      <el-form-item label="OA-统一社会信息代码" :label-width="formLabelWidth" prop="alias">{{formData.usccode}}</el-form-item> -->
    </el-form>
    <div class="userList" style="margin-top:50px;">
      <element-table :tableData="managementData" :colConfigs="managementHead"></element-table>
      <element-pagination :total="totalCount" :pageSize="pageSize" :currentPage="currentPage" @handleCurrentChange="handleCurrentChange" @handleSizeChange="handleSizeChange"></element-pagination>
    </div>
  </div>
  <div v-else>
    <img src="@/assets/img/logo.png" style="margin-left:50%;margin-top:5%;transform:translateX(-50%);width:50%;">
  </div>
</template>
<script>
import elementPagination from '@/components/ElementPagination'
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    elementPagination, elementTable
  },
  data () {
    return {
      loadingData: false,
      pageSize: 5,
      currentPage: 1,
      loadings: false,
      totalCount: 1,
      orgid: '',
      navTitleList: [],
      formLabelWidth: '150px',
      formData: {},
      managementData: [],
      managementHead: [
        { prop: 'userid', label: 'ID' },
        { prop: 'account', label: '账号' },
        { prop: 'fullname', label: '名称' },
        { prop: 'isexpiredName', label: '是否过期' },
        { prop: 'islockName', label: '是否锁定' },
        { prop: 'statusName', label: '状态' }
      ]
    }
  },
  mounted () {
    this.$root.eventBus.$on('giveMechanismId', (data, $parent) => {
      this.orgid = data.id
      this.loadingData = true
      this.navTitleList = $parent
      this.fetchData(data.id)
      this.fetchUserList(data.id)
      setTimeout(() => { this.loadingData = false }, 200)
    })
  },
  methods: {
    fetchData (id) {
      this.$api.getMechanismTreeDetail(id).then(res => {
        this.formData = res
        this.loadingData = false
      })
    },
    fetchUserList (id) {
      const params = {
        size: this.pageSize,
        page: this.currentPage - 1,
        orgid: id
      }
      this.$api.getMechanismTreeUser(params).then(res => {
        this.managementData = res.rows
        this.totalCount = res.total
      })
    },
    handleCurrentChange (val) {
      this.currentPage = val
      this.fetchUserList(this.orgid)
    },
    handleSizeChange (val) {
      this.pageSize = val
      this.fetchUserList(this.orgid)
    }
  }
}
</script>

<style lang="stylus" scoped>
.editModal{
  overflow auto
  padding-bottom 50px
  header{
    border-bottom:1px solid #ccc;
    line-height 25px
    padding 12px 0
    margin-bottom 30px
    color #686B6D
    font-size 16px
    span{
      font-size 14px
      color #787878
      margin-left 3px
    }
  }
  >>>.el-dialog{
    padding-right:50px;
  }
  >>>.el-select{
    width:100%;
  }
  >>>.el-form-item__label{
    text-align:right;
  }
  .el-form-item{
    border-bottom:1px solid #f0f0f0;
    margin-bottom 0px
    padding 10px 0
  }
  >>>.looksCls{
    .el-form-item{
      border-bottom:1px solid #ccc;
    }
    .el-input__suffix{
      display:none;
    }
    .el-input__inner{
      background:#ffffff;
      border:none;
      color:#000000;
    }
  }
  >>>.dialog-footer{
    text-align right
    button {
      width 120px
    }
  }
  >>>.el-input.is-disabled .el-input__inner{
    color #000000
  }
}
</style>
