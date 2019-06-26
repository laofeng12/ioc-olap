<template>
  <div class="app-container">
    <h4 class="w-info-title">{{userInfo.fullname}} 基本信息</h4>
    <el-form class="w-info-default" ref="form" :model="userInfo" label-width="100px">
      <el-form-item label="用户ID：">
        <span>{{userInfo.userid}}</span>
      </el-form-item>

      <el-form-item label="登录账号：">
        <span>{{userInfo.account}}</span>
      </el-form-item>

      <el-form-item label="名称：">
        <span>{{userInfo.fullname}}</span>
      </el-form-item>

      <el-form-item label="是否过期：">
        <span>{{userInfo.isexpiredName}}</span>
      </el-form-item>

      <el-form-item label="是否锁定：">
        <span>{{userInfo.islockName}}</span>
      </el-form-item>

      <el-form-item label="状态：">
        <span>{{userInfo.statusName}}</span>
      </el-form-item>
    </el-form>
    <div class="close-button-wrap">
      <el-button @click="$router.go(-1)">关 闭</el-button>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      userInfo: {}
    }
  },
  created () {
    this.getUserInfo()
  },
  methods: {
    getUserInfo () {
      let userId = this.$route.params.id
      this.$api.getUserInfo(userId).then(res => {
        this.userInfo = res
        console.log(res)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.close-button-wrap {
  margin: 20px 0 0 20px;
}
</style>
