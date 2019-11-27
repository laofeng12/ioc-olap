<template>
  <div class="addMeasure">
    <el-dialog :title="titles" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form :model="formData" ref="formData" :rules="rules">
        <el-form-item label="度量名称" :label-width="formLabelWidth" prop="name">
          <el-input v-model="formData.name" autocomplete="off" placeholder="请输入度量名称（1~10个字）" maxlength="10" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="计算方式" :label-width="formLabelWidth" prop="function.expression">
          <el-select v-model="formData.function.expression" placeholder="请选择" @change="selectChange">
            <el-option v-for="item in computeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型" :label-width="formLabelWidth" prop="column">column</el-form-item>
        <!-- <el-form-item label="类型" :label-width="formLabelWidth" prop="function.parameter.type">
          <el-select v-model="formData.function.parameter.type" placeholder="请选择" :disabled="isDisabledtype" @change="selectType">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item> -->
        <el-form-item label="选择字段" :label-width="formLabelWidth" prop="function.parameter.value">
          <el-select v-model="formData.function.parameter.value" placeholder="请选择" :disabled="isDisabledtext" @change="selectValue">
            <el-option v-for="(item, index) in fieldtextOption" :key="index" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item  v-if="formData.function.expression !== 'COUNT'" style="margin-top:-10px;" :label-width="formLabelWidth">
          <el-checkbox label="显示所有字段" v-model="formData.checkedAll" @change="changeAll"></el-checkbox>
        </el-form-item>
        <el-form-item label="扩展列长度" v-if="formData.function.expression === 'EXTENDED_COLUMN'" :label-width="formLabelWidth">
          <el-input v-model="formData.name" autocomplete="off" placeholder="请输入长度数值"></el-input>
        </el-form-item>
        <div v-if="formData.function.expression === 'COUNT_DISTINCT'" class="coutDistinct">
          <el-form-item label="返回类型" :label-width="formLabelWidth">
            <el-select v-model="formData.type" placeholder="请选择" :disabled="isDisabledtype">
              <el-option v-for="item in backType" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
        </div>
        <div v-if="formData.computeMode==='TOP_N'" class="coutTopn">
          <el-form-item label="返回类型" :label-width="formLabelWidth">
            <el-select v-model="formData.top" placeholder="请选择" :disabled="isDisabledtype">
              <el-option v-for="item in topOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
          <p>聚合字段</p>
          <el-table :data="formData.answers">
            <el-table-column
              label="序号"
              prop='index'
              width="100"
              align="center">
            </el-table-column>
            <el-table-column
              label="选择字段"
              align="center">
              <template slot-scope="scope">
                <el-form-item :prop="'answers.' + scope.$index + '.answertext'" :rules='rules.answertext'>
                  <el-select v-model="scope.row.answertext" placeholder="请选择字段">
                    <el-option v-for="(item, index) in fieldtextOption" :key="index" :label="item.label" :value="item.label"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              label="编码类型"
              align="center">
              <template slot-scope="scope">
                <el-form-item :prop="'answers.' + scope.$index + '.answertext'" :rules='rules.answertext'>
                  <el-select v-model="scope.row.answertext" placeholder="请选择字段">
                    <el-option v-for="(item, index) in datas" :key="index" :label="item.codename" :value="item.codename"></el-option>
                  </el-select>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              label="长度"
              width="100"
              align="center">
              <template slot-scope="scope">
                <el-form-item :prop="'answers.' + scope.$index + '.answerName'" :rules='rules.answerName'>
                  <el-input type="text" v-model="scope.row.answerName"  maxlength="20"></el-input>
                </el-form-item>
              </template>
            </el-table-column>
            <el-table-column
              label="操作"
              fixed="right"
              width="200"
              align="center">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="handDeleteMethod(scope.row.roleid, scope.$index)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-button type="primary" @click="addtext">+添加字段</el-button>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="closeBtn()">取消</el-button>
        <el-button type="primary" @click="submitBtn()">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    border: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      formData: {
        function: {
          expression: '',
          returntype: '',
          parameter: {
            type: 'column',
            value: ''
          }
        },
        answers: [],
        type: '6',
        id: '',
        checkedAll: false
      },
      firstName: '',
      titles: '',
      isNew: 1,
      formLabelWidth: '100px',
      dialogFormVisible: false,
      isDisabledtype: false,
      isDisabledtext: false,
      tableData: [],
      fieldtextOption: [],
      computeOptions: [{
        value: 'SUM',
        label: 'SUM'
      }, {
        value: 'MAX',
        label: 'MAX'
      },
      {
        value: 'COUNT',
        label: 'COUNT'
      }, {
        value: 'MIN',
        label: 'MIN'
      }, {
        value: 'COUNT_DISTINCT',
        label: 'COUNT_DISTINCT '
      },
      {
        value: 'AVG',
        label: 'AVG'
      }
      ],
      typeOptions: [
        { value: 'column', label: 'column' }
        // { value: 'constant', label: 'constant' }
      ],
      backType: [
        { value: '1', label: 'Error Rate <9.75%' },
        { value: '2', label: 'Error Rate <4.88%' },
        { value: '3', label: 'Error Rate <2.44%' },
        { value: '4', label: 'Error Rate <1.72%' },
        { value: '5', label: 'Error Rate <1.22%' },
        { value: '6', label: 'Precisely(More Memory And Storage Needed)' }
      ],
      topOptions: [
        { value: '1', label: 'TOP 10' },
        { value: '2', label: 'TOP 100' },
        { value: '3', label: 'TOP 500' },
        { value: '4', label: 'TOP 1000' },
        { value: '5', label: 'TOP 5000' },
        { value: '6', label: 'TOP 10000' }
      ],
      jsonType: ['smallint', 'int4', 'double', 'tinyint', 'numeric', 'long8', 'integer', 'real', 'float',
        'decimal(19,4)', 'bigint'],
      rules: {
        name: [
          { required: true, message: '请输入度量名称', trigger: 'blur' }
        ],
        'function.expression': [
          { required: true, message: '请选择计算方式', trigger: 'change' }
        ],
        'function.parameter.type': [
          { required: true, message: '请选择类型', trigger: 'change' }
        ],
        'function.parameter.value': [
          { required: true, message: '请选择字段', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters({
      measureTableList: 'measureTableList',
      selectTableTotal: 'selectTableTotal',
      saveSelectFiled: 'saveSelectFiled',
      SaveFactData: 'SaveFactData',
      jointResultData: 'jointResultData',
      saveSelectAllList: 'saveSelectAllList',
      batchCreateJob: 'batchCreateJob'
    })
  },
  methods: {
    resetData () {
      this.formData = {
        function: {
          expression: '',
          returntype: '',
          parameter: {
            type: 'column',
            value: ''
          }
        },
        answers: [],
        type: '6',
        checkedAll: false
      }
    },
    // 初始化需要选择的字段
    initData (val) {
      this.fieldtextOption = []
      let n = val || false
      // 创建count计算方式需要的
      let selectData = []
      // 创建一个接收事实表的盒子
      let factData = []
      // 创建一个接受所有维度的盒子
      let AllData = []
      // 遍历筛选出第三步所有勾选的数据
      // this.saveSelectAllList.forEach((item, index) => {
      //   let items = JSON.parse(item)
      //   this.jointResultData.lookups.forEach((n, i) => {
      //     if (items.resourceId === n.id) {
      //       items.data.columns.forEach((k, i) => {
      //         AllData.push({
      //           label: n.alias + '.' + k.name,
      //           id: k.id,
      //           dataType: k.dataType
      //         })
      //       })
      //     }
      //   })
      // })
      this.saveSelectAllList.forEach((item, index) => {
        // let items = JSON.parse(item)
        this.jointResultData.lookups.forEach((n, i) => {
          if (item.resourceId === n.id) {
            item.column.forEach((k, i) => {
              AllData.push({
                label: n.columnAlias + '.' + k.name,
                id: k.id,
                dataType: k.type
              })
            })
          }
        })
      })
      // 遍历筛选出第三步勾选的数据（去掉事实表的）(如果为count计算方式的时候)
      this.saveSelectFiled.map(res => {
        selectData.push({ label: res.tableName + '.' + res.titName, id: res.id, dataType: res.dataType || res.type })
      })
      // 遍历筛选出所有事实表的数据
      this.SaveFactData.map(item => {
        factData.push(
          { id: item.id, dataType: item.type, label: `${item.tableName}.${item.columnAlias}` }
        )
      })
      const fieldtextOption = n === true ? [...factData, ...AllData] : (n === false ? [...factData] : [...selectData])
      let list = []
      this.batchCreateJob.forEach(v => {
        list = [...list, ...v.meta.columns]
      })
      fieldtextOption.forEach(item => {
        list.forEach(v => {
          // const label = item.label.split('.')[1].toUpperCase()
          const label = item.label.split('.')[1]
          if (label === v.name) {
            item.dataType = v.datatype || v.type
          }
        })
      })
      this.fieldtextOption = fieldtextOption
    },
    closeBtn () {
      this.dialogFormVisible = false
      this.$refs.formData.clearValidate()
      this.$parent.init()
    },
    selectValue (val) {
      let expressionList = ['SUM', 'AVG']
      let result = this.fieldtextOption.filter((res, index) => {
        return res.label === val
      })
      this.formData.function.returntype = this.formData.function.expression === 'COUNT_DISTINCT' ? 'bitmap' : result[0].dataType
      if (expressionList.includes(this.formData.function.expression)) {
        if (!this.jsonType.includes(result[0].dataType)) {
          this.$message.warning('不支持当前字段类型~')
          this.formData.function.parameter.value = ''
        }
      }
    },
    selectType (val) {
      if (val === 'constant') {
        this.formData.function.parameter.value = 1
        // this.formData.function.returntype = 'bigint'
        this.isDisabledtext = true
      } else {
        this.formData.function.parameter.value = ''
        this.isDisabledtext = false
      }
    },
    // 获取已经保存的数据的name（避免重复）
    getSavemeasureTableList (val) {
      if (this.isNew === 0) {
        let Result = []
        this.measureTableList.map(res => {
          Result.push(res.name)
        })
        return Result.includes(val)
      }
    },
    submitBtn () {
      this.$refs.formData.validate((valid) => {
        if (valid) {
          if (this.getSavemeasureTableList(this.formData.name)) return this.$message.warning('该度量名称已存在~')
          this.dialogFormVisible = false
          // 创建随机唯一标识id
          if (!this.formData.id) {
            let id = Math.random().toString(36).substr(3)
            this.formData['id'] = id
            this.formData['firstName'] = this.firstName
          }
          Object.assign(this.formData, {
            isNew: this.isNew,
            showDim: true,
            checkedAll: this.formData.checkedAll
          })
          this.$store.dispatch('MeasureTableList', this.formData).then(res => {
            if (res) {
              this.$message.success('设置成功~')
              this.$refs.formData.clearValidate()
              this.checkedAll = false
              this.resetData()
            }
          })
          this.$parent.init()
        }
      })
    },
    dialog (data) {
      this.firstName = data ? JSON.parse(JSON.stringify(data)).name : ''
      this.dialogFormVisible = true
      let checkedAll = data ? data.checkedAll : ''
      this.initData(checkedAll)
      if (data) {
        this.formData = data
        this.isNew = 1
        this.titles = '编辑度量'
      } else {
        this.resetData()
        this.titles = '新增度量'
        this.isNew = 0
        setTimeout(() => {
          this.$refs.formData.clearValidate()
        }, 100)
      }
    },
    // 选择显示所有字段
    changeAll (n) {
      this.formData.function.parameter.value = ''
      this.initData(n)
    },
    selectChange (val) {
      this.formData.function.parameter.value = ''
      this.formData.checkedAll = false
      switch (val) {
        case 'COUNT':
          this.initData(3)
          break
        case 'COUNT_DISTINCT':
          this.formData.function.parameter.value = ''
          this.isDisabledtype = true
          this.initData(false)
          break
        default:
          this.isDisabledtype = false
          this.isDisabledtext = false
          this.initData(false)
          break
      }
    },
    handDeleteMethod (id, index) {
      this.formData.answers.splice(index, 1)
    },
    addtext () {
      let idx = this.formData.answers.length + 1
      this.formData.answers.push({ index: idx, answertext: '' })
    }
  }
}
</script>

<style lang="stylus" scoped>
.addMeasure{
  >>>.el-dialog__body{
    .el-tag{
      margin-right 20px
      margin-bottom 10px
    }
  }
  >>>.el-dialog{

  }
  >>>.el-select{
    width 100%
  }
    >>>.el-form-item{
      margin-bottom 20px!important
      .el-input__inner{
        height 30px
        .el-input__suffix{
          top 5px
        }
      }
    }
  .container::-webkit-scrollbar{
    width 8px
    height 8px
    background #fff
  }
  .container::-webkit-scrollbar-track{
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color:#fff;
  }
  .container::-webkit-scrollbar-thumb{
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #f0f0f0;
  }
}
</style>
