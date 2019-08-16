<template>
  <div class="rename">
    <el-dialog title="模型刷新设置" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <el-form-item label="日期字段" :label-width="formLabelWidth">
          {{form.partitionDateColumn}}
        </el-form-item>
        <el-form-item label="刷新区间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.value"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :default-time="['12:00:00']">
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
import { reloadModel } from '@/api/modelList'
export default {
  data () {
    return {
      form: {
        value: ''
      },
      formLabelWidth: '120px',
      dialogFormVisible: false
    }
  },
  methods: {
    handlebtn () {
      this.$confirm('确定刷新此模型？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.dialogFormVisible = false
        this.$parent.changeLoading()
        let parmas = {
          cubeName: this.form.partitionDateColumn,
          start: Date.parse(new Date(this.form.value[0])) / 1000,
          end: Date.parse(new Date(this.form.value[1])) / 1000
        }
        this.$throttle(async () => {
          await reloadModel(parmas).then(res => {
            this.$message.success('合并成功~')
            this.$parent.closeChangeLoading()
          }).catch(_ => {
            this.$parent.closeChangeLoading()
          })
        })
      })
    },
    dialog (val) {
      console.log(val)
      this.form.partitionDateColumn = val.partitionDateColumn
      this.dialogFormVisible = true
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
