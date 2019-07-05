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
        :highlight-current="showTree"
        @node-click="getCurrents"
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
export default {
  data () {
    return {
      treeLoading: false,
      showTree: true,
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
      this.treeLoading = true
      this.$store.dispatch('GetTreeList').then(res => {
        if (res && res.code === 200) {
          this.treeLoading = false
          this.setTree(res.list)
          setTimeout(() => {
            this.$refs.tree.store.nodesMap[this.treeList[0].id].expanded = true
          }, 500)
          // 默认传递
          this.defaultFrist(this.treeList)
        }
      })
    },
    // 默认点击第一项的递归计算
    defaultFrist (val) {
      // console.log(val)
    },
    setTree (val) {
      let item = []
      val.map((list, i) => {
        let newData = {}
        newData.label = list.rdcAme
        newData.id = list.rdcId
        newData.pId = list.prdcid
        newData.is_show_add = true
        newData.children = list.children ? this.setTree(list.children) : []
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
      if (data.pId === 0) return
      this.$store.dispatch('GetSerchTable', data.id).then(res => {
        if (res.code === 200) {
          this.$root.eventBus.$emit('getserchTableList', res, 1)
        }
        // 存储当前选择数据源
        this.$store.dispatch('setSerchTable', res)
      })
    },
    setCurrentKey (val) {
      console.log(val)
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
  },
  created () {
    this.fetchTreeList()
  }
}
</script>

<style lang="stylus" scoped>
  .mechanism{
    height 98%
    width 200px
    height calc(100vh - 150px)
    border-right 1px solid #f0f0f0
    .trees{
      width 198px
      height 100%
      overflow-y auto
    }
    >>>.el-tree{
      // min-width 500px
      overflow: initial
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
