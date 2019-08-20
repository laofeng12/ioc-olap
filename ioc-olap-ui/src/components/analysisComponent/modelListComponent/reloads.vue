<template>
  <div class="rename">
    <el-dialog title="模型刷新设置" :visible.sync="dialogFormVisible">
      <el-form :model="form" v-if="isReload">
        <el-form-item label="日期字段" :label-width="formLabelWidth">
          {{form.partitionDateColumn}}
        </el-form-item>
        <el-form-item label="刷新区间" :label-width="formLabelWidth">
          <!-- <el-date-picker
            v-model="form.value"
            type="datetimerange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :default-time="['12:00:00']">
          </el-date-picker> -->
          <el-select>
            <el-select v-model="form.interval" placeholder="请选择刷新区间">
              <!-- <el-option v-for="item in intervalData" :key="item" :label="item" :value="item"></el-option> -->
            </el-select>            
          </el-select>
        </el-form-item>
      </el-form>
      <div v-else>
        No Segment to refresh.
      </div>
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
        interval: []
      },
      isReload: false,
      intervalData: '',
      formLabelWidth: '120px',
      dialogFormVisible: false
    }
  },
  methods: {
    handlebtn () {
      if (!this.isReload) return this.dialogFormVisible = false
      this.$confirm('确定刷新此模型？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.dialogFormVisible = false
        this.$parent.changeLoading()
        let parmas = {
          cubeName: this.form.partitionDateColumn
          // start: Date.parse(new Date(this.form.value[0])) / 1000,
          // end: Date.parse(new Date(this.form.value[1])) / 1000
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
      this.dialogFormVisible = true
      this.isReload = val.segments.length > 0 ? true : false
      this.form.partitionDateColumn = val.partitionDateColumn
      let arr = []
      val.segments.map(item => {
        arr.push({
          date_range_end: item.date_range_end,
          date_range_start: item.date_range_start,
          status: item.status
        })
      })
      console.log(arr)
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
