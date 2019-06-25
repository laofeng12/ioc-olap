<template>
  <div class="w-info-title">
    <span>{{title}}</span>
  </div>
</template>

<script>
export default {
  props: {
    title: {
      type: String,
      default: ''
    }
  }
}
</script>

<style lang="scss" scoped>
.w-info-title {
  font-size: 0;
  color: #000000;
  opacity: 0.85;
  margin-bottom: 8px;
  span {
    font-size: 16px;
  }
}
</style>
