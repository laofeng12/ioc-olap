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
const cityOptions = ['表名称表名称表名称1', '表名称表名称表名称2', '表名称表名称表名称3', '表名称表名称表名称4']
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
  width 300px
  float left
  padding 0 25px
  >>>.el-checkbox{
    display block
    height 30px
    line-height 30px
  }
  >>>.el-input{
    width 300px
    height 50px
    .el-input__inner{
      height 30px
    }
  }
}
</style>
