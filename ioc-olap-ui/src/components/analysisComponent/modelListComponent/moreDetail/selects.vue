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
  data () {
    return {
      descriptionData: [
        { index: '1', dataType: '数据湖', columnName: 'table_name_table_name_table_name_table_name' },
        { index: '2', dataType: '本地上传', columnName: 'table_name_table_name_table_name_table_name' },
        { index: '3', dataType: '数据湖', columnName: 'table_name_table_name_table_name_table_name' },
        { index: '4', dataType: '本地上传', columnName: 'table_name_table_name_table_name_table_name' },
        { index: '5', dataType: '数据湖', columnName: 'table_name_table_name_table_name_table_name' },
        { index: '6', dataType: '本地上传', columnName: 'table_name_table_name_table_name_table_name' },
        { index: '7', dataType: '数据湖', columnName: 'table_name_table_name_table_name_table_name' }
      ],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'dataType', label: '来源' },
        { prop: 'columnName', label: '表名称' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        console.log('父亲来的', this.jsonData)
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
