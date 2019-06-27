<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane label="模型列表" name="1">
        <model-list></model-list>
      </el-tab-pane>
      <el-tab-pane label="构建列表" name="2">
        <construct-list></construct-list>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import modelList from '@/components/olapComponent/modelList'
import constructList from '@/components/olapComponent/constructList'
export default {
  components: {
    modelList, constructList
  },
  data () {
    return {
      activeName: '1'
    }
  }
}
</script>
