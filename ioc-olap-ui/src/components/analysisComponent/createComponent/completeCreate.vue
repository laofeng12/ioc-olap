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
             <el-input type="textarea" placeholder="" v-model="totalSaveData.cube.cubeDescData.description"></el-input>
           </div>
         </template>
       </el-form-item>
    </el-form>
    <steps class="steps" :step="7" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/analysisComponent/modelCommon/steps'
import { mapGetters } from 'vuex'
import { saveolapModeldata } from '@/api/olapModel'
export default {
  components: {
    steps
  },
  data () {
    return {
      formData: {
        factName: '',
        dimensionName: '',
        dimensionFiled: '',
        measureFiled: '',
        engine: ''
        // description: '123123'
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      // 获取信息
      this.selectTableTotal.map(item => {
        if (item.filed === 1) {
          this.formData.factName = item.label
        }
      })
      this.formData.dimensionName = this.jointResult.lookups.length + 1
      this.formData.dimensionFiled = this.saveSelectFiled.length
      this.formData.measureFiled = this.measureTableList.length
      this.formData.engine = this.engine_types === '2' ? 'MapReduce' : 'Spark'
      // 整理接口数据-----
      this.totalSaveData.models.modelDescData.fact_table = this.jointResult.fact_table // 事实表明
      this.totalSaveData.models.modelDescData.lookups = this.jointResult.lookups // 表的关系
      this.totalSaveData.models.modelDescData.dimensions = this.dimensions
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_column = this.reloadData.partition_date_column.join(',')
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_format = this.reloadData.partition_date_format.join(',')
      this.totalSaveData.models.modelDescData.partition_desc.partition_time_format = this.reloadData.partition_time_format.join(',')
      this.totalSaveData.cube.cubeDescData.dimensions = this.saveNewSortListstructure
      this.totalSaveData.cube.cubeDescData.aggregation_groups = this.aggregation_groups
      this.totalSaveData.cube.cubeDescData.mandatory_dimension_set_list = this.mandatory_dimension_set_list
      this.totalSaveData.cube.cubeDescData.hbase_mapping = this.hbase_mapping
      this.totalSaveData.cube.cubeDescData.measures = this.measureTableList
      this.totalSaveData.cube.cubeDescData.rowkey = this.rowkeyData
      this.totalSaveData.cube.cubeDescData.engine_type = this.engine_types
      this.totalSaveData.body.filterCondidion = this.relaodFilterList // 刷新过滤
      this.totalSaveData.body.INTERVAL = this.reloadData.INTERVAL
      this.totalSaveData.body.frequencytype = this.reloadData.frequencytype
      this.totalSaveData.body.autoReload = this.reloadData.autoReload
      this.totalSaveData.body.dataMany = this.reloadData.dataMany
      console.log(this.totalSaveData, '高级', this.hbase_mapping)
    },
    nextModel (val) {
      // this.$message.error('暂未完成')
      saveolapModeldata(this.totalSaveData).then(res => {
        console.log(res)
      })
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/analysisModel/createolap/advancedSet')
    }
  },
  computed: {
    ...mapGetters({
      saveSelectFiled: 'saveSelectFiled', // 已选的维度
      selectTableTotal: 'selectTableTotal',
      mandatory_dimension_set_list: 'mandatory_dimension_set_list', // 黑白名单
      selectDataidList: 'selectDataidList',
      reloadNeedData: 'reloadNeedData',
      engine_types: 'engine_types', // 构建引擎
      hbase_mapping: 'hbase_mapping', // 高级组合
      aggregation_groups: 'aggregation_groups', // 聚合
      rowkeyData: 'rowkeyData', // rowkeys
      dimensions: 'dimensions', // 设置维度表
      saveNewSortListstructure: 'saveNewSortListstructure', // 设置维度表(已选维度)
      measureTableList: 'measureTableList', // 设置度量
      reloadData: 'reloadData', // 刷新页面
      relaodFilterList: 'relaodFilterList', // 刷新过滤设置
      jointResult: 'jointResult', // 表关系数据
      totalSaveData: 'totalSaveData'
    })
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
