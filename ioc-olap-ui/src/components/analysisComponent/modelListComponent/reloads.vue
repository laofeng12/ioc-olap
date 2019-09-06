<template>
  <div class="rename">
    <el-dialog title="模型刷新设置" :visible.sync="dialogFormVisible">
      <el-form :model="form" v-if="isReload">
        <el-form-item label="日期字段" :label-width="formLabelWidth">
          {{form.partitionDateColumn}}
        </el-form-item>
        <el-form-item label="刷新区间" :label-width="formLabelWidth">
          <el-select v-model="form.interval" placeholder="请选择刷新区间">
            <el-option v-for="(item, index) in intervalOption" :key="index" :label="item.value" :value="item.id"></el-option>
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
import { filterTime, reduceObj } from '@/utils/index'
export default {
  data () {
    return {
      form: {
        interval: []
      },
      isReload: false,
      intervalData: '',
      intervalOption: [],
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
        let result = this.intervalOption.filter(res => {
          return res.id === this.form.interval
        })
        this.dialogFormVisible = false
        if (result[0].status === 'NEW') return this.$message.warning('已经是最新数据~')
        this.$parent.changeLoading()
        let parmas = {
          cubeName: this.form.partitionDateColumn,
          buildType: result[0].status,
          startTime: result[0].startTime,
          endTime: result[0].endTime
        }
        this.$throttle(async () => {
          await reloadModel(parmas).then(res => {
            this.$message.success('刷新成功~')
            this.$parent.closeChangeLoading()
          }).catch(_ => {
            this.$parent.closeChangeLoading()
          }).finally(_ => {
            this.$parent.closeChangeLoading()
          })
        })
      })
    },
    dialog (val) {
      this.dialogFormVisible = true
      console.log(val)
      this.isReload = val.segments.length > 0
      this.form.partitionDateColumn = val.partitionDateColumn
      val.segments.map(item => {
        this.intervalOption.push({
          value: `${filterTime(item.date_range_start)}-${filterTime(item.date_range_end)}`,
          status: item.status,
          id: item.uuid,
          startTime: item.date_range_start,
          endTime: item.date_range_end
        })
      })
      this.intervalOption = reduceObj(this.intervalOption, 'id')
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
