<template>
  <div class="slectFiled">
    <el-dialog title="选择维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <!-- <div class="item" v-for="(n, index) in options" :key="index">
          <p>{{n.tableName}}</p>
          <div class="itemFind">
            <el-checkbox-group ref="group" v-model="selctCheckData">
              <el-checkbox-button v-for="item in n.list" @change="selectChange" :label="item" :key="item">{{item}}</el-checkbox-button>
            </el-checkbox-group>
          </div>
        </div> -->
        <el-checkbox-group ref="group" v-model="selctCheckData">
          <el-checkbox-button v-for="item in options" @change="selectChange" :label="item.value" :key="item.id">{{item.value}}</el-checkbox-button>
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
      dialogFormVisible: false,
      selctCheckData: [],
      options: [
        { comment: '啦啦啦啦1', value: 'lalalalal1', tableName: 'a1', list: ['lalalal1', 'lalalalal2', 'lalalala3', 'lalalal4'] },
        { comment: '啦啦啦啦2', value: 'lalalalal2', tableName: 'a2', list: ['bababab1', 'babababa2', 'babababa3', 'babababa4'] },
        { comment: '啦啦啦啦3', value: 'lalalalal3', tableName: 'a3', list: ['kekekek1', 'kekekek2', 'kekekeke3', 'kekekeke4'] },
        { comment: '啦啦啦啦4', value: 'lalalalal4', tableName: 'a4', list: ['ppppp1', 'ppppp2', 'ppppp3', 'ppppp4'] }
      ],
      index: '',
      type: '',
      findIndex: ''
    }
  },
  methods: {
    closeBtn () {
      this.dialogFormVisible = false
    },
    selectChange (value) {
      // console.log(this.selctCheckData, '啦啦啦啦啦')
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
    dialog (type, index, findIndex) {
      this.dialogFormVisible = true
      // this.options = this.saveNewSortList
      this.options = type !== 6 ? this.reloadNeedData : this.measureTableList.map(item => { return { value: item.name, id: item.id } })
      console.log(this.options, '无敌的')
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
      selectDataidList: 'selectDataidList',
      savedimensionDataId: 'savedimensionDataId',
      savehetComposeDataId: 'savehetComposeDataId',
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
        background #009688!important
        color #ffffff
        border 1px solid #009688!important
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
      border 1px solid #009688!important
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
