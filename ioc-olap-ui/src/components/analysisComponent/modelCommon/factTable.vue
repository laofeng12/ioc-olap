<template>
  <div class="factTable" onselectstart = "return false">
     <!-- <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input> -->
     <el-row class="left-search">
       <el-input
         size="small"
         suffix-icon="el-icon-search"
         placeholder="请输入关键词"
         v-model="value"
         clearable>
       </el-input>
     </el-row>
     <el-button type="text" @click="changes" style="margin: 16px 0;width: 100%;color: #0486FE; background: #fff!important;border: 1px solid #0486FE;"  size="small">设置事实表</el-button>
     <ul v-if="dataList && dataList.length">
       <li v-for="(item, index) in dataList" id="dragbtn" :class= "current === index?'actives':''" @mousedown="dragLi(item)" :key="index" @dblclick="changeLi(item, index)">
         <i class="el-icon-date" style="margin-right:3px;margin-top:8px;"></i>
         <span class="tableTitle" :class="item.filed === 1 || item.label === fact_tableName ? 'factClass' : ''">{{item.label}}</span>
         <span class="factTitle" v-if="item.filed === 1 || item.label === fact_tableName">事实表</span>
        </li>
     </ul>
     <div v-else>暂无数据</div>
     <setfact-table ref="dialog"></setfact-table>
  </div>
</template>

<script>
import setfactTable from '@/components/analysisComponent/dialog/setfactTable'
import { mapGetters } from 'vuex'
export default {
  components: {
    setfactTable
  },
  data () {
    return {
      factTable: null,
      fact_tableName: '',
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
      this.dataList = [...this.selectTableTotal] || []
      if (!Array.isArray(this.ModelAllList)) {
        this.fact_tableName = this.jointResultData.fact_table.split('.')[1]
        this.dataList.map(item => {
          if (item.table_name === this.fact_tableName) {
            item.filed = 1
          }
        })
      }
      this.checkFactFile()
    },
    checkFactFile () {
      let datalist = this.dataList || []
      for (let i = 0; i < datalist.length; i++) {
        let t = datalist[i]
        if (t.filed) {
          this.factTable = t
          this.$parent.clickTable(t)
          break
        }
      }
    },
    changes (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (item, index) {
      item.database = item.database || 'test'
      if (this.factTable) {
        if (this.factTable.label !== item.label) {
          this.current = index
          this.$parent.clickTable(item)
        }
      } else {
        this.$message.warning('请先设置事实表')
      }
    },
    dragLi (item) {
      item.database = item.database || 'test'
      if (this.factTable) {
        if (this.factTable.label !== item.label) {
          this.$parent.dragTable(item)
        }
      } else {
        this.$message.warning('请先设置事实表')
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      jointResultData: 'jointResultData',
      ModelAllList: 'ModelAllList',
      savemousedownData: 'savemousedownData'
    })
  }
}
</script>

<style lang="stylus" scoped>
.factTable{
  max-width 240px
  padding 16px
  font-size 14px
  float left
  background #ffffff
  border-right 1px solid #f0f0f0
  height 100%
  ul{
    cursor pointer
    overflow auto
    width 100%
    height 74%
    li{
      height 30px
      line-height 30px
      color #000000
      display inline-flex
      min-width 100%
      .factTitle{
        width 52px
        height 20px
        font-size: 13px;
        color: #0486FE;
        text-align: center;
        line-height: 20px;
        background: #EFF7FF;
        margin-top 5px
        border: 1px solid #0486FE;
        border-radius 3px
        margin-left: 3px;
        vertical-align: middle;
      }
    }
    .actives{
      color #009688
    }
    .factClass{
      font-size: 14px;
      color: #0486FE;
    }
  }

  // >>>.el-input__suffix{
  //   margin-top -5px
  // }
}

</style>
