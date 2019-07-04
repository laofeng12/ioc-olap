<template>
  <div class="steps">
    <el-button v-if="step!==1" @click="prevModel">上一步</el-button>
    <el-button type="primary" @click="nextModel">下一步</el-button>
    <el-button type="primary" v-if="step===7">完成</el-button>
  </div>
</template>

<script>
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
      this.$emit('nextModel', this.step)
    },
    prevModel () {
      this.$emit('prevModel', this.step)
    }
  }
}
</script>

<style lang="stylus" scoped>
.steps{
  text-align center
  margin-top 20px
}
</style>
