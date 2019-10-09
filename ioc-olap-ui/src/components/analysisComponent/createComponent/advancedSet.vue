<template>
  <div class="advancedSet">
     <el-form :model='formData' ref="formData">
        <el-form-item label="高级设置" class="item_line"></el-form-item>
        <div class="aggregation">
          <div class="aggregation_head">
            <span>维度分组聚合</span>
            <span style="color:green;margin-left:10px;cursor:pointer;" @click="addaAggregation">+添加聚合小组</span>
          </div>
          <el-card class="box-card" v-for="(item, index) in aggregation_groups" :key="index">
            <div slot="header" class="clearfix">
              <span>聚合小组</span>
              <el-button style="float:right;padding:3px 0;" type="text" @click="handleRms(index)">删除</el-button>
            </div>
            <div class="item_box">
              <span>包含维度</span>
              <div class="box_r" @click="getTotalModal(index, 1)">
                <el-tag type="" @close.stop="rmTag(index, 1, n)" v-for="(n, i) in item.includes" :key="i" closable>
                  <h6>{{n}}</h6>
                </el-tag>
              </div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r" @click="getTotalModal(index, 2)">
                <el-tag type="" @close.stop="rmTag(index, 2, n)" v-for="(n, i) in item.select_rule.mandatory_dims" :key="i" closable><h6>{{n}}</h6></el-tag>
              </div>
            </div>
            <div class="item_box noflex">
              <span>层级维度</span>
              <div class="adds" v-for="(itemData, i) in item.select_rule.hierarchy_dims" :key="i">
                <div @click="getTotalModal(index, 3, i)">
                  <el-tag @close.stop="rmTag(index, 3, n, i)" v-for="(n, q) in itemData" :key="q" closable><h6>{{n}}</h6></el-tag>
                </div>
                <p>
                  <i class="el-icon-remove" @click="removelevelData(index, i)"></i>
                  <i class="el-icon-circle-plus" @click="addlevelData(index)"></i>
                </p>
              </div>
            </div>
            <div class="item_box noflex">
              <span>联合维度</span>
              <div class="adds" v-for="(jsonData, t) in item.select_rule.joint_dims" :key="t">
                <div @click="getTotalModal(index, 4, t)">
                  <el-tag @close.stop="rmTag(index, 4, x, t)" v-for="(x, y) in jsonData" :key="y" closable><h6>{{x}}</h6></el-tag>
                </div>
                <p>
                  <i class="el-icon-remove" @click="removejointData(index, t)"></i>
                  <i class="el-icon-circle-plus" @click="addjointData(index)"></i>
                </p>
              </div>
            </div>
          </el-card>
        </div>
        <div class="setRowkeys">
          <p style="margin:20px 0">Rowkeys设置</p>
          <el-table
            :data="rowkeyData.rowkey_columns"
            ref="multipleTable"
            tooltip-effect="dark"
            style="margin-top: 10px;">
            <el-table-column type="index" width="50" label="序号" align="center"></el-table-column>
            <el-table-column prop="column" label="字段名称" align="center"> </el-table-column>
            <el-table-column label="编码类型" align="center">
              <template slot-scope="scope">
                <el-form-item class="selects">
                  <el-select v-model.number="scope.row.columns_Type" placeholder="请选择"  @visible-change="codingType(scope.row.code_types)">
                    <el-option v-for="(item, index) in encodingOption" :key="index" :label="item" :value="item"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="长度" width="100" align="center">
              <template slot-scope="scope">
                <el-form-item class="selects">
                  <el-input type="text" v-model="scope.row.lengths" :disabled="['boolean', 'fixed_length', 'fixed_length_hex', 'integer'].includes(scope.row.columns_Type)?false:true"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="碎片区" align="center">
              <template slot-scope="scope">
                <el-form-item class="selects">
                  <el-select v-model="scope.row.isShardBy" placeholder="请选择">
                    <el-option v-for="(item, index) in isShardByOptions" :key="index" :label="item" :value="item"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="listSet">
          <span>维度黑白名单设置</span>
          <div class="listSet__box">
            <div class="adds" v-for="(n, i) in mandatory_dimension_set_list" :key="i">
              <div @click="lastGetModal(i, 5)">
                <el-tag @close.stop="lastrmTag(5, x, i)" v-for="(x, y) in n" :key="y" closable><h6>{{x}}</h6></el-tag>
              </div>
              <p>
                <i class="el-icon-remove" @click="removedimensionData(i)"></i>
                <i class="el-icon-circle-plus" @click="addimensionData(i)"></i>
              </p>
            </div>
          </div>
        </div>
        <el-form-item label="模型构建引擎">
          <template>
            <div>
              <el-select v-model="totalSaveData.cube.cubeDescData.engine_type" placeholder="请选择" @change="changeEngine">
                <el-option v-for="item in engineOptions" :key="item.engine" :label="item.label" :value="item.engine"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <div class="listSet hetCompose">
          <span>高级列组合</span>
          <div class="listSet__box hetCompose__box" v-if="hbase_mapping.column_family && hbase_mapping.column_family.length">
            <div class="adds" v-for="(n, i) in hbase_mapping.column_family" :key="i">
              <div @click="lastGetModal(i, 6)">
                <el-tag @close.stop="lastrmTag(6, x, i)" v-for="(x, y) in n.columns[0].measure_refs" :key="y" closable><h6>{{x}}</h6></el-tag>
              </div>
              <p>
                <i class="el-icon-remove" @click="removehetComposeData(i)"></i>
                <i class="el-icon-circle-plus" @click="addhetComposeData(i)"></i>
              </p>
            </div>
          </div>
          <div class="listSet__box hetCompose__box" v-else>
            <p class="nos">
              <i class="el-icon-circle-plus" @click="addhetComposeData()"></i>
            </p>
          </div>
        </div>
     </el-form>
     <select-aggregation ref="selectFiled"></select-aggregation>
     <steps class="steps" :step="6" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/analysisComponent/modelCommon/steps'
