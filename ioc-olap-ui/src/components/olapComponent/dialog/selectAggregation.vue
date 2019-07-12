<template>
  <div class="slectFiled">
    <el-dialog title="选择维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <div class="item" v-for="(n, index) in options" :key="index">
          <p>{{n.tableName}}</p>
          <div class="itemFind">
            <el-checkbox-group v-model="selctCheckData" true-label="1" false-label="0">
              <el-checkbox-button v-for="item in n.list" @change="selectChange" :label="item" :key="item.id">{{item.columnName}}</el-checkbox-button>
            </el-checkbox-group>
            <!-- <el-tag v-for="(item, index) in n.list" :key="index">{{item.columnName}}</el-tag> -->
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBtn()">取消</el-button>
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
      tableData: [],
      options: []
    }
  },
  methods: {
    closeBtn () {
      this.tableData = []
    },
    selectChange (value) {
      console.log(value, '====', this.selctCheckData)
    },
    submitBtn (index) {
      this.dialogFormVisible = false
    },
    dialog (data) {
      this.dialogFormVisible = true
      this.options = this.saveNewSortList
      console.log(this.options)
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
      display: flex;
      justify-content: flex-start;
      flex-flow: row-wrap;
    }
    .el-checkbox-button{
      width 200px
      margin-left 20px
      margin-bottom 20px
      span{
        text-align center
        width 100%
        font-size 11px
      }
    }
    .el-checkbox-button:last-child .el-checkbox-button__inner{
      border-radius 0
    }
    .el-checkbox-button:first-child .el-checkbox-button__inner{
      border-radius 0
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
