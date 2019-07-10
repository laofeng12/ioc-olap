<template>
  <div class="completeCreate">
    <el-form :model="formData">
       <el-form-item label="模板基本信息" class="item_line"></el-form-item>
       <el-form-item label="事实表">{{formData.factName}}</el-form-item>
       <el-form-item label="维度表">{{formData.dimensionName}}</el-form-item>
       <el-form-item label="维度字段">{{formData.dimensionFiled}}</el-form-item>
       <el-form-item label="度量字段">{{formData.measureFiled}}</el-form-item>
       <el-form-item label="构建引擎">{{formData.engine}}</el-form-item>
       <el-form-item label="描述信息" prop="description">
         <template slot-scope="scope">
           <div>
             <el-input type="textarea" placeholder=""></el-input>
           </div>
         </template>
       </el-form-item>
    </el-form>
    <steps class="steps" :step="7" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
export default {
  components: {
    steps
  },
  data () {
    return {
      formData: {
        factName: '表表拜拜比比比吧',
        dimensionName: '表表拜拜比比比吧',
        dimensionFiled: '表表拜拜比比比吧',
        measureFiled: '表表拜拜比比比吧',
        engine: '表表拜拜比比比吧',
        description: ''
      }
    }
  },
  methods: {
    nextModel (val) {
      // this.$router.push('/olap/createolap/completeCreate')
      this.$message.error('暂未完成')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/reloadSet')
    }
  }
}
</script>

<style lang="stylus" scoped>
.completeCreate{
  padding 30px 100px
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
    >>>.el-form-item__label{
      sont-size 14px
      font-weight 700
    }
  }
  >>>.el-form-item{
    margin-bottom 0
    .el-form-item__label{
      width 100px
      text-align left
      font-size 13px
      font-weight 400
    }
  }
}
</style>
