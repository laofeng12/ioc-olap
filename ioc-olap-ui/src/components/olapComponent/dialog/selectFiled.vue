<template>
  <div class="slectFiled">
    <el-dialog title="已选维度" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="container">
        <div class="item" v-for="(n, index) in options" :key="index">
          <p>{{n.name}}</p>
          <div class="itemFind">
            <el-tag v-for="(item, index) in n.list" :key="index">{{item.name}}</el-tag>
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
      value2: '',
      dialogFormVisible: false,
      tableData: [],
      options: [
        {
          name: '表名称1',
          list: [
            { id: 1, name: '表名称1' },
            { id: 1, name: '表名称1' },
            { id: 1, name: '表名称1' },
            { id: 1, name: '表名称1' },
            { id: 1, name: '表名称1' },
            { id: 1, name: '表名称1' },
            { id: 1, name: '表名称1' }
          ]
        },
        {
          name: '表名称2',
          list: [
            { id: 1, name: '表名称2' },
            { id: 1, name: '表名称2' },
            { id: 1, name: '表名称2' },
            { id: 1, name: '表名称2' },
            { id: 1, name: '表名称2' },
            { id: 1, name: '表名称2' },
            { id: 1, name: '表名称2' }
          ]
        },
        {
          name: '表名称2',
          list: [
            { id: 1, name: '表名称3' },
            { id: 1, name: '表名称3' },
            { id: 1, name: '表名称3' },
            { id: 1, name: '表名称3' },
            { id: 1, name: '表名称3' },
            { id: 1, name: '表名称3' },
            { id: 1, name: '表名称3' }
          ]
        }
      ]
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
      let map = {}
      let dast = []
      data.forEach((item, index) => {
        let ai = data[index]
        console.log(ai)
        if (!map[ai.tableName]) {
          dast.push({
            comment: item.comment,
            columnName: item.columnName,
            tableName: item.tableName,
            list: [ai]
          })
          // console.log('dast===', dast)
        } else {
          dast.forEach((n, i) => {
            let dj = dast[i]
            if (ai.tableName === dj.tableName) {
              dj.list.push(ai)
            }
          })
        }
      })
      console.log(dast, 'lalalalal')
    }
  }
}
</script>

<style lang="stylus" scoped>
.slectFiled{
  >>>.el-dialog__body{
    .el-tag{
      width 20%
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
