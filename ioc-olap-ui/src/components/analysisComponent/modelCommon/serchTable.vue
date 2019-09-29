<template>
  <div class="serchTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <div class="trees" ref="treesBox">
      <el-tree
        ref="trees"
        :data="dataList"
        default-expand-all
        node-key="id"
        v-loading="loading"
        :expand-on-click-node='false'
        show-checkbox
        :default-checked-keys="defaultKey"
        @check-change="handleCheckChange"
        @node-click="handleNodeClick">
        </el-tree>
        <span v-if="dataList && dataList[0].children.length < 1" style="width:200px;position:absolute;text-align:center;top:150px;">暂无数据</span>
     </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { reduceObj } from '@/utils/index'
import { setTimeout } from 'timers'
export default {
  data () {
    return {
      value: '',
      loading: false,
      defaultKey: [],
      dataList: [{
        // id: 1,
        label: '全选',
        children: []
      }]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      // 判断是否有数据 如果没有数据的话就需改变树的高度
      // 接收数据湖传递的信息
      this.$root.eventBus.$on('getserchTableList', (res, type) => {
        this.loading = true
        this.dataList[0].children = []
        let orgId = res.orgId
        /* KELIN */
        if (type && type === 1) {
          this.$store.dispatch('GetThreeList', { orgId: orgId }).then(res => { // kelin
            if (res) {
              res.map(res => { this.dataList[0].children.push({ id: res.resourceId, orgId: orgId, resourceId: res.resourceId, label: res.resourceTableName, database: res.database }) }) // kelin
              this.loading = false
              this.$root.eventBus.$emit('saveSelectTables')
            }
          })
        // if (type && type === 1) {
        //   this.$store.dispatch('GetThreeList', { orgId: orgId, type: res.type, databaseType: res.databaseType }).then(res => {
        //     if (res.code === 200) {
        //       res.data.map(res => { this.dataList[0].children.push({ id: res.resourceCode, resourceId: res.resourceId, label: res.resourceTableName }) })
        //       this.loading = false
        //       this.$root.eventBus.$emit('saveSelectTables')
        //     }
        //   })
        } else {
          this.serchTableList.map(res => { this.dataList[0].children.push({ id: res.resourceId, resourceId: res.resourceId, label: res.resourceTableName }) })
          setTimeout(() => { this.loading = false }, 300)
        }
      })
      // 接收本地上传传递的信息
      this.$root.eventBus.$on('getUploadTable', res => {
        // 初始化列表树
        this.dataList[0].children = []
        this.loading = true
        // 获取本地上传的数据
        this.$store.dispatch('GetdsUploadTable').then(res => {
          if (res.code === 200) {
            this.loading = false
            // 将获取的数据绑定到列表树上
            res.rows.map(res => {
              this.dataList[0].children.push({
                id: res.dsUploadTableId,
                label: res.tableCode
              })
            })
            // 判断是否存在已勾选的本地上传数据
            if (this.saveLocalSelectTable.length < 1) {
              setTimeout(() => { this.$refs.trees.setCheckedKeys([]) })
            }
            // 触发复选框的方法
            this.$root.eventBus.$emit('saveSelectTables')
          }
        })
      })
      // 接收已选择的复选框数据
      this.$root.eventBus.$on('saveSelectTables', _ => {
        // 初始化默认勾选框数组 以及树列表的复选框
        this.defaultKey = []
        this.$refs.trees.setCheckedKeys([])
        // 遍历已经勾选的数据赋值到勾选框数组
        this.selectTableTotal.map(item => { this.defaultKey.push(item.id) })
        // 去重勾选框数组
        setTimeout(() => { this.defaultKey = [...new Set(this.defaultKey)] }, 500)
      })
      // 重置复选框
      this.$root.eventBus.$on('clearSelect', _ => {
        this.$refs.trees.setCheckedKeys([])
      })
    },
    handleNodeClick (value) {
      console.log(value)
      if (value.label === '全选') return
      this.$refs.treesBox.style.height = this.dataList[0].children.length > 0 ? '80%' : 'auto'
      let searchType = this.$store.state.selectStep.searchType
      if (searchType === 1) {
        /** 数据湖 */
        // this.$store.dispatch('GetResourceInfo', { resourceId: value.resourceId, type: searchType }).then(res => {
        //   let datas = []
        //   let columnData = res.data.column // 子段说明
        //   res.data.column.forEach(item => {
        //     datas.push(item.id)
        //   })
        //   let obj = {
        //     params: {
        //       'columnIdList': datas,
        //       'page': 0,
        //       'size': 0
        //     },
        //     data: {
        //       resourceId: value.resourceId,
        //       type: searchType
        //     }
        //   }
        //   this.$store.dispatch('getResourceData', obj).then(res => {
        //     this.$root.eventBus.$emit('getTabdataList', res, columnData)
        //   })
        // })
        // 模拟数据kelin
        this.$store.dispatch('GetResourceInfo', value.resourceId).then(res => {
          this.$root.eventBus.$emit('klinFetchData', JSON.parse(res).data.columns)
        })
      } else {
        const parmas = {
          dsDataSourceId: 2,
          tableName: value.label
        }
        const valparams = {
          dbType: 3,
          dsDataSourceId: 2,
          tableName: value.label
        }
        this.$root.eventBus.$emit('getLocalTableHeadList', parmas)
        this.$root.eventBus.$emit('getLocalTableContentList', valparams)
      }
    },
    // 勾选框的选择
    handleCheckChange (data, type, node) {
      // 拿到当前勾选的数据
      let nodeData = this.$refs.trees.getCheckedNodes()
      // 定义一个对象传入当前勾选的状态type和选择的数据
      let list = {
        type: type,
        delData: data
      }
      // ${this.$store.state.selectStep.searchType} 根据这个来判断是本地上传还是数据湖
      if (this.$store.state.selectStep.searchType === 1) {
        // 判断是否是勾选 勾选的话就存储这条数据
        if (type) {
          this.$store.dispatch('getSelectTableList', nodeData)
        } else {
        // 否则就要删除这条对应的数据
          this.$store.dispatch('delSelectTableList', list)
        }
      } else if (this.$store.state.selectStep.searchType === 2) {
        if (type) {
          this.$store.dispatch('getLocalSelectTableList', nodeData)
        } else {
          this.$store.dispatch('delLocalSelectTableList', list)
        }
      }
      this.$store.dispatch('setSelectTableTotal')
    }
  },
  computed: {
    ...mapGetters({
      treeList: 'treeList',
      saveSelectTable: 'saveSelectTable',
      selectTableTotal: 'selectTableTotal',
      serchTableList: 'serchTableList',
      saveLocalSelectTable: 'saveLocalSelectTable'

    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('getTableHeadList')
    this.$root.eventBus.$off('getTabdataList')
    this.$root.eventBus.$off('getLocalTableHeadList')
    this.$root.eventBus.$off('getTableContentList')
    this.$root.eventBus.$off('getLocalTableContentList')
    this.$root.eventBus.$off('saveSelectTables')
    this.$root.eventBus.$off('klinFetchData')
  }
}
</script>

<style lang="stylus" scoped>
.serchTable{
  background #ffffff
  width 240px
  float left
  padding 16px
  box-shadow: -5px 0 10px 0 rgba(0,0,0,0.05);
  height 98%
  .trees{
    width 240px
    height 85%
    overflow auto
    padding-right 30px
  }
  >>>.el-tree{
    height 100%
    width 100%
    overflow auto
    padding-top 16px
  }
  >>>.el-radio{
    display block
    height 30px
    line-height 30px
  }
  >>>.el-input{
    .el-input__inner{
      height 32px
    }
  }
  >>>.el-input__suffix{
    margin-top -8px
  }
  >>>.el-radio-group{
    height 90%
    width 230px
    overflow-y auto
  }
  >>>.el-tree-node__content{
    .el-tree-node__expand-icon::before{
      content: ''
      margin-top -100px
    }
  }
  >>>.expanded{
    margin-top:-100px!important;
  }
  >>>.el-tree-node__children{
    display inline
    .el-tree-node__content{
      padding-left 0!important
    }
    .el-tree-node{
      min-width: 120%;
    }
  }
}
.trees::-webkit-scrollbar{
  width 8px
  height 8px
  background #fff
}
.trees::-webkit-scrollbar-track{
  -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
  border-radius: 10px;
  background-color:#fff;
}
.trees::-webkit-scrollbar-thumb{
  border-radius: 10px;
  -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
  background-color: #f0f0f0;
}
</style>
