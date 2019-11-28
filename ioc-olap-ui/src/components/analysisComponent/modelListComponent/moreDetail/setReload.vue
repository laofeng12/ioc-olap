<template>
  <div class="setreload">
    <h4>刷新设置</h4>
    <p style="margin-top:15px;">
      <span>自动刷新模型？</span>
      <span>{{ this.jsonData.timingreFresh && this.jsonData.timingreFresh.autoReload !== 1 ? '否' : '是' }}</span>
    </p>
    <p v-if="this.jsonData.timingreFresh && this.jsonData.timingreFresh.autoReload === 1" style="margin-top:10px;">
      <span>更新频率</span>
      <span>{{this.jsonData.timingreFresh && frequencytypes(this.jsonData.timingreFresh.frequencytype)}}/{{this.jsonData.timingreFresh && this.jsonData.timingreFresh.interval}}次</span>
    </p>
    <ul>
         <div>
           <span>日期字段表</span>
           <span>{{dataList.tables || '无'}}</span>
         </div>
         <div>
           <span>日期字段</span>
           <span>{{dataList.columns || '无'}}</span>
         </div>
         <div>
           <span>日期格式</span>
           <span>{{dataList.partition_date_format || '无'}}</span>
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
      descriptionData: [],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'tableName', label: '表名称' },
        { prop: 'field', label: '字段' },
        { prop: 'pattern', label: '过滤方式' },
        { prop: 'parameter', label: '过滤值' }
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
        this.descriptionData = this.jsonData.filterCondidion
        this.descriptionData.map((res, index) => { res.index = index + 1 })
      }
      console.log(this.dataList)
    },
    frequencytypes (val) {
      let obj = ['', '小时', '天', '月']
      return obj[val]
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
