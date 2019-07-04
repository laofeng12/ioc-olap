<template>
  <div class="tabHead">
    <div class="tabHead_item">
      <div :class="Number(selectId) === 1 ? 'actives' : ''">1、选择数据源</div>
      <div :class="Number(selectId) === 2 ? 'actives' : ''">2、建立表关系</div>
      <div :class="Number(selectId) === 3 ? 'actives' : ''">3、设置维度字段</div>
      <div :class="Number(selectId) === 4 ? 'actives' : ''">4、设置度量字段</div>
      <div :class="Number(selectId) === 5 ? 'actives' : ''">5、刷新及过滤设置</div>
      <div :class="Number(selectId) === 6 ? 'actives' : ''">6、高级设置</div>
      <div :class="Number(selectId) === 7 ? 'actives' : ''">7、完成创建</div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    selectId: {
      type: [String, Number],
      default: 1
    },
    titleData: {
      type: Array,
      default: () => {
        return [{
          value: '填写基本信息'
        }, {
          value: '邮箱激活'
        }, {
          value: '完善信息'
        }]
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
.tabHead{
  width 100%
  overflow hidden
  .tabHead_item{
    height 25px
    margin 0 auto
    width 90%
    display:flex;
      &>div{
      flex:1;
      // width 200px
      // float left
      height:25px;
      line-height 25px
      font-size 10px
      text-align center
      background #f5f5f6
      color #8a8a8a
      position relative
      span{
        width 23px
        height 23px
        line-height 20px
        text-align center
        color #ffffff
        font-size 14px
        border-radius 50%
        margin-right 5px
        background #8a8a8a
        display inline-block
      }
    }
    &>div:nth-child(1)::before{
      content: "";
      position: absolute;
      left: -12px;
      top: 1px;
      width: 23px;
      height: 23px;
      background: #ffffff;
      border-left: 5px solid #ffffff
      border-bottom: 5px solid #ffffff;
      transform: rotate(225deg);
      z-index:10;
    }
    &>div::after{
      content: "";
      position: absolute;
      right: -12px;
      top: 1px;
      width: 23px;
      height: 23px;
      background: #f5f5f6;
      border-left: 5px solid #ffffff
      border-bottom: 5px solid #ffffff;
      transform: rotate(225deg);
      z-index:10;
    }
    &>div.actives{
      background #59AFF9
      color #ffffff
      span{
        background #3085d7
      }
    }
    &>div.actives::after{
      content: "";
      position: absolute;
      right: -12px;
      top: 1px;
      width: 23px;
      height: 23px;
      background: #59AFF9!important;
    }
    &>div.actives::after{
      content: "";
      position: absolute;
      right: -12px;
      top: 1px;
      width: 23px;
      height: 23px;
      background: #59AFF9!important;
    }
  }
}
</style>
