<template>
  <div class="queries f-s-14 c-333 dis-flex">
    <FolderAside :menuList="menuList" :menuDefault="menuDefault"></FolderAside>
    <div class="content">
      <ResultBox v-if="tableData.length > 0" :tableData="tableData"></ResultBox>
    </div>
    <el-dialog class="visible" title="保存查询结果" :visible.sync="dialogFormVisible" width="40%">
      <el-form :model="form">
        <el-form-item label="选择文件夹" label-width="120px">
          <el-select class="visibleInput" v-model="form.region" placeholder="请选择文件夹">
            <el-option label="文件夹1" value="shanghai"></el-option>
            <el-option label="文件夹2" value="beijing"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="查询结果名称" label-width="120px">
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

import ResultBox from '@/components/olapComponent/common/ResultBox'
import FolderAside from '@/components/olapComponent/common/FolderAside'

export default {
  components: { ResultBox, FolderAside },
  data () {
    return {
      search: '',
      textarea: '',
      lineNumber: '',
      checked: false,
      tableData: [
        [
          { colspan: 1, rowspan: 1, value: '标题1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '标题8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ],
        [
          { colspan: 1, rowspan: 1, value: '内容1', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容2', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容3', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容4', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容5', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容6', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容7', type: 3, attrs: {} },
          { colspan: 1, rowspan: 1, value: '内容8', type: 3, attrs: {} }
        ]
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
      menuList: [
        {
          catalogList: [],
          dataId: 779035117190185,
          dataName: '测试嵌套报表'
        },
        {
          catalogList: [ {
            dataId: 795406468250198,
            dataName: '东莞中小学成绩报告',
            dataType: 3,
            isShare: 1
          } ],
          dataId: 796247848830160,
          dataName: '东莞'
        },
        {
          catalogList: [ {
            dataId: 795247414460141,
            dataName: '测试汇总88',
            dataType: 1,
            isShare: 1
          } ],
          dataId: 776468771050089,
          dataName: '测试通用报表'
        },
        {
          catalogList: [ {
            dataId: 795385794900198,
            dataName: '测试11',
            dataType: 2,
            isShare: 1
          } ],
          dataId: 777364408760098,
          dataName: '测试主从报表'
        }
      ],
      menuDefault: {
        children: 'catalogList', // 子集的属性
        label: 'dataName', // 标题的属性
        disabled: function (resData) {
          if (resData.isShare === 0) {
            return false
          } else {
            return true
          }
        }
      }
    }
  },
  mounted () {},
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
      .result {
        overflow: auto;
        .resultBox {
          .top {
            .left {
              .item {
                margin-right: 10px;
                .tit {
                  margin-right: 2px;
                }
              }
            }
            .right {
              margin: 20px 0;
            }
          }
        }
      }
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
