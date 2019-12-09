<template>
  <div class="serchTable">
    <div @click="handleSelect" class="selctNum">已选择：<i>{{selectTableTotal.length || '请选择数据'}}</i></div>
     <el-input type="text" placeholder="请输入关键词" suffix-icon="el-icon-search" v-model="serachvalue" clearable></el-input>
     <div class="trees" ref="treesBox" v-loading="loading">
       <el-scrollbar style="height:100%">
         <el-tree
        ref="trees"
        :data="dataList"
        default-expand-all
        node-key="id"
        :expand-on-click-node='false'
        show-checkbox
        highlight-current
        :filter-node-method="filterNode"
        :default-checked-keys="defaultKey"
        @check-change="handleCheckChange"
        @node-click="handleNodeClick">
        <span class="custom-tree-node" slot-scope="{ node  }">
          <el-tooltip v-if="node.label.length >= 18" class="node__item-tip" effect="dark"
            :content="node.label ? node.label : ''" placement="top" popper-class="my-dep-toolTip">
            <span>{{ node.label ? node.label : '全选' }}</span>
          </el-tooltip>
          <span v-else  class="show-ellipsis">{{node.label}}</span>
        </span>
        </el-tree>
        <!-- <span v-else class="null_data">暂无数据</span> -->
       </el-scrollbar>
        <!-- <p v-if="loading">加载中...</p> -->
     </div>
     <!-- <p v-if="noMore">没有更多了</p> -->
     <!-- 选择 -->
     <select-modal ref="dialog"></select-modal>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { setTimeout } from 'timers'
import { getResourceList } from '@/api/newOlapModel'
import selectModal from '@/components/analysisComponent/createComponent/selectStepComponent/selectModal'


export default {
  name: 'serchTable',
  data () {
    return {
      noMore: false,
      serachvalue: '',
      loading: false,
      defaultKey: [],
      dataList: [{
        id: 9999999999,
        label: '全选',
        children: []
      }]
    }
  },
  components: {
    selectModal
  },
  watch: {
    serachvalue (val) {
      this.$refs.trees.filter(val)
    }
  },
  created () {

  },
  mounted () {
    this.initEvent()
    this.init()
  },
  methods: {
    initEvent () {
      // 弹出框移除数据
      this.$root.eventBus.$on('modal-remove', async data => {
        const checkedNodes = this.$refs.trees.getCheckedNodes()
        const startIndex = checkedNodes.findIndex(t => t.id === data.id)
        // 删除的数据在当前树形列表存在：直接调用tree 自身的setCheckedNodes 去触发vuex 中的数据更新
        if (startIndex > -1) {
          checkedNodes.splice(startIndex, 1)
          this.defaultKey = checkedNodes
          this.$refs.trees.setCheckedNodes(checkedNodes)
        } else {
          // 提交到vuex 中处理(之前是这样处理的，这里也沿用这样的逻辑)
          // 1、删除数据
          // 2、定义一个对象传入当前勾选的状态type和选择的数据
          let list = {
            type: false,
            delData: data
          }
          await this.$store.dispatch('delSelectTableList', list)
          // 合并数据
          await this.$store.dispatch('setSelectTableTotal')
        }
      })
      // 获取资源信息列表
      this.$root.eventBus.$on('getserchTableList', (data) => this.getserchTableList(data))
      // 接收已选择的复选框数据
      this.$root.eventBus.$on('saveSelectTables', _ => {
        this.setDefualtKey()
      })
      // 重置复选框
      this.$root.eventBus.$on('clearSelect', _ => {
        this.$refs.trees.setCheckedKeys([])
      })
    },
    init () {
      // 获取资源信息列表
      // this.$root.eventBus.$on('getserchTableList', (data) => this.getserchTableList(data))
      // this.setDefualtKey()
    },
    setDefualtKey () {
       // 初始化默认勾选框数组 以及树列表的复选框
        this.defaultKey = []
        this.$refs['trees'].setCheckedKeys([])
        // 遍历已经勾选的数据赋值到勾选框数组
        this.selectTableTotal && this.selectTableTotal.forEach(item => { this.defaultKey.push(String(item.resourceId)) })
        // 去重勾选框数组
        this.$nextTick(_ => {
          this.defaultKey = [...new Set(this.defaultKey)]
        })
        // setTimeout(() => {  }, 500)
    },
    // 获取资源列表
    async getserchTableList ({orgId, type, databaseType}) {
      try {
          this.loading = true
          this.dataList[0].children = []
          let params = {
            orgId,
            type,
            databaseType
          }
         const { data } =  await getResourceList(params)
         Array.isArray(data) && data.forEach(t => {
          this.dataList[0].children.push({id: t.resourceId, label: t.resourceTableName, orgId, ...t})
         })
         if (this.$route.query.cubeName) {
           this.setDefualtKey()
         }
        } catch (e) {
        } finally {
          this.loading = false
        }
    },
    // 查看已选择
    handleSelect () {
      if (this.selectTableTotal.length) this.$refs.dialog.dialog()
    },
    // 过滤
    filterNode (value, data) {
      if (!value) return true
       return data.label && data.label.indexOf(value) !== -1
    },
    // 点击节点
    handleNodeClick (data) {
      if (data.label === '全选') return
      this.$root.eventBus.$emit('getTableHeadList', data)
    },
    // 勾选框的选择
    async handleCheckChange (data, type, node) {
      // 拿到当前勾选的数据
      let nodeData = this.$refs.trees.getCheckedNodes()
      // 定义一个对象传入当前勾选的状态type和选择的数据
      let list = {
        type: type,
        delData: data
      }
     if (type) {
        await this.$store.dispatch('getSelectTableList', nodeData)
      } else {
        await this.$store.dispatch('delSelectTableList', list)
      }
      await this.$store.dispatch('setSelectTableTotal')
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      serchTableList: 'serchTableList'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('getTableHeadList')
    this.$root.eventBus.$off('saveSelectTables')
    this.$root.eventBus.$off('modal-remove')
  }
}
</script>

<style lang="stylus" scoped>

.selctNum{
  margin-bottom: 5px;
  cursor: pointer;
      i{
        color #0486FE;
        font-style:normal;
      }
    }
.serchTable{
  background #ffffff
  width 330px
  padding 16px
  font-size 14px
  box-shadow: -5px 0 10px 0 rgba(0,0,0,0.05);
  height 100%
  .trees{
    width: 298px !important;
    height: calc(100% - 68px);
    overflow auto
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
    margin-top -3px
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
    .custom-tree-node {
      display: inline-block;
      width: 85%;
      text-overflow: ellipsis;
      text-overflow: ellipsis;
      overflow: hidden;
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
      min-width: 100%;
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
