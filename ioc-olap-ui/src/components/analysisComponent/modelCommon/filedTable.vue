<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value"  clearable></el-input>
     <ul v-if="tabaleList && tabaleList.length">
       <el-tooltip v-for="(item, index) in tabaleList" :key="index" effect="dark" :content="titleData[index]" placement="right">
        <li
          :class="item.isActive===1?'actives':''"
          :style="{color: current===index?colors:''}"
          :key="index" @click="changeLi(item, index)">
          <i class="el-icon-date" style="margin-right:3px;margin-top:8px;"></i>
          <span class="tableTitle">{{item.alias}}</span>
          <span class="filds" v-if="titleData[index]===dataList.fact_table.split('.')[1]">事实表</span>
        </li>
       </el-tooltip>
     </ul>
     <div v-else style="margin-top:50px;text-align:center;">暂无数据</div>
     <setfact-table ref="dialog"></setfact-table>
  </div>
</template>

<script>
import setfactTable from '@/components/analysisComponent/dialog/setfactTable'
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
      ids: '',
      primary_key: '',
      titleData: [], // 表名
      // 模拟数据
      dataList: {}
    }
  },
  watch: {
    // '$route' () {
    //   this.init()
    // }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      /**
       * 1、深拷贝拿到第二步建表生成的数据 this.jointResultData
       * 2、遍历数据拿到对应的别名（左侧菜单需要展示别名）
       *
       */
      this.dataList = JSON.parse(JSON.stringify(this.jointResultData))
      this.titleData = []
      this.dataList.lookups.map((item, index) => {
        // 存储所有的事实表名
        this.titleData.push(item.alias)
        // 取出事实表对应的id以及foreign_key
        if (this.dataList.fact_table.split('.')[1] === item.joinTable) {
          this.ids = item.joinId
          this.primary_key = item.join.foreign_key
        }
      })
      // 创建一条事实表的数据push到集合里
      let factData = {
        alias: this.dataList.fact_table.split('.')[1],
        id: this.ids,
        table: '',
        joinTable: this.dataList.fact_table.split('.')[1],
        join: {
          foreign_key: this.primary_key
        }
      }
      this.dataList.lookups = [factData, ...this.dataList.lookups] // 组合数据
      this.titleData = [...new Set([this.dataList.fact_table.split('.')[1], ...this.titleData])] // 组合事实表的别名跟普通表的别名
      // 存储已组合的名称到store
      this.$store.dispatch('SaveLeftTitle', this.titleData)
      this.dataList.lookups = reduceObj(this.dataList.lookups, 'alias')
      // 初始化已选择的表
      setTimeout(() => {
        this.changeLi(this.dataList.lookups[0], 0)
        this.current = 0
      }, 300)
      // 接收设置表关系的数据
      // 接收已选择的表
      this.$root.eventBus.$on('tableNameActive', _ => {
        this.getActiveChange()
      })
      this.getActiveChange()
    },
    getActiveChange () {
      this.titleData.forEach((item, index) => {
        this.saveSelectFiled.forEach((n, i) => {
          if (item === n.tableName) {
            this.dataList.lookups[index]['isActive'] = 1
          }
        })
      })
    },
    cahngges (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (item, index) {
      this.current = index
      // const parmas = {
      //   dsDataSourceId: 2,
      //   tableName: item.alias
      // }
      // this.$store.dispatch('GetColumnList', parmas).then(res => {
      //   res.data && res.data.map((n, i) => {
      //     n.mode = n.mode ? n.mode : '2'
      //     n.derived = n.columnName
      //     n.tableName = item
      //     n.filed = item === this.dataList.fact_table ? '1' : '0'
      //     n.id = `${item}${i}`
      //   })
      //   // 存储选择对应的表
      //   this.$root.eventBus.$emit('filedTable', res.data, res.code)
      //   // 存储已选择的表
      //   this.$store.dispatch('SaveList', res.data)
      // })
      // kelin
      // console.log(item, '====', this.dataList.fact_table)
      this.$root.eventBus.$emit('filedTable', item, this.dataList.fact_table.split('.')[1])
      // 存储事实表的所有字段
      if (index === 0) {
        this.saveSelectAllList.map((item, index) => {
          // let items = JSON.parse(item)
          if (item.resourceId === this.dataList.lookups[0].id) {
            let list = {
              data: item.column,
              list: this.dataList.lookups[0]
            }
            this.$store.commit('SaveFactData', list)
          }
        })
      }
      // ------------------------- 数据湖
      // this.$store.dispatch('GetResourceInfo', { resourceId: '811937214570250', type: 1 }).then(res => {
      //   let datas = []
      //   res.data.column.forEach(item => {
      //     datas.push(item.columnAlias)
      //   })
      //   let obj = {
      //     params: {
      //       'columnList': datas,
      //       'page': 0,
      //       'size': 0
      //     },
      //     data: {
      //       resourceId: '811937214570250',
      //       type: 1
      //     }
      //   }
      //   this.$store.dispatch('getResourceData', obj).then(res => {
      //     res.data.columnList.map((n, i) => {
      //       n.mode = n.mode ? n.mode : '2'
      //       n.derived = n.name
      //       n.dataType = n.dataType ? n.dataType : 'string'
      //       n.tableName = item.alias ? item.alias : ''
      //       n.filed = item.alias === this.dataList.fact_table ? '1' : '0'
      //       n.id = `${item.alias}${i}`
      //     })
      //     // 存储选择对应的表
      //     this.$root.eventBus.$emit('filedTable', res.data.columnList)
      //   })
      // })
    }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('filedTable')
    this.$root.eventBus.$off('tableNameActive')
  },
  computed: {
    ...mapGetters({
      saveSelectAllList: 'saveSelectAllList',
      saveSelectFiled: 'saveSelectFiled',
      jointResultData: 'jointResultData',
      saveSelectFiledTree: 'saveSelectFiledTree'
    }),
    getFeact () {
      let data = this.dataList.fact_table
      return data.substring(data.indexOf('.') + 1)
    },
    tabaleList () {
      if (this.value) {
        return this.dataList.lookups.filter(t => t.alias.indexOf(this.value) !== -1) || []
      }
      return this.dataList.lookups || []
    }
  }
}
</script>

<style lang="stylus" scoped>
.factTable{
  max-width 230px
  padding 10px
  background #ffffff
  float left
  height calc(100vh - 40px)
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
      .tableTitle{
        width: 70%;
        text-overflow: ellipsis;
        overflow: hidden;
        display: inline-block;
        vertical-align: bottom;
      }
      .filds{
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
