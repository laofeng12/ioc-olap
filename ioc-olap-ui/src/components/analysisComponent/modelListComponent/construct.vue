<template>
  <div class="rename">
    <el-dialog title="构建模型" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <el-form-item label="日期字段" :label-width="formLabelWidth">
          {{dataList.partitionDateColumn}}
        </el-form-item>
        <el-form-item label="开始时间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.startTime"
            :disabled="isDisabled"
            type="datetime"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.endTime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="选择日期时间">
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
import { buildModeling } from '@/api/modelList'
export default {
  data () {
    return {
      dataList: {},
      form: {},
      isDisabled: false,
      formLabelWidth: '120px',
      dialogFormVisible: false
    }
  },
  methods: {
    handlebtn () {
      this.$confirm('确定构建此模型？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.dialogFormVisible = false
        this.$parent.changeLoading()
        let parmas = {
          cubeName: this.dataList.name,
          start: this.form.startTime ? (Date.parse(new Date(this.form.startTime)) / 1000) : '',
          end: this.form.endTime ? (Date.parse(new Date(this.form.endTime)) / 1000) : ''
        }
        this.$throttle(async () => {
          await buildModeling(parmas).then(res => {
            this.$message.success('构建成功~')
            this.form.endTime = ''
            this.$parent.closeChangeLoading()
          }).catch(_ => {
            this.$parent.closeChangeLoading()
          })
        })
      })
    },
    dialog (val) {
      this.dataList = val
      this.dialogFormVisible = true
      console.log('time===', val)
      val.segments.forEach(item => {
        this.form.startTime = item.date_range_end ? item.date_range_end : ''
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
