<template>
  <div class="steps">
    <el-button v-if="step!==1" @click="prevModel">上一步</el-button>
    <el-button v-if="step!==7" type="primary" @click="nextModel">下一步</el-button>
    <el-button type="primary" v-if="step===7" @click="nextModel">完成</el-button>
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
.steps{
  text-align center
  z-index 1000
}
</style>
