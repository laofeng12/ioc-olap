<template>
  <div class="rename">
    <el-dialog title="合并模型块" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <el-form-item label="日期字段" :label-width="formLabelWidth">
          {{11111111111}}
        </el-form-item>
        <el-form-item label="合并开始区间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.startTime"
            type="datetime"
            placeholder="请选择构建时间区间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="合并结束区间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.endTime"
            type="datetime"
            placeholder="请选择构建时间区间">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="handlebtn">确 定</el-button>
      </div>
    </el-dialog>  
  </div>
</template>

<script>
export default {
  data () {
    return {
      form: {},
      formLabelWidth: '120px',
      dialogFormVisible: false
    }
  },
  methods: {
    handlebtn () {
      this.$confirm('确定合并此模型？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message({
          type: 'success',
          message: '合并成功!'
        });
        this.dialogFormVisible = false
      })
    },
    dialog () {
      this.dialogFormVisible = true
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>

