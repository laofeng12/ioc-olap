<template>
  <div class="tableRelation">
    <div class="containers">
      <serch-table></serch-table>
      <task-wark></task-wark>
    </div>
    <steps class="steps" :step="2" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import serchTable from '@/components/olapComponent/common/serchTable'
import steps from '@/components/olapComponent/common/steps'
import taskWark from '@/components/olapComponent/common/taskWark'
import { mapGetters } from 'vuex'
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
      this.$router.push('/olap/createolap/setFiled')
      this.$parent.getStepCountAdd(val)
    },
    prevModel (val) {
      this.$router.push('/olap/createolap/selectStep')
      this.$parent.getStepCountReduce(val)
      this.$root.eventBus.$emit('saveSelectTables', this.saveSelectTable, this.saveLocalSelectTable)
      this.$root.eventBus.$emit('openDefaultTree')
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      saveLocalSelectTable: 'saveLocalSelectTable'
    })
  }
}
</script>

<style lang="stylus" scoped>
.tableRelation{
  height calc(100vh - 150px)
  position relattive
  .containers{
    padding 20px 5px
  }
}
</style>
