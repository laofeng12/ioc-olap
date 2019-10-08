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
            :picker-options="pickerOptions1"
            placeholder="选择日期时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="结束时间" :label-width="formLabelWidth">
          <el-date-picker
            v-model="form.endTime"
            format="yyyy-MM-dd HH:mm:ss"
            value-format="yyyy-MM-dd HH:mm:ss"
            :picker-options="pickerOptions2"
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
      form: {
        startTime: '',
        endTime: ''
      },
      formLabelWidth: '120px',
      dialogFormVisible: false,
      pickerOptions2: {
        disabledDate: (time) => {
          // return time.getTime() < this.form.startTime
        }
      }
    }
  },
  methods: {
    handlebtn () {
      console.log('time===', this.getTimezoneOffset(this.form.startTime))
      this.$confirm('确定构建此模型？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.dialogFormVisible = false
        this.$parent.changeLoading()
        let parmas = {
          cubeName: this.dataList.name,
          start: this.form.startTime ? this.getTimezoneOffset(this.form.startTime) : '',
          end: this.form.endTime ? this.getTimezoneOffset(this.form.endTime) : ''
        }
        this.$throttle(async () => {
          await buildModeling(parmas).then(res => {
            this.$message.success('构建成功~')
            this.form.startTime = ''
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
      val.segments.forEach(item => {
        this.form.startTime = item.date_range_end ? item.date_range_end : ''
      })
    },
    getTimezoneOffset (time) {
      let zoneOffset = 8
      // 算出时差,并转换为毫秒：
      let offset2 = new Date(time).getTimezoneOffset() * 60 * 1000
      // 算出现在的时间：
      let nowDate2 = new Date(time).getTime()
      // 此时东8区的时间
      let currentZoneDate = new Date(nowDate2 + offset2 + zoneOffset * 60 * 60 * 1000)
      return Date.parse(new Date(currentZoneDate)) / 1000
    }

  }
}
</script>

<style lang="stylus" scoped>
.rename{
  >>>.el-form-item{
    margin-bottom 20px!important
  }
}
</style>
