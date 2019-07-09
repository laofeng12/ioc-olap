<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <Aside :menuList="menuList"></Aside>
    <div class="content">
      <div class="editSql">
        <div class="editor">
          <div class="number">
            <div>1</div>
            <div>2</div>
            <div>3</div>
            <div>4</div>
            <div>5</div>
            <div>6</div>
            <div>7</div>
            <div>8</div>
            <div>9</div>
            <div>10</div>
          </div>
          <el-input class="textarea" type="textarea" :rows="10" placeholder="请输入内容" v-model="textarea"></el-input>
        </div>
        <div class="bottom">
          <el-button type="primary" size="mini">查询</el-button>
          <el-checkbox class="checkbox" v-model="checked">限制查询行数</el-checkbox>
          <el-input class="lineNumber" v-model="lineNumber" :disabled="!checked" size="mini"></el-input>
        </div>
      </div>
      <ResultBox v-if="tableData.length > 0" :theadData="theadData" :tableData="tableData" :titleShow="true"></ResultBox>
    </div>
    <el-dialog class="visible" title="保存查询结果" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="form">
        <el-form-item label="选择文件夹" :label-width="formLabelWidth">
          <el-select class="visibleInput" v-model="form.region" placeholder="请选择文件夹">
            <el-option label="文件夹1" value="shanghai"></el-option>
            <el-option label="文件夹2" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="查询结果名称" :label-width="formLabelWidth">
          <el-input class="visibleInput" v-model="form.name" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Aside from '@/components/olapComponent/common/Aside'
import ResultBox from '@/components/olapComponent/common/ResultBox'

export default {
  components: { Aside, ResultBox },
  data () {
    return {
      search: '',
      textarea: '',
      lineNumber: '',
      checked: false,
      theadData: [
        { prop: 'column1', label: '日期' },
        { prop: 'column2', label: '姓名' },
        { prop: 'column3', label: '地址' },
        { prop: 'column4', label: '日期' },
        { prop: 'column5', label: '姓名' },
        { prop: 'column6', label: '地址' }
      ],
      tableData: [
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
        { column1: '2016-05-02', column2: '王小虎', column3: '上海市普陀区金沙江路 1518 弄', column4: '2016-05-02', column5: '王小虎', column6: '上海市普陀区金沙江路 1518 弄' },
      ],
      dialogFormVisible: false,
      form: {
        name: '',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
      },
      formLabelWidth: '120px',
      menuList: [
        {
          title: '交通数据模型',
          id: '1',
          row: [
            { name: '路口违章车辆1', id: '1-1' },
            { name: '路口违章车辆2', id: '1-2' },
            { name: '路口违章车辆3', id: '1-3' },
            { name: '路口违章车辆4', id: '1-4', row: [ { name: '闯红灯', id: '1-4-1' }, { name: '压线', id: '1-4-2' } ] }
          ]
        },
        {
          title: '交通数据模型',
          id: '2'
        }
      ]
    }
  },
  component: { Aside },
  methods: {
    handleOpen (key, keyPath) {
      console.log('open', key, keyPath)
    },
    handleClose (key, keyPath) {
      console.log('close', key, keyPath)
    },
    handleSelect (key, keyPath) {
      console.log('select', key, keyPath)
    }
  }
}
</script>
<style lang="scss" scoped>
  .queries {
    .content {
      width: 100%;
      flex-grow: 1;
      .editSql {
        .editor {
          display: flex;
          .number {
            width: 40px;
            text-align: center;
            height: 30px;
            line-height: 30px;
            margin-top: 7px;
          }
          .textarea {
            overflow: hidden;
            /deep/ textarea {
              line-height: 30px;
              border: none;
            }
          }
        }
        .bottom {
          display: flex;
          align-items: center;
          justify-content: center;
          margin-top: 20px;
          .checkbox {
            margin-left: 20px;
          }
          .lineNumber {
            width: 60px;
          }
        }
      }
      /*.result {*/
        /*overflow: auto;*/
        /*.resultBox {*/
          /*.top {*/
            /*.left {*/
              /*.item {*/
                /*margin-right: 10px;*/
                /*.tit {*/
                  /*margin-right: 2px;*/
                /*}*/
              /*}*/
            /*}*/
            /*.right {*/
              /*margin: 20px 0;*/
            /*}*/
          /*}*/
        /*}*/
      /*}*/
    }
    .visible {
      .visibleInput {
        width: 250px;
      }
    }
  }
  .cur-poi {
    cursor: pointer;
  }
</style>
