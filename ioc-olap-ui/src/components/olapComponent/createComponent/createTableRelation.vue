<template>
  <div class="tableRelation">
    <div class="containers">
      <serch-table></serch-table>
      <task-wark></task-wark>
    </div>
    <steps :step="2" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import serchTable from '@/components/olapComponent/common/serchTable'
import steps from '@/components/olapComponent/common/steps'
import taskWark from '@/components/olapComponent/common/taskWark'
export default {
  components: {
    serchTable, steps, taskWark
  },
  data () {
    return {

    }
  },
  methods: {
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
    }
  }
}
</script>

<style lang="stylus" scoped>
.tableRelation{
  .containers{
    height calc(100vh - 150px)
    padding 20px 5px
  }
}
</style>
