<template>
  <div class="app-menu">
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
// let id = 1000
export default {
  data () {
    return {
      treeLoading: false,
      resultList: [],
      treeList: [],
      resetNodeList: [], // 当前以及parent标签名
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  mounted () {
    this.$root.eventBus.$on('saveok', data => {
      this.fetchTreeList()
    })
  },
  methods: {
    fetchTreeList () {
      try {
        this.treeLoading = true
        this.$api.getNewResourseTree().then(res => {
          if (res.code === 200) {
            // this.resetTree(res.rows)
            this.setTree(res.resources)
            this.treeLoading = false
            setTimeout(() => {
              this.$refs.tree.store.nodesMap[this.treeList[0].id].expanded = true
            }, 500)
          }
        })
      } catch (error) {
        this.$message.error(error || '服务异常')
      }
    },
    // 循环tree树
    resetTree (val) {
      let list = val || []
      let result = []
      let hashTable = {}
      for (let i = 0; i < list.length; i++) {
        let label = list[i]
        let id = parseInt(label.id)
        let parentId = parseInt(label.pId)

        hashTable[id] = label
        label.children = []

        if (parentId === 0) {
          result.push(label)
        } else {
          let parent = hashTable[parentId]
          if (!parent) {
            // console.log('这个label找不到父级', label.id)
          }
          parent && parent.children.push(label)
        }
        this.setTree(result)
      }
    },
    setTree (val) {
      let item = []
      val.map((list, i) => {
        let newData = {}
        newData.label = list.resName
        newData.id = list.resId
        newData.pId = list.parentResId
        newData.is_show_add = false
        newData.is_show_del = false
        newData.children = list.subResList ? this.setTree(list.subResList) : []
        item.push(newData)
      })
      this.treeList = item
      return item
    },
    edit (data, node) {
      this.getCurrents(data, node)
    },
    append (data, node) {
      this.resetNode(node)
      this.$root.eventBus.$emit('giveId', data, 1, this.resetNodeList)
    },
    remove (node, data) {
      this.$confirm('是否删除该资源?', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        try {
          const params = {
            id: data.id
          }
          this.$api.deleteResourseTree(params).then(res => {
            if (res.code === 200) {
              this.$message.success('删除成功')
              const parent = node.parent
              this.$store.dispatch('GetMenuList')
              const children = parent.data.children || parent.data
              const index = children.findIndex(d => d.id === data.id)
              children.splice(index, 1)
            }
          })
        } catch (error) {
          this.$message.error(error || '服务异常')
        }
      })
    },
    renderContent (h, {
      node,
      data,
      store
    }) {
      return h('span', {
        // 这里添加hover事件
        on: {
          'mouseenter': () => {
            data.is_show_add = true
            data.is_show_del = true
          },
          // 鼠标离开
          'mouseleave': () => {
            data.is_show_add = false
            data.is_show_del = false
          }
        }
      }, [
        h('span', {
          // 显示名称
        }, node.label),
        h('span', {
          style: {
            display: data.is_show_add ? '' : 'none'
          }
        }, [
          // 编辑
          h('el-button', {
            props: {
              type: 'text',
              size: 'small'
            },
            style: {
              display: data.pId === '0' ? 'none' : '',
              marginLeft: '15px'
            },
            on: {
              click: (e) => {
                this.edit(data, node)
                e.stopPropagation()
              }
            }
          }, '编辑'),
          h('el-button', {
            props: {
              type: 'text',
              size: 'small'
            },
            style: {
              display: (data.children.length > 0 && data.pId !== '0') || data.pId === '0' ? 'none' : '',
              marginLeft: '15px'
            },
            on: {
              click: (e) => {
                this.remove(node, data)
                e.stopPropagation()
              }
            }
          }, '删除'),
          // 添加
          h('el-button', {
            props: {
              type: 'text',
              size: 'small'
            },
            style: {
              marginLeft: '15px'
            },
            on: {
              click: (e) => {
                this.append(data, node)
                e.stopPropagation()
              }
            }
          }, '新增')
        ])
      ])
    },
    // 选中当前修改
    getCurrents (data, node, me) {
      this.resetNode(node)
      data.pId !== '0' && this.$root.eventBus.$emit('giveId', data, null, this.resetNodeList)
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
    this.$root.eventBus.$off('giveId')
  },
  created () {
    this.fetchTreeList()
  }
}
</script>

<style lang="stylus" scoped>
>>>.el-tree{
  .el-tree-node__content{
    font-size: 18px;
    height 35px
    line-height 35px
    color: rgba(0,0,0,0.85);
  }
  .el-tree-node__children{
    padding-bottom 50px
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
</style>
