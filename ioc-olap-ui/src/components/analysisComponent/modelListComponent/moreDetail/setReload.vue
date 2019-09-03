<template>
  <div class="setreload">
    <h4>刷新设置</h4>
    <ul>
      <li v-for="(item, index) in setData" :key="index">
         <div>
           <span>日期字段表</span>
           <span>{{item.partition_date_column}}</span>
         </div>
         <div>
           <span>日期字段</span>
           <span>{{item.partition_date_format}}</span>
         </div>
         <div>
           <span>日期格式</span>
           <span>{{item.partition_time_format}}</span>
         </div>
      </li>
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
      setData: [
        { partition_date_column: 'asdasdasdasd', partition_date_format: 'qqqqqqqqqqqqq', partition_time_format: 'yy-mm-dd' }
      ],
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
    li{
      height 30px
      line-height 30px
      div{
        span:nth-child(1){
          width 120px
        }
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

