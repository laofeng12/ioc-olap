<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-button type="text" @click="changes">设置事实表</el-button>
     <ul v-if="dataList && dataList.length">
       <li v-for="(item, index) in dataList" :class= "current === index?'actives':''" :key="index" @click="changeLi(index)">
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
      value: '',
      current: '',
      dataList: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      console.log(this.selectTableTotal)
      this.dataList = this.selectTableTotal
    },
    changes (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (index) {
      this.current = index
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal'
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
