<template>
      <el-dialog title="筛选设置"  :visible.sync="dialogFormVisible" :width='width' class="newfile-dialog"  @close="close">
        <!--日期类型-->
        <div v-if="showDateF===true">
          <date-temp ref="dateForm" v-for="(item,index) in textItems && textItems.options"
                      :key="index"
                      :index="index"
                      :items="item"
                      @deleteIndex="delFilterDate"  @uploadData="getFilterData" @addFilter="addDateFilter">
          </date-temp>
          <div style="margin-bottom: 10px" class="dialog-footer">
              <el-button @click="close" size="small">取 消</el-button>
              <el-button type="primary"  size="small"  @click="sureClick('dateForm')">确 定</el-button>
          </div>
        </div>
        <!--文本类型-->
        <div v-else-if="showTextF===true">
          <text-temp ref="textForm" v-for="(item,index) in textItems && textItems.options"
                    :key="index"
                    :index="index"
                    :items="item"
                    @deleteIndex="delFilterText"  @uploadData="getTextData" @addFilter="addTextFilter"></text-temp>
          <div style="margin-bottom: 10px" class="dialog-footer">
              <el-button @click="close" size="small">取 消</el-button>
              <el-button type="primary"  size="small"  @click="sureClick('textForm')">确 定</el-button>
          </div>
        </div>
        <!--数值类型-->
        <div v-else-if="showNumF===true" >
          <num-temp ref="numForm" v-for="(item,index) in textItems && textItems.options"
                    :key="index"
                    :index="index"
                    :items="item"
                    @deleteIndex="delFilterNum"  @uploadData="getNumData" @addFilter="addNumFilter"></num-temp>
          <div style="margin-bottom:10px;margin-top:5px" class="dialog-footer">
              <el-button @click="close" size="small">取 消</el-button>
              <el-button type="primary"  size="small"  @click="sureClick('numForm')">确 定</el-button>
          </div>
        </div>
       </el-dialog>
</template>

<script>
import DateTemp from './DateTemp.vue'
import TextTemp from './TextTemp.vue'
import NumTemp from './NumTemp.vue'

