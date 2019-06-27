<template>
  <div class="creates">
    <head-box></head-box>
    <select-step></select-step>
  </div>
</template>

<script>
import headBox from '@/components/olapComponent/common/head'
import selectStep from '@/components/olapComponent/createComponent/selectStep'
export default {
  components: { headBox, selectStep }
}
</script>
