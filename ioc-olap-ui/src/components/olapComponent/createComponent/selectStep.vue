<template>
  <div class="selectStep">
    <el-tabs v-model="activeName" type="border-card">
      <el-tab-pane label="数据湖" name="1">
        <data-lake></data-lake>
      </el-tab-pane>
      <el-tab-pane label="本地上传" name="2">
        <local-upload></local-upload>
      </el-tab-pane>
      <el-tab-pane label="已选择" name="3" :disabled="true" class="selctNum">
        <span slot="label" style="cursor:pointer" @click="cahngges" class="selctNum">已选择：<i>10</i></span>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import dataLake from '@/components/olapComponent/createComponent/selectStepComponent/datalake'
import localUpload from '@/components/olapComponent/createComponent/selectStepComponent/localUpload'
export default {
  components: {
    dataLake, localUpload
  },
  data () {
    return {
      activeName: '1'
    }
  },
  methods: {
    cahngges (val) {
      this.$message.success('还剩10条哦~~~~')
    }
  }
}
</script>

<style lang="stylus" scoped>
.selectStep{
  >>>.el-tabs__content{
    height calc(100vh - 150px)
    padding 20px 5px
    overflow-x auto
  }
  >>>.el-tabs__header{
    margin-top 15px
    border-top 1px solid #cccccc
  }
  >>>.selctNum{
    i{
      color green
    }
  }
}
</style>
