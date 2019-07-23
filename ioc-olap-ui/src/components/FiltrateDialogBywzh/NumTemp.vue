<template>
  <el-form ref="form" :model="form" :rules="rules">
    <el-row :gutter="10">
      <el-col :span="6">
        <el-select v-model="form.region" class="select-one">
          <el-option
            v-for="item in numOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value">
          </el-option>
        </el-select>
      </el-col>
      <div v-if="form.region==='null'||form.region==='!null'"></div>
      <div v-else-if="form.region==='(a,b)'">
        <el-col :span="7">
          <el-form-item label="" prop="numMin">
            <el-input type="number" v-model.number="form.numMin" placeholder="请输入条件值"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="1" class="text">
          至
        </el-col>
        <el-col :span="7">
          <el-form-item label="" prop="numMax">
            <el-input type="number" v-model.number="form.numMax" placeholder="请输入条件值"></el-input>
          </el-form-item>
        </el-col>
      </div>
      <div v-else>
        <el-col :span="12">
          <el-form-item label="" prop="num">
            <el-input type="number" v-model.number="form.num" placeholder="请输入条件值"></el-input>
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
  name: 'numTemp',
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
      numOptions: [ {
        value: '=',
        label: '等于'
      }, {
        value: '(a,b)',
        label: '范围'
      },
      {
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
        value: 'null',
        label: '为空'
      }, {
        value: '!null',
        label: '不为空'
      }],
      form: this.items,
      rules: {
        num: [{ required: true, message: '请输入数值', trigger: 'blur' }],
        numMin: [{ required: true, message: '请输入最小数值', trigger: 'blur' }],
        numMax: [{ required: true, message: '请输入最大数值', trigger: 'blur' }]
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
        // console.log('numTemp items', newV)
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
    // 数值过滤添加模版
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
