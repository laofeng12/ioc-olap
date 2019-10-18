<template>
  <div class="selectModal">
    <el-dialog title="已选择数据表" :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="main" v-if="tableData&&tableData.length">
        <el-tooltip v-for="(item, index) in tableData" :key="index" class="item" effect="dark" :content="item.label" placement="top">
          <el-tag type="">{{item.label}}</el-tag>
        </el-tooltip>
      </div>
      <div v-else style="text-align:center;margin-top:100px;">暂无数据</div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBtn()">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { getLocalStorage } from '@/utils/index'
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
      tableData: []
    }
  },
  methods: {
    closeBtn () {
      this.tableData = []
    },
    submitBtn (index) {
      this.dialogFormVisible = false
    },
    dialog (val, data) {
      this.dialogFormVisible = true
      // this.tableData = [...this.saveSelectTable, ...this.saveLocalSelectTable]
      this.tableData = (this.selectTableTotal || getLocalStorage('selectTableTotal')).filter(res => {
        return res.label
      })
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      selectTableTotal: 'selectTableTotal',
      saveLocalSelectTable: 'saveLocalSelectTable'
    })
  }
}
</script>

<style lang="stylus" scoped>
.selectModal{
  >>>.el-dialog__body{
    height 300px
    overflow-y auto
    .el-tag{
      width 28%
      float left
      margin-left 3%
      margin-bottom 20px
      font-size 12px
      background #0486FE
      color #ffffff
      text-align center
      border-radius 0
      text-overflow: ellipsis;
      overflow: hidden;
      i{
        float right
        margin-top 8px
        color #ffffff
      }
    }
  }
}
</style>
