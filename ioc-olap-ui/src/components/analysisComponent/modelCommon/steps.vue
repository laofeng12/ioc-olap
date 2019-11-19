<template>
  <div class="step_dv">
    <div class="steps">
      <el-button v-if="step !== 1" @click="prevModel">上一步</el-button>
      <el-button v-if="step !== 7" type="primary" @click="nextModel">下一步</el-button>
      <el-button type="primary" v-if="step === 7" @click="nextModel">完成</el-button>
    </div>
  </div>
</template>

<script>
import { throttle } from '@/utils/index'
export default {
  props: {
    step: {
      type: [String, Number]
    }
  },
  data () {
    return {
    }
  },
  methods: {
    nextModel () {
      throttle(_ => {
        this.$emit('nextModel', this.step)
      }, 300)
    },
    prevModel () {
      throttle(_ => {
        this.$emit('prevModel', this.step)
      }, 300)
    }
  }
}
</script>

<style lang="stylus" scoped>
.step_dv{
    background #ffffff
    height 52px
    line-height 52px
    width 100%
    position fixed
    z-index 10
    bottom 0
    left 0
    box-shadow: 0 0 5px #e7e7e9;
  }
.steps{
  position: fixed;
  right:50px;
  .el-button{
    width 74px
    height 32px
  }
  .el-button--primary{
    width 74px
    height 32px
  }
}
</style>
