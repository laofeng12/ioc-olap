<template>
  <div class="setFiled">
    <div class="containers">
      <serch-table></serch-table>
      <main>

      </main>
    </div>
    <steps :step="3" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import serchTable from '@/components/olapComponent/common/serchTable'
import steps from '@/components/olapComponent/common/steps'
export default {
  components: {
    serchTable,
    steps
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
.setFiled{
  .containers{
    height calc(100vh - 150px)
    padding 20px 5px
  }
}
</style>
