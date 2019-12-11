<template>
  <div class="selectModal">
    <el-dialog title="已选择数据表" 
    :close-on-click-modal="false"
    :visible.sync="dialogFormVisible" @close="closeBtn">
      <div class="main" v-if="tableData && tableData.length">
        <el-tooltip v-for="(item, index) in tableData" :key="index" class="item" effect="dark" :content="item.resourceTableName" placement="top">
          <el-tag type="" closable @close="removeTag(item)">{{item.resourceTableName}}</el-tag>
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
  name: 'selectModal',
  props: {
    border: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      dialogFormVisible: false
    }
  },
  methods: {
    closeBtn () {
      // this.tableData = []
    },
    submitBtn (index) {
      this.dialogFormVisible = false
    },
    dialog (val, data) {
      this.dialogFormVisible = true
    },
    // 移除选择
    async removeTag (data) {
      await this.$store.commit('REMOVE_SETSELCT_TABLE_COUNT', data)
      this.$emit('unselect', data)
      // await this.$root.eventBus.$emit('modal-remove', data)
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      selectTableTotal: 'selectTableTotal',
      saveLocalSelectTable: 'saveLocalSelectTable'
    }),
    tableData () {
      return this.selectTableTotal.length ? this.selectTableTotal : []
      // return (this.selectTableTotal || getLocalStorage('selectTableTotal')).filter(res => {
      //   return res.label
      // })
      // if (this.selectTableTotal.length) {
      //   return this.selectTableTotal
      // }
      // return []
    }
  }
}
</script>

<style lang="stylus" scoped>
.selectModal{
  >>>.el-dialog {
    width 60%;
  }
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
