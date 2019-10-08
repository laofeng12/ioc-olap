<template>
  <div class="reloadSetModal">
    <el-dialog title="过滤设置" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form :model="formData" :rules="rules" ref="formDataModel">
        <el-form-item label="选择字段表" :label-width="formLabelWidth" prop="tableName">
          <el-select v-model="formData.tableName" placeholder="请选择字段表" @change="selectTable">
            <el-option v-for="(item, index) in tableOptions" :key="index" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择字段" :label-width="formLabelWidth" prop="field">
          <el-select v-model="formData.field" placeholder="请选择字段">
            <el-option v-for="(item, index) in textOptions" :key="index" :label="item.name" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择过滤条件" :label-width="formLabelWidth" prop="pattern">
          <el-select v-model="formData.pattern" placeholder="请选择过滤条件">
            <el-option v-for="item in filterOptions" :key="item.value" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="设置过滤值" :label-width="formLabelWidth" prop="parameter">
          <el-input v-model="formData.parameter" autocomplete="off" placeholder="请输入过滤值"></el-input>
          <el-input v-if="formData.pattern === 'BETWEED'" v-model="formData.parameterbe" autocomplete="off" placeholder="请输入过滤值"></el-input>
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
      tableOptions: [],
      textOptions: [],
      filterOptions: [
        { value: '0', label: '=' },
        { value: '1', label: '<>' },
        { value: '2', label: '>' },
        { value: '3', label: '<' },
        { value: '4', label: '>=' },
        { value: '5', label: '<=' }
        // { value: '6', label: 'BETWEED' }
      ],
      rules: {
        tableName: [
          { required: true, message: '请选择字段表', trigger: 'change' }
        ],
        field: [
          { required: true, message: '请选择字段', trigger: 'change' }
        ],
        pattern: [
          { required: true, message: '请选择过滤条件', trigger: 'change' }
        ],
        parameter: [
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
      this.selectTableTotal.map(item => { item.filed = item.label === this.jointResultData.fact_table.split('.')[1] ? 1 : 0 })
      this.tableOptions = this.selectTableTotal.filter(res => { return res.filed === 1 })
      // this.tableOptions = [...this.selectTableTotal] || []
    },
    closeBtn () {
      this.dialogFormVisible = false
      this.$refs.formDataModel.clearValidate()
    },
    selectTable (val) {
      // const params = {
      //   dsDataSourceId: 2,
      //   tableName: val
      // }
      // this.$store.dispatch('GetColumnList', params).then(res => {
      //   this.textOptions = res.data
      // })
      // this.$store.dispatch('GetResourceInfo', { resourceId: '1' }).then(res => {
      //   this.textOptions = res.data.columns
      // })
      this.saveSelectAllList.forEach((item, index) => {
        let items = JSON.parse(item)
        if (items.name === val) {
          this.textOptions = items.data.columns
        }
      })
    },
    submitBtn () {
      this.$refs.formDataModel.validate((valid) => {
        if (valid) {
          this.dialogFormVisible = false
          let ids = Math.random().toString(36).substr(3)
          this.formData['ids'] = ids
          this.formData['isNew'] = this.isNew
          this.$store.dispatch('ReloadFilterTableList', this.formData).then(res => {
            if (res) {
              this.$message.success('保存成功~')
              this.formData = {}
              this.$parent.init()
              this.$refs.formDataModel.clearValidate()
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
        setTimeout(() => {
          this.$refs.formDataModel.clearValidate()
        }, 100)
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      jointResultData: 'jointResultData',
      saveSelectAllList: 'saveSelectAllList'
    })
  }
}
</script>

<style lang="stylus" scoped>
.reloadSetModal{
  >>>.el-dialog__body{
    padding: 30px 40px 30px 10px;
    .el-tag{
      margin-right 20px
      margin-bottom 10px
    }
  }
  >>>.el-select{
    width 100%
  }
   >>>.el-input{
    width 100%!important
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
