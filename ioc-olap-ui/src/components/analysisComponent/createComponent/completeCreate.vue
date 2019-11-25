<template>
  <div class="completeCreate"  v-loading="isLoading">
    <el-form :model="totalSaveData" ref="formData"  :rules="rules" >
       <el-form-item label="模板基本信息" class="item_line"></el-form-item>
       <el-form-item label="事实表">{{jointResultData.fact_table}}</el-form-item>
       <el-form-item label="维度表">{{jointResultData.lookups.length}}</el-form-item>
       <el-form-item label="维度字段">{{saveSelectFiled.length}}</el-form-item>
       <el-form-item label="度量字段">{{measureTableList.length}}</el-form-item>
       <el-form-item label="构建引擎">{{engine_types === '2' ? 'MapReduce' : 'Spark'}}</el-form-item>
       <el-form-item label="模型名称" prop="cube.cubeDescData.name" class="labelName">
         <template slot-scope="scope">
           <div>
             <!-- <el-input type="text" placeholder="" :disabled="!Array.isArray(ModelAllList)" v-model="totalSaveData.cube.cubeDescData.name" maxlength="50" show-word-limit></el-input> -->
             <el-input type="text" placeholder="" v-if="!!Array.isArray(ModelAllList)" v-model="totalSaveData.cube.cubeDescData.name" maxlength="50" show-word-limit></el-input>
             <span v-else>{{totalSaveData.cube.cubeDescData.name}}</span>
           </div>
         </template>
       </el-form-item>
       <el-form-item label="描述信息" prop="description">
         <template slot-scope="scope">
           <div>
             <el-input type="textarea" placeholder="" v-model="totalSaveData.cube.cubeDescData.description" maxlength="200" show-word-limit></el-input>
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
import { throttle, reduceObj } from '@/utils/index'
import { isCheckModelName } from '@/utils/rules'

