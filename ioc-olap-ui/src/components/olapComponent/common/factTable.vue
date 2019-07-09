<template>
  <div class="factTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-button type="text" @click="cahngges">设置事实表</el-button>
     <ul v-if="dataList && dataList.length">
       <li v-for="(item, index) in dataList" :class= "current === index?'actives':''" :key="index" @click="changeLi(index)"><i class="el-icon-date" style="margin-right:3px;"></i>{{item.label}}</li>
     </ul>
     <div v-else>暂无数据</div>
     <setfact-table ref="dialog"></setfact-table>
  </div>
</template>

<script>
import setfactTable from '@/components/olapComponent/dialog/setfactTable'
export default {
  components: {
    setfactTable
  },
  data () {
    return {
      value: '',
      current: '',
      dataList: []
    }
  },
  mounted () {
    // 接受设置表关系的数据
    this.$root.eventBus.$on('createTable', res => {
      res && res.map(res => {
        this.dataList.push({
          id: res.id,
          label: res.label
        })
      })
      setTimeout(() => { this.loading = false }, 300)
    })
  },
  methods: {
    cahngges (val) {
      this.$refs.dialog.dialog()
    },
    changeLi (index) {
      this.current = index
    }
  }
}
</script>

<style lang="stylus" scoped>
.factTable{
  min-width 200px
  width 200px
  float left
  padding 0 25px
  border-right 1px solid #f0f0f0
  height calc(100vh - 100px)
  overflow auto
  ul{
    cursor pointer
    li{
      height 30px
      line-height 30px
      color #000000
    }
    .actives{
      color #009688
    }
  }
  >>>.el-input{
    height 30px
    .el-input__inner{
      height 30px
    }
  }
  >>>.el-input__suffix{
    margin-top -5px
  }
}
</style>
