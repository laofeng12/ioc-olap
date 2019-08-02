<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <ul v-if="dataList.lookups && dataList.lookups.length">
       <li v-for="(item, index) in dataList.lookups"
        :class="item.isActive===1?'actives':''"
        :style="{color: current===index?colors:''}"
        :key="index" @click="changeLi(titleData[index], dataList.fact_table, index)">
         <i class="el-icon-date" style="margin-right:3px;"></i>
         {{titleData[index]}}
         <span v-if="titleData[index]===dataList.fact_table">事实表</span>
       </li>
     </ul>
     <div v-else style="margin-top:50px;text-align:center;">暂无数据</div>
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
      value: '',
      current: '',
      colors: 'red',
      titleData: [], // 表名
      // 模拟数据
      dataList: {}
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.dataList = this.saveLeftFiled
      // 遍历去重数据拿到表名称
      this.dataList.lookups.map((item, index) => {
        this.titleData.push(item.table, item.joinTable)
      })
      this.titleData = [...new Set(this.titleData)]
      // 初始化已选择的表
      setTimeout(() => {
        this.changeLi(this.titleData[0], 0)
        this.current = 0
      }, 300)
      // 接收设置表关系的数据
      // this.dataList = this.selectTableTotal
      // 接收已选择的表
      this.$root.eventBus.$on('tableNameActive', _ => {
        setTimeout(() => {
          this.titleData.forEach((item, index) => {
            this.saveSelectFiled.forEach((n, i) => {
              if (item === n.tableName) {
                this.dataList.lookups[index]['isActive'] = 1
              }
            })
          })
        }, 300)
      })
      console.log(this.dataList)
    },
    cahngges (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (item, name, index) {
      this.current = index
      const parmas = {
        dsDataSourceId: 2,
        tableName: item
      }
      this.$store.dispatch('GetColumnList', parmas).then(res => {
        res.data && res.data.map((n, i) => {
          n.mode = n.mode ? n.mode : '2'
          n.derived = n.columnName
          n.tableName = item
          n.filed = item === this.dataList.fact_table ? '1' : '0'
          n.id = `${item}${i}`
        })
        // 存储选择对应的表
        this.$root.eventBus.$emit('filedTable', res.data, res.code)
        // 存储已选择的表
        this.$store.dispatch('SaveList', res.data)
      })
    }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('filedTable')
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectFiled: 'saveSelectFiled',
      saveLeftFiled: 'saveLeftFiled',
      saveSelectFiledTree: 'saveSelectFiledTree'
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
