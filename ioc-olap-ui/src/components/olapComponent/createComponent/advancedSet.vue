<template>
  <div class="advancedSet">
     <el-form :model='formData' ref="formData">
        <el-form-item label="高级设置" class="item_line"></el-form-item>
        <div class="aggregation">
          <div class="aggregation_head">
            <span>维度分组聚合</span>
            <span style="color:green;margin-left:10px;cursor:pointer;" @click="addaAggregation">+添加聚合小组</span>
          </div>
          <el-card class="box-card" v-for="(item, index) in aggregationData" :key="index">
            <div slot="header" class="clearfix">
              <span>聚合小组1</span>
              <el-button style="float:right;padding:3px 0;" type="text" @click="handleRms(index)">删除</el-button>
            </div>
            <div class="item_box">
              <span>包含维度</span>
              <div class="box_r" @click="getTotalModal"></div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r"></div>
            </div>
            <div class="item_box noflex">
              <span>层级维度</span>
              <div class="adds" v-for="(n, i) in item.levelData" :key="i">
                <div class=""></div>
                <p>
                  <i class="el-icon-remove" @click="removelevelData(index, i)"></i>
                  <i class="el-icon-circle-plus" @click="addlevelData(i)"></i>
                </p>
              </div>
            </div>
            <div class="item_box noflex">
              <span>联合维度</span>
              <div class="adds" v-for="(k, t) in item.jointData" :key="t">
                <div class=""></div>
                <p>
                  <i class="el-icon-remove" @click="removejointData(index, t)"></i>
                  <i class="el-icon-circle-plus" @click="addjointData(t)"></i>
                </p>
              </div>
            </div>
          </el-card>
        </div>
        <div class="setRowkeys">
          <p style="margin:20px 0">Rowkeys设置</p>
          <el-table
            :data="formData.tableData"
            ref="multipleTable"
            tooltip-effect="dark"
            @selection-change="handleSelectionChange"
            style="margin-top: 10px;">
            <el-table-column prop="apiName" label="序号" align="center"> </el-table-column>
            <el-table-column prop="type" label="字段名称" align="center"> </el-table-column>
            <el-table-column label="编码类型" align="center">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.catalogName'" class="selects">
                  <el-select v-model="scope.row.catalogName" placeholder="请选择">
                    <el-option v-for="(item, index) in dataType" :key="index" :label="item.codename" :value="item.codename"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column label="长度" width="100" align="center">
              <template slot-scope="scope">
                <div>
                  <el-input type="text" v-model="scope.row.apiPaths"></el-input>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="apiPaths" label="碎片区" align="center">
              <template slot-scope="scope">
                <el-form-item :prop="'tableData.' + scope.$index + '.catalogName'" class="selects">
                  <el-select v-model="scope.row.catalogName" placeholder="请选择">
                    <el-option v-for="(item, index) in dataCity" :key="index" :label="item.codename" :value="item.codename"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <div class="listSet">
          <span>维度黑白名单设置</span>
          <div class="listSet__box">
            <div class="adds" v-for="(n, i) in dimensionData" :key="i">
              <div class=""></div>
              <p>
                <i class="el-icon-remove" @click="removedimensionData(index, i)"></i>
                <i class="el-icon-circle-plus" @click="addimensionData(i)"></i>
              </p>
            </div>
          </div>
        </div>
        <el-form-item label="模型构建引擎">
          <template>
            <div>
              <el-select v-model="formData.engine" placeholder="请选择">
                <el-option v-for="item in engineOptions" :key="item.id" :label="item.label" :value="item.id"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <div class="listSet hetCompose">
          <span>高级列组合</span>
          <div class="listSet__box hetCompose__box" v-if="hetComposeData && hetComposeData.length">
            <div class="adds" v-for="(n, i) in hetComposeData" :key="i">
              <div class=""></div>
              <p>
                <i class="el-icon-remove" @click="removehetComposeData(index, i)"></i>
                <i class="el-icon-circle-plus" @click="addhetComposeData(i)"></i>
              </p>
            </div>
          </div>
          <div class="listSet__box hetCompose__box" v-else>
            <p class="nos">
              <i class="el-icon-circle-plus" @click="addhetComposeData(i)"></i>
            </p>
          </div>
        </div>
     </el-form>
     <select-filed ref="selectFiled"></select-filed>
     <steps class="steps" :step="6" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