import selectAggregation from '@/components/analysisComponent/dialog/selectAggregation'
import { mapGetters } from 'vuex'
import { getEncodingList } from '@/api/olapModel'
import { reduceObj } from '@/utils/index'
export default {
  components: {
    steps, selectAggregation
  },
  data () {
    return {
      autoReload: false,
      dataMany: false,
      selectLoading: false,
      isOutput: true, // 是否可以输出长度
      modalIndex: 0, // 记录当前点击的是哪个维度框
      levelDataIndex: 0, // 记录层级维度index
      jointDataIndex: 0, // 记录联合维度index
      dimensionDataIndex: 0, // 记录黑白名单index
      hitDataIndex: 0, // 记录高级设置index
      radio: 3,
      formData: {
        engine_typeTit: '2' // 构建引擎
      },
      tableData: [],
      dimensionData: [{}], // 维度黑白名单
      engineOptions: [ // 模型构建引擎
        { engine: '2', label: 'MapReduce ' },
        { engine: '4', label: 'Spark ' }
      ],
      hetComposeData: [], // 高级组合
      encodingOption: [],
      isShardByOptions: ['true', 'false'],
      codingTypeData: {},
      getAllcoding: { 'data': { 'date': ['date', 'time', 'dict'], 'double': ['dict'], 'varchar': ['boolean', 'dict', 'fixed_length', 'fixed_length_hex', 'integer'], 'tinyint': ['boolean', 'date', 'time', 'dict', 'integer'], 'numeric': ['dict'], 'integer': ['boolean', 'date', 'time', 'dict', 'integer'], 'real': ['dict'], 'float': ['dict'], 'smallint': ['boolean', 'date', 'time', 'dict', 'integer'], 'datetime': ['date', 'time', 'dict'], 'int4': ['boolean', 'date', 'time', 'dict', 'integer'], 'char': ['boolean', 'dict', 'fixed_length', 'fixed_length_hex', 'integer'], 'long8': ['boolean', 'date', 'time', 'dict', 'integer'], 'time': ['date', 'time', 'dict'], 'decimal': ['dict'], 'bigint': ['boolean', 'date', 'time', 'dict', 'integer'], 'timestamp': ['date', 'time', 'dict'] }, 'msg': '' },
      rowkey: {
        'rowkey_columns': [
          // {
          //   'column': 'KYLIN_SALES.PART_DT',
          //   'encoding': 'dict',
          //   'isShardBy': 'false',
          //   'encoding_version': 1
          // }
        ]
      }
    }
  },
  mounted () {
    this.resortAggregation()
    this.init()
  },
  methods: {
    init () {
      console.log(this.hbase_mapping.column_family)
      // 获取对应的字段
      getEncodingList().then(res => {
        // this.getAllcoding = res
      })
      // 重置高级组合
      this.hbase_mapping.column_family.forEach((item, index) => {
        if (item.name === 'F1') {
          item.columns[0].measure_refs.forEach((n, i) => {
            if (n === '_COUNT_') {
              item.columns[0].measure_refs.splice(i, 1)
            }
          })
        }
      })
      let datas = [...this.reloadNeedData]
      let arr = []
      datas.map(item => {
        arr.push({
          column: item.value,
          encoding: '',
          lengths: '',
          code_types: item.type ? item.type : '',
          columns_Type: 'dict',
          encoding_version: '1',
          isShardBy: item.isShardBy ? String(item.isShardBy) : 'false'
        })
        return arr
      })
      this.rowkeyData.rowkey_columns = reduceObj([...arr], 'column')
    },
    resortAggregation () {
      this.aggregation_groups.forEach(item => {
        let data = item.select_rule
        if (data.hierarchy_dims.length === 0) data.hierarchy_dims = [[]]
        if (data.joint_dims.length === 0) data.joint_dims = [[]]
      })
    },
    nextModel (val) {
      if (this.judgeSuccess()) {
        return
      }
      this.$parent.getStepCountAdd(val)
      this.$router.push('/analysisModel/createolap/completeCreate')
    },
    // 判断必选的维度以及度量
    judgeSuccess () {
      const { hierarchy_dims, joint_dims } = this.aggregation_groups[0].select_rule
      let hierarchy_dimsLen = hierarchy_dims[0].length
      let joint_dimsLen = joint_dims[0].length
      if (hierarchy_dimsLen > 0 && hierarchy_dimsLen < 2) return this.$message.warning('至少选择两个层级维度')
      if (joint_dimsLen > 0 && joint_dimsLen < 2) return this.$message.warning('至少选择两联合级维度')
      if (this.aggregation_groups[0].includes.length < 1) return this.$message.warning('请选择包含维度~')
      if (this.hbase_mapping.column_family.length < 1 || (this.hbase_mapping.column_family[0].columns && this.hbase_mapping.column_family[0].columns[0].measure_refs.length < 1)) return this.$message.warning('请选择高级列组合~')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/analysisModel/createolap/reloadSet')
    },
    changeEngine (val) {
      this.$store.dispatch('SetEngine', val)
    },
    // 选择对应的编码类型
    codingType (val) {
      for (let item in this.getAllcoding.data) {
        if (val.split('(')[0] === item) {
          this.encodingOption = this.getAllcoding.data[item]
        }
      }
    },
    codingChange (val) {

    },
    // 添加聚合小组
    addaAggregation () {
      this.$store.dispatch('addAggregationList')
    },
    // 删除聚合小组
    handleRms (index) {
      if (this.aggregation_groups.length === 1) return this.$message.error('必须保留一个~')
      this.aggregation_groups.splice(index, 1)
    },
    // 添加层级维度
    addlevelData (index) {
      this.$store.dispatch('AddlevelData', index)
    },
    // 添加联合维度
    addjointData (index) {
      this.$store.dispatch('AddjointData', index)
    },
    // 删除层级维度
    removelevelData (index, i) {
      if (this.aggregation_groups[index].select_rule.hierarchy_dims.length === 1) return this.$message.error('必须保留一个~')
      this.aggregation_groups[index].select_rule.hierarchy_dims.splice(i, 1)
    },
    // 删除联合维度
    removejointData (index, i) {
      if (this.aggregation_groups[index].select_rule.joint_dims.length === 1) return this.$message.error('必须保留一个~')
      this.aggregation_groups[index].select_rule.joint_dims.splice(i, 1)
    },
    // 添加维度黑白名单
    addimensionData () {
      this.$store.dispatch('AddimensionData')
    },
    // 删除维度黑白名单
    removedimensionData (index) {
      if (this.mandatory_dimension_set_list.length === 1) return this.$message.error('必须保留一个~')
      this.mandatory_dimension_set_list.splice(index, 1)
    },
    // 添加高级列组合
    addhetComposeData () {
      this.$store.dispatch('AddhetComposeData')
    },
    // 删除高级列组合
    removehetComposeData (index) {
      this.hbase_mapping.column_family.splice(index, 1)
    },
    // 选择维度
    getTotalModal (index, type, findIndex) {
      this.modalIndex = index
      this.$refs.selectFiled.dialog(type, index, findIndex)
    },
    // 黑白名单or高级列弹框
    lastGetModal (index, type) {
      this.$refs.selectFiled.dialog(type, index)
    },
    rmTag (index, type, id, findIndex) {
      const list = {
        index: index,
        type: type,
        id: id,
        findIndex: findIndex
      }
      this.$store.dispatch('RmtagList', list)
    },
    lastrmTag (type, id, findIndex) {
      const list = {
        id: id,
        type: type,
        findIndex: findIndex
      }
      this.$store.dispatch('RmtagList', list)
    }
  },
  computed: {
    ...mapGetters({
      saveSelectFiled: 'saveSelectFiled',
      mandatory_dimension_set_list: 'mandatory_dimension_set_list', // 黑白名单
      selectDataidList: 'selectDataidList',
      reloadNeedData: 'reloadNeedData',
      totalSaveData: 'totalSaveData',
      measureTableList: 'measureTableList',
      dimensions: 'dimensions',
      hbase_mapping: 'hbase_mapping', // 高级组合
      aggregation_groups: 'aggregation_groups', // 聚合
      rowkeyData: 'rowkeyData' // rowkeys
    })
  }
}
</script>

