<template>
  <div class="reloadSet">
     <el-form :model="formData">
        <el-form-item label="刷新设置" class="item_line"></el-form-item>
        <el-form-item label="自动刷新" class="item_line"></el-form-item>
        <el-form-item label="自动刷新模型？">
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
        <el-form-item label="更新频率" v-if="formData.autoReload">
          <template>
            <div class="uplaodNum">
              <el-input type="text" v-model="formData.reloadCount"></el-input>
              <el-radio-group v-model="formData.timeType">
                <el-radio :label="1">小时</el-radio>
                <el-radio :label="2">天</el-radio>
                <el-radio :label="3">月</el-radio>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期字段" class="item_line"></el-form-item>
        <el-form-item label="日期字段表" class="datarowmore">
          <template>
            <div>
               <el-select v-model="formData.serveTable" placeholder="请选择数据表" @change="selectTable">
                <el-option v-for="item in tableOptions" :key="item.label" :label="item.label" :value="item.label"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期字段">
          <template>
            <div>
               <el-select v-model="formData.dateText" placeholder="请选择日期字段">
                <el-option v-for="item in textOptions" :key="item.comment" :label="item.columnName" :value="item.comment"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期格式">
          <template>
            <div>
               <el-select v-model="formData.dateFormat" placeholder="请选择日期格式">
                <el-option v-for="item in formatOptions" :key="item.id" :label="item.value" :value="item.id"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期存在多列？">
          <template>
            <div>
              <el-switch
                v-model="formData.dataMany"
                active-color="#13ce66"
                @change="changeDataMany"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <div v-if="formData.dataMany">
        <el-form-item label="日期字段表" class="datarowmore">
          <template>
            <div>
               <el-select v-model="formData.serveTable1" placeholder="请选择数据表" @change="selectTable">
                <el-option v-for="item in tableOptions" :key="item.label" :label="item.label" :value="item.label"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期字段">
          <template>
            <div>
               <el-select v-model="formData.dateText1" placeholder="请选择日期字段">
                <el-option v-for="item in textOptions" :key="item.comment" :label="item.columnName" :value="item.comment"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期格式">
          <template>
            <div>
               <el-select v-model="formData.dateFormat1" placeholder="请选择日期格式">
                <el-option v-for="item in formatOptions" :key="item.id" :label="item.value" :value="item.id"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        </div>
        <el-form-item label="过滤设置" class="item_line"></el-form-item>
        <el-table
          :data="formData.tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          style="margin-top: 10px;">
          <el-table-column type="index" width="50" prop="序号" align="center"></el-table-column>
          <el-table-column prop="reloadName" label="表名称" align="center"> </el-table-column>
          <el-table-column prop="reloadText" label="字段" align="center"> </el-table-column>
          <el-table-column prop="filterType" label="过滤方式" align="center"> </el-table-column>
          <el-table-column prop="filterValue" label="过滤值" align="center"> </el-table-column>
          <el-table-column
            label="操作"
            width="100"
            align="center">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" @click="addMeasure(scope.row)" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete" @click="handleChange(scope)"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary" @click="addReloadSet()">添加过滤条件</el-button>
     </el-form>
     <add-reload-set ref="dialog"></add-reload-set>
     <steps class="steps" :step="5" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
import addReloadSet from '@/components/olapComponent/dialog/addReloadSet'
import { mapGetters } from 'vuex'
export default {
  components: {
    steps, addReloadSet
  },
  data () {
    return {
      formData: {
        autoReload: false,
        dataMany: false,
        reloadCount: '',
        timeType: 1,
        serveTable: '',
        serveTable1: '',
        dateText: '',
        dateText1: '',
        dateFormat: '',
        dateFormat1: ''
      },
      tableOptions: [],
      textOptions: [],
      formatOptions: [
        { id: 1, value: 'yyyy-MM-dd hh:mm:ss' },
        { id: 2, value: 'yyyy-MM-dd' },
        { id: 3, value: 'hh:mm:ss' }
      ],
      tableData: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.tableOptions = this.selectTableTotal
      this.tableData = [...this.relaodFilterList]
    },
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      this.$router.push('/olap/createolap/advancedSet')
      console.log(this.formData)
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/setMeasure')
    },
    addReloadSet (data) {
      data ? this.$refs.dialog.dialog(data) : this.$refs.dialog.dialog()
    },
    selectTable (val) {
      const params = {
        dsDataSourceId: 2,
        tableName: val
      }
      this.$store.dispatch('GetColumnList', params).then(res => {
        this.textOptions = res.data
      })
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
      console.log(val)
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      relaodFilterList: 'relaodFilterList'
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
