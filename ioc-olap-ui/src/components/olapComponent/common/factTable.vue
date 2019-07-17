<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-button type="text" @click="changes">设置事实表</el-button>
     <ul v-if="dataList && dataList.length">
       <li v-for="(item, index) in dataList" id="dragbtn" :class= "current === index?'actives':''" @mousedown="dragLi(item)" :key="index" @dblclick="changeLi(item, index)">
         <i class="el-icon-date" style="margin-right:3px;"></i>
         {{item.label}}
         <span v-if="item.filed === 1">事实表</span>
        </li>
     </ul>
     <div v-else>暂无数据</div>
     <setfact-table ref="dialog"></setfact-table>
  </div>
</template>

<script>
import setfactTable from '@/components/olapComponent/dialog/setfactTable'
import { mapGetters } from 'vuex'
export default {
  components: {
    setfactTable
  },
  data () {
    return {
      isSetFact: false,
      value: '',
      current: '',
      dataList: []
    }
  },
  directives: {
    drag: {
      // 指令的定义
      bind: function (el) {
        let odiv = el // 获取当前元素
        let firstTime = ''; let lastTime = ''
        odiv.onmousedown = (e) => {
          document.getElementById('dragbtn').setAttribute('data-flag', false)
          firstTime = new Date().getTime()
          //  算出鼠标相对元素的位置
          let disY = e.clientY - odiv.offsetTop
          document.onmousemove = (e) => {
            //  用鼠标的位置减去鼠标相对元素的位置，得到元素的位置
            let top = e.clientY - disY
            //  页面范围内移动元素
            if (top > 0 && top < document.body.clientHeight - 48) {
              odiv.style.top = top + 'px'
            }
          }
          document.onmouseup = (e) => {
            document.onmousemove = null
            document.onmouseup = null
            // onmouseup 时的时间，并计算差值
            lastTime = new Date().getTime()
            if ((lastTime - firstTime) < 200) {
              document.getElementById('dragbtn').setAttribute('data-flag', true)
            }
          }
        }
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.dataList = this.selectTableTotal
      this.checkFactFile()
    },
    checkFactFile () {
      let datalist = this.dataList || []
      for (let i = 0; i < datalist.length; i++) {
        let t = datalist[i]
        if (t.filed) {
          this.isSetFact = true
          this.$parent.clickTable(t)
          break
        }
      }
    },
    changes (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (item, index) {
      if (this.isSetFact) {
        console.log(this.savemousedownData)
        this.current = index
        this.$parent.clickTable(item)
      } else {
        alert('请先设置事实表')
      }
    },
    dragLi (item) {
      if (this.isSetFact) {
        this.$parent.dragTable(item)
      } else {
        alert('请先设置事实表')
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      savemousedownData: 'savemousedownData'
    })
  }
}
</script>

<style lang="stylus" scoped>
.factTable{
  max-width 230px
  // width 200px
  float left
  // padding 0 25px
  border-right 1px solid #f0f0f0
  height calc(100vh - 100px)
  overflow auto
  ul{
    cursor pointer
    li{
      height 30px
      line-height 30px
      color #000000
      span{
        background #009688
        color #ffffff
        padding 2px 6px
        font-size 10px
        border-radius 3px
      }
    }
    .actives{
      color #009688
    }
  }
  >>>.el-input{
    height 30px
    .el-input__inner{
      height 30px
    }
  }
  >>>.el-input__suffix{
    margin-top -5px
  }
}
</style>
