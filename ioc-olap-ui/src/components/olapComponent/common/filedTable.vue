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
         <span v-if="index===0">事实表</span>
       </li>
     </ul>
     <div v-else style="margin-top:50px;text-align:center;">暂无数据</div>
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
      colors: 'red',
      titleData: [], // 表名
      // 模拟数据
      dataList: {
        'name': 'bb',
        'description': '',
        'fact_table': 'DS_U_sIDNmLKXM',
        lookups: [{
          'table': 'DS_U_x5OSRKK1c',
          'alias': 'DS_U_1Z2zyQFtD',
          'joinTable': 'KYLIN_SALES',
          'kind': 'LOOKUP',
          'join': {
            'type': 'inner',
            'primary_key': [
              'KYLIN_CAL_DT.CAL_DT'
            ],
            'foreign_key': [
              'KYLIN_SALES.PART_DT'
            ],
            'isCompatible': [
              true
            ],
            'pk_type': [
              'date'
            ],
            'fk_type': [
              'date'
            ]
          }
        },
        {
          'table': 'DS_U_sIDNmLKXM',
          'alias': 'KYLIN_CAL_DT',
          'joinTable': 'DS_U_sXvf440QG',
          'kind': 'LOOKUP',
          'join': {
            'type': 'inner',
            'primary_key': [
              'KYLIN_CAL_DT.CAL_DT'
            ],
            'foreign_key': [
              'KYLIN_SALES.PART_DT'
            ],
            'isCompatible': [
              true
            ],
            'pk_type': [
              'date'
            ],
            'fk_type': [
              'date'
            ]
          }
        },
        {
          'table': 'DS_U_sXvf440QG',
          'alias': 'KYLIN_CAL_DT',
          'joinTable': 'DS_U_BJMxXakML',
          'kind': 'LOOKUP',
          'join': {
            'type': 'inner',
            'primary_key': [
              'KYLIN_CAL_DT.CAL_DT'
            ],
            'foreign_key': [
              'KYLIN_SALES.PART_DT'
            ],
            'isCompatible': [
              true
            ],
            'pk_type': [
              'date'
            ],
            'fk_type': [
              'date'
            ]
          }
        },
        {
          'table': 'DS_U_BJMxXakML',
          'alias': 'KYLIN_CAL_DT',
          'joinTable': 'DS_U_OBkpFiiWr',
          'kind': 'LOOKUP',
          'join': {
            'type': 'inner',
            'primary_key': [
              'KYLIN_CAL_DT.CAL_DT'
            ],
            'foreign_key': [
              'KYLIN_SALES.PART_DT'
            ],
            'isCompatible': [
              true
            ],
            'pk_type': [
              'date'
            ],
            'fk_type': [
              'date'
            ]
          }
        }]
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      // 遍历去重数据拿到表名称
      this.dataList.lookups.map((item, index) => {
        this.titleData.push(item.table, item.joinTable)
      })
      this.titleData = [...new Set(this.titleData)]
      // 初始化已选择的表
      setTimeout(() => { this.changeLi(this.titleData[0], 0) }, 300)
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
          // 存储已选择后的维度表
          // this.$store.dispatch('saveSelectFiledTree', this.dataList.lookups)
        }, 300)
      })
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
        res.data.map((n, i) => {
          n.mode = n.mode ? n.mode : '2'
          n.apiPaths = n.columnName
          n.tableName = item
          n.filed = name
          n.id = `${item}${i}`
        })
        // 存储选择对应的表
        this.$store.dispatch('SaveRightTableList', res.data)
        this.$root.eventBus.$emit('filedTable', res.data, res.code)
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
