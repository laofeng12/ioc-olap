<template>
  <div class="app-container resource">
      <menubox class="leftbox"></menubox>
      <context class="rightbox"></context>
  </div>
</template>

<script>
import menubox from '@/components/system/Mechanism/menu'
import context from '@/components/system/Mechanism/context'
export default {
  components: {
    menubox, context
  }
}
</script>

<style lang="stylus" scoped>
.resource{
  position absolute
  height calc(100vh - 100px)
  display flex
  flex-flow: row
  width 96%
  .leftbox{
    width 350px
    min-width 350px
    border-right 1px solid #ccc
    height 90%
    overflow auto
    z-index 10
    >>>.el-tree{
      min-width 1000px
      height 100%
    }
  }
  .rightbox{
    flex 1
    padding-left 20px
    // overflow auto
    // padding-bottom 50px
  }
  .leftbox::-webkit-scrollbar{
    width 8px
    height 8px
    background #fff
  }
  .leftbox::-webkit-scrollbar-track{
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color:#fff;
  }
  .leftbox::-webkit-scrollbar-thumb{
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #B5D2DE;
  }
}
</style>
