<template>
  <div class="addMeasure">
    <el-dialog title="新增度量" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-form :model="formData" ref="formData">
        <el-form-item label="度量名称" :label-width="formLabelWidth">
          <el-input v-model="formData.name" autocomplete="off" placeholder="请输入度量名称（1~10个字）"></el-input>
        </el-form-item>
        <el-form-item label="计算方式" :label-width="formLabelWidth">
          <el-select v-model="formData.function.expression" placeholder="请选择" @change="selectChange">
            <el-option v-for="item in computeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类型" :label-width="formLabelWidth">
          <el-select v-model="formData.function.parameter.type" placeholder="请选择" :disabled="isDisabledtype">
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="选择字段" :label-width="formLabelWidth">
          <el-select v-model="formData.function.parameter.value" placeholder="请选择" :disabled="isDisabledtext">
            <el-option v-for="item in fieldtextOption" :key="item.id" :label="item.value" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item style="margin-top:-20px;" :label-width="formLabelWidth">
          <el-checkbox label="显示所有字段"></el-checkbox>
        </el-form-item>
        <el-form-item label="扩展列长度" v-if="formData.function.expression === 'EXTENDED_COLUMN'" :label-width="formLabelWidth">
          <el-input v-model="formData.name" autocomplete="off" placeholder="请输入长度数值"></el-input>
        </el-form-item>
        <div v-if="formData.function.expression === 'COUNT_DISTINCT'" class="coutDistinct">
          <el-form-item label="返回类型" :label-width="formLabelWidth">
            <el-select v-model="formData.type" placeholder="请选择" :disabled="isDisabledtype">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value"></el-option>
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
                    <el-option v-for="(item, index) in datas" :key="index" :label="item.codename" :value="item.codename"></el-option>
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
                    <el-option v-for="(item, index) in datas" :key="index" :label="item.codename" :value="item.codename"></el-option>
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
            type: '',
            value: ''
          },
          showDim: false
        },
        answers: []
      },
      isNew: 1,
      formLabelWidth: '100px',
      dialogFormVisible: false,
      isDisabledtype: false,
      isDisabledtext: false,
      tableData: [],
      fieldtextOption: [
        { id: 1, value: 'aaaa' },
        { id: 2, value: 'bbb' },
        { id: 3, value: 'cccc' }
      ],
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
        value: 'COUNT',
        label: 'COUNT'
      }, {
        value: 'COUNT_DISTINCT',
        label: 'COUNT_DISTINCT '
      }, {
        value: 'TOP_N',
        label: 'TOP_N'
      }, {
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
      rules: {
        answertext: [
          { required: false, message: '请选择字段', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal'
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
          },
          showDim: false
        },
        answers: []
      }
    },
    closeBtn () {
      this.dialogFormVisible = false
    },
    submitBtn (index) {
      this.dialogFormVisible = false
      let id = Math.random().toString(36).substr(3)
      this.formData['id'] = id
      this.formData['isNew'] = this.isNew
      this.$store.dispatch('MeasureTableList', this.formData).then(res => {
        if (res) {
          this.$message.success('保存成功~')
          this.resetData()
        }
      })
      this.$parent.init()
    },
    dialog (data) {
      this.dialogFormVisible = true
      if (data) {
        this.formData = data
        this.isNew = 1
      } else {
        this.resetData()
        this.isNew = 0
      }
    },
    selectChange (val) {
      switch (val) {
        case 'COUNT':
          this.formData.function.parameter.type = 'constant'
          this.formData.fieldtext = 1
          this.isDisabledtype = true
          this.isDisabledtext = true
          break
        case 'PERCENTILE':
          this.formData.function.parameter.type = 'column'
          this.isDisabledtype = true
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
      this.formData.answers.push({ index: 1, answertext: '' })
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
