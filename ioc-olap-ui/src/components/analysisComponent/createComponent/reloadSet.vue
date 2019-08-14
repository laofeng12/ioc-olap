<template>
  <div class="reloadSet">
     <el-form :model="formData" :rules="rules" ref="formData">
        <el-form-item label="刷新设置" class="item_line"></el-form-item>
        <el-form-item label="自动刷新" class="item_line"></el-form-item>
        <el-form-item label="自动刷新模型？">
          <template>
            <div>
              <el-switch
                v-model.number="formData.autoReload"
                @change="changeUploadNum"
                active-color="#13ce66"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="更新频率" v-if="formData.autoReload">
          <template>
            <div class="uplaodNum">
              <el-input type="text" v-model="formData.INTERVAL"></el-input>
              <el-radio-group v-model="formData.frequencytype">
                <el-radio :label="1">小时</el-radio>
                <el-radio :label="2">天</el-radio>
                <el-radio :label="3">月</el-radio>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期字段" class="item_line"></el-form-item>
        <el-form-item label="日期字段表" class="datarowmore">
          <el-select v-model="formData.data1a" placeholder="请选择数据表" @change="selectTable" @visible-change="visibleData(0)">
            <el-option v-for="(item, index) in tableOptions" :key="item.id" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期字段" prop="data1b">
          <el-select v-model="formData.data1b" placeholder="请选择日期字段">
            <el-option v-for="(item, index) in textOptions" :key="index" :label="item.name" :value="item.name"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期格式">
          <el-select v-model="formData.partition_date_format" placeholder="请选择日期格式">
            <el-option v-for="item in formatOptions" :key="item.id" :label="item.value" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期存在多列？">
          <template>
            <div>
              <el-switch
                v-model="formData.partition_type"
                active-color="#13ce66"
                @change="changeDataMany"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <div v-if="formData.partition_type">
        <el-form-item label="日期字段表" class="datarowmore">
          <el-select v-model="formData.data2a" placeholder="请选择数据表" @change="selectTable" @visible-change="visibleData(1)">
            <el-option v-for="(item, index) in tableOptions" :key="index" :label="item.label" :value="item.label"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期字段" prop="data2b">
          <el-select v-model="formData.data2b" placeholder="请选择日期字段">
            <el-option v-for="(item, index) in textOptions" :key="index" :label="item.name" :value="item.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="日期格式">
          <el-select v-model="formData.partition_time_format" placeholder="请选择日期格式">
            <el-option v-for="item in formatOptions" :key="item.id" :label="item.value" :value="item.value"></el-option>
          </el-select>
        </el-form-item>
        </div>
        <el-form-item label="过滤设置" class="item_line"></el-form-item>
        <el-table
          :data="relaodFilterList"
          ref="multipleTable"
          tooltip-effect="dark"
          style="margin-top: 10px;">
          <el-table-column type="index" width="50" prop="序号" align="center"></el-table-column>
          <el-table-column prop="TABLENAME" label="表名称" align="center"> </el-table-column>
          <el-table-column prop="FIELD" label="字段" align="center"> </el-table-column>
          <el-table-column prop="PATTERN" label="过滤方式" align="center"> </el-table-column>
          <el-table-column prop="PARAMETER" label="过滤值" align="center">
            <template slot-scope="scope">
              <div>
                <span>{{scope.row.PARAMETER}}</span>
                <span v-if="scope.row.PATTERN === 'BETWEED'">，{{scope.row.PARAMETERBE}}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            width="100"
            align="center">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" @click="addReloadSet(scope.row)" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete" @click="handleChange(scope)"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button style="float:right;margin-top:20px;" type="primary" @click="addReloadSet()">添加过滤条件</el-button>
     </el-form>
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
    return {
      formData: {
        autoReload: false,
        partition_type: false,
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
      tableOptions: [
        // { label: 'a' },
        // { label: 'b' },
        // { label: 'c' }
      ],
      textOptions: [
        // { comment: 'aaa', columnName: 'aaa' },
        // { comment: 'bbb', columnName: 'bbb' },
        // { comment: 'vccc', columnName: 'vccc' },
        // { comment: 'vvvv', columnName: 'vvvv' },
        // { comment: 'bbbbb', columnName: 'bbbbb' }
      ],
      formatOptions: [
        { id: 1, value: 'yyyy-MM-dd hh:mm:ss' },
        { id: 2, value: 'yyyy-MM-dd' },
        { id: 3, value: 'hh:mm:ss' }
      ],
      tableData: [],
      rules: {
        data1b: [
          { required: false, message: '请选择日期字段', trigger: 'change' }
        ],
        data2b: [
          { required: false, message: '请选择日期字段', trigger: 'change' }
        ]
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.tableOptions = [...this.selectTableTotal]
      this.tableData = this.relaodFilterList
      this.formData = this.reloadData
    },
    nextModel (val) {
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_column = this.formData.data1a ? `${this.formData.data1a}.${this.formData.data1b}` : ''
      this.totalSaveData.models.modelDescData.partition_desc.partition_date_format = this.formData.partition_date_format ? this.formData.partition_date_format : ''
      this.totalSaveData.models.modelDescData.partition_desc.partition_type = 'APPEND'
      if (this.formData.partition_type === true) {
        this.totalSaveData.models.modelDescData.partition_desc.partition_time_column = `${this.formData.data2a}.${this.formData.data2b}`
        this.totalSaveData.models.modelDescData.partition_desc.partition_time_format = this.formData.partition_time_format
      }
      this.$refs.formData.validate(valid => {
        if (valid) {
          this.$parent.getStepCountAdd(val)
          this.$router.push('/analysisModel/createolap/advancedSet')
        }
      })
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/analysisModel/createolap/setMeasure')
    },
    verification () {
      this.$refs.formData.validate((valid) => {
        if (valid) {

        }
      })
    },
    addReloadSet (data) {
      data ? this.$refs.dialog.dialog(data) : this.$refs.dialog.dialog()
    },
    visibleData (type) {
      this.idx = type
    },
    selectTable (val) {
      const params = {
        dsDataSourceId: 2,
        tableName: val
      }
      let valId = this.selectTableTotal.filter((res, index) => {
        return res.label === val
      })
      // this.idx === 0 ? this.formData.data1b = '' : this.formData.data2b = ''
      // this.$store.dispatch('GetColumnList', params).then(res => {
      //   this.textOptions = res.data
      // })
      this.saveSelectAllList.forEach((item, index) => {
        let items = JSON.parse(item)
        if (items.resourceId === valId[0].id) {
          this.textOptions = items.data.columns
        }
      })
      // this.$store.dispatch('GetResourceInfo', { resourceId: valId[0].id, type: '1' }).then(res => {
      //   if (res) {
      //     this.textOptions = res.data.columns
      //   }
      // })
    },
    handleChange (val) {
      let idx = val.$index
      this.$confirm('是否删除这条数据？', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('deleteReloadFilterTableList', val.row.id)
        setTimeout(() => {
          this.$message.success('删除成功')
          this.tableData.splice(idx, 1)
        }, 500)
      })
    },
    changeUploadNum (val) {
      console.log(val)
    },
    changeDataMany (val) {
      if (val !== true) {
        // this.formData.partition_date_column.splice(1, 1)
        // this.formData.partition_date_format.splice(1, 1)
        // this.formData.partition_time_format.splice(1, 1)
        delete this.totalSaveData.models.modelDescData.partition_desc.partition_time_column
        delete this.totalSaveData.models.modelDescData.partition_desc.partition_time_format
      }
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      relaodFilterList: 'relaodFilterList',
      reloadData: 'reloadData',
      saveSelectAllList: 'saveSelectAllList',
      totalSaveData: 'totalSaveData'
    })
  }
}
</script>

<style lang="stylus" scoped>
.reloadSet{
  padding 30px 100px
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
  }
  >>>.el-form-item__label{
    width 120px
    text-align left
  }
  >>>.el-input{
    height 30px
    margin-right 30px
    .el-input__inner{
      height 30px
    }
  }
  >>>.el-input__suffix{
    top 10px
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
      width 100px
    }
  }
}
</style>
