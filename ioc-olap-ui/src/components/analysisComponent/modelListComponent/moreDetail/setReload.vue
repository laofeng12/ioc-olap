<template>
  <div class="setreload">
    <h4>刷新设置</h4>
    <p style="margin-top:15px;">
      <span>自动刷新模型？</span>
      <span>{{ !dataList.interval ? '否' : '是' }}</span>
    </p>
    <p v-if="dataList.interval">
      <span>更新频率</span>
      <span>{{dataList.frequencytype}}/{{dataList.interval}}</span>
    </p>
    <ul>
         <div>
           <span>日期字段表</span>
           <span>{{dataList.tables}}</span>
         </div>
         <div>
           <span>日期字段</span>
           <span>{{dataList.columns}}</span>
         </div>
         <div>
           <span>日期格式</span>
           <span>{{dataList.partition_date_format}}</span>
         </div>
    </ul>
    <p>
      <span>日期存在多列？</span>
      <span>否</span>
    </p>
    <h4>过滤设置</h4>
    <element-table :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
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
      dataList: {
        tables: '',
        columns: '',
        frequencytype: ''
      },
      descriptionData: [
        { index: '1', name: 'USER_ID', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '2', name: 'USER_ID1', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '3', name: 'USER_ID2', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '4', name: 'USER_ID3', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '5', name: 'USER_ID4', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '6', name: 'USER_ID5', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '7', name: 'USER_ID6', expression: 'string', value: '用户标识', returntype: '正常模式' }
      ],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'name', label: '表名称' },
        { prop: 'expression', label: '字段' },
        { prop: 'value', label: '过滤方式' },
        { prop: 'returntype', label: '过滤值' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        const { partition_date_column, frequencytype } = this.jsonData.ModesList.partition_desc
        this.dataList = this.jsonData.ModesList.partition_desc
        this.dataList.tables = partition_date_column.split('.')[0]
        this.dataList.columns = partition_date_column.split('.')[1]
        this.dataList.frequencytype = frequencytype === 1 ? '小时' : (frequencytype === 2 ? '天' : '月')
        console.log(this.dataList)
        // let { partition_date_column, partition_time_format, partition_date_format } = this.jsonData.ModesList.partition_desc
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
.setreload{
  padding 20px 20%
  h4{
    height 50px
    line-height 50px
    border-bottom 1px solid #cccccc
  }
  span{
    display inline-block
  }
  ul{
    height 100px
    padding 5px
    div{
      height 30px
      line-height 30px
    }
    div{
      span:nth-child(1){
        width 120px
      }
    }
  }
  p{
    padding-left 5px
    span:nth-child(1){
      width 120px
    }
  }
}
</style>
