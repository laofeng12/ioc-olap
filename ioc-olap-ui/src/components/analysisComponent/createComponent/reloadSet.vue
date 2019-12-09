<template>
  <div class="reloadSet">
     <el-form :model="formData" :rules="rules" ref="formData">
      <h3 style="margin: 0 0 0 -8px;;padding: 0 0 5px 0;">刷新设置</h3>
      <h4>自动刷新2</h4>
        <el-form-item label="自动刷新模型?">
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
        <el-form-item label="更新频率" v-if="formData.autoReload" prop="interval">
          <template>
            <div class="uplaodNum">
              <el-input-number controls-position="right"  v-model="formData.interval" :min="1"></el-input-number>
              <el-radio-group v-model="formData.frequencytype">
                <el-radio :label="1">小时</el-radio>
                <el-radio :label="2">天</el-radio>
                <el-radio :label="3">月</el-radio>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <h4 style="margin-top:30px;">日期字段</h4>
        <el-form-item label="日期字段表" class="datarowmore" prop="data1a">
          <el-select v-model="formData.data1a" placeholder="请选择数据表" @change="selectTable" @visible-change="visibleData(0)" clearable>
            <el-option v-for="(item) in tableOptions" :key="item.id" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期字段" prop="data1b">
          <el-select v-model="formData.data1b" placeholder="请选择日期字段" clearable>
            <el-option v-for="(item) in textOptions" :key="item.id" :label="item.columnAlias" :value="item.definition"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期格式" prop="partition_date_format">
          <el-select v-model="formData.partition_date_format" placeholder="请选择日期格式" clearable>
            <el-option v-for="item in formatOptions" :key="item.id" :label="item.value" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期存在多列？">
          <template>
            <div>
              <el-switch
                v-model="formData.ispartition_type"
                active-color="#13ce66"
                @change="changeDataMany"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <div v-if="formData.ispartition_type">
        <el-form-item label="日期字段表" class="datarowmore" prop="data2a">
          <el-select v-model="formData.data2a" placeholder="请选择数据表" @change="selectTable" @visible-change="visibleData(1)" clearable>
            <el-option v-for="(item, index) in tableOptions" :key="index" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期字段" prop="data2b">
          <el-select v-model="formData.data2b" placeholder="请选择日期字段" clearable>
            <el-option v-for="(item, index) in textOptions" :key="index" :label="item.columnAlias" :value="item.definition"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期格式" prop="partition_time_format">
          <el-select v-model="formData.partition_time_format" placeholder="请选择日期格式" clearable>
            <el-option v-for="item in formatOptionsMore" :key="item.id" :label="item.value" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        </div>

     </el-form>

     <h3>过滤设置</h3>
     <el-table
       :data="relaodFilterList"
       ref="multipleTable"
       tooltip-effect="dark"
       header-cell-class-name="tableHead"
       stripe
       style="margin-top: 10px;">
       <el-table-column type="index" width="100" label="序号"></el-table-column>
       <el-table-column prop="tableName" label="表名称"> </el-table-column>
       <el-table-column prop="field" label="字段"> </el-table-column>
       <el-table-column prop="pattern" label="过滤方式"> </el-table-column>
       <el-table-column prop="parameter" label="过滤值">
         <template slot-scope="scope">
           <div>
             <span>{{scope.row.parameter}}</span>
             <span v-if="scope.row.pattern === 'BETWEED'">，{{scope.row.parameterbe}}</span>
           </div>
         </template>
       </el-table-column>
       <el-table-column
         label="操作"
         width="100">
         <template slot-scope="scope">
           <div class="play">
             <el-button type="text" size="mini" @click="addReloadSet(scope.row)" icon="el-icon-edit"></el-button>
             <el-button type="text" size="mini" icon="el-icon-delete" @click="handleChange(scope)"></el-button>
           </div>
         </template>
       </el-table-column>
     </el-table>
     <div style="overflow: hidden;background: #fff;padding: 0 16px 16px 0;">
      <el-button style="float:right;margin-top:20px;" type="primary" @click="addReloadSet()">添加过滤条件</el-button>
     </div>
     <add-reload-set ref="dialog"></add-reload-set>
     <steps class="steps" :step="5" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/analysisComponent/modelCommon/steps'
