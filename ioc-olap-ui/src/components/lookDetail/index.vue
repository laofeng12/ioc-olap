<template>
  <div class="lookDetails" v-if="tableData && tableData.length">
    <el-dialog title="" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form class="my-table" :data="tableData" tooltip-effect="dark">
          <el-form-item v-for="(item, index) in colConfigs" :key="index" v-if="item!==''" :label="item.label+'：'">
              <img v-if="item.logo" :src="tableData[0][item.logo] ? tableData[0][item.logo]:defaultUrl" width="30" height="30" alt="">
              <a v-else-if="item.link" :href="tableData[0][item.link]" target="_blank">{{tableData[0][item.prop] || '未设置'}}</a>
              <span v-else>{{tableData[0][item.prop] || '未设置'}}</span>
          </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBtn()">关闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    // colConfigs: Array,
    // tableData: Array,
    border: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      defaultUrl: require('@/assets/img/logo.png'),
      dialogFormVisible: false,
      colConfigs: [],
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
      this.colConfigs = val
      this.tableData.push(data)
    }
  }
}
</script>

<style lang="stylus" scoped>
.lookDetails{
  >>>a{
    text-decoration: underline
  }
  >>>.el-dialog__body{
    padding 5px 40px 30px 40px
    .el-form-item{
      margin-bottom 5px
      border-bottom 1px solid #f0f0f0
    }
  }
}
</style>
