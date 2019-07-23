<template>
  <el-form ref="form" :model="form" :rules="rules">
    <el-row :gutter="10" class="filterlist-row">
      <el-col :span="5">
        <el-select v-model="form.region" class="select-one">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-col>
      <el-col :span="17" v-if="form.region==='(a,b)'">
        <el-form-item label="" required>
          <el-col :span="11">
            <el-form-item prop="dateStart">
              <el-date-picker type="datetime" placeholder="请选择最小范围值" v-model="form.dateStart" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col class="text" :span="2">至</el-col>
          <el-col :span="11">
            <el-form-item prop="dateEnd">
              <el-date-picker type="datetime" placeholder="请选择最大范围值" v-model="form.dateEnd" style="width: 100%;"></el-date-picker>
            </el-form-item>
          </el-col>
        </el-form-item>
      </el-col>
      <el-col :span="17" v-else-if="form.region==='(a,b]'">
        <el-col :span="1" class="text">前</el-col>
        <el-col :span="22">
          <el-form-item prop="days">
            <el-col>
              <el-input type="number" v-model="form.days" placeholder="请输入天数"></el-input>
            </el-col>
          </el-form-item>
        </el-col>
        <el-col :span="1" class="text">天</el-col>
      </el-col>
      <div v-else-if="form.region==='null'||form.region==='!null'"></div>
      <div v-else>
        <el-col :span="8">
          <el-form-item prop="dateSpecific">
            <el-date-picker type="datetime" placeholder="请选择具体时间值" v-model="form.dateSpecific" style="width: 100%;"></el-date-picker>
          </el-form-item>
        </el-col>
      </div>
      <el-col :span="2">
        <el-button  v-if="index===0" type="primary" icon="el-icon-plus" circle size="small" @click="addFilter()"></el-button>
        <el-button  v-else type="danger" icon="el-icon-minus"  circle size="small" @click="delTemp()"></el-button>
      </el-col>
    </el-row>
  </el-form>
</template>

<script>
export default {
  name: 'dateTemp',
  props: {
    index: {
      type: Number,
      required: true
    },
    items: {
      type: Object,
      default: () => {}
    }
  },
  data () {
    return {
      options: [{
        value: '(a,b)',
        label: '范围'
      }, {
        value: '=',
        label: '等于'
      }, {
        value: '≠',
        label: '不等于'
      }, {
        value: '<',
        label: '小于'
      }, {
        value: '>',
        label: '大于'
      }, {
        value: '<=',
        label: '小于等于'
      }, {
        value: '>=',
        label: '大于等于'
      }, {
        value: '(a,b]',
        label: '相对时间'
      }, {
        value: 'null',
        label: '为空'
      }, {
        value: '!null',
        label: '不为空'
      }
      ],
      form: this.items,
      rules: {
        dateSpecific: [{ required: true, message: '请选择日期', trigger: 'change' }],
        dateStart: [{ required: true, message: '请选择日期', trigger: 'change' }],
        dateEnd: [{ required: true, message: '请选择日期', trigger: 'change' }],
        days: [{ required: true, message: '请输入天数', trigger: 'blur' }]
      }
    }
  },
  watch: {
    form: {
      handler (newV, oldV) {
        this.incrementTotal()
        this.$emit('uploadData', { index: this.index, data: newV })
      },
      deep: true
    },
    items: {
      handler (newV, oldV) {
        // console.log('datetemp items', newV)
        // this.form = { ...newV[this.index] }
        this.form = newV
      },
      deep: true
    }
  },
  methods: {
    delTemp: function () {
      this.$emit('deleteIndex', this.index)
    },
    // 日期过滤添加模版
    addFilter () {
      this.$emit('addFilter', this.index)
    },
    incrementTotal () {
      let bool
      this.$refs['form'].validate((valid) => {
        // console.log(valid)
        if (!valid) {
          bool = false
          return false
        } else {
          bool = true
        }
      })
      return bool
    }
  }
}
</script>

<style lang="scss" scoped>
  .filterlist-row{
    margin-bottom: 10px;
  }
  .text{
    line-height: 40px;
  }
</style>