<style lang="stylus" scoped>
.advancedSet{
  padding 30px
  margin-top 30px
  background #ffffff
  padding-bottom 100px
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
  }
  >>>.el-form-item__label{
    width 120px
    text-align left
  }
  >>>.el-table__body-wrapper{
    height 350px
    overflow-y auto
  }
  .selects{
    margin-bottom 0
  }
  >>>.el-form-item{
  }
  .aggregation{
    margin-top 20px
    .box-card{
      margin-top 20px
      .item_box{
        display flex
        margin-bottom 30px
        span{
          width 80px
        }
        .box_r{
          border 1px solid #cccccc
          flex 1
          cursor pointer
        }
        >>>.el-tag{
         width: 80px;
         height: 22px;
         line-height: 22px;
         border-radius: 0;
         background: #F6F6F6;
         border: 1px solid #D9D9D9;
         margin: 5px 0 5px 10px;
         text-align: center;
         font-size: 12px;
         color: #262626;
          // i{
          //   float right!important
          //   margin-top 8px
          // }
        }
        .adds{
          border none!important
          padding 0
          width 100%
          display flex
          div{
            flex 1
            border 1px solid #cccccc
            cursor pointer
          }
        }
        .adds:first-child{
          margin-top -20px
        }
        p{
          width 80px
          text-align center
          margin-top 10px
          i{
            color red
            font-size 25px
          }
          i:nth-child(2){
            color green
          }
        }
      }
      .noflex{
        display initial!important
      }
    }
  }
  h6{
    text-overflow: ellipsis;
    float left
    width: 90%;
    overflow: hidden;
  }
  .listSet{
    margin-top 20px
    span{
      width 100px
    }
    .listSet__box{
      margin-top -20px
      .adds{
        display flex
        width 100%
        margin-bottom 10px
        div{
          margin-left 120px
          flex 1
          border 1px solid #cccccc
          cursor pointer
        }
      }
      p{
        width 80px
        text-align center
        margin-top 10px
        i{
          color red
          font-size 25px
        }
        i:nth-child(2){
          color green
        }
      }
      >>>.el-tag{
         width: 80px;
         height: 22px;
         line-height: 22px;
         border-radius: 0;
         background: #F6F6F6;
         border: 1px solid #D9D9D9;
         margin: 5px 0 5px 10px;
         text-align: center;
         font-size: 12px;
         color: #262626;
          // i{
          //   float right!important
          //   margin-top 8px
          // }
          .el-tag__close {
              color: #5A5A5A !important;
          }
        }
    }
    .nos{
      margin-left 100px
      margin-top -10px
    }
  }
}
</style>
