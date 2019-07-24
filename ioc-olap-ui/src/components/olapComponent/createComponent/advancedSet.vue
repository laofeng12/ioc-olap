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
                <el-tag type="" @close.stop="rmTag(index, 1, n)" v-for="(n, i) in item.includes" :key="i" closable>{{n}}</el-tag>
              </div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r" @click="getTotalModal(index, 2)">
                <el-tag type="" @close.stop="rmTag(index, 2, n)" v-for="(n, i) in item.select_rule.mandatory_dims" :key="i" closable>{{n}}</el-tag>
              </div>
            </div>
            <div class="item_box noflex">
              <span>层级维度</span>
              <div class="adds" v-for="(itemData, i) in item.select_rule.hierarchy_dims" :key="i">
                <div @click="getTotalModal(index, 3, i)">
                  <el-tag @close.stop="rmTag(index, 3, n, i)" v-for="(n, q) in itemData" :key="q" closable>{{n}}</el-tag>
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
                  <el-tag @close.stop="rmTag(index, 4, x, i)" v-for="(x, y) in jsonData" :key="y" closable>{{x}}</el-tag>
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
            :data="tableData"
            ref="multipleTable"
            tooltip-effect="dark"
            @selection-change="handleSelectionChange"
            style="margin-top: 10px;">
            <el-table-column prop="columnName" label="字段名称" align="center"> </el-table-column>
            <el-table-column label="编码类型" align="center">
              <template slot-scope="scope">
                <el-form-item class="selects">
                  <el-select v-model="scope.row.dataType" placeholder="请选择" @visible-change="codingType(scope.row.dataType)">
                    <el-option v-for="(item, index) in encodingOption" :key="index" :label="item" :value="item"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="长度" width="100" align="center">
              <template slot-scope="scope">
                <el-form-item class="selects">
                  <el-input type="text" v-model="scope.row.mode"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column prop="apiPaths" label="碎片区" align="center">
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
            <div class="adds" v-for="(n, i) in savedimensionData" :key="i">
              <div @click="lastGetModal(i, 5)">
                <el-tag @close.stop="lastrmTag(5, x, i)" v-for="(x, y) in n" :key="y" closable>{{x}}</el-tag>
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
              <el-select v-model="formData.engine_type" placeholder="请选择">
                <el-option v-for="item in engineOptions" :key="item.engine" :label="item.label" :value="item.engine"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <div class="listSet hetCompose">
          <span>高级列组合</span>
          <div class="listSet__box hetCompose__box" v-if="savehetComposeData && savehetComposeData.length">
            <div class="adds" v-for="(n, i) in savehetComposeData" :key="i">
              <div @click="lastGetModal(i, 6)">
                <el-tag @close.stop="lastrmTag(6, x, i)" v-for="(x, y) in n" :key="y" closable>{{x}}</el-tag>
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
import steps from '@/components/olapComponent/common/steps'
import selectAggregation from '@/components/olapComponent/dialog/selectAggregation'
import { mapGetters } from 'vuex'
import { getLocalStorage } from '@/utils/index'
export default {
  components: {
    steps, selectAggregation
  },
  data () {
    return {
      autoReload: false,
      dataMany: false,
      modalIndex: 0, // 记录当前点击的是哪个维度框
      levelDataIndex: 0, // 记录层级维度index
      jointDataIndex: 0, // 记录联合维度index
      dimensionDataIndex: 0, // 记录黑白名单index
      hitDataIndex: 0, // 记录高级设置index
      radio: 3,
      formData: {
        engine_type: '1' // 构建引擎
      },
      tableData: [],
      dimensionData: [{}], // 维度黑白名单
      engineOptions: [ // 模型构建引擎
        { engine: '1', label: 'MapReduce ' },
        { engine: '2', label: 'Spark ' }
      ],
      hetComposeData: [], // 高级组合
      encodingOption: [],
      isShardByOptions: ['true', 'false'],
      codingTypeData: {
        'string': ['date', 'time', 'dict'],
        'date': ['date', 'time', 'dict'],
        'double': ['dict'],
        'varchar': ['boolean', 'dict', 'fixed_length', 'fixed_length_hex', 'integer'],
        'number': ['boolean', 'dict', 'fixed_length', 'fixed_length_hex', 'integer'],
        'tinyint': ['boolean', 'date', 'time', 'dict', 'integer'],
        'numeric': ['dict'],
        'integer': ['boolean', 'date', 'time', 'dict', 'integer'],
        'real': ['dict'],
        'float': ['dict'],
        'smallint': ['boolean', 'date', 'time', 'dict', 'integer'],
        'datetime': ['date', 'time', 'dict'],
        'int4': ['boolean', 'date', 'time', 'dict', 'integer'],
        'char': ['boolean', 'dict', 'fixed_length', 'fixed_length_hex', 'integer'],
        'long8': ['boolean', 'date', 'time', 'dict', 'integer'],
        'time': ['date', 'time', 'dict'],
        'decimal': ['dict'],
        'bigint': ['boolean', 'date', 'time', 'dict', 'integer'],
        'timestamp': ['date', 'time', 'dict']
      },
      dataCity: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      let datas = [...this.saveSelectFiled]
      datas.map(item => {
        item.column = item.columnName
        item.encoding = item.encoding ? item.encoding : ''
        item.isShardBy = item.isShardBy ? item.isShardBy : ''
        item.length = item.length ? item.length : ''
      })
      this.tableData = datas
    },
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      this.$router.push('/olap/createolap/completeCreate')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/reloadSet')
    },
    handleSelectionChange (val) {

    },
    // 选择对应的编码类型
    codingType (val) {
      for (let item in this.codingTypeData) {
        if (val === item) {
          this.encodingOption = this.codingTypeData[item]
        }
      }
    },
    // 添加聚合小组
    addaAggregation () {
      this.$store.dispatch('addAggregationList')
    },
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
    removelevelData (index, i) {
      if (this.aggregation_groups[index].select_rule.hierarchy_dims.length === 1) return this.$message.error('必须保留一个~')
      this.aggregation_groups[index].select_rule.hierarchy_dims.splice(i, 1)
    },
    removejointData (index, i) {
      if (this.aggregation_groups[index].select_rule.joint_dims.length === 1) return this.$message.error('必须保留一个~')
      this.aggregation_groups[index].select_rule.joint_dims.splice(i, 1)
    },
    // 添加维度黑白名单
    addimensionData () {
      this.$store.dispatch('AddimensionData')
    },
    removedimensionData (index) {
      if (this.savedimensionData.length === 1) return this.$message.error('必须保留一个~')
      this.savedimensionData.splice(index, 1)
    },
    // 添加高级列组合
    addhetComposeData () {
      this.$store.dispatch('AddhetComposeData')
    },
    removehetComposeData (index) {
      this.savehetComposeData.splice(index, 1)
    },
    // 选择维度
    getTotalModal (index, type, findIndex) {
      this.modalIndex = index
      this.$refs.selectFiled.dialog(type, index, findIndex)
    },
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
      savedimensionData: 'savedimensionData',
      selectDataidList: 'selectDataidList',
      savehetComposeData: 'savehetComposeData',
      aggregation_groups: 'aggregation_groups'
    })
  }
}
</script>

<style lang="stylus" scoped>
.advancedSet{
  padding 30px 100px
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
          padding 25px
          cursor pointer
        }
        >>>.el-tag{
          width 30%
          float left
          margin-left 1%
          margin-bottom 10px
          font-size 11px
          text-align center
          background #FBFBFB
          color #555555
          i{
            float right!important
            margin-top 8px
          }
        }
        .adds{
          border none!important
          padding 0
          width 100%
          display flex
          div{
            flex 1
            border 1px solid #cccccc
            padding 25px
            margin-left 80px
            margin-bottom 20px
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
          padding 25px
          border 1px solid #cccccc
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
          width 30%
          float left
          margin-left 1%
          margin-bottom 10px
          font-size 11px
          text-align center
          background #FBFBFB
          color #555555
          i{
            float right!important
            margin-top 8px
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
