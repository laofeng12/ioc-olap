<template>
    <el-form ref="form" :model="form" :rules="rules">
      <el-row :gutter="10" class="filterlist-row">
        <el-col :span="8">
          <el-select v-model="form.region" class="select-one">
            <el-option
              v-for="item in textOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-col>
        <div v-if="form.region==='null'||form.region==='!null'"></div>
        <el-col :span="16" v-else>
          <el-form-item label="" prop="range">
            <el-col :span="20">
              <el-input v-model="form.range" placeholder="请输入条件值"></el-input>
            </el-col>
            <el-col :span="4">
              <el-button  v-if="index===0" type="primary" icon="el-icon-plus" circle size="small" @click="addFilter()"></el-button>
              <el-button  v-else type="danger" icon="el-icon-minus"  circle size="small" @click="delTemp()"></el-button>
            </el-col>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
</template>

<script>
export default {
  name: 'textTemp',
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
      textOptions: [ {
        value: '=',
        label: '等于'
      }, {
        value: '≠',
        label: '不等于'
      }, {
        value: '[a,b]',
        label: '包含'
      }, {
        value: '(a,b)',
        label: '不包含'
      }, {
        value: '[a,b)',
        label: '开头包含'
      }, {
        value: '(a,b]',
        label: '结尾包含'
      }, {
        value: 'null',
        label: '为空'
      }, {
        value: '!null',
        label: '不为空'
      }],
      form: this.items,
      rules: {
        range: [{ required: true, message: '请输入条件值', trigger: 'blur' }]
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
        console.log('texttemp items', newV)
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
    // 文本过滤添加模版
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
    /deep/ .el-select {
      z-index: 1;
    }
  }
  .text{
    line-height: 40px;
  }
</style>
