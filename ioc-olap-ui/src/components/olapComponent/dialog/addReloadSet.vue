<template>
  <div class="addMeasure">
    <el-dialog title="过滤设置" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form :model="form" :rules="rules">
        <el-form-item label="选择字段表" :label-width="formLabelWidth">
          <el-select v-model="form.region" placeholder="请选择字段表">
            <el-option v-for="item in computeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择字段" :label-width="formLabelWidth">
          <el-select v-model="form.type" placeholder="请选择字段">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择过滤条件" :label-width="formLabelWidth">
          <el-select v-model="form.value" placeholder="请选择过滤条件">
            <el-option v-for="item in datas" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设置度量值" :label-width="formLabelWidth">
          <el-input v-model="form.name" autocomplete="off" placeholder="请输入度量值"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closeBtn()">取消</el-button>
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
      form: {
        answers: []
      },
      formLabelWidth: '100px',
      dialogFormVisible: false,
      tableData: [],
      typeOptions: [],
      datas: [],
      computeOptions: [{
        value: 'SUM',
        label: 'SUM'
      }, {
        value: 'MAX',
        label: 'MAX'
      }, {
        value: 'MIN',
        label: 'MIN'
      }, {
        value: 'COUNT',
        label: 'COUNT'
      }, {
        value: 'COUNT_DISTINCT',
        label: 'COUNT_DISTINCT '
      }, {
        value: 'TOP_N',
        label: 'TOP_N'
      }, {
        value: 'EXTENDED_COLUMN',
        label: 'EXTENDED_COLUMN'
      }, {
        value: 'PERCENTILE',
        label: 'PERCENTILE'
      }],
      rules: {
        answertext: [
          { required: false, message: '请选择字段', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    closeBtn () {
      this.form.region = ''
      this.dialogFormVisible = false
    },
    submitBtn (index) {
      this.dialogFormVisible = false
    },
    dialog (tableData) {
      this.dialogFormVisible = true
    }
  }
}
</script>

<style lang="stylus" scoped>
.addMeasure{
  height 300px
  padding 20px
  overflow auto
  >>>.el-dialog__body{
    .el-tag{
      margin-right 20px
      margin-bottom 10px
    }
  }
  .coutDistinct, .coutTopn{
    >>>.el-form-item{
      margin-bottom 10px
      .el-input__inner{
        height 30px
        .el-input__suffix{
          top 5px
        }
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
