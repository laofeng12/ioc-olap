<template>
  <div class="reloadSet">
     <el-form>
        <el-form-item label="刷新设置" class="item_line"></el-form-item>
        <el-form-item label="自动刷新" class="item_line"></el-form-item>
        <el-form-item label="自动刷新模型？">
          <template>
            <div>
              <el-switch
                v-model="autoReload"
                @change="changeUploadNum"
                active-color="#13ce66"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="更新频率" v-if="autoReload">
          <template>
            <div class="uplaodNum">
              <el-input type="text"></el-input>
              <el-radio-group v-model="radio">
                <el-radio :label="3">小时</el-radio>
                <el-radio :label="6">天</el-radio>
                <el-radio :label="9">月</el-radio>
              </el-radio-group>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期字段" class="item_line"></el-form-item>
        <el-form-item label="日期字段表">
          <template>
            <div>
               <el-select v-model="value1" placeholder="请选择数据表">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期字段">
          <template>
            <div>
               <el-select v-model="value2" placeholder="请选择日期字段">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期格式">
          <template>
            <div>
               <el-select v-model="value3" placeholder="请选择日期格式">
                <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
              </el-select>
            </div>
          </template>
        </el-form-item>
        <el-form-item label="日期存在多列？">
          <template>
            <div>
              <el-switch
                v-model="dataMany"
                active-color="#13ce66"
                @change="changeDataMany"
                inactive-color="#cccccc">
              </el-switch>
            </div>
          </template>
        </el-form-item>
        <div v-if="dataMany">
          <el-form-item label="日期字段表">
            <template>
              <div>
                <el-select v-model="value1" placeholder="请选择数据表">
                  <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
              </div>
            </template>
          </el-form-item>
          <el-form-item label="日期字段">
            <template>
              <div>
                <el-select v-model="value2" placeholder="请选择日期字段">
                  <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
              </div>
            </template>
          </el-form-item>
          <el-form-item label="日期格式">
            <template>
              <div>
                <el-select v-model="value3" placeholder="请选择日期格式">
                  <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value"></el-option>
                </el-select>
              </div>
            </template>
          </el-form-item>
        </div>
        <el-form-item label="过滤设置" class="item_line"></el-form-item>
        <el-table
          :data="tableData"
          ref="multipleTable"
          tooltip-effect="dark"
          @selection-change="handleSelectionChange"
          style="margin-top: 10px;">
          <el-table-column type="selection" prop="序号" align="center"></el-table-column>
          <el-table-column prop="apiName" label="表名称" align="center"> </el-table-column>
          <el-table-column prop="type" label="字段" align="center"> </el-table-column>
          <el-table-column prop="catalogName" label="过滤方式" align="center"> </el-table-column>
          <el-table-column prop="apiPaths" label="过滤值" align="center">
            <template slot-scope="scope">
              <div>
                <el-input type="text" v-model="scope.row.apiPaths"></el-input>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center">
            <template slot-scope="scope">
              <div class="play">
                <el-button type="text" size="mini" icon="el-icon-edit"></el-button>
                <el-button type="text" size="mini" icon="el-icon-delete"></el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-button type="primary">添加过滤条件</el-button>
     </el-form>
     <steps class="steps" :step="5" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import steps from '@/components/olapComponent/common/steps'
export default {
  components: {
    steps
  },
  data () {
    return {
      autoReload: false,
      dataMany: false,
      radio: 3,
      value1: '',
      value2: '',
      value3: '',
      options: [{
        value: '选项1',
        label: '黄金糕'
      }, {
        value: '选项2',
        label: '双皮奶'
      }, {
        value: '选项3',
        label: '蚵仔煎'
      }, {
        value: '选项4',
        label: '龙须面'
      }, {
        value: '选项5',
        label: '北京烤鸭'
      }],
      tableData: [
        { apiName: '111', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '222', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '333', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '444', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' },
        { apiName: '555', type: '递归', catalogName: 'string', apiPaths: '啦啦啦啦啦', radio: '2' }
      ]
    }
  },
  methods: {
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      // this.$router.push('/olap/createolap/reloadSet')
      this.$message.error('暂未开发')
    },
    prevModel (val) {
      this.$parent.getStepCountReduce(val)
      this.$router.push('/olap/createolap/setMeasure')
    },
    handleSelectionChange (val) {

    },
    changeUploadNum (val) {
      console.log(val)
    },
    changeDataMany (val) {
      console.log(val)
    }
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
  .uplaodNum{
    float left
    >>>.el-input{
      width 100px
    }
  }
}
</style>
