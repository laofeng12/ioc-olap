<template>
  <div class="app-menu mechanism">
    <!-- <div class="tag"></div> -->
    <el-tree
      :data="treeList"
      ref="tree"
      v-loading="treeLoading"
      :expand-on-click-node="false"
      node-key="id"
      @node-click="getCurrents"
      :render-content="renderContent"
      :props="defaultProps">
    </el-tree>
  </div>
</template>

<script>
export default {
  data () {
    return {
      treeLoading: false,
      resultList: [],
      treeList: [],
      resetNodeList: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  methods: {
    fetchTreeList () {
      try {
        this.treeLoading = true
        this.$api.getMechanismTree().then(res => {
          this.setTree(res.resources)
          this.treeLoading = false
          setTimeout(() => {
            this.$refs.tree.store.nodesMap[this.treeList[0].id].expanded = true
          }, 500)
        })
      } catch (error) {
        this.$message.error(error || '服务异常')
      }
    },
    setTree (val) {
      let item = []
      val.map((list, i) => {
        let newData = {}
        newData.label = this.getTypeText(list.orgtype) ? `【${this.getTypeText(list.orgtype)}】` + list.orgName : list.orgName
        newData.id = list.orgId
        newData.pId = list.parentId
        newData.is_show_add = true
        newData.children = list.subOrgList ? this.setTree(list.subOrgList) : []
        item.push(newData)
      })
      this.treeList = item
      return item
    },
    getTypeText (type) {
      switch (type) {
        case '002001001':
          return '部'
        case '002001002':
          return '科'
        case '002001003':
          return '组'
        default:
          return ''
      }
    },
    renderContent (h, { node, data, store }) {
      return h('span', {
        class: `tree${node.id}`
      }, [
        h('span', {
        }, node.label)
      ])
    },
    // 选中当前修改
    getCurrents (data, node, me) {
      this.resetNode(node)
      if (data.pId) {
        this.$root.eventBus.$emit('giveMechanismId', data, this.resetNodeList)
      }
    },
    // 获取父节点的label
    resetNode (val) {
      this.resetNodeList = []
      if (val.parent && val.parent.parent !== null) {
        this.resetNode(val.parent)
        this.resetNodeList.push(val.label)
      } else {
        this.resetNodeList.push(val.label)
      }
    }
  },
  // 防止兄弟组件多次通信
  beforeDestroy () {
    this.$root.eventBus.$off('giveMechanismId')
  },
  created () {
    this.fetchTreeList()
  }
}
</script>

<style lang="stylus" scoped>
.mechanism{
  >>>.el-tree-node__content{
    .tag{
        // width:300px;
        // height:50px;
        padding 5px 10px
        border:1px solid #f0f0f0;
        position:relative;
        background-color:#FFF;
        z-index 1000
        position absolute
        top -15px
    }
    .tag:before,.tag:after{
        content:"";display:block;
        border-width:10px;
        position:absolute; bottom:-20px;
        left:50%;
        transform translateX(-50%)
        border-style:solid dashed dashed;
        border-color:#f0f0f0 transparent transparent;
        font-size:0;
        line-height:0;
    }
    .tag:after{
        bottom:-18px;
        border-color:#FFF transparent transparent;
    }
  }
  >>>.el-tree-node__children{
    padding-right 10px
  }
  >>>.el-loading-spinner{
    width 35%
  }
  >>>.el-tree{
  .el-tree-node__content{
    font-size: 18px;
    height 35px
    line-height 35px
    color: rgba(0,0,0,0.85);
  }
  .el-tree-node__children{
    .el-tree-node__content{
    font-size: 16px;
    color: rgba(0,0,0,0.65)!important;
    }
  }
}
>>>.el-tree-node__expand-icon{
  background url('../../../assets/img/treeAdd.png') no-repeat
  width 18px
  height 18px
  background-size 100% 100%
  margin-right 3px
  transform rotate(0)
}
>>>.expanded{
  background url('../../../assets/img/treeRduce.png') no-repeat
  width 18px
  height 18px
  background-size 100% 100%
  margin-right 3px
  transform rotate(0)
}
>>>.el-tree-node__expand-icon::before{
  content ''
}
>>>.is-leaf{
  background none
  width 0
  height 0
  color transparent
  cursor default
}
}
</style>
