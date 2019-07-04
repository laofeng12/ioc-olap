<template>
  <div class="serchTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <!-- <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox> -->
     <el-radio-group v-model="checkedCities" @change="handleCheckedCitiesChange" v-if="dataList && dataList.length" v-loading="loading">
       <el-radio v-for="(item, index) in dataList" :label="item" :key="index">{{item.name}}</el-radio>
     </el-radio-group>
     <div v-else style="width:200px;text-align:center;">暂无数据</div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  data () {
    return {
      value: '',
      checkAll: false,
      loading: false,
      checkedCities: [],
      dataList: [],
      isIndeterminate: true
    }
  },
  mounted () {
    this.$root.eventBus.$on('getserchTableList', res => {
      this.dataList = []
      this.loading = true
      if (res.code === 200) {
        res.data.map(res => {
          this.dataList.push({
            id: res.RD_ID,
            name: res.DS__DLT_CODE
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
            name: res.tableCode
          })
        })
        setTimeout(() => { this.loading = false }, 300)
      }
    })
  },
  methods: {
    handleCheckedCitiesChange (value) {
      const parmas = {
        dsDataSourceId: 10,
        tableName: value.name
      }
      const valparams = {
        dbType: 3,
        dsDataSourceId: 10,
        tableName: value.name
      }
      this.$store.dispatch('GetColumnList', parmas).then(data => {
        data.code === 200 && this.$root.eventBus.$emit('getTableHeadList', data)
      })
      this.$store.dispatch('GetTableData', valparams).then(res => {
        res.code === 200 && this.$root.eventBus.$emit('getTableContentList', res)
      })
      let checkedCount = value.length
      this.checkAll = checkedCount === this.dataList.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.dataList.length
    }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('getTableHeadList')
    this.$root.eventBus.$off('getTableContentList')
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
}
</style>