import addReloadSet from '@/components/analysisComponent/dialog/addReloadSet'
import { mapGetters } from 'vuex'
export default {
  components: {
    steps, addReloadSet
  },
  data () {
    const checkNumber = (rule, val, callback) => {
      const value = Number(val)
      if (!value) {
        return callback(new Error('频率不能为空'))
      }
      setTimeout(() => {
        if (!Number.isInteger(value)) {
          callback(new Error('请输入数字值'))
        } else {
          if (value <= 0) {
            callback(new Error('必须为正整数'))
          } else {
            callback()
          }
        }
      }, 1)
    }
    return {
      formData: {
        autoReload: false,
        ispartition_type: false,
        idx: 0,
        data1a: '',
        data1b: '',
        data2a: '',
        data2b: '',
        partition_date_column: '', // 第一条数据表加字段
        partition_date_format: '', // 第一条数据
        partition_time_column: '',
        partition_time_format: '',
        interval: null,
        frequencytype: 1
      },
      tableOptions: [],
      textOptions: [],
      formatOptions: [
        { id: 1, value: 'yyyy-MM-dd hh:mm:ss' },
        { id: 2, value: 'yyyy-MM-dd' },
        { id: 3, value: 'hh:mm:ss' }
      ],
      formatOptionsMore: [
        { id: 1, value: 'HH:mm:ss' },
        { id: 2, value: 'HH:mm' },
        { id: 3, value: 'HH' }
      ],
      tableData: [],
      rules: {
        data1b: [
          { required: false, message: '请选择日期字段', trigger: 'change' }
        ],
        data2b: [
          { required: false, message: '请选择日期字段', trigger: 'change' }
        ],
        data2a: [
          { required: false, message: '请选择表', trigger: 'change' }
        ],
        data1a: [
          { required: false, message: '请选择表', trigger: 'change' }
        ],
        partition_date_format: [
          { required: false, message: '请选择日期格式', trigger: 'change' }
        ],
        partition_time_format: [
          { required: false, message: '请选择日期格式', trigger: 'change' }
        ],
        interval: [
          { required: true, validator: checkNumber, trigger: 'blur' }
        ]
      }
    }
  },
  activated () {
    this.init()
    this.searchList()
  },
  methods: {
    init () {
      /**
       * 遍历已选择的表（筛选出事实表的这条数据）
       * 赋值给日期字段表 tableOptions
       */
      this.selectTableTotal.forEach(item => { item.filed = item.label === this.jointResultData.fact_table.split('.')[1] ? 1 : 0 })
      this.tableOptions = this.selectTableTotal.filter(res => { return res.filed === 1 })
      // 默认调用根据表名去获取对应的字段名
      this.fetchDeac(this.tableOptions[0].label)
      this.formData = JSON.parse(JSON.stringify(this.reloadData))
      // this.formData = this.catchForm
    },
    searchList () {
      // 获取已经设置保存过的刷新过滤数据
      this.tableData = [...this.relaodFilterList]
    },
    nextModel (val) {
      this.processReloadData()
      this.$store.commit('SET_RELAOD_FILTER', this.formData)
      this.$refs.formData.validate(valid => {
        if (valid) {
          this.$parent.getStepCountAdd(val)
          this.$router.push('/analysisModel/createolap/advancedSet')
        }
      })
    },
    
    // 处理后端需要的数据
    processReloadData () {
      /**
       * ${partition_date_column} -- 拼接数据表跟表对应的字段
       * ${partition_date_format} -- 赋值第一个字段表对应的时间格式
       * ${partition_type} -- 日期是否存在多列
       **/
      this.totalSaveData.timingreFresh.autoReload = 
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_column = this.formData.data1a ? `${this.formData.data1a}.${this.formData.data1b}` : ''
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_format = this.formData.partition_date_format ? this.formData.partition_date_format : ''
      this.totalSaveData.models.modelDescData.partition_desc.partition_type = 'APPEND'
      if (this.formData.ispartition_type === true) {
        // 如果开启就选择表跟字段
        this.rules.data2a[0].required = true
        // 如果开启了日期多列就添加第二个日期格式
        this.totalSaveData.models.modelDescData.partition_desc.partition_time_column = this.formData.data2a ? `${this.formData.data2a}.${this.formData.data2b}` : ''
        this.totalSaveData.models.modelDescData.partition_desc.partition_time_format = this.formData.partition_time_format ? this.formData.partition_time_format : ''
      }
      // 如果选择了数据表 字段表就得变成必填
      // if (this.formData.data1a) this.rules.data1b[0].required = true
      // if (this.formData.data2a) this.rules.data2b[0].required = true
      this.rules.data1b[0].required = !!this.formData.data1a
      this.rules.data2b[0].required = !!this.formData.data2a
      // 如果选择了字段表 日期格式就得变成必填
      // if (this.formData.data1b) this.rules.partition_date_format[0].required = true
      // if (this.formData.data2b) this.rules.partition_time_format[0].required = true
      this.rules.partition_date_format[0].required = !!this.formData.data1b
      this.rules.partition_time_format[0].required = !!this.formData.data1b
      // 如果单独选择了日期格式  就要把日期字段变成必填
      // if (this.formData.partition_date_format) {
      //   this.rules.data1a[0].required = true
      // }
      this.rules.data1a[0].required = !!this.formData.partition_date_format
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/analysisModel/createolap/setMeasure')
    },
    // 添加过滤条件
    addReloadSet (data) {
      data ? this.$refs.dialog.dialog(data) : this.$refs.dialog.dialog()
    },
    visibleData (type) {
      this.idx = type
    },
    // 根据表的name查出对应的所有字段
    fetchDeac (val) {
      let valId = this.selectTableTotal.filter((res, index) => {
        return res.label === val
      })
      this.saveSelectAllList.forEach((item, index) => {
        // let items = JSON.parse(item)
        if (item.resourceId === valId[0].id) {
          const textOptions = item.column.filter(v => v.type === 'date' || v.type === 'timestamp' || v.type === 'datetime')
          this.textOptions = textOptions
        }
      })
    },
    selectTable (val) {
      if (val !== '') {
        this.fetchDeac(val)
      } else {
        this.textOptions = []
        this.formData.data1b = ''
      }
    },
    // 删除刷新过滤列表
    handleChange (val) {
      let idx = val.$index
      this.$confirm('是否删除这条数据？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('deleteReloadFilterTableList', val.row.ids)
        setTimeout(() => {
          this.$message.success('删除成功')
          this.tableData.splice(idx, 1)
        }, 500)
      })
    },
    changeUploadNum (val) {
      // console.log(val)
    },
    // 改变日期是否存在多列按钮
    changeDataMany (val) {
      console.log(val)
      if (!val) {
        // 倘若关闭是否存在多列就得关闭第二个对应的日期格式
        delete this.totalSaveData.models.modelDescData.partition_desc.partition_time_column
        delete this.totalSaveData.models.modelDescData.partition_desc.partition_time_format
      }
    }
  },
  computed: {
    ...mapGetters(['selectTableTotal', 'relaodFilterList', 'jointResultData', 'SaveFactData', 'reloadData', 'saveSelectAllList', 'totalSaveData'])
  }
}
</script>

