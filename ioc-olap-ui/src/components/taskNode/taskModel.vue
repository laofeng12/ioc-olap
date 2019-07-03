<template>
  <task-node-model :node="nodeModel">
      <span class="task-node-model-label">{nodeModel.name}</span>
  </task-node-model>
</template>
<script>
export default {
  data () {
    return {
      nodeModel: [{
        id: '111'
      }, {
        id: '222'
      }]
    }
  }
}
</script>
