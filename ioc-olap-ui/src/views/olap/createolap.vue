<template>
  <div class="creates">
    <header>
      <el-button icon="el-icon-arrow-left"></el-button>
      <el-input v-model="value" placeholder="请输入模型名称(1~20字)"></el-input>
    </header>
    <head-box :selectId="selectStep"></head-box>
    <div>
      <transition name="fade-transform" mode="out-in">
        <router-view></router-view>
      </transition>
    </div>
  </div>
</template>

<script>
import headBox from '@/components/olapComponent/common/head'
export default {
  components: { headBox },
  data () {
    return {
      selectStep: 1,
      value: ''
    }
  },
  methods: {
    getStepCountAdd (val) {
      this.selectStep += 1
    },
    getStepCountReduce (val) {
      this.selectStep -= 1
    }
  }
}
</script>
<style lang="stylus" scoped>
.creates{
  position relative
  margin-top -25px
  width 100%
  header{
    height 40px
    width 100%
    background #F2F2F2
    margin-bottom 15px
    >>>.el-button{
      float left
      background #F2F2F2
      border none
      font-size 20px
      height 38px
      border-right 1px solid #ccc
    }
    >>>.el-input{
      float left
      width 200px
      margin-left 20px
      height 30px
      margin-top 5px
      .el-input__inner{
        height 30px
      }
    }
  }
  .showBox{
    opacity 1
    >>>.serchTable{
      height auto
    }
    >>>.selectStep >>>.el-tabs__content{
     height calc(100vh - 200px)
    }
  }
  .showBox.selectStep >>>.el-tabs__content{
    height calc(100vh - 200px)
  }
  .hideBox{
    opacity 0
    height 0
    >>>.serchTable{
      height 0
      width 0
    }
    >>>.containers{
      height 0
    }
  }
  .hideBox.selectStep >>>.el-tabs__content{
    height 0
  }
}
</style>
