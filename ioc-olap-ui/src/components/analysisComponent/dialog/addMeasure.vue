<template>
  <div class="addMeasure">
    <el-dialog title="新增度量" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form :model="formData" ref="formData" :rules="rules">
        <el-form-item label="度量名称" :label-width="formLabelWidth" prop="name">
          <el-input v-model="formData.name" autocomplete="off" placeholder="请输入度量名称（1~10个字）"></el-input>
        </el-form-item>
        <el-form-item label="计算方式" :label-width="formLabelWidth" prop="function.expression">
          <el-select v-model="formData.function.expression" placeholder="请选择" @change="selectChange">
            <el-option v-for="item in computeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型" :label-width="formLabelWidth" prop="function.parameter.type">
          <el-select v-model="formData.function.parameter.type" placeholder="请选择" :disabled="isDisabledtype" @change="selectType">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择字段" :label-width="formLabelWidth" prop="function.parameter.value">
          <el-select v-model="formData.function.parameter.value" placeholder="请选择" :disabled="isDisabledtext" @change="selectValue">
            <el-option v-for="(item, index) in fieldtextOption" :key="index" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item  v-if="formData.function.expression !== 'COUNT'" style="margin-top:-10px;" :label-width="formLabelWidth">
          <el-checkbox label="显示所有字段" @change="changeAll"></el-checkbox>
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
                <el-form-item :prop="'answers.' + scope.$index + '.answertext'">
                  <el-select v-model="scope.row.answertext" placeholder="请选择字段">
                    <el-option v-for="(item, index) in fieldtextOption" :key="index" :label="item.label" :value="item.label"></el-option>
                  </el-select>
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
import { deflate } from 'zlib'
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
            type: '',
            value: ''
          }
        },
        answers: []
      },
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
      },
      {
        value: 'EXTENDED_COLUMN',
        label: 'EXTENDED_COLUMN'
      }, {
        value: 'PERCENTILE',
        label: 'PERCENTILE'
      }],
      typeOptions: [
        { value: 'column', label: 'column' },
        { value: 'constant', label: 'constant' }
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
      jsonType: ['smallint', 'int4', 'double', 'tinyint', 'numeric', 'long8', 'integer', 'real', 'float', 'decimal(19,4)', 'bigint'],
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
          { required: false, message: '请选择字段', trigger: 'change' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectFiled: 'saveSelectFiled',
      SaveFactData: 'SaveFactData',
      saveSelectAllList: 'saveSelectAllList'
    })
  },
  mounted () {
    console.log(this.formData)
  },
  methods: {
    resetData () {
      this.formData = {
        function: {
          expression: '',
          returntype: '',
          parameter: {
            type: '',
            value: ''
          }
        },
        answers: []
      }
    },
    closeBtn () {
      this.dialogFormVisible = false
      this.$refs.formData.clearValidate()
    },
    selectValue (val) {
      let result = this.fieldtextOption.filter((res, index) => {
        return res.label === val
      })
      this.formData.function.returntype = result[0].dataType
      if (this.jsonType.indexOf(result[0].dataType) === -1) {
        this.$message.warning('不支持当前字段类型~')
        this.formData.function.parameter.value = ''
      }
    },
    selectType (val) {
      if (val === 'constant') {
        this.formData.function.parameter.value = 1
        this.formData.function.returntype = 'bigint'
        this.isDisabledtext = true
      } else {
        this.formData.function.parameter.value = ''
        this.isDisabledtext = false
      }
    },
    submitBtn (index) {
      this.$refs.formData.validate((valid) => {
        if (valid) {
          this.dialogFormVisible = false
          let id = Math.random().toString(36).substr(3)
          this.formData['id'] = id
          this.formData['isNew'] = this.isNew
          this.formData['showDim'] = true
          this.$store.dispatch('MeasureTableList', this.formData).then(res => {
            if (res) {
              this.$message.success('保存成功~')
              this.$refs.formData.clearValidate()
            }
          })
          this.$parent.init()
        }
      })
    },
    dialog (data) {
      this.dialogFormVisible = true
      this.fieldtextOption = []
      this.SaveFactData.map(item => {
        this.fieldtextOption.push(
          { id: item.id, dataType: item.dataType, label: `${item.tableName}.${item.name}` }
        )
      })
      if (data) {
        this.formData = data
        this.isNew = 1
      } else {
        this.resetData()
        this.isNew = 0
        setTimeout(() => {
          this.$refs.formData.clearValidate()
        }, 100)
      }
    },
    changeAll (n) {
      this.fieldtextOption = []
      this.formData.function.parameter.value = ''
      let AllData = []
      this.saveSelectAllList.forEach((item, index) => {
        let findData = []
        let items = JSON.parse(item)
        items.data.columns.forEach((n, i) => {
          findData.push({
            name: items.name + '.' + n.name,
            id: n.id,
            dataType: n.dataType
          })
        })
        AllData = AllData.concat(findData)
      })
      n === true
        ? AllData.map(res => {
          this.fieldtextOption.push({ id: res.id, dataType: res.dataType, label: res.name })
        })
        : this.SaveFactData.map(item => {
          this.fieldtextOption.push(
            { id: item.id, dataType: item.dataType, label: `${item.tableName}.${item.name}` }
          )
        })
    },
    selectChange (val) {
      this.formData.function.parameter.value = ''
      switch (val) {
        case 'COUNT':
          this.formData.function.parameter.type = 'constant'
          this.formData.function.parameter.value = 1
          this.formData.function.returntype = 'bigint'
          this.isDisabledtype = true
          this.isDisabledtext = true
          break
        case 'PERCENTILE':
          this.formData.function.parameter.type = 'column'
          this.formData.function.parameter.value = ''
          this.isDisabledtype = true
          this.isDisabledtext = false
          break
        case 'COUNT_DISTINCT':
          this.formData.function.parameter.value = 1
          this.isDisabledtext = true
          break
        default:
          this.isDisabledtype = false
          // this.formData.fieldtext = ''
          this.isDisabledtext = false
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
  height 300px
  padding 20px
  overflow auto
  >>>.el-dialog__body{
    .el-tag{
      margin-right 20px
      margin-bottom 10px
    }
  }
  >>>.el-select{
    width 100%
  }
  .coutDistinct, .coutTopn{
    >>>.el-form-item{
      margin-bottom 10px
      .el-input__inner{
        height 30px
        .el-input__suffix{
          top 5px
        }
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
