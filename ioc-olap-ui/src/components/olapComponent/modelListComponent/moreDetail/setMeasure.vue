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
  data () {
    return {
      descriptionData: [
        { index: '1', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '2', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '3', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '4', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '5', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '6', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' },
        { index: '7', columnName1: 'USER_ID', columnName2: 'string', columnName3: '用户标识', columnName4: '正常模式' }
      ],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'columnName1', label: '度量名称' },
        { prop: 'columnName2', label: '计算方式' },
        { prop: 'columnName3', label: '计算值' },
        { prop: 'columnName4', label: '返回类型' }
      ]
    }
  }
}
</script>

<style lang="stylus" scoped>
.detail_setmeasure{
}
</style>
