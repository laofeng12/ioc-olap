<template>
  <div class="creates">
    <head-box :selectId="selectStep"></head-box>
    <select-step :class="selectStep === 1?'showBox':'hideBox'"></select-step>
    <create-table-relation :class="selectStep === 2?'showBox':'hideBox'"></create-table-relation>
    <set-filed :class="selectStep === 3?'showBox':'hideBox'"></set-filed>
    <set-measure :class="selectStep === 4?'showBox':'hideBox'"></set-measure>
  </div>
</template>

<script>
import headBox from '@/components/olapComponent/common/head'
import selectStep from '@/components/olapComponent/createComponent/selectStep'
import createTableRelation from '@/components/olapComponent/createComponent/createTableRelation'
import setFiled from '@/components/olapComponent/createComponent/setFiled'
import setMeasure from '@/components/olapComponent/createComponent/setMeasure'
export default {
  components: { headBox, selectStep, createTableRelation, setFiled, setMeasure },
  data () {
    return {
      selectStep: 1
    }
  },
  methods: {
    getStepCountAdd (val) {
      console.log(val)
      this.selectStep += 1
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
    >>>.selectStep >>>.el-tabs__content{
     height calc(100vh - 200px)
    }
  }
  .showBox.selectStep >>>.el-tabs__content{
    height calc(100vh - 200px)
  }
  .hideBox{
    opacity 0
    height 0
    >>>.serchTable{
      height 0
      width 0
    }
    >>>.containers{
      height 0
    }
  }
  .hideBox.selectStep >>>.el-tabs__content{
    height 0
  }
}
</style>
