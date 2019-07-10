<template>
  <div class="slectFiled">
    <el-dialog title="已选维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <div class="item" v-for="(n, index) in options" :key="index">
          <p>{{n.tableName}}</p>
          <div class="itemFind">
            <el-tag v-for="(item, index) in n.list" :key="index">{{item.columnName}}</el-tag>
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
      this.dialogFormVisible = true
      var map = {}
      var dest = []
      for (var i = 0; i < data.length; i++) {
        var ai = data[i]
        if (!map[ai.tableName]) {
          dest.push({
            comment: ai.comment,
            tableName: ai.tableName,
            columnName: ai.columnName,
            list: [ai]
          })
          map[ai.tableName] = ai
        } else {
          for (var j = 0; j < dest.length; j++) {
            var dj = dest[j]
            if (dj.tableName === ai.tableName) {
              dj.list.push(ai)
              break
            }
          }
        }
      }
      this.options = dest
    }
  }
}
</script>

<style lang="stylus" scoped>
.slectFiled{
  >>>.el-dialog__body{
    .el-tag{
      margin-right 20px
      margin-bottom 10px
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
