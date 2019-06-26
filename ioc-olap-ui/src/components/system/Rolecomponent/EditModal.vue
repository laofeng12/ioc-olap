<template>
  <div class="editModal">
    <el-dialog :title="head_title" :visible.sync="dialogFormVisible"  @close="submitClose">
      <el-form :model="formData" :rules="rules" :class="iptCls" ref="formData">
        <el-form-item label="角色名称" :label-width="formLabelWidth" prop="rolename">
          <el-input v-model="formData.rolename" autocomplete="off" :disabled="isdisabled" maxlength="36"></el-input>
        </el-form-item>
        <el-form-item label="别名" :label-width="formLabelWidth" prop="alias">
          <el-input v-model="formData.alias" autocomplete="off" :disabled="isdisabled" maxlength="36"></el-input>
        </el-form-item>
        <el-form-item label="备注" :label-width="formLabelWidth" prop="memo">
          <el-input v-model="formData.memo" autocomplete="off" :disabled="isdisabled" maxlength="100"></el-input>
        </el-form-item>
        <!-- <el-form-item label="是否允许删除" :label-width="formLabelWidth" prop="allowdel">
          <el-select v-model="formData.allowdel" placeholder="请选择" :disabled="isdisabled">
            <el-option v-for="item in isRequiredOption" :key="item.bid" :label="item.basename" :value="item.bid"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否允许编辑" :label-width="formLabelWidth" prop="allowedit">
          <el-select v-model="formData.allowedit" placeholder="请选择" :disabled="isdisabled">
            <el-option v-for="item in isRequiredOption" :key="item.bid" :label="item.basename" :value="item.bid"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" :label-width="formLabelWidth" prop="enabled">
          <el-select v-model="formData.enabled" placeholder="请选择" :disabled="isdisabled">
            <el-option v-for="item in isRequiredOption" :key="item.bid" :label="item.basename" :value="item.bid"></el-option>
          </el-select>
        </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="submitClose" v-text="head_title === '查看详情'?'关闭':'取消'">取 消</el-button>
        <el-button type="primary" v-if="head_title !== '查看详情'" @click="submitBtn('formData', submitType)">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data () {
    return {
      dialogFormVisible: false,
      isdisabled: false,
      submitType: '',
      head_title: '',
      formLabelWidth: '120px',
      formData: {},
      isRequiredOption: [
        { basename: '是', bid: 1 }, { basename: '否', bid: 0 }
      ],
      rules: {
        rolename: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        alias: [
          { required: true, message: '请输入别名', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        memo: [
          { required: false, message: '请输入备注', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        allowdel: [
          { required: false, message: '是否允许删除', trigger: 'blur' }
        ],
        allowedit: [
          { required: false, message: '是否允许编辑', trigger: 'blur' }
        ],
        enabled: [
          { required: false, message: '是否启用', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    iptCls () {
      return this.isdisabled === true ? 'looksCls' : ''
    }
  },
  methods: {
    dialog (type, val) {
      this.submitType = type
      val ? this.formData = val : this.formData = {}
      this.head_title = type === 'add' ? '新增角色' : (type === 'edit' ? '编辑角色' : '查看详情')
      type === 'looks' ? this.isdisabled = true : this.isdisabled = false
      this.dialogFormVisible = true
    },
    submitBtn (formData, type) {
      if (type === 'looks') {
        this.dialogFormVisible = false
        return
      }
      this.$refs[formData].validate((valid) => {
        if (valid) {
          console.log(this.formData)
          try {
            const isNew = type === 'add' ? 'true' : 'false'
            const params = {
              ...this.formData
            }
            this.$api.editRole(params, isNew).then(res => {
              this.dialogFormVisible = false
              if (res.code === 200) {
                this.$message.success('保存成功')
                this.$refs.formData.clearValidate()
                this.$parent.fetchData()
              }
            })
          } catch (error) {
            this.$message.error(error || '服务异常')
          }
        }
      })
    },
    submitClose () {
      this.$refs.formData.clearValidate()
      this.dialogFormVisible = false
    }
  }
}
</script>

<style lang="stylus" scoped>
.editModal{
  >>>.el-form-item.is-success .el-input__inner, .el-form-item.is-success .el-input__inner:focus, .el-form-item.is-success .el-textarea__inner, .el-form-item.is-success .el-textarea__inner:focus{
    border-color #DCDFE6
  }
}
</style>