<style lang="stylus" scoped>
.reloadSet{
  background #f2f2f2;
  height: 100%;
  margin-top: 16px;
  margin-bottom: 76px;
  h3{
    font-family: PingFangSC-Medium;
    font-size: 16px;
    color: #262626;
    letter-spacing: 0;
    background: #fff;
    margin-top: 16px;
    padding: 16px 16px 0 16px;
  }
  h4{
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #444444;
    letter-spacing: 0;
    line-height: 12px;
    width 100%
    border-bottom 1px solid #D9D9D9
    height 30px
    line-height 30px
    font-weight: 100;
  }
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
  }
  .el-form{padding: 16px 24px !important;}
  .uplaodNum{
    >>>.el-form-item__error{
      left 200px
    }
    >>>.el-radio-group {
      margin-left:20px;
    }
  }
  >>>.el-table__body, >>>.el-table__header{
    width auto!important
  }
  >>>.el-form-item{
    margin-top:20px;
  }
  >>>.el-form-item__label{
    width 180px
    text-align right
    font-family: PingFangSC-Regular;
    font-size: 14px;
    color: #5A5A5A;
  }
  >>>.el-input{
    height 40px
    width 100%
    margin-left 15px
    margin-right 30px
    .el-input__inner{
      height 40px
    }
  }
  >>>.el-input__suffix{
    // top 10px
  }
  >>>.is-focus{
    .el-input__suffix{
      top 0px
    }
  }
  .datarowmore{
    width 80%
  }
  .uplaodNum{
    float left
    >>>.el-input{
      margin-left: 0 !important;
    }
  }
  >>>.el-table__body tr:nth-child(even){
      background #F5F7FA
    }
  >>>.el-table__header th{
      background #444444
      padding 8px 0
      color #ffffff
      font-family: PingFangSC-Regular;
      font-size: 14px;
    }
  >>>.el-table{
    overflow initial!important
    .el-table__body-wrapper{
      overflow initial!important
    }
  }
  .formData{
    >>>.el-form-item__error{
      left 200px!important
    }
  }
}
</style>
