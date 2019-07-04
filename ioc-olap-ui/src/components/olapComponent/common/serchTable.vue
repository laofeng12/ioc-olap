<template>
  <div class="serchTable">
     <el-input type="text" placeholder="请输入关键词" v-model="value" clearable></el-input>
     <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">全选</el-checkbox>
     <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
       <el-checkbox v-for="city in cities" :label="city" :key="city">{{city}}</el-checkbox>
     </el-checkbox-group>
  </div>
</template>

<script>
const cityOptions = ['表名称表名称表名称1', '表名称表名称表名称2', '表名称表名称表名称3', '表名称表名称表名称4', '表名称表名称表名称5', '表名称表名称表名称6', '表名称表名称表名称7', '表名称表名称表名称8', '表名称表名称表名称9', '表名称表名称表名称10', '表名称表名称表名11', '表名称表名称表名称13', '表名称表名称表名称14']
export default {
  data () {
    return {
      value: '',
      checkAll: false,
      checkedCities: ['表名称表名称表名称1', '表名称表名称表名称3'],
      cities: cityOptions,
      isIndeterminate: true
    }
  },
  methods: {
    handleCheckAllChange (val) {
      this.checkedCities = val ? cityOptions : []
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
  width 240px
  float left
  padding 0 25px
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
