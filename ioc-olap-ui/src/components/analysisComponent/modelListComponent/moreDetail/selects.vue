<template>
  <div class="detail_select">
    <p>已选择数据表:</p>
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
  watch: {
    jsonData () {
      this.init()
    }
  },
  data () {
    return {
      descriptionData: [],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'type', label: '来源' },
        { prop: 'table_name', label: '表名称' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        this.descriptionData = this.jsonData.TableList && this.jsonData.TableList[0].tableList.map((res, index) => {
          // 0 数据湖 1 数据集 2 自定义 3本地空间
          return { index: index + 1, type: Number(res.type) === 0 ? '数据湖' : Number(res.type) === 1 ? ' 数据集': Number(res.type) === 2 ? '自定义' : '本地空间', table_name: res.table_name }
        })
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
.detail_select{
  p{
    height 50px
    line-height 50px
  }
  div{
    margin-top 0px
  }
}
</style>