import selectFiled from '@/components/olapComponent/dialog/selectFiled'
import { mapGetters } from 'vuex'
export default {
  components: {
    steps, selectFiled
  },
  data () {
    return {
      autoReload: false,
      dataMany: false,
      radio: 3,
      formData: {
        engine: '1',
        tableData: [
          { apiName: '111', type: '递归', catalogName: 'string', apiPaths: '' },
          { apiName: '222', type: '递归', catalogName: 'string', apiPaths: '' },
          { apiName: '333', type: '递归', catalogName: 'string', apiPaths: '' },
          { apiName: '444', type: '递归', catalogName: 'string', apiPaths: '' },
          { apiName: '555', type: '递归', catalogName: 'string', apiPaths: '' }
        ]
      },
      aggregationData: [ // 聚合小组
        {
          containData: [], // 包含维度
          necessaryData: [], // 必要维度
          levelData: [ // 层级维度
            { index: '' }
          ],
          jointData: [ // 联合维度
            { index: '' }
          ]
        }
      ],
      dimensionData: [ // 维度黑白名单
        { index: '' }
      ],
      engineOptions: [ // 模型构建引擎
        { engine: '1', label: 'MapReduce ' },
        { engine: '2', label: 'MapReduce ' }
      ],
      hetComposeData: [], // 高级组合
      dataType: [{
        value: '选项1',
        label: '黄金糕'
      }, {
        value: '选项2',
        label: '双皮奶'
      }, {
        value: '选项3',
        label: '蚵仔煎'
      }, {
        value: '选项4',
        label: '龙须面'
      }, {
        value: '选项5',
        label: '北京烤鸭'
      }],
      dataCity: []
    }
  },
  methods: {
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      this.$router.push('/olap/createolap/completeCreate')
      // this.$message.error('暂未开发')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/reloadSet')
    },
    handleSelectionChange (val) {

    },
    // 添加聚合小组
    addaAggregation () {
      this.aggregationData.push({
        containData: [],
        necessaryData: [],
        levelData: [
          { index: '' }
        ],
        jointData: [
          { index: '' }
        ]
      })
    },
    handleRms (index) {
      if (this.aggregationData.length === 1) return this.$message.error('必须保留一个~')
      this.aggregationData.splice(index, 1)
    },
    // 添加层级维度
    addlevelData (index) {
      this.aggregationData[index].levelData.push({
        index: ''
      })
    },
    removelevelData (index, i) {
      if (this.aggregationData[index].levelData.length === 1) return this.$message.error('必须保留一个~')
      this.aggregationData[index].levelData.splice(i, 1)
    },
    // 添加联合维度
    addjointData (index) {
      this.aggregationData[index].jointData.push({
        index: ''
      })
    },
    removejointData (index, i) {
      if (this.dimensionData.length === 1) return this.$message.error('必须保留一个~')
      this.dimensionData.splice(i, 1)
    },
    // 添加维度黑白名单
    addimensionData (index) {
      this.dimensionData.push({
        index: ''
      })
    },
    removedimensionData (index) {
      if (this.dimensionData.length === 1) return this.$message.error('必须保留一个~')
      this.dimensionData.splice(index, 1)
    },
    // 添加高级列组合
    addhetComposeData (index) {
      this.hetComposeData.push({
        index: ''
      })
    },
    removehetComposeData (index) {
      this.hetComposeData.splice(index, 1)
    },
    changeUploadNum (val) {
      console.log(val)
    },
    changeDataMany (val) {
      console.log(val)
    },
    getTotalModal () {
      this.$refs.selectFiled.dialog(this.saveSelectFiled)
    }
  },
  computed: {
    ...mapGetters({
      saveSelectFiled: 'saveSelectFiled'
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
    }
    .nos{
      margin-left 100px
      margin-top -10px
    }
  }
}
</style>
