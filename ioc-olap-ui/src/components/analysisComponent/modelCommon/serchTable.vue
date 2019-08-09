<template>
  <div class="serchTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <div class="trees">
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
        <span v-if="dataList && dataList[0].children.length < 1" style="width:200px;text-align:center;margin-top:50px;display:block;">暂无数据</span>
     </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
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
      // 接收数据湖传递的信息
      this.$root.eventBus.$on('getserchTableList', (res, type) => {
        this.loading = true
        this.dataList[0].children = []
        // console.log(res)
        /* KELIN */
        if (type && type === 1) {
          this.$store.dispatch('GetThreeList', { orgId: res.orgId }).then(res => { // kelin
            if (res) {
              res.map(res => { this.dataList[0].children.push({ id: res.resourceId, resourceId: res.resourceId, label: res.resourceTableName, database: res.database }) }) // kelin
              this.loading = false
              this.$root.eventBus.$emit('saveSelectTables')
            }
          })
        // if (type && type === 1) {
        //   this.$store.dispatch('GetThreeList', { orgId: res.orgId, type: res.type, databaseType: res.databaseType }).then(res => {
        //     if (res.code === 200) {
        //       res.data.map(res => { this.dataList[0].children.push({ id: res.resourceCode, resourceId: res.resourceId, label: res.resourceTableName }) })
        //       this.loading = false
        //     }
        //   })
        } else {
          this.serchTableList.map(res => { this.dataList[0].children.push({ id: res.resourceId, resourceId: res.resourceId, label: res.resourceTableName }) })
          setTimeout(() => { this.loading = false }, 300)
        }
      })
      // 接收本地上传传递的信息
      this.$root.eventBus.$on('getUploadTable', res => {
        this.dataList[0].children = []
        this.loading = true
        this.$store.dispatch('GetdsUploadTable').then(res => {
          if (res.code === 200) {
            this.loading = false
            res.rows.map(res => {
              this.dataList[0].children.push({
                id: res.dsUploadTableId,
                label: res.tableCode
              })
            })
            if (this.dataList[0].children.length < 1) {
              this.$refs.trees.setCheckedKeys([])
            }
            this.$root.eventBus.$emit('saveSelectTables')
          }
        })
      })
      // 接收已选择的复选框数据
      this.$root.eventBus.$on('saveSelectTables', _ => {
        this.defaultKey = []
        this.$refs.trees.setCheckedKeys([])
        if (this.$store.state.selectStep.searchType === 1) {
          this.saveSelctchckoutone.map(item => { this.defaultKey.push(item.id) })
        } else {
          this.saveSelctchckouttwo.map(item => { this.defaultKey.push(item.id) })
        }
        setTimeout(() => {
          this.defaultKey = [...new Set(this.defaultKey)]
        }, 500)
      })
      // 重置复选框
      this.$root.eventBus.$on('clearSelect', _ => {
        this.$refs.trees.setCheckedKeys([])
      })
    },
    handleNodeClick (value) {
      if (value.label === '全选') return
      let searchType = this.$store.state.selectStep.searchType
      if (searchType === 1) {
        // this.$store.dispatch('GetResourceInfo', { resourceId: value.resourceId, type: searchType }).then(res => {
        //   let datas = []
        //   let columnData = res.data.column // 子段说明
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
        //       resourceId: value.resourceId,
        //       type: searchType
        //     }
        //   }
        //   this.$store.dispatch('getResourceData', obj).then(res => {
        //     this.$root.eventBus.$emit('getTabdataList', res, columnData)
        //   })
        // })
        // 模拟数据kelin
        this.$store.dispatch('GetResourceInfo', { resourceId: value.resourceId }).then(res => {
          this.$root.eventBus.$emit('klinFetchData', res.data.columns)
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
        // this.$store.dispatch('GetColumnList', parmas).then(data => {
        //   if (data.code === 200) {
        //     this.$root.eventBus.$emit('getLocalTableHeadList', data)
        //   }
        // })
        // this.$store.dispatch('GetTableData', valparams).then(res => {
        //   if (res.code === 200) {
        //     this.$root.eventBus.$emit('getLocalTableContentList', res)
        //   }
        // })
      }
    },
    // 勾选框的选择
    handleCheckChange (data, type, node) {
      if (this.$store.state.selectStep.searchType === 1) {
        let list = {
          type: type,
          delData: data
        }
        if (type === true) {
          this.$store.dispatch('getSelectTableList', this.$refs.trees.getCheckedNodes())
        } else {
          this.$store.dispatch('delSelectTableList', list)
        }
      } else {
        this.$store.dispatch('getLocalSelectTableList', this.$refs.trees.getCheckedNodes())
      }
      // this.$store.state.selectStep.searchType === 1
      //   ? this.$store.dispatch('getSelectTableList', this.$refs.trees.getCheckedNodes())
      //   : this.$store.dispatch('getLocalSelectTableList', this.$refs.trees.getCheckedNodes())
      // 设置已选择的数据表的数量
      this.$store.dispatch('setSelectTableTotal')
    }
  },
  computed: {
    ...mapGetters({
      treeList: 'treeList',
      saveSelectTable: 'saveSelectTable',
      saveSelctchckoutone: 'saveSelctchckoutone',
      saveSelctchckouttwo: 'saveSelctchckouttwo',
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
  width 230px
  float left
  padding 0 25px
  border-right 1px solid #f0f0f0
  height calc(100vh - 100px)
  .trees{
    height calc(100vh - 300px)
    overflow-y auto
    width 200px
  }
  >>>.el-radio{
    display block
    height 30px
    line-height 30px
  }
  >>>.el-input{
    height 50px
    .el-input__inner{
      height 30px
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
    }
  }
  >>>.el-tree-node__children{
    .el-tree-node__content{
      padding-left 0!important
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
