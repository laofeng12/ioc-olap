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
  props: {
    jsonData: {
      type: [Object, Array]
    }
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
        { prop: 'table', label: '表名称' },
        { prop: 'column', label: '字段名称' },
        { prop: 'column_type', label: '字段类型' },
        { prop: 'name', label: '显示名称' },
        { prop: 'mode', label: '维度组合模式' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        this.descriptionData = [...this.jsonData.CubeList[0].dimensions]
        this.descriptionData.map((res, index) => {
          res.index = index + 1
          res.mode = res.derived ? '衍生模式' : '正常模式'
          res.column_type = res.column_type ? res.column_type : 'String'
        })
        console.log(this.descriptionData)
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
.detail_setfiled{
}
</style>
