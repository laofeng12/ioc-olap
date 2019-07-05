<template>
  <div class="serchTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-tree
      :data="dataList"
       default-expand-all
       node-key="id"
      :expand-on-click-node='false'
      show-checkbox
      :default-checked-keys="defaultKey"
      @check-change="handleCheckChange"
      @node-click="handleNodeClick">
      </el-tree>
  </div>
</template>

<script>

export default {
  data () {
    return {
      value: '',
      checkAll: false,
      loading: false,
      checkedCities: [],
      defaultKey: [],
      dataList: [{
        id: '',
        label: '全选',
        children: []
      }],
      isIndeterminate: true
    }
  },
  mounted () {
    // 接收数据湖传递的信息
    this.$root.eventBus.$on('getserchTableList', res => {
      this.dataList[0].children = []
      this.loading = true
      if (res.code === 200) {
        res.data.map(res => {
          this.dataList[0].id = res.requestId
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
          this.dataList[0].id = res.requestId
          this.dataList[0].children.push({
            id: res.dsUploadTableId,
            label: res.tableCode
          })
        })
        setTimeout(() => { this.loading = false }, 300)
      }
    })
    // 接收已选择的复选框数据
    this.$root.eventBus.$on('saveSelectTable', res => {
      res.map(item => {
        this.defaultKey.push(item.id)
      })
      this.defaultKey = [...new Set(this.defaultKey)]
      console.log('当前的数据', this.defaultKey)
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
      type ? this.$store.dispatch('saveSelectTable', data) : this.$store.dispatch('deleteSelectTable', data)
    }
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
  height calc(100vh - 150px)
  border-right 1px solid #f0f0f0
  padding-bottom 50px
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
</style>
