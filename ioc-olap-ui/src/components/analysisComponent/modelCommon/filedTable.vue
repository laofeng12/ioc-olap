<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <!-- <ul v-if="titleData && titleData.length">
       <li v-for="(item, index) in titleData"
        :class="item.isActive===1?'actives':''"
        :style="{color: current===index?colors:''}"
        :key="index" @click="changeLi(item, index)">
         <i class="el-icon-date" style="margin-right:3px;"></i>
         {{titleData[index]}}
         <span v-if="titleData[index]===getFeact">事实表</span>
       </li>
     </ul> -->
     <ul v-if="dataList.lookups && dataList.lookups.length">
       <li v-for="(item, index) in dataList.lookups"
        :class="item.isActive===1?'actives':''"
        :style="{color: current===index?colors:''}"
        :key="index" @click="changeLi(item, index)">
         <i class="el-icon-date" style="margin-right:3px;"></i>
         <span class="tableTitle">{{titleData[index]}}</span>
         <span class="filds" v-if="titleData[index]===dataList.fact_table">事实表</span>
       </li>
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
  mounted () {
    this.init()
  },
  methods: {
    init () {
      // this.dataList = this.saveLeftFiled // 静态数据
      this.dataList = this.jointResultData
      this.dataList.lookups.map((item, index) => {
        this.titleData.push(item.alias)
        if (this.dataList.fact_table.substring(this.dataList.fact_table.indexOf('.') + 1) === item.joinTable) {
          this.ids = item.id
          this.primary_key = item.join.foreign_key
        }
      })
      let factData = {
        alias: this.dataList.fact_table,
        joinId: this.ids,
        joinTable: this.dataList.fact_table.substring(this.dataList.fact_table.indexOf('.') + 1),
        join: {
          primary_key: this.primary_key
        }
      }
      this.dataList.lookups = [factData, ...this.dataList.lookups]
      this.titleData = [...new Set([this.dataList.fact_table, ...this.titleData])]
      this.dataList.lookups = reduceObj(this.dataList.lookups, 'alias')
      // 初始化已选择的表
      setTimeout(() => {
        this.changeLi(this.dataList.lookups[0], 0)
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
      this.$root.eventBus.$emit('filedTable', item, this.dataList.fact_table)
      // 存储事实表的所有字段
      if (index === 0) {
        this.saveSelectAllList.map((item, index) => {
          let items = JSON.parse(item)
          if (items.resourceId === this.dataList.lookups[0].joinId) {
            let list = {
              data: items.data.columns,
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
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectAllList: 'saveSelectAllList',
      saveSelectFiled: 'saveSelectFiled',
      jointResultData: 'jointResultData',
      saveLeftFiled: 'saveLeftFiled',
      saveSelectFiledTree: 'saveSelectFiledTree'
    }),
    getFeact () {
      let data = this.dataList.fact_table
      return data.substring(data.indexOf('.') + 1)
    }
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
      .tableTitle{
        width: 70%;
        text-overflow: ellipsis;
        overflow: hidden;
        display: inline-block;
        vertical-align: bottom;
      }
      .filds{
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
