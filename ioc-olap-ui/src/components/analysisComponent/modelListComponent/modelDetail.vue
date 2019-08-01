<template>
  <div class="modelDetail">
    <div class="tabHead_item">
      <div v-for="(item, index) in dataHead" @click="selectTab(item.id, item.view)" :class="String(cureent) === item.id?'actives':''" :key="index">{{item.value}}</div>
    </div>
    <div class="content_box">
      <component :is="currentView"></component>
    </div>
    <el-button type="primary" @click="closeDetail">关闭</el-button>
  </div>
</template>

<script>
import { selects, settableLine, setFiled, setMeasure, setReload, setAdvance, setcomplate } from '@/components/analysisComponent/modelListComponent/moreDetail'
export default {
  components: {
    selects, settableLine, setFiled, setMeasure, setReload, setAdvance, setcomplate
  },
  data () {
    return {
      cureent: 1,
      currentView: 'selects',
      dataHead: [
        { id: '1', value: '1、选择数据源', view: 'selects' },
        { id: '2', value: '2、建立表关系', view: 'settableLine' },
        { id: '3', value: '3、设置维度字段', view: 'setFiled' },
        { id: '4', value: '4、设置度量字段', view: 'setMeasure' },
        { id: '5', value: '5、刷新及过滤设置', view: 'setReload' },
        { id: '6', value: '6、高级设置', view: 'setAdvance' },
        { id: '7', value: '7、完成创建', view: 'setcomplate' }
      ]
    }
  },
  methods: {
    selectTab (id, view) {
      this.cureent = id
      this.currentView = view
    },
    closeDetail () {
      this.$emit('closeExpands')
    }
  }
}
</script>

<style lang="stylus" scoped>
.modelDetail{
  width 100%
  overflow hidden
  .tabHead_item{
    height 25px
    margin 0 auto
    width 90%
    display:flex;
      &>div{
      flex:1;
      cursor pointer
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
  .content_box{
    height 300px
  }
  >>>.el-button{
    margin-left 50%
    transform translateX(-50%)
    width 120px
    margin-top 50px
  }
}
</style>
