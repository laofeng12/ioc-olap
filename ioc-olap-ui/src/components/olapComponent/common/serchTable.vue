<template>
  <div class="serchTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
     <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
       <el-checkbox v-for="(item, index) in dataList" :label="item.name" :key="index">{{item.name}}</el-checkbox>
     </el-checkbox-group>
  </div>
</template>

<script>
export default {
  data () {
    return {
      value: '',
      checkAll: false,
      checkedCities: ['表名称表名称表名称1', '表名称表名称表名称3'],
      dataList: [],
      isIndeterminate: true
    }
  },
  mounted () {
    this.$root.eventBus.$on('getserchTableList', res => {
      this.dataList = []
      if (res.code === 200) {
        res.data.map(res => {
          this.dataList.push({
            id: res.RD_ID,
            name: res.DS__DLT_CODE
          })
        })
        console.log(this.dataList)
      }
    })
  },
  methods: {
    handleCheckAllChange (val) {
      this.checkedCities = val ? this.dataList : []
      this.isIndeterminate = false
    },
    handleCheckedCitiesChange (value) {
      let checkedCount = value.length
      this.checkAll = checkedCount === this.cities.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.cities.length
    }
  }
}
</script>

<style lang="stylus" scoped>
.serchTable{
  width 230px
  float left
  padding 0 25px
  padding-bottom 50px
  >>>.el-checkbox{
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
  >>>.el-checkbox-group{
    height 90%
    width 230px
    overflow-y auto
  }
  >>>.el-checkbox-group::-webkit-scrollbar{
    width 8px
    height 8px
    background #fff
  }
  >>>.el-checkbox-group::-webkit-scrollbar-track{
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color:#fff;
  }
  >>>.el-checkbox-group::-webkit-scrollbar-thumb{
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #B5D2DE;
  }
}
</style>
