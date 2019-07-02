<template>
  <div class="creates">
    <head-box :selectId="selectStep"></head-box>
    <select-step :class="selectStep === 1?'showBox':'hideBox'"></select-step>
    <create-table-relation :class="selectStep === 2?'showBox':'hideBox'"></create-table-relation>
    <set-filed :class="selectStep === 3?'showBox':'hideBox'"></set-filed>
  </div>
</template>

<script>
import headBox from '@/components/olapComponent/common/head'
import selectStep from '@/components/olapComponent/createComponent/selectStep'
import createTableRelation from '@/components/olapComponent/createComponent/createTableRelation'
import setFiled from '@/components/olapComponent/createComponent/setFiled'
export default {
  components: { headBox, selectStep, createTableRelation, setFiled },
  data () {
    return {
      selectStep: 1
    }
  },
  methods: {
    getStepCountAdd (val) {
      this.selectStep = val + 1
    },
    getStepCountReduce (val) {
      this.selectStep -= 1
    }
  }
}
</script>
<style lang="stylus" scoped>
.creates{
  position relative
  width 100%
  .showBox{
    opacity 1
    >>>.serchTable{
      height auto
    }
  }
  .hideBox{
    opacity 0
    height 0
    >>>.serchTable{
      height 0
    }
  }
}
</style>
