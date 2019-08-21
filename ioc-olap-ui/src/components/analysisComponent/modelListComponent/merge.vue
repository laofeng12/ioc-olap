<template>
  <div class="rename">
    <el-dialog title="合并模型块" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <!-- <el-form-item label="日期字段" :label-width="formLabelWidth">
          {{dataList.partitionDateColumn}}
        </el-form-item> -->
        <el-form-item label="合并开始区间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.startTime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="datetime"
            placeholder="请选择构建时间区间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="合并结束区间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.endTime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
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
import { mergeCubeModeling } from '@/api/modelList'
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
        this.dialogFormVisible = false
        this.$parent.changeLoading()
        let parmas = {
          cubeName: this.dataList.name,
          start: Date.parse(new Date(this.form.startTime)) / 1000,
          end: Date.parse(new Date(this.form.endTime)) / 1000
        }
        this.$throttle(async () => {
          await mergeCubeModeling(parmas).then(res => {
            this.$message.success('合并成功~')
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
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