export default {
  name: 'filtrate-dialog',
  components: { DateTemp, TextTemp, NumTemp },
  props: {
    dateData: {
      type: Array,
      default: () => [{ 'region': '1', 'dateSpecific': '', 'dataStart': '', 'dataEnd': '', 'days': '' }]
    },
    textData: {
      type: Array,
      default: () => [{ region: '1', range: '' }]
    },
    numData: {
      type: Array,
      default: () => [{ region: '1', num: null, numMin: null, numMax: null }]
    },
    dialogStatus: {
      type: String,
      default: 'add'
    },
    dialogVisible: {
      type: Boolean,
      default: false
    },
    width: {
      type: String,
      default: '50%'
    },
    isDate: {
      type: Boolean,
      default: false
    },
    filterType: {
      type: String,
      default: 'DATE'
    },
    showDateF: {
      type: Boolean,
      default: false
    },
    showTextF: {
      type: Boolean,
      default: false
    },
    showNumF: {
      type: Boolean,
      default: false
    },
    // 获取父组件传过来的筛选数据对应id (整块object传过来)
    getDataId: {
      type: Object,
      dafault: () => {}
    },
    getTextAllData: {
      type: [Array, Object],
      default: () => []
    }
  },
  created () {
    // console.log(this.getDataId, this.getTextAllData)
  },
  data () {
    return {
      textItems: this.getTextAllData.find(item => item.dataId === this.getDataId.dataId),
      options: [{
        value: '1',
        label: '范围'
      }, {
        value: '2',
        label: '等于'
      }, {
        value: '3',
        label: '不等于'
      }, {
        value: '4',
        label: '小于'
      }, {
        value: '5',
        label: '大于'
      }, {
        value: '6',
        label: '小于等于'
      }, {
        value: '7',
        label: '大于等于'
      }, {
        value: '8',
        label: '相对时间'
      }, {
        value: '9',
        label: '为空'
      }, {
        value: '10',
        label: '不为空'
      }],
      textOptions: [{
        value: '1',
        label: '等于'
      }, {
        value: '3',
        label: '不等于'
      }, {
        value: '4',
        label: '包含'
      }, {
        value: '5',
        label: '不包含'
      }, {
        value: '6',
        label: '开头包含'
      }, {
        value: '7',
        label: '结尾包含'
      }, {
        value: '8',
        label: '为空'
      }, {
        value: '9',
        label: '不为空'
      }],
      numOptions: [ {
        value: '=',
        label: '等于'
      }, {
        value: '2',
        label: '范围'
      },
      {
        value: '3',
        label: '不等于'
      }, {
        value: '4',
        label: '小于'
      }, {
        value: '5',
        label: '大于'
      }, {
        value: '6',
        label: '小于等于'
      }, {
        value: '7',
        label: '大于等于'
      }, {
        value: '8',
        label: '为空'
      }, {
        value: '9',
        label: '不为空'
      }],
      dialogFormVisible: this.dialogVisible,
      form: {
        region: '1',
        dateSpecific: '',
        dataStart: '',
        dataEnd: '',
        days: '',
        range: '',
        num: null,
        numMin: null,
        numMax: null
      },
      textForm: {
        region: '1',
        range: ''
      },
      textRules: {
        // range: [{ required: true, message: '请输入条件值', trigger: 'blur' }]
      },
      rules: {
        num: [{ type: 'number', message: '请输入数值' }],
        numMin: [{ type: 'number', message: '请输入数值' }],
        numMax: [{ type: 'number', message: '请输入数值' }],
        range: [{ required: true, message: '请输入条件值', trigger: 'blur' }]
      }
    }
  },
  watch: {
    getTextAllData: {
      handler: function (val, oldVal) {
        this.getOptions('getTextAllData')
      },
      deep: true
    },
    getDataId: {
      handler: function (val, oldVal) {
        this.getOptions('getDataId')
      },
      deep: true
    },
    textItems: {
      handler: function (val, oldVal) {
        this.getOptions('textItems')
      },
      deep: true
    },
    dialogVisible: {
      handler: function (val, oldVal) {
        this.dialogFormVisible = val
      },
      deep: true
    }
  },
  methods: {
    getOptions (status) {
      this.textItems = this.getTextAllData.find(item => item.dataId === this.getDataId.dataId)
      // console.log('textItems.options', status, this.textItems, this.getDataId)
    },
    sureClick (formName) {
      const arr = this.$refs[formName]
      let bool = true
      arr && arr.find(item => {
        if (!item.incrementTotal()) {
          bool = false
        }
      })
      if (bool) {
        this.dialogFormVisible = false
        this.$emit('sureClick', this.dialogFormVisible)
      }
    },
    close () {
      // console.log(this.textItems)
      this.dialogFormVisible = false
      this.$emit('close', this.dialogFormVisible)
    },
    // 日期过滤添加模版
    addDateFilter () {
      this.textItems.options.push({ region: '(a,b)', dateSpecific: '', dataStart: '', dataEnd: '', days: '' })
    },
    // 获取日期筛选项所有值
    getFilterData: function (val) {
      let index = val.index
      this.textItems.options[index] = val.data
      console.log('获取日期筛选项I got the data:', JSON.stringify(this.items))
    },
    // 删除日期筛选项
    delFilterDate: function (index) {
      this.textItems.options.splice(index, 1)
      console.log('删除日期筛选项deleted:', JSON.stringify(this.items))
    },
    // 文本过滤添加模版
    addTextFilter (index) {
      this.textItems.options.push({ region: '=', range: '' })
    },
    // 获取文本筛选项所有值
    getTextData: function (val) {
      // console.log(val)
      let index = val.index
      this.textItems.options[index] = val.data
      // this.textFormArr = val.data
      console.log('获取文本筛选I got the data:', JSON.stringify(this.textItems))
    },
    // 删除文本筛选项
    delFilterText: function (index) {
      this.textItems.options.splice(index, 1)
      console.log('删除文本筛选deleted:', JSON.stringify(this.textItems))
    },
    // 数值过滤添加模版
    addNumFilter () {
      this.textItems.options.push({ region: '=', num: null, numMin: null, numMax: null })
    },
    // 获取数值筛选项所有值
    getNumData: function (val) {
      let index = val.index
      this.textItems.options[index] = val.data
      console.log('获取数值筛选I got the data:', JSON.stringify(this.numItems))
    },
    // 删除文本筛选项
    delFilterNum: function (index) {
      this.textItems.options.splice(index, 1)
      console.log('删除数值deleted:', JSON.stringify(this.numItems))
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
