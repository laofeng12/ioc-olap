<template>
  <div class="detail_setmeasure">
    <div>
      <element-table :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
    </div>
  </div>
</template>

<script>
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    elementTable
  },
  props: {
    jsonData: {
      type: [Object, Array]
    }
  },
  data () {
    return {
      descriptionData: [],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'name', label: '度量名称' },
        { prop: 'expression', label: '计算方式' },
        { prop: 'value', label: '计算值' },
        { prop: 'returntype', label: '返回类型' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        this.descriptionData = this.jsonData.CubeList[0].measures.map((item, index) => {
          return {
            index: index + 1,
            name: item.name,
            expression: item.function.expression,
            value: item.function.parameter.value,
            returntype: item.function.returntype
          }
        })
      } 
    }
  }
}
</script>

<style lang="stylus" scoped>
.detail_setmeasure{
}
</style>
