<template>
  <div class="serchTable">
    <div @click="handleSelect" class="selctNum">已选择：<i>{{selectTableTotal.length || '请选择数据'}}</i></div>
     <el-input type="text" placeholder="请输入关键词" suffix-icon="el-icon-search" v-model.trim="serachvalue" clearable :maxlength="20"></el-input>
     <div class="trees" ref="treesBox" v-loading="loading">
       <el-scrollbar style="height:100%">
           <el-table
              empty-text="无搜索结果，请重新输入关键字"
              ref="tableSearchList"
              :data="tableList"
              tooltip-effect="dark"
              style="width: 100%"
              current-row-key="resourceId"
              :reserve-selection="true"
              row-key='resourceId'
              @row-click="handleRowClick"
              @select="handleSelectRow"
              @selection-change="handleSelectionChange">
              <el-table-column
                type="selection"
                width="40"
                align="center"
                :class-name="!isShow ? 'thlf' : ''"
                :reserve-selection="true"
                :selectable="isAllowSelect">
              </el-table-column>
              <el-table-column
                :class-name="!isShow ? 'thlfName' : ''"
                :label="isShow ? '表名称' : '全选'"
                :width="isShow ? '170' : 'auto'"
                :show-overflow-tooltip="true">
                <template slot-scope="scope">{{ scope.row.resourceTableName }}</template>
              </el-table-column>
              <el-table-column
                v-if="isShow"
                prop="fullPermitLevel"
                label="权限状态"
                width="80"
                :formatter="levelFormatter">
              </el-table-column>
              <el-table-column
              v-if="isShow"
                prop="address"
                label="操作">
                 <template slot-scope="scope">
                   <el-button size="mini" type="text" @click="gotoSubscription(scope.row)">订阅此表字段</el-button>
                </template>
              </el-table-column>
            </el-table>

         <!-- <el-tree
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
        </el-tree> -->
        <!-- <span v-else class="null_data">暂无数据</span> -->
       </el-scrollbar>
        <!-- <p v-if="loading">加载中...</p> -->
     </div>
     <!-- <p v-if="noMore">没有更多了</p> -->
     <!-- 选择 -->
     <select-modal ref="dialog" @unselect="unselect"></select-modal>
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
      dataType: '', // 判断是否是数据湖
      noMore: false,
      serachvalue: '',
      loading: false,
      isAllowTrigger: true, // 是否允许触发
      defaultKey: [], 
      dataList: [] // 数据源
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectTable: 'saveSelectTable'
      // serchTableList: 'serchTableList',
    }),
    // 是否显示权限部分, type 类型（0:数据湖,1:数据集,2:自定义源;3:本地空间）数据湖才显示
    isShow () {
      return this.dataType === 0
    },
    // tableData
    tableList () {
      if (!this.serachvalue) {
        return this.dataList
      } else {
        return this.dataList.filter(t => String(t.resourceTableName).toLowerCase().includes(this.serachvalue.toLowerCase()))
      }
    }
  },
  components: {
    selectModal
  },
  watch: {
    // serachvalue (val) {
    //   this.$refs.trees.filter(val)
    // }
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
      const _that = this
      /* 【已弃用】 */
      // 弹出层移出事件
      // this.$root.eventBus.$on('modal-remove', async data => {
      //   _that.$refs['tableList'].toggleRowSelection(data, false)
        // const checkedNodes = this.$refs.trees.getCheckedNodes()
        // const startIndex = checkedNodes.findIndex(t => t.id === data.id)
        // // 删除的数据在当前树形列表存在：直接调用tree 自身的setCheckedNodes 去触发vuex 中的数据更新
        // if (startIndex > -1) {
        //   checkedNodes.splice(startIndex, 1)
        //   this.defaultKey = checkedNodes
        //   this.$refs.trees.setCheckedNodes(checkedNodes)
        // } else {
        //   // 提交到vuex 中处理(之前是这样处理的，这里也沿用这样的逻辑)
        //   // 1、删除数据
        //   // 2、定义一个对象传入当前勾选的状态type和选择的数据
        //   let list = {
        //     type: false,
        //     delData: data
        //   }
        //   await this.$store.dispatch('delSelectTableList', list)
        //   // 合并数据
        //   await this.$store.dispatch('setSelectTableTotal')
        // }
      // })
      // 获取资源信息列表
      this.$root.eventBus.$on('getserchTableList', (data) => this.getserchTableList(data))
      // 接收已选择的复选框数据
      // this.$root.eventBus.$on('saveSelectTables', _ => {
      //   this.setDefualtKey()
      // })
      // 重置复选框
      this.$root.eventBus.$on('clearSelect', _ => {
        this.$refs.trees.setCheckedKeys([])
      })
    },
    // 取消选择
    unselect (row) {
      this.$nextTick(_ => {
        this.dataList.forEach(t => {
          if (t.resourceId === row.resourceId) {
            this.$refs.tableSearchList.toggleRowSelection(t, false)
          }
        })
      })
    },
    // 行点击
    handleRowClick (row) {
      this.$root.eventBus.$emit('getTableHeadList', row)
    },
    // 允许勾选
    isAllowSelect (row, index) {
      // 1 部分可用 2 全部可用
      // 只有数据胡的才受权限控制
      if (!this.isShow) return true
      return row.fullPermitLevel === 1 ||  row.fullPermitLevel === 2
    },
    // 权限状态
    levelFormatter ({ fullPermitLevel = null}) {
      if (!fullPermitLevel) return '不可用'
      return Number(fullPermitLevel) === 1 ? '部分可用' : '全部可用'
    },
    init () {
      // 获取资源信息列表
      // this.$root.eventBus.$on('getserchTableList', (data) => this.getserchTableList(data))
      // this.setDefualtKey()
    },
    // 选择勾选
    handleSelectRow (selection, row) {
    },
    // 勾选状态发生改变
    async handleSelectionChange (row) {
      // 拿到当前勾选的数据
      // let nodeData = this.$refs.trees.getCheckedNodes()
      // 定义一个对象传入当前勾选的状态type和选择的数据
      // let list = {
      //   delData: data
      // }
      // 编辑时不触发
      if (this.isAllowTrigger) {
        await this.$store.dispatch('getSelectTableList', row)
        await this.$store.dispatch('setSelectTableTotal')
      }
    },
    // 【已弃用】
    // setDefualtKey () {
    //    // 初始化默认勾选框数组 以及树列表的复选框
    //     this.defaultKey = []
    //     this.$refs['trees'].setCheckedKeys([])
    //     // 遍历已经勾选的数据赋值到勾选框数组
    //     this.selectTableTotal && this.selectTableTotal.forEach(item => { this.defaultKey.push(String(item.resourceId)) })
    //     // 去重勾选框数组
    //     this.$nextTick(_ => {
    //       this.defaultKey = [...new Set(this.defaultKey)]
    //     })
    //     // setTimeout(() => {  }, 500)
    // },
    // 设置默认勾选
    setDefualtSelct () {
      this.$refs['tableSearchList'].clearSelection()
      this.dataList.forEach(t => {
        if (this.saveSelectTable.find(i => t.resourceId === i.resourceId)) {
          // 编辑时候的勾选不触发状态改变
          this.isAllowTrigger = false
          this.$refs.tableSearchList.toggleRowSelection(t, true)
        }
      })
      this.isAllowTrigger = true
    },
    // 获取资源列表
    async getserchTableList ({orgId, type, databaseType}) {
      // type 类型（0:数据湖,1:数据集,2:自定义源;3:本地空间
      this.dataType = type
      try {
          this.dataList = []
          this.loading = true
          let params = {
            orgId,
            type,
            databaseType
          }
         const { data } =  await getResourceList(params)
         Array.isArray(data) && data.forEach(t => {
          this.dataList.push({id: t.resourceId, label: t.resourceTableName, orgId, ...t})
         })
         // 默认查第一个表
         this.dataList.length && this.$root.eventBus.$emit('getTableHeadList', this.dataList[0])
         // 编辑
         if (this.$route.query.cubeName) {
           // 设置勾选状态
           this.setDefualtSelct()
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
    // 【弃用】
    // 勾选框的选择
    // async handleCheckChange (data, type, node) {
    //   // 拿到当前勾选的数据
    //   let nodeData = this.$refs.trees.getCheckedNodes()
    //   // 定义一个对象传入当前勾选的状态type和选择的数据
    //   let list = {
    //     type: type,
    //     delData: data
    //   }
    //  if (type) {
    //     await this.$store.dispatch('getSelectTableList', nodeData)
    //   } else {
    //     await this.$store.dispatch('delSelectTableList', list)
    //   }
    //   await this.$store.dispatch('setSelectTableTotal')
    // }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('getTableHeadList')
    // this.$root.eventBus.$off('saveSelectTables')
    // this.$root.eventBus.$off('modal-remove')
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
  width 430px
  padding 16px
  font-size 14px
  box-shadow: -5px 0 10px 0 rgba(0,0,0,0.05);
  height 100%
  .trees{
    width: 100%;
    height: calc(100% - 68px);
    overflow auto;
    margin-top: 10px;
  }
  >>> .el-table__header th {
    font-weight: 400!important;
    color: #262626!important;
    background: #eceef5!important;
  }
  >>> .el-table__header th.thlf {
      background: white !important;
      text-align: left !important;
  }
  >>> .el-table__header th.thlfName {
    background: white !important;
    text-align: left !important;
    left: -15px;
  }
  >>> .el-table {
    width: 100%;
    padding: 0 !important;
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


</style>
