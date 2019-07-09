<template>
  <div class="advancedSet">
     <el-form>
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
              <div class="box_r"></div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r"></div>
            </div>
            <div class="item_box adds">
              <span>层级维度</span>
              <div>
                <div class="box_r"></div>
                <p>
                  <i class="el-icon-remove" @click="removelevelData"></i>
                  <i class="el-icon-circle-plus" @click="addlevelData"></i>
                </p>
              </div>
            </div>
            <div class="item_box adds">
              <span>联合维度</span>
              <div>
                <div class="box_r"></div>
                <p>
                  <i class="el-icon-remove" @click="removejointData"></i>
                  <i class="el-icon-circle-plus" @click="addjointData"></i>
                </p>
              </div>
            </div>
          </el-card>
        </div>
     </el-form>
     <add-reload-set ref="dialog"></add-reload-set>
     <steps class="steps" :step="6" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
import addReloadSet from '@/components/olapComponent/dialog/addReloadSet'
export default {
  components: {
    steps, addReloadSet
  },
  data () {
    return {
      autoReload: false,
      dataMany: false,
      radio: 3,
      aggregationData: [
        {
          containData: [],
          necessaryData: [],
          levelData: [],
          jointData: []
        }
      ],
      options: [{
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
      tableData: [
        { apiName: '111', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '222', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '333', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '444', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '555', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' }
      ]
    }
  },
  methods: {
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      // this.$router.push('/olap/createolap/reloadSet')
      this.$message.error('暂未开发')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/reloadSet')
    },
    addReloadSet () {
      this.$refs.dialog.dialog()
    },
    handleSelectionChange (val) {

    },
    // 添加聚合小组
    addaAggregation () {
      this.aggregationData.push({
        containData: [],
        necessaryData: [],
        levelData: [],
        jointData: []
      })
    },
    handleRms (index) {
      if (this.aggregationData.length === 1) return this.$message.error('必须保留一个~')
      this.aggregationData.splice(index, 1)
    },
    // 添加层级维度
    addlevelData () {
      this.levelData.push({
        index: ''
      })
    },
    removelevelData (index) {

    },
    // 添加联合维度
    addjointData () {
      this.jointData.push({
        index: ''
      })
    },
    removejointData (index) {

    },
    changeUploadNum (val) {
      console.log(val)
    },
    changeDataMany (val) {
      console.log(val)
    }
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
        div{
          border 1px solid #cccccc
          flex 1
          padding 25px
          cursor pointer
        }
        .adds{
          border none!important
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
    }
  }
}
</style>
