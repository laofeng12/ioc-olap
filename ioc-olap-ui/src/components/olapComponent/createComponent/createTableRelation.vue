<template>
  <div class="tableRelation">
    <div class="containers">
      <serch-table></serch-table>
    </div>
    <steps :step="2" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import serchTable from '@/components/olapComponent/common/serchTable'
import steps from '@/components/olapComponent/common/steps'
export default {
  components: {
    serchTable, steps
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
