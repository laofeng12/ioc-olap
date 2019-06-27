<template>
  <div class="selectStep">
    <el-tabs v-model="activeName">
      <el-tab-pane label="数据湖" name="1">
        <data-lake></data-lake>
      </el-tab-pane>
      <el-tab-pane label="本地上传" name="2">
        <local-upload></local-upload>
      </el-tab-pane>
      <el-tab-pane label="已选择" name="3">
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
  }
}
</script>

<style lang="stylus" scoped>
.selectStep{
  >>>.el-tabs__content{
    height calc(100vh - 200px)
    position absolute
    width 100%
  }
  >>>.el-tabs__header{
    margin-top 30px
    border-top 1px solid #cccccc
  }
}
</style>
