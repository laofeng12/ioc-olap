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
            type="datetime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
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
        // console.log('时间', this.form.startTime)
        // var date = this.form.startTime
        // date = date.substring(0, 19)
        // date = date.replace(/-/g, '/')
        // var timestamp = new Date(date).getTime()
        // console.log('转换后', timestamp)
        let parmas = {
          cubeName: this.dataList.partitionDateColumn,
          start: Date.parse(new Date(this.form.startTime)) / 1000,
          end: Date.parse(new Date(this.form.endTime)) / 1000
        }
        buildModeling(parmas).then(res => {
          console.log(res)
        })
        // this.$message({
        //   type: 'success',
        //   message: '构建成功!'
        // })
        // this.dialogFormVisible = false
      })
    },
    dialog (val) {
      console.log('====', val)
      this.dataList = val
      this.dialogFormVisible = true
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
