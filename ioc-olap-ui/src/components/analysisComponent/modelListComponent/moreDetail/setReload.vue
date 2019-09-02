<template>
  <div class="setreload">
    <h4>刷新设置</h4>
    <ul>
      <li v-for="(item, index) in setData" :key="index">
         <div>
           <span>日期字段表</span>
           <span>{{item.data1}}</span>
         </div>
         <div>
           <span>日期字段</span>
           <span>{{item.data2}}</span>
         </div>
         <div>
           <span>日期格式</span>
           <span>{{item.data3}}</span>
         </div>
      </li>
    </ul>
    <p>
      <span>日期存在多列？</span>
      <span>否</span>
    </p>
    <h4>过滤设置</h4>
    <el-input style="margin-top:20px;" type="textarea" v-model="setDetail" disabled placeholder=""></el-input>
  </div>
</template>

<script>
export default {
  data () {
    return {
      setData: [
        { data1: 'asdasdasdasd', data2: 'qqqqqqqqqqqqq', data3: 'yy-mm-dd' }
      ],
      setDetail: '拉拉啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊'
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

