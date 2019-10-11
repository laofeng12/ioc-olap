<template>
  <div class="mechanism folderAside">
    <el-input type="text" placeholder="输入机构名称筛选" v-model="serachvalue" clearable></el-input>
    <div class="trees">
      <el-tree
      :data="treeList"
        ref="tree"
        v-loading="treeLoading"
        auto-expand-parent
        :expand-on-click-node="false"
        node-key="id"
        @node-expand="nodeExpand"
        @node-click="getCurrents"
        highlight-current
        :default-expanded-keys="defaultOpenKeys"
        :render-content="renderContent"
        :filter-node-method="filterNode"
        :props="defaultProps">
      </el-tree>
    </div>
  </div>
</template>

<script>
import { setTimeout } from 'timers'
import { mapGetters } from 'vuex'
import { getselectCatalog } from '@/api/olapModel'
import { getLocalStorage } from '@/utils/index'
export default {
  data () {
    return {
      treeLoading: false,
      showTree: true,
      serachvalue: '',
      treeList: [
        {
          label: '数据湖',
          id: '1337',
          children: []
        },
        {
          label: '自建目录',
          id: '1338',
          children: []
        }
      ],
      defaultOpenKeys: [], // 默认展开的key
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  watch: {
    serachvalue (val) {
      this.$refs.tree.filter(val)
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      // 默认选择第一行
      // let ModelAllList = this.ModelAllList.length > 0 ? this.ModelAllList : JSON.parse(getLocalStorage('ModelAllList'))
      // let serchTableList = this.serchTableList.length > 0 ? this.serchTableList : JSON.parse(getLocalStorage('serchTableList'))
      // console.log('刷新后的', serchTableList)
      // 判断是否是刷新过后的
      // if (this.serchTableList.length < 1 && JSON.parse(getLocalStorage('ModelAllList'))) {
      //   setTimeout(() => {
      //     this.$root.eventBus.$emit('getserchTableList', serchTableList, 1)
      //   }, 500)
      // }
      if (this.$route.query.cubeName) {
        setTimeout(() => { this.$root.eventBus.$emit('getserchTableList', { orgId: this.ModelAllList.TableList[0].orgId }, 1) }, 1000)
      }
      this.$root.eventBus.$on('openDefaultTree', res => {
        setTimeout(() => {
          this.$root.eventBus.$emit('getserchTableList', this.serchTableList)
          this.$store.dispatch('changeSerachtype', 1)
          this.$root.eventBus.$emit('saveSelectTables')
        }, 1000)
      })
    },
    fetchTreeList (val) {
      this.treeLoading = true
      // this.fetchDatas(val)
      this.fetchKelinData()
    },
    fetchDatas (val) {
      /** 数据湖 */
      this.$store.dispatch('GetTreeList').then(res => {
        if (res && res.code === 200) {
          this.treeLoading = false
          this.setTree(res.data.dataLakeDirectoryTree, 1)
          this.setTree(res.data.dataSetDirectoryTree, 2)
          const ids = val || this.treeList[0].id
          const newids = ids.length > 10 ? this.treeList[0].id : val
          newids && setTimeout(() => {
            this.$refs.tree.store.nodesMap[newids].expanded = true
          }, 500)
          this.defaultFrist(this.treeList)
        }
      }).finally(() => {
        this.treeLoading = false
      })
    },
    fetchKelinData () {
      // kelin测试
      getselectCatalog().then(res => {
        this.treeLoading = false
        res.map(res => {
          res.label = res.orgName
          res.id = res.orgId
        })
        this.treeList = res
      })
    },
    // 默认点击第一项的递归计算
    defaultFrist (val) {
      // console.log(val)
    },
    setTree (val, type) {
      let item = []
      val && val.map((list, i) => {
        let newData = {}
        newData.label = list.orgName
        newData.databaseType = list.databaseType
        newData.orgId = list.orgId
        newData.resNum = list.resNum
        newData.id = list.id
        newData.parentId = list.parentId
        newData.is_show_add = true
        newData.isLeaf = list.isLeaf
        newData.type = list.type
        newData.orgtype = list.orgtype
        newData.isTable = list.isTable
        // newData.children = list.children ? this.setTree(list.children) : []
        newData.children = list.isLeaf === true ? [] : [{}]
        item.push(newData)
      })
      type === 1 ? this.treeList[0].children = item : this.treeList[1].children = item
      return item
    },
    filterNode (value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    renderContent (h, { node, data, store }) {
      return h('span', {
        class: `tree${node.id}`
      }, [
        h('span', {
        }, node.label)
      ])
    },
    // 展开列表
    nodeExpand (data, node, me) {
      if (!data.orgId) return
      this.fetchTree(data, node)
    },
    // 选中对应的表
    getCurrents (data, node, me) {
      if (data.isLeaf === true) {
        if (data.databaseType !== '1') {
          this.$message.warning('暂只支持HIVE类型数据查询')
        } else {
          this.fetchResourceList(data, node.parent.key)
        }
      }
      // 为资源列表的时候
      if (!data.isTable && data.resNum > 0) {
        // this.fetchResourceList(data)
      }
      // kelin
      let lastId = node.parent.label ? node.parent.key : node.key
      this.fetchResourceList(data, lastId)
      // 保存数据到store
      this.$store.dispatch('SaveSelectData', data)
    },
    fetchTree (data) {
      this.treeLoading = true
      this.$store.dispatch('GetTreeTwoList', { orgId: data.orgId, databaseType: data.databaseType }).then(res => {
        if (res.code === 200) {
          this.treeLoading = false
          res.data.map(res => {
            res.label = res.orgName
            res.children = res.isLeaf === true || res.isTable === true ? [] : [{}]
          })
          if (res.data.length) data.children = res.data
        }
      }).finally(() => {
        this.treeLoading = false
      })
    },
    fetchResourceList (data, nodeId) {
      this.$root.eventBus.$emit('getserchTableList', data, 1)
      // 点击时清除其他选择框
      this.$root.eventBus.$emit('clearSelect')
      // 存储当前点击的父节点的id
      this.$store.dispatch('setLastClickTab', nodeId)
      // 保存选择的数据源数据
    },
    fetchResourceInfo (data, nodeId) {
      this.$store.dispatch('GetResourceInfo', { resourceId: data.orgId, type: data.type }).then(res => {
        if (res.code === 200) {
          this.$root.eventBus.$emit('getserchTableList', res)
          // 点击时清除其他选择框
          this.$root.eventBus.$emit('clearSelect')
          // 存储当前点击的父节点的id
          this.$store.dispatch('setLastClickTab', nodeId)
        }
      })
    }
    // fetchTree (id, nodeId) {
    //   this.$store.dispatch('GetSerchTable', id).then(res => {
    //     if (res.code === 200) {
    //       this.$root.eventBus.$emit('getserchTableList', res)
    //       // 点击时清除其他选择框
    //       this.$root.eventBus.$emit('clearSelect')
    //       // 存储当前选择数据源
    //       this.$store.dispatch('setSerchTable', res)
    //       // 存储当前点击的父节点的id
    //       this.$store.dispatch('setLastClickTab', nodeId)
    //       // 保存选择的数据源数据
    //     }
    //   })
    // }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      saveLocalSelectTable: 'saveLocalSelectTable',
      ModelAllList: 'ModelAllList',
      lastClickTab: 'lastClickTab',
      mockjsonData: 'mockjsonData', // 模拟数据
      serchTableList: 'serchTableList'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('clearSelect')
    this.$root.eventBus.$off('saveSelectTables')
  },
  created () {
    this.fetchTreeList(this.lastClickTab)
    this.$nextTick(function () {
      this.$refs.tree.setCurrentKey(this.lastClickTab)
    })
  }
}
</script>

<style lang="stylus" scoped>
  .mechanism{
    background #ffffff
    height 100%
    .trees{
      width 208px
      height 85%
      // overflow auto
    }
    >>>.el-tree{
      height 100%
      width 100%
      overflow auto
    }
    >>>.el-input__suffix{
      margin-top -10px
    }
    >>>.el-tree{
    .el-tree-node__content{
      font-size: 14px;
      height 25px
      line-height 25px
      color: rgba(0,0,0,0.85);
    }
    .el-tree-node__children{
      .el-tree-node__content{
      font-size: 12px;
      color: rgba(0,0,0,0.65)!important;
      }
    }
  }
  >>>.el-loading-spinner{
    width 100%
    top 30%
    .circular{
      width 32px
      height 32px
    }
  }
    >>>.el-input{
      .el-input__inner{
        height 32px
      }
    }
    >>>.is-leaf{
      background none
      width 0
      height 0
      color transparent
      cursor default
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
    background-color: #B5D2DE;
  }
}
</style>
