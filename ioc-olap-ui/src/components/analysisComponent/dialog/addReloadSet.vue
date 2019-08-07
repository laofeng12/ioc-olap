<template>
  <div class="addMeasure">
    <el-dialog title="过滤设置" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form :model="formData" :rules="rules" ref="formData">
        <el-form-item label="选择字段表" :label-width="formLabelWidth" prop="TABLENAME">
          <el-select v-model="formData.TABLENAME" placeholder="请选择字段表" @change="selectTable">
            <el-option v-for="(item, index) in tableOptions" :key="index" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择字段" :label-width="formLabelWidth" prop="FIELD">
          <el-select v-model="formData.FIELD" placeholder="请选择字段">
            <el-option v-for="(item, index) in textOptions" :key="index" :label="item.name" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择过滤条件" :label-width="formLabelWidth" prop="PATTERN">
          <el-select v-model="formData.PATTERN" placeholder="请选择过滤条件">
            <el-option v-for="item in filterOptions" :key="item.value" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设置过滤值" :label-width="formLabelWidth" prop="PARAMETER">
          <el-input v-model="formData.PARAMETER" autocomplete="off" placeholder="请输入过滤值"></el-input>
          <el-input v-if="formData.PATTERN === 'BETWEED'" v-model="formData.PARAMETERBE" autocomplete="off" placeholder="请输入过滤值"></el-input>
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
      formData: {
        TABLENAME: ''
      },
      isNew: 1,
      formLabelWidth: '120px',
      dialogFormVisible: false,
      tableData: [],
      typeOptions: [],
      tableOptions: [
        // { label: 'a' },
        // { label: 'b' },
        // { label: 'c' }
      ],
      textOptions: [
        // { comment: 'aaa', columnName: 'aaa' },
        // { comment: 'bbb', columnName: 'bbb' },
        // { comment: 'vccc', columnName: 'vccc' },
        // { comment: 'vvvv', columnName: 'vvvv' },
        // { comment: 'bbbbb', columnName: 'bbbbb' }
      ],
      filterOptions: [
        { value: '0', label: '=' },
        { value: '1', label: '<>' },
        { value: '2', label: '>' },
        { value: '3', label: '<' },
        { value: '4', label: '>=' },
        { value: '5', label: '<=' },
        { value: '6', label: 'BETWEED' }
      ],
      rules: {
        TABLENAME: [
          { required: true, message: '请选择字段表', trigger: 'change' }
        ],
        FIELD: [
          { required: true, message: '请选择字段', trigger: 'change' }
        ],
        PATTERN: [
          { required: true, message: '请选择过滤条件', trigger: 'change' }
        ],
        PARAMETER: [
          { required: true, message: '请设置过滤值', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.tableOptions = [...this.selectTableTotal] || []
    },
    closeBtn () {
      this.dialogFormVisible = false
      this.$refs.formData.clearValidate()
    },
    selectTable (val) {
      const params = {
        dsDataSourceId: 2,
        tableName: val
      }
      // this.$store.dispatch('GetColumnList', params).then(res => {
      //   this.textOptions = res.data
      // })
      this.$store.dispatch('GetResourceInfo', { resourceId: '1' }).then(res => {
        this.textOptions = res.data.columns
      })
    },
    submitBtn () {
      this.$refs.formData.validate((valid) => {
        if (valid) {
          this.dialogFormVisible = false
          let id = Math.random().toString(36).substr(3)
          this.formData['id'] = id
          this.formData['isNew'] = this.isNew
          this.$store.dispatch('ReloadFilterTableList', this.formData).then(res => {
            if (res) {
              this.$message.success('保存成功~')
              this.formData = {}
              this.$parent.init()
            }
          })
        }
      })
    },
    dialog (data) {
      this.dialogFormVisible = true
      if (data) {
        this.formData = data
        this.isNew = 1
      } else {
        this.formData = {}
        this.isNew = 0
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal'
    })
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
  >>>.el-select{
    width 100%
  }
  >>>.is-focus{
    .el-input__suffix{
      top 0px
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
