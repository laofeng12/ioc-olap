<template>
  <div class="mechanism">
    <el-input type="text" placeholder="输入机构名称筛选" v-model="value" clearable></el-input>
    <div class="trees">
      <el-tree
        :data="treeList"
        ref="tree"
        v-loading="treeLoading"
        :expand-on-click-node="false"
        node-key="id"
        @node-click="getCurrents"
        :render-content="renderContent"
        :filter-node-method="filterNode"
        :props="defaultProps">
      </el-tree>
    </div>
  </div>
</template>

<script>
import { getMechanismTree } from '@/api/common'
export default {
  data () {
    return {
      treeLoading: false,
      value: '',
      resultList: [],
      treeList: [],
      resetNodeList: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  watch: {
    value (val) {
      this.$refs.tree.filter(val)
    }
  },
  methods: {
    fetchTreeList () {
      try {
        this.treeLoading = true
        getMechanismTree().then(res => {
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
        newData.label = list.orgName
        newData.id = list.orgId
        newData.pId = list.parentId
        newData.is_show_add = true
        newData.children = list.subOrgList ? this.setTree(list.subOrgList) : []
        item.push(newData)
      })
      this.treeList = item
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
    height 98%
    width 20%
    .trees{
      height 100%
      overflow-y auto
    }
    >>>.el-tree{
      min-width 500px
      overflow: initial
    }
    >>>.el-loading-spinner{
      width 35%
    }
    >>>.el-tree__empty-block{
      width 35%
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
    >>>.el-input{
      // width 300px
      height 50px
      .el-input__inner{
        height 30px
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
