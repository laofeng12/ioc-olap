<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <ul v-if="dataList && dataList.length">
       <li v-for="(item, index) in dataList"
        :class="item.isActive===1?'actives':''"
        :style="{color: current===index?colors:''}"
        :key="index" @click="changeLi(item, index)">
         <i class="el-icon-date" style="margin-right:3px;"></i>
         {{item.label}}
       </li>
     </ul>
     <div v-else style="margin-top:50px;text-align:center;">暂无数据</div>
     <setfact-table ref="dialog"></setfact-table>
  </div>
</template>

<script>
import setfactTable from '@/components/olapComponent/dialog/setfactTable'
import { mapGetters } from 'vuex'
import { reduceObj } from '@/utils/index'
export default {
  components: {
    setfactTable
  },
  data () {
    return {
      value: '',
      current: '',
      colors: 'red',
      dataList: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
    // 初始化已选择的表
      this.dataList = reduceObj(this.saveSelectFiledTree, 'label')
      setTimeout(() => { this.changeLi(this.dataList[0], 0) }, 300)
      // 接收设置表关系的数据
      this.dataList = this.selectTableTotal
      // 接收已选择的表
      this.$root.eventBus.$on('tableNameActive', _ => {
        setTimeout(() => {
          this.dataList.forEach((item, index) => {
            this.saveSelectFiled.forEach((n, i) => {
              if (item.label === n.tableName) {
                item['isActive'] = 1
              }
            })
          })
          this.dataList = reduceObj(this.dataList, 'label')
          // 存储已选择后的维度表
          this.$store.dispatch('saveSelectFiledTree', this.dataList)
        }, 300)
      })
    },
    cahngges (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (item, index) {
      this.current = index
      const parmas = {
        dsDataSourceId: 2,
        tableName: item.label
      }
      this.$store.dispatch('GetColumnList', parmas).then(res => {
        let datas = []
        res.data.map(n => {
          datas.push({
            comment: n.comment,
            columnName: n.columnName,
            tableName: item.label
          })
        })
        this.$root.eventBus.$emit('filedTable', datas, res.code)
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
