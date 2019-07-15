<template>
  <div class="slectFiled">
    <el-dialog title="选择维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <div class="item" v-for="(n, index) in options" :key="index">
          <p>{{n.tableName}}</p>
          <div class="itemFind">
            <el-checkbox-group ref="group" v-model="selctCheckData" true-label="false">
              <el-checkbox-button v-for="item in n.list" @change="selectChange" :label="item" :key="item.id">{{item.columnName}}</el-checkbox-button>
            </el-checkbox-group>
            <!-- <el-tag v-for="(item, index) in n.list" :key="index">{{item.columnName}}</el-tag> -->
          </div>
        </div>
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
        { comment: '啦啦啦啦1', columnName: 'lalalalal1', tableName: 'a1', list: [ { comment: '啦啦啦啦', columnName: 'lalalalal' }, { comment: '啦啦啦啦', columnName: 'lalalalal1' } ] },
        { comment: '啦啦啦啦2', columnName: 'lalalalal2', tableName: 'a2', list: [ { comment: '啦啦啦啦', columnName: 'lalalala2' }, { comment: '啦啦啦啦', columnName: 'lalalala22' } ] },
        { comment: '啦啦啦啦3', columnName: 'lalalalal3', tableName: 'a3', list: [ { comment: '啦啦啦啦', columnName: 'lalalala3' }, { comment: '啦啦啦啦', columnName: 'lalalala33' } ] },
        { comment: '啦啦啦啦4', columnName: 'lalalalal4', tableName: 'a4', list: [ { comment: '啦啦啦啦', columnName: 'lalalala4' }, { comment: '啦啦啦啦', columnName: 'lalalala44' } ] }
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
      console.log(index)
      this.dialogFormVisible = true
      // 判断点击的是否是当前的维度框
      if (type !== this.type || index !== this.index || findIndex !== this.findIndex) {
        this.selctCheckData = []
      }
      // this.options = this.saveNewSortList
      this.index = index
      this.type = type
      this.findIndex = findIndex
    },
    resetsData () {
      this.selctCheckData = []
    }
  },
  computed: {
    ...mapGetters({
      saveNewSortList: 'saveNewSortList'
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
      width 200px
      margin-left 20px
      margin-bottom 20px
      border-left 1px solid #DCDFE6
      span{
        text-align center
        width 100%
        font-size 11px
      }
    }
    .el-checkbox-button:last-child{
    }
    .el-checkbox-button:last-child .el-checkbox-button__inner{
      border-radius 0
    }
    .el-checkbox-button:first-child .el-checkbox-button__inner{
      border-radius 0
      border-left none
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
