<template>
  <div class="setreload" v-if="jsonData">
    <h4>模型基本信息</h4>
    <div>
      <span>事实表</span>
      <span>{{jsonData.ModesList.fact_table}}</span>
    </div>
    <div>
      <span>维度表</span>
      <span>{{jsonData.dimensionLength || '10'}}</span>
    </div>
    <div>
      <span>维度字段</span>
      <span>{{jsonData.dimensionFiledLength || '5'}}</span>
    </div>
    <div>
      <span>度量字段</span>
      <span>{{jsonData.measureFiledLength || '2'}}</span>
    </div>
    <div>
      <span>构建引擎</span>
      <span>{{jsonData.CubeList[0].engine_type === '1' ? 'MapReduce' : 'Spark' }}</span>
    </div>
    <h4>描述信息</h4>
    <el-input style="margin-top:20px;" type="textarea" v-model="jsonData.CubeList[0].description" disabled placeholder=""></el-input>
  </div>
</template>

<script>
export default {
  props: {
    jsonData: {
      type: [Object, Array]
    }
  },
  data () {
    return {
      list: {}
    }
  },
  mounted () {
  },
  methods: {}
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
