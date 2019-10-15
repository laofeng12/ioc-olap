<template>
  <div class="rename">
    <el-dialog title="构建模型" :visible.sync="dialogFormVisible">
      <el-form :model="form" ref="form" :rules="rules">
        <div v-if="dataList.partitionDateColumn">
          <el-form-item label="日期字段" :label-width="formLabelWidth">
            {{dataList.partitionDateColumn}}
          </el-form-item>
          <el-form-item label="开始时间" :label-width="formLabelWidth" prop="startTime">
            <el-date-picker
              v-model="form.startTime"
              value-format="timestamp"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
          </el-form-item>
          <el-form-item label="结束时间" :label-width="formLabelWidth" prop="endTime">
            <el-date-picker
              v-model="form.endTime"
              value-format="timestamp"
              :picker-options="pickerOptions"
              type="datetime"
              placeholder="选择日期时间">
            </el-date-picker>
          </el-form-item>
        </div>
        <div v-else>确定构建此模型？</div>
        <el-form-item label="自动刷新模型?" class="uploadItem">
          <template>
            <div>
              <el-switch
                v-model="formData.autoReload"
                @change="changeUploadNum"
                active-color="#13ce66"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="更新频率" v-if="formData.autoReload" prop="interval"  style="margin-left:40px">
          <template>
            <div class="uplaodNum">
              <el-input type="number" v-model="formData.interval"></el-input>
              <el-radio-group v-model="formData.frequencytype">
                <el-radio :label="1">小时</el-radio>
                <el-radio :label="2">天</el-radio>
                <el-radio :label="3">月</el-radio>
              </el-radio-group>
            </div>
          </template>
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
import { buildModeling, getTimingrefresh } from '@/api/modelList'
export default {
  data () {
    return {
      dataList: {},
      formData: {},
      form: {
        startTime: '',
        endTime: ''
      },
      formLabelWidth: '120px',
      dialogFormVisible: false,
      pickerOptions: {
        disabledDate: (time) => {
          return time.getTime() < this.form.startTime
        }
      },
      rules: {
        startTime: [
          { required: true, message: '请选择开始时间', trigger: 'change' }
        ],
        endTime: [
          { required: true, message: '请选择结束时间', trigger: 'change' }
        ]
      }
    }
  },
  methods: {
    handlebtn () {
      this.$refs.form.validate((valid) => {
        if (valid) {
          // 判断是否是全量
          if (!this.dataList.partitionDateColumn) {
            this.form.startTime = ''
            this.form.endTime = ''
          }
          this.formData.autoReload = this.formData.autoReload === true ? 1 : 0
          this.formData.isNew = false
          this.$confirm('确定构建此模型？', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }).then(() => {
            this.dialogFormVisible = false
            this.$parent.changeLoading()
            let parmas = {
              cubeName: this.dataList.name,
              start: this.form.startTime ? this.getTimezoneOffset(this.form.startTime) : '0',
              end: this.form.endTime ? this.getTimezoneOffset(this.form.endTime) : '0',
              timingrefresh: this.formData
            }
            this.$throttle(async () => {
              await buildModeling(parmas).then(res => {
                this.$message.success('构建成功~')
                this.form.startTime = ''
                this.form.endTime = ''
                this.$parent.closeChangeLoading()
              }).catch(_ => {
                this.$parent.closeChangeLoadingLoser()
                this.form.startTime = ''
                this.form.endTime = ''
              })
            })
          })
        }
      })
    },
    changeUploadNum () {

    },
    // getTimingrefresh
    _getTimingrefresh (name) {
      getTimingrefresh({ cubeName: name }).then(res => {
        this.formData = res
        this.formData.autoReload = !!res.autoReload
        console.log(this.formData)
      })
    },
    dialog (val) {
      this.dataList = val
      this.dialogFormVisible = true
      val.segments.forEach(item => {
        this.form.startTime = item.date_range_end ? item.date_range_end : ''
      })
      this._getTimingrefresh(val.name)
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
  >>>.el-input{
    width 100%
  }
  >>>.el-dialog__body{
    padding 0px 20px
  }
  .uplaodNum{
    // float left
    margin-left 40px
    >>>.el-input{
      width 100px
      margin-right 30px
    }
  }
}
</style>