export default {
  name:'completeCreate',
  components: {
    steps
  },
  data () {
    return {
      isLoading: false,
      formData: {
        factName: '',
        dimensionLength: '',
        dimensionFiledLength: '',
        measureFiledLength: '',
        engine: ''
        // description: '123123'
      },
      rules: {
        'cube.cubeDescData.name': [
          { required: true, message: '请输入模型名称', trigger: 'blur' },
          { validator: isCheckModelName, trigger: 'blur' }
        ]
      }
    }
  },
  created () {
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
      // 整理接口数据-----
      this.totalSaveData.models.modelDescData.fact_table = this.jointResultData.fact_table // 事实表明
      this.totalSaveData.models.modelDescData.lookups = this.jointResultData.lookups.filter(item => {
        return item.alias !== this.jointResultData.fact_table
      }) // 表的关系
      /**
       * 处理聚合小组
       */
      this.totalSaveData.cube.cubeDescData.aggregation_groups = JSON.parse(JSON.stringify(this.aggregation_groups))
      this.totalSaveData.cube.cubeDescData.mandatory_dimension_set_list = JSON.parse(JSON.stringify(this.mandatory_dimension_set_list))
      this.totalSaveData.cube.cubeDescData.aggregation_groups.forEach((i, n) => {
        let item = i.select_rule
        item.hierarchy_dims.forEach(k => {
          if (k.length === 0) item.hierarchy_dims = []
        })
        item.joint_dims.forEach(k => {
          if (k.length === 0) item.joint_dims = []
        })
      })
      this.totalSaveData.cube.cubeDescData.mandatory_dimension_set_list.forEach((n, i) => {
        if (n.length === 0) this.totalSaveData.cube.cubeDescData.mandatory_dimension_set_list = []
      })
      this.totalSaveData.cube.cubeDescData.hbase_mapping = JSON.parse(JSON.stringify(this.hbase_mapping))
      this.totalSaveData.cube.cubeDescData.hbase_mapping.column_family.forEach((item, index) => {
        if (item.name === 'F1') {
          item.columns[0].measure_refs.push('_COUNT_')
        }
      })
      this.totalSaveData.cube.cubeDescData.measures = this.measureTableList
      this.totalSaveData.cube.cubeDescData.rowkey = this.rowkeyData
      this.totalSaveData.filterCondidion = this.relaodFilterList // 刷新过滤
      this.totalSaveData.timingreFresh.interval = Number(this.reloadData.interval)
      this.totalSaveData.timingreFresh.frequencytype = this.reloadData.frequencytype
      this.totalSaveData.timingreFresh.autoReload = this.reloadData.autoReload === true ? 1 : 0
      this.totalSaveData.timingreFresh.dataMany = this.reloadData.dataMany === true ? 1 : 0
      this.totalSaveData.cubeDatalaketableNew = this.selectStepList
      // 增加  databaseId
      this.totalSaveData.cubeDatalaketableNew.forEach(tb => {
        tb.tableList.forEach(t => {
          let target = this.batchCreateJob.find(item => item.resourceId === t.resourceId)
          if (target) {
            t.virtualTableName = t.table_name // 虚拟表名
            t.databaseId = target.databaseId
            // t.databaseId = target.databaseId
            // t.table_name = target.writerTableName // 真实表名
          }
          t.database = 'olap'
        })
      })
      this.totalSaveData.dimensionLength = this.jointResultData.lookups.length
      this.totalSaveData.dimensionFiledLength = this.saveSelectFiled.length
      this.totalSaveData.measureFiledLength = this.measureTableList.length
      // models放入所有选择的表字段
      /**
       * models中的dimensions放入所有选择的表字段
      */
      let dest = []
      this.saveSelectAllListFiled.map((item, index) => {
        // let data = JSON.parse(item)
        // 遍历第二步存储的表 根据id来找出事实表的那条数据
        this.totalSaveData.models.modelDescData.lookups.forEach((n, i) => {
          if (item.resourceId === n.id) {
            dest.push({
              table: n.alias,
              columns: item.column.map(res => {
                return res.columnAlias
              })
            })
          }
        })
        if (this.jointResultData.fact_table.split('.')[1] === item.resourceTableName) {
          dest.push({
            table: item.resourceTableName,
            columns: item.column.map(res => {
              return res.columnAlias
            })
          })
        }
        return dest
      })
      this.totalSaveData.models.modelDescData.dimensions = []
      // 增加对应关系
      let relations = []
      this.totalSaveData.cubeDatalaketableNew.forEach(tb => {
        tb.tableList.forEach(t => {
          let target = this.batchCreateJob.find(item => item.resourceId === t.resourceId)
          if (target) {
            let writerTableName = target.writerTableName
            let table_name = t.table_name
            relations.push({
              jobId: target.jobId,
              resourceId: target.resourceId,
              tableName: writerTableName,
              virtualTableName: table_name
            })
          }
        })
      })
      this.totalSaveData.relations = relations
      // lookups 增加虚拟表
      this.totalSaveData.models.modelDescData.lookups.forEach(t => {
        let target = this.batchCreateJob.find(item => item.resourceId === t.id)
          if (target) {
            t.virtualTableName = t.table ? t.table.replace('async.','').replace('DEFAULT.','') : t.table // 虚拟表名
            t.table = t.table ?  t.table.replace('DEFAULT','async') : t.table
            // t.table_name = target.writerTableName // 真实表名
          }
      })
      // 处理fact_table
      let tempStr = this.totalSaveData.models.modelDescData.fact_table.split('.')[0]
      this.totalSaveData.models.modelDescData.fact_table = this.totalSaveData.models.modelDescData.fact_table.replace(tempStr,'olap')
      // 处理 lookups
      this.totalSaveData.models.modelDescData.lookups.forEach(t => {
        let tempStr = t.table.split('.')[0]
        t.table = t.table.replace(tempStr,'olap')
      })
      // 处理刷新过滤设置
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_column ?  this.totalSaveData.timingreFresh.buildMode = 1 : this.totalSaveData.timingreFresh.buildMode = 0
    },
    changesEncoding () {
      // 过滤rowkey
      this.totalSaveData.cube.cubeDescData.rowkey.rowkey_columns.forEach(res => {
        let leh = res.lengths ? `:${res.lengths}` : ''
        res.encoding = `${res.columns_Type}${leh}`
      })
      this.totalSaveData.cubeDatalaketableNew.map(res => {
        res.tableList = reduceObj(res.tableList, 'table_id')
      })
      this.totalSaveData.cube.cubeDescData.dimensions = JSON.parse(JSON.stringify(this.dimensions))
    },
    // 处理 dimensions（选择维度）
    nextModel (val) {
      if (!this.totalSaveData.cube.cubeDescData.name.length) {
          this.$message.warning('请填写模型名称~')
          return
        }
        // 处理表关联关系(暂时先这样处理,中间不知道哪里处理没了)
      this.totalSaveData.models.modelDescData.lookups.forEach(t => {
        t.join.type = this.tableJoinType
      })
      try {
        this.changesEncoding()
        this.$refs.formData.validate(valid => {
          if (valid) {
            this.isLoading = true
            throttle(async () => {
              await saveolapModeldata(this.totalSaveData).then(_ => {
                this.isLoading = false
                this.$message.success('保存成功')
                this.$router.push('/analysisModel/Configuration')
                this.$store.dispatch('resetList') // 保存成功所有module 触发resetList
              }).catch(_ => this.isLoading = false )
            }, 1000)
          }
        })
      } catch (e) {
        this.isLoading = false
      }
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/analysisModel/createolap/advancedSet')
    }
  },
  computed: {
    ...mapGetters({
      tableJoinType: 'tableJoinType',
      batchCreateJob: 'batchCreateJob',
      totalSaveData: 'totalSaveData',
      ModelAllList: 'ModelAllList',
      saveSelectFiled: 'saveSelectFiled', // 已选的维度
      selectTableTotal: 'selectTableTotal',
      mandatory_dimension_set_list: 'mandatory_dimension_set_list', // 黑白名单
      selectDataidList: 'selectDataidList',
      reloadNeedData: 'reloadNeedData',
      saveSelectAllListFiled: 'saveSelectAllListFiled', // 建表后对应的所有字段
      engine_types: 'engine_types', // 构建引擎
      hbase_mapping: 'hbase_mapping', // 高级组合
      aggregation_groups: 'aggregation_groups', // 聚合
      rowkeyData: 'rowkeyData', // rowkeys
      dimensions: 'dimensions', // 设置维度表
      saveNewSortListstructure: 'saveNewSortListstructure', // 设置维度表(已选维度)
      measureTableList: 'measureTableList', // 设置度量
      reloadData: 'reloadData', // 刷新页面
      relaodFilterList: 'relaodFilterList', // 刷新过滤设置
      selectStepList: 'selectStepList', // 保存第一步数据
      jointResultData: 'jointResultData' // 表关系数据
    })
  }
}
</script>

<style lang="stylus" scoped>
.completeCreate{
  padding: 0 16px 16px 16px;
  margin-top: 16px;
  background #ffffff
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
    >>>.el-form-item__label{
      font-size 16px
    }
  }
  .labelName{
    >>>.el-input{
      width 300px
      height 35px!important
    }
    >>>.el-form-item__error{
      margin-left 100px
    }
  }
  >>>.el-form-item{
    margin-bottom 0
	.el-textarea__inner{
		min-height 160px!important;
	}
    .el-form-item__label{
      font-size: 14px;
      color: #262626;
      text-align left
    }
  }
}
</style>
