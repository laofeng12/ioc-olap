<template>
  <div class="setreload">
    <h4>模型基本信息</h4>
    <div>
      <span>事实表</span>
      <span>{{list.data1}}</span>
    </div>
    <div>
      <span>维度表</span>
      <span>{{list.data2}}</span>
    </div>
    <div>
      <span>维度字段</span>
      <span>{{list.data3}}</span>
    </div>
    <div>
      <span>度量字段</span>
      <span>{{list.data4}}</span>
    </div>
    <div>
      <span>构建引擎</span>
      <span>{{list.data5}}</span>
    </div>
    <h4>描述信息</h4>
    <el-input style="margin-top:20px;" type="textarea" v-model="list.setDetail" disabled placeholder=""></el-input>
  </div>
</template>

<script>
export default {
  data () {
    return {
      list: {
        setDetail: '拉拉啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊啊',
        data1: 'lalalalla',
        data2: 'lalalalla',
        data3: 'lalalalla',
        data4: 'lalalalla',
        data5: 'lalalalla'
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
  div{
    height 30px
    line-height 30px
    span:nth-child(1){
      width 120px
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

