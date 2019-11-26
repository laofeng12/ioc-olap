<template>
  <div class="slectFiled">
    <el-dialog title="选择维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <el-checkbox-group ref="group" v-model="selctCheckData" v-if="type === 1" size="mini">
          <el-checkbox-button  v-for="(item, index) in options" :label="item.value" :key="index">{{item.value}}</el-checkbox-button>
        </el-checkbox-group>
        <el-checkbox-group ref="group" v-model="selctCheckData" v-else-if="type === 5" size="mini">
          <el-checkbox-button  v-for="item in options" :label="item.value" :key="item.id">{{item.value}}</el-checkbox-button>
        </el-checkbox-group>
        <el-checkbox-group ref="group" v-model="selctCheckData" v-else-if="type === 6" size="mini">
          <el-checkbox-button  v-for="item in options" :label="item.value" :key="item.id">{{item.value}}</el-checkbox-button>
        </el-checkbox-group>
        <el-checkbox-group ref="group" v-model="selctCheckData" size="mini" v-else>
          <el-checkbox-button v-if="optionData && optionData.length > 0" v-for="(item, index) in optionData" :label="item" :key="index">{{item}}</el-checkbox-button>
          <!-- <el-checkbox-button v-for="(item, index) in options" :label="item" :key="index">{{item}}</el-checkbox-button> -->
        </el-checkbox-group>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closeBtn()">取消</el-button>
        <el-button type="primary" @click="submitBtn()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    border: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      optionData: [], // 处理后的数据 递减 如果其他类别选择后就不能选择
      reloadNeedDataList: [], // 定义一个数组来接收选择的数据
      dialogFormVisible: false,
      selctCheckData: [], // 选择的列id
      options: [], // 接收第三步选择的数据
      index: '', // 记录当前选择的是维度的哪个框
      type: '', // 记录当前选择的是哪个维度
      findIndex: '' // 记录当前点击的是维度下的哪个列
    }
  },
  mounted () {
    this.resortAggregation()
    this.init()
  },
  watch: {
    // '$route' () {
    //   this.init()
    // }
  },
  methods: {
    init () {
      // 处理编辑的时候
      this.reloadNeedDataList = JSON.parse(JSON.stringify(this.reloadNeedData))
    },
    resortAggregation () {
      this.selectDataidList.forEach(item => {
        if (item.levelDataId.length === 0) item.levelDataId = [[]]
        if (item.jointDataId.length === 0) item.jointDataId = [[]]
      })
    },
    closeBtn () {
      this.dialogFormVisible = false
    },
    submitBtn (index) {
      this.dialogFormVisible = false
      let slectData = {
        data: this.selctCheckData,
        index: this.index,
        type: this.type,
        findIndex: this.findIndex
      }
      this.$store.dispatch('SaveAggregationWD', slectData)
    },
    getArrDifference (arr1, arr2) {
      return arr1.concat(arr2).filter(function (v, i, arr) {
        return arr.indexOf(v) === arr.lastIndexOf(v)
      })
    },
    dialog (type, index, findIndex) {
      this.dialogFormVisible = true
      switch (type) {
        case 1:
          this.options = this.reloadNeedDataList
          break
        case 5:
          this.options = this.reloadNeedDataList
          break
        case 6:
          this.options = this.measureTableList.map(item => { return { value: item.name, id: item.id } })
          break
        default:
          // this.options = this.saveselectIncludesData
          // 递减的功能（选择过后下面的就没法选择）
          let arrD = [...new Set(this.recordingData)]
          this.optionData = [...this.getArrDifference(this.saveselectIncludesData, arrD)]
          break
      }
      this.index = index
      this.type = type
      this.findIndex = findIndex
      switch (type) {
        case 1:
          this.selctCheckData = this.selectDataidList[this.index].includesId
          break
        case 2:
          this.selctCheckData = this.selectDataidList[this.index].necessaryDataId
          break
        case 3:
          this.selctCheckData = this.selectDataidList[this.index].levelDataId[this.findIndex]
          break
        case 4:
          this.selctCheckData = this.selectDataidList[this.index].jointDataId[this.findIndex]
          break
        case 5:
          this.selctCheckData = this.savedimensionDataId[this.index]
          break
        case 6:
          this.selctCheckData = this.savehetComposeDataId[this.index]
          break
        default:
          break
      }
    },
    resetsData () {
      this.selctCheckData = []
    }
  },
  computed: {
    ...mapGetters({
      saveNewSortList: 'saveNewSortList',
      ModelAllList: 'ModelAllList',
      recordingData: 'recordingData',
      aggregation_groups: 'aggregation_groups',
      selectDataidList: 'selectDataidList',
      savedimensionDataId: 'savedimensionDataId',
      savehetComposeDataId: 'savehetComposeDataId',
      saveselectIncludesData: 'saveselectIncludesData', // 选择过的包含维度
      measureTableList: 'measureTableList', // 度量数据
      reloadNeedData: 'reloadNeedData' // 包含维度
    })
  }
}
</script>

<style lang="stylus" scoped>
.slectFiled{
  >>>.el-dialog{
    width 70%
  }
  >>>.el-dialog__body{
    .el-checkbox-group{
    }
    .el-checkbox-button{
      // width 200px
      margin-left 20px
      margin-bottom 20px
      // border-left 1px solid #DCDFE6
      span{
        text-align center
        width 100%
        font-size 11px
      }
    }
    .el-checkbox-button__inner{
      border 1px solid #DCDFE6
    }
    .is-checked{
      .el-checkbox-button__inner{
        background #0486FE!important
        color #ffffff
        border 1px solid #0486FE!important
      }
    }
    .el-checkbox-button:last-child{
    }
    .el-checkbox-button:last-child .el-checkbox-button__inner{
      border-radius 0
    }
    .el-checkbox-button:first-child .el-checkbox-button__inner{
      border-radius 0
      // border-left none
    }
    .el-checkbox-button.is-checked .el-checkbox-button__inner{
      border 1px solid #0486FE!important
    }
  }
  .container{
    border 1px solid #cccccc
    border-radius:5px;
    height 300px
    padding 20px
    overflow auto
    .item{
      margin-bottom 10px
      .itemFind{
        padding 30px 10px 10px 10px
        margin-top 10px
        border 1px solid #cccccc
        border-radius:5px;
      }
    }
  }
  .container::-webkit-scrollbar{
    width 8px
    height 8px
    background #fff
  }
  .container::-webkit-scrollbar-track{
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color:#fff;
  }
  .container::-webkit-scrollbar-thumb{
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #f0f0f0;
  }
}
</style>
