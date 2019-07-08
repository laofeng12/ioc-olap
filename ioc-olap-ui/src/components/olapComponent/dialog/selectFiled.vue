<template>
  <div class="setfactTable">
    <el-dialog title="事实表设置" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-select v-model="value2" placeholder="请选择">
        <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
      </el-select>
      <p style="margin-top:10px;">说明：</p>
      <p>1、事实表为用来存储事实的度量值和个维度的码值，即存储业务实际的发生值，如空气的检测值、水污染的检测值等；</p>
      <p>2、OLAP分析建立的模型是对事实表的各值从多维度进行分析，解析数据；</p>
      <p>3、事实表的选择影响后续模型的建立，请选择需要分析的数值作为事实表；</p>
      <p>4、切换事实表，将清除当前已建立的关系，请谨慎操作。</p>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBtn()">关闭</el-button>
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
      }]
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
    }
  }
}
</script>

<style lang="stylus" scoped>
.setfactTable{
  >>>.el-dialog__body{
  }
  >>>.el-icon-arrow-up{
    margin-top 9px
  }
  >>>.el-icon-arrow-up.is-reverse{
    margin-top -3px
  }
  p{
    font-size 12px
  }
}
</style>
