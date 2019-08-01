<template>
  <div class="detail_setfiled">
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
  data () {
    return {
      descriptionData: [
        { index: '1', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '2', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '3', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '4', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '5', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '6', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '7', dataType: 'table_name_table_name_table_name_table_name', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' }
      ],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'dataType', label: '表名称' },
        { prop: 'columnName1', label: '字段名称' },
        { prop: 'columnName2', label: '字段类型' },
        { prop: 'columnName3', label: '显示名称' },
        { prop: 'columnName4', label: '维度组合模式' }
      ]
    }
  }
}
</script>

<style lang="stylus" scoped>
.detail_setfiled{
}
</style>
