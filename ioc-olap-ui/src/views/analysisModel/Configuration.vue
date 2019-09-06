<template>
  <div>
    <el-tabs v-model="activeName" @tab-click="tabClick">
      <el-tab-pane label="模型列表" name="1">
        <model-list></model-list>
      </el-tab-pane>
      <el-tab-pane label="分享列表" name="3">
        <share-list></share-list>
      </el-tab-pane>
      <el-tab-pane label="构建列表" name="2">
        <construct-list></construct-list>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import modelList from '@/components/analysisComponent/modelList'
import ShareList from '@/components/analysisComponent/ShareList'
import constructList from '@/components/analysisComponent/constructList'
export default {
  components: {
    modelList, constructList, ShareList
  },
  data () {
    return {
      activeName: '1'
    }
  },
  methods: {
    tabClick (val) {
      if (val.name === '2') {
        this.$root.eventBus.$emit('cubObjList')
      }
    }
  }
}
</script>
