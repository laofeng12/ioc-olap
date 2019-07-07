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
    // 接收数据湖传递的信息
    this.$root.eventBus.$on('getserchTableList', (res, type) => {
      this.dataList[0].children = []
      // if (type && type === 1) {
      //   this.$refs.trees.setCheckedKeys([])
      // }
      this.loading = true
      if (res.code === 200) {
        res.data.map(res => {
          this.dataList[0].children.push({
            id: res.RD_ID,
            label: res.DS__DLT_CODE
          })
        })
        setTimeout(() => { this.loading = false }, 300)
      }
    })
    // 接收本地上传传递的信息
    this.$root.eventBus.$on('getUploadTable', res => {
      this.dataList[0].children = []
      this.loading = true
      if (res.code === 200) {
        res.rows.map(res => {
          this.dataList[0].children.push({
            id: res.dsUploadTableId,
            label: res.tableCode
          })
        })
        if (this.dataList[0].children.length < 1) {
          this.$refs.trees.setCheckedKeys([])
        }
        setTimeout(() => { this.loading = false }, 300)
      }
    })
    // 接收已选择的复选框数据
    this.$root.eventBus.$on('saveSelectTables', (res1, res2) => {
      this.defaultKey = []
      this.$refs.trees.setCheckedKeys([])
      if (this.$store.state.common.searchType === 1) {
        res2.map(item => {
          this.defaultKey.push(item.id)
        })
      } else {
        res1.map(item => {
          this.defaultKey.push(item.id)
        })
      }
      setTimeout(() => {
        this.loading = false
        this.defaultKey = [...new Set(this.defaultKey)]
      }, 500)
    })
    // 重置复选框
    this.$root.eventBus.$on('clearSelect', _=> {
      this.$refs.trees.setCheckedKeys([])
    })
  },
  methods: {
    handleNodeClick (value) {
      let searchType = this.$store.state.common.searchType
      let dsDataSourceId = searchType === 1 ? 10 : 2
      let dbType = searchType === 1 ? 3 : 0
      let tableName = value.label
      const parmas = {
        dsDataSourceId,
        tableName
      }
      const valparams = {
        dbType,
        dsDataSourceId,
        tableName
      }
      this.$store.dispatch('GetColumnList', parmas).then(data => {
        if (data.code === 200) {
          switch (searchType) {
            case 1:
              this.$root.eventBus.$emit('getTableHeadList', data)
              break
            case 2:
              this.$root.eventBus.$emit('getLocalTableHeadList', data)
              break
            default:
              break
          }
        }
      })
      this.$store.dispatch('GetTableData', valparams).then(res => {
        if (res.code === 200) {
          switch (searchType) {
            case 1:
              this.$root.eventBus.$emit('getTableContentList', res)
              break
            case 2:
              this.$root.eventBus.$emit('getLocalTableContentList', res)
              break
            default:
              break
          }
        }
      })
    },
    // 勾选框的选择
    handleCheckChange (data, type, node) {
      this.$store.state.common.searchType === 1
        ? this.$store.dispatch('getSelectTableList', this.$refs.trees.getCheckedNodes())
        : this.$store.dispatch('getLocalSelectTableList', this.$refs.trees.getCheckedNodes())
      // 设置已选择的数据表的数量
      this.$store.dispatch('setSelectTableCount')
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('getTableHeadList')
    this.$root.eventBus.$off('getLocalTableHeadList')
    this.$root.eventBus.$off('getTableContentList')
    this.$root.eventBus.$off('getLocalTableContentList')
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
