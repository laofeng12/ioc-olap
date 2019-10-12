<template>
  <div class="slectFiled">
    <el-dialog title="已选维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <div class="item" v-for="(n, index) in options" :key="index">
          <p>{{n.table}}</p>
          <div class="itemFind">
            <el-tag v-for="(item, index) in n.columns" :key="index">{{item}}</el-tag>
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
      tableData: [],
      options: []
    }
  },
  methods: {
    closeBtn () {
      this.tableData = []
    },
    submitBtn (index) {
      this.dialogFormVisible = false
    },
    dialog (data) {
      console.log(this.saveNewSortListstructure)
      // 如果修改了表的别名 需要把之前存在的表去掉
      this.dialogFormVisible = true
      this.options = this.saveNewSortListstructure
      // this.$store.dispatch('changeAlias', this.saveNewTitle).then(_ => {
      //   this.dialogFormVisible = true
      //   this.options = this.saveNewSortListstructure
      // })
    }
  },
  computed: {
    ...mapGetters({
      saveNewSortListstructure: 'saveNewSortListstructure',
      saveNewTitle: 'saveNewTitle'
    })
  }
}
</script>

<style lang="stylus" scoped>
.slectFiled{
  >>>.el-dialog__body{
    .el-tag{
      margin-right 20px
      width 200px
      margin-bottom 10px
      background #009688
      color #ffffff
      text-align center
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
        padding 10px
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
