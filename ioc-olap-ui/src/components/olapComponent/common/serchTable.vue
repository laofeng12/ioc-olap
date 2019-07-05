<template>
  <div class="serchTable">
     <!-- <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
     <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange" v-if="dataList && dataList.length" v-loading="loading">
       <el-checkbox v-for="(item, index) in dataList" :label="item" @change="labelChange()" :key="index">{{item.name}}</el-checkbox>
     </el-checkbox-group>
     <div v-else style="width:200px;text-align:center;">暂无数据</div> -->
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-tree :data="dataList" default-expand-all :expand-on-click-node='false' show-checkbox @check-change="handleCheckChange" @node-click="handleNodeClick"></el-tree>
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
      dataList: [{
        id: 1,
        label: '全选',
        children: [
          {
            id: 2,
            label: 'asdasdasd'
          },
          {
            id: 3,
            label: 'asdasdasdasd'
          }
        ]
      }],
      isIndeterminate: true
    }
  },
  mounted () {
    // 接收数据湖传递的信息
    this.$root.eventBus.$on('getserchTableList', res => {
      this.dataList = []
      this.loading = true
      if (res.code === 200) {
        res.data.map(res => {
          this.dataList.push({
            id: res.RD_ID,
            label: res.DS__DLT_CODE
          })
        })
        setTimeout(() => { this.loading = false }, 300)
      }
    })
    // 接收本地上传传递的信息
    this.$root.eventBus.$on('getUploadTable', res => {
      this.dataList = []
      this.loading = true
      if (res.code === 200) {
        res.rows.map(res => {
          this.dataList.push({
            id: res.dsUploadTableId,
            label: res.tableCode
          })
        })
        setTimeout(() => { this.loading = false }, 300)
      }
    })
  },
  methods: {
    handleNodeClick (value) {
      console.log(value)
      let searchType = this.$store.state.common.searchType
      let dsDataSourceId = searchType === 1 ? 10 : 2
      let dbType = searchType === 1 ? 3 : 0
      let tableName = value[0].name
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
    handleCheckChange (data, type, node) {
      console.log(type)
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
