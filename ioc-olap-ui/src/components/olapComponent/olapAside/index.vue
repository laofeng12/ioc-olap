<template>
  <div class="chart-setting-box">
    <el-form :model="dashBoardForm" :rules="rules" ref="addDashboardForm">
      <el-row :gutter="8">
        <el-col :span="12" class="cus-drag-setting">
          <div class="grid-content">
            <!--报表名称-->
            <shirink-pannel name="报表名称">
              <div slot="content">
                <el-form-item label="" prop="fileName">
                  <el-select v-model="dashBoardForm.fileName" placeholder="请选择olap模型"
                             @change="getFileName(dashBoardForm.fileName)" size="small">
                    <el-option label="新建" value="1"></el-option>
                    <el-option
                      v-for="item in categoryList"
                      :key="item.categoryId"
                      :label="item.categoryName"
                      :value="item.categoryId">
                    </el-option>
                  </el-select>
                </el-form-item>
                <el-form-item label="" prop="statementName">
                  <el-input v-model="dashBoardForm.statementName" placeholder="请输入报表名称（1～20字）" size="small"></el-input>
                </el-form-item>
              </div>
            </shirink-pannel>

            <!--数据集/自助报表-->
            <shirink-pannel name="数据集/自助报表" :isHasAdd="true" @addClick="addDataSet()">
              <div slot="content">
                <div class="no-dataset" v-if="dataSetData.length === 0">请选择数据集/自助报表</div>
                <ul class="has-dataset" v-else>
                  <li class="data-list" v-for=" (item, index) in dataSetData" :key="index"><span><i
                    class="el-icon-tickets"></i>{{item.name}}</span><i class="close el-icon-close"
                                                                       @click="delDataSet()"></i></li>
                </ul>
              </div>
            </shirink-pannel>

            <!--维度-->
            <shirink-pannel name="维度">
              <div class="data-set" slot="content">
                <div class="no-dataset" v-if="dataSetData.length === 0">请选择数据集</div>
                <div v-else>
                  <el-input size="small" placeholder="请输入关键词" v-model="dashBoardForm.searchKey">
                    <i slot="prefix" class="el-input__icon el-icon-search"></i>
                  </el-input>
                  <ul class="has-dataset dimen" id="dimen">
                    <li class="data-list dimen-list " v-for=" (item, index) in dimenData" :key="index"
                        :data-type="item.type">
                      <span><i class="el-icon-notebook-2"></i>{{item.name}}</span>
                    </li>
                  </ul>
                </div>
              </div>
            </shirink-pannel>

            <!--度量-->
            <shirink-pannel name="度量">
              <div class="data-set" slot="content">
                <div class="no-dataset" v-if="dataSetData.length === 0">请选择数据集</div>
                <div v-else>
                  <el-input size="small" placeholder="请输入关键词" v-model="dashBoardForm.measureSearch">
                    <i slot="prefix" class="el-input__icon el-icon-search"></i>
                  </el-input>
                  <ul class="has-dataset dimen measure" id="measure">
                    <li class="data-list dimen-list measure-list " v-for=" (item,index) in measureData" :key="index"
                        :data-type="item.type">
                      <span><i class="el-icon-notebook-2"></i>{{item.name}}</span>
                    </li>
                  </ul>
                </div>
              </div>
            </shirink-pannel>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="grid-content">
            <!--维度设定-->
            <shirink-pannel name="维度设定">
              <div slot="content" class="filtrate-wrap">
                <ul class="has-dataset draw-list drawListClass" id="drawList">
                  <header>维度(X轴)：</header>
                  <div class="no-dataset drawListClass" v-if="rItems.length === 0">拖动数据到此处</div>
                  <filter-temp v-for="(item,index) in rItems" :name="item.name" :dataType="item.dataType"
                               :data-id="item.dataId"
                               :key="index"
                               :index="index"
                               :items="rItems"
                               :showEdit="false"
                               @deleteIndex="delRow">
                  </filter-temp>
                </ul>
                <ul class="has-dataset draw-list drawColClass">
                  <header>维度(Y轴)：</header>
                  <div class="no-dataset drawColClass" v-if="cItems.length === 0">拖动数据到此处</div>
                  <filter-temp v-for="(item,index) in cItems" :name="item.name" :dataType="item.dataType"
                               :data-id="item.dataId"
                               :key="index"
                               :index="index"
                               :items="cItems"
                               :showEdit="false"
                               @deleteIndex="delCol">
                  </filter-temp>
                </ul>
                <ul class="has-dataset draw-list drawValClass">
                  <header>数值：</header>
                  <div class="no-dataset drawValClass" v-if="nItems.length === 0">拖动数据到此处</div>
                  <filter-temp v-for="(item,index) in nItems" :name="item.name" :dataType="item.dataType"
                               :data-id="item.dataId"
                               :key="index"
                               :index="index"
                               :items="nItems"
                               :showEdit="false"
                               @deleteIndex="delVal">
                  </filter-temp>
                </ul>
              </div>
            </shirink-pannel>

            <!--筛选器-->
            <shirink-pannel name="筛选器">
              <div slot="content" class="filtrate-wrap">
                <ul class="has-dataset filtrate-list filtrateClass" id="filtrate">
                  <div class="no-dataset filtrateClass" id="no-filtrate" v-if="bItems.length === 0">拖动数据到此处</div>
                  <filter-temp v-else v-for="(item,index) in bItems" :name="item.name" :dataType="item.dataType"
                               :data-id="item.dataId"
                               :key="index"
                               :index="index"
                               :items="bItems" @editClick="editF(item.dataType, item, index)" @deleteIndex="delFiter">
                  </filter-temp>
                </ul>
              </div>
            </shirink-pannel>
          </div>
        </el-col>
      </el-row>
    </el-form>

    <!-- 选择数据集/自助报表 -->
    <el-dialog title="选择数据集/自助报表" :visible.sync="dialogDataSet" class="newfile-dialog">
      <el-form :inline="true" :model="formInline" label-width="80px">
        <el-form-item label="数据集：" prop="">
          <el-input v-model="formInline.name" placeholder="请输入关键词" size="small"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search()" size="small">查询</el-button>
        </el-form-item>
      </el-form>
      <div>
        <el-table :header-cell-style="{background:'#f0f2f5'}" height="250" :data="tableData" border style="width: 100%">
          <el-table-column type="index" label="序号" width="100"></el-table-column>
          <el-table-column prop="address" label="数据集名称"></el-table-column>
          <el-table-column prop="name" label="创建人"></el-table-column>
          <el-table-column prop="date" label="创建时间"></el-table-column>
          <el-table-column label="操作" width="80">
            <template slot-scope="scope">
              <el-button @click="chooseRow(scope.row)" type="text" size="small">选择</el-button>
            </template>
          </el-table-column>
        </el-table>
        <element-pagination :isSureBtn="true" @sureClick="dialogDataSet = false"></element-pagination>
      </div>
    </el-dialog>

    <!--拖拽后弹出筛选弹窗-->
    <!--日期窗口-->
    <filtrate-dialog :getDataId="getDataId" :getTextAllData="bItems" :showDateF='true' width='50%'
                     :dialogVisible="dialogDateFiltrate" @sureClick="dateSure" @close="dateClose()"></filtrate-dialog>
    <!--文本窗口-->
    <filtrate-dialog :getDataId="getDataId" :getTextAllData="bItems" :showTextF='true' width="500px"
                     :dialogVisible="dialogTextFiltrate" @sureClick="textSure" @close="textClose()"></filtrate-dialog>
    <!--数值窗口-->
    <filtrate-dialog :getDataId="getDataId" :getTextAllData="bItems" :showNumF='true' width="500px"
                     :dialogVisible="dialogNumFiltrate" @sureClick="numSure" @close="numClose()"></filtrate-dialog>

    <!-- 新建文件夹弹框开始 -->
    <el-dialog title="新建文件夹" :visible.sync="dialogFormVisible" class="newfile-dialog" width="400px">
      <el-form :model="ruleForm" :rules="rules" ref="ruleForm">
        <el-form-item label="" prop="name">
          <el-input v-model="ruleForm.name" placeholder="请输入文件夹名称（1~20字）" size="small"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false" size="small">取 消</el-button>
        <el-button type="primary" size="small" @click="submitForm('ruleForm')">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import ShirinkPannel from '@/components/olapComponent/olapAside/ShirinkPannel'
import ElementPagination from '@/components/olapComponent/olapAside/ElementPagination'
import FilterTemp from '@/components/BITemp/FilterTemp'
// import RowTemp from '@/components/BITemp/RowTemp'
// import StatementTable from '@/components/BITemp/StatementTable'
import FiltrateDialog from '@/components/FiltrateDialogBywzh'
import _ from 'lodash'
import Sortable from 'sortablejs'

export default {
  components: { ShirinkPannel, FilterTemp, ElementPagination, FiltrateDialog },
  data () {
    return {
      categoryList: [],
      dialogDataSet: false,
      dataSetData: [{ name: '南城交通数据源1' }],
      formInline: {
        name: ''
      },
      dashBoardForm: {
        searchKey: '',
        measureSearch: '',
        statementName: '',
        fileName: '',
        name: ''
      },
      tableData: [
        {
          date: '2016-05-02',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄'
        }
      ],
      dimenData: [
        { name: '南城鸿福路文本', type: 'Text', id: '1' }
      ],
      measureData: [
        { name: '车辆4', type: 'Integer', id: '44' }
      ],
      // 拖拽到筛选器
      nItems: [],
      bItems: [],
      rItems: [],
      cItems: [],
      getDataId: {},
      dialogDateFiltrate: false,
      dialogTextFiltrate: false,
      dialogNumFiltrate: false,
      dialogFormVisible: false,
      ruleForm: {
        name: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入文件夹名称', trigger: 'blur' },
          { min: 1, max: 20, message: '文件夹名称不能超过20个字，请重新输入', trigger: 'blur' }
        ],
        fileName: [
          { required: true, message: '请选择报表存放文件夹', trigger: 'change' }
        ],
        statementName: [
          { required: true, message: '请输入自助报表名称', trigger: 'blur' },
          { min: 1, max: 20, message: '自助报表名称为1～20个字，请重新输入', trigger: 'blur' }
        ]
      }
    }
  },
  watch: {
    'dashBoardForm.searchKey' (val) {
      if (val) this.handleShrinkSearch(val, 'searchKey')
    },
    'dashBoardForm.measureSearch' (val) {
      if (val) this.handleShrinkSearch(val, 'measureSearch')
    }
  },
  mounted () {
    this.setSortTable()
  },
  methods: {
    // 选择文件夹名称
    getFileName (fileValue) {
      if (fileValue === '1') {
        this.dashBoardForm.fileName = ''
        this.dialogFormVisible = true
      }
    },
    submitForm (formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.dashBoardForm.fileName = this.ruleForm.name
          this.dialogFormVisible = false
        } else {
          return false
        }
      })
    },
    // 添加数据集/自助报表
    addDataSet () {
      this.dialogDataSet = true
    },
    // 删除数据集
    delDataSet () {
      this.$confirm('确定删除“引用数据集名称”吗？其相关字段维度将一起删除！', '删除数据集', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message({
          type: 'success',
          message: '删除成功!'
        })
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '已取消删除'
        })
      })
    },
    // 数据集选择当前行列
    chooseRow (row) {
      console.log(row)
    },
    // 删除维度行
    delRow (index) {
      this.rItems.splice(index, 1)
      console.log('deleted:', JSON.stringify(this.rItems))
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    // 删除维度列
    delCol (index) {
      this.cItems.splice(index, 1)
      console.log('deleted:', JSON.stringify(this.cItems))
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    // 删除数值
    delVal (index) {
      this.nItems.splice(index, 1)
      console.log('deleted:', JSON.stringify(this.nItems))
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    // 删除筛选器
    delFiter (index) {
      this.bItems.splice(index, 1)
      console.log('筛选器deleted:', JSON.stringify(this.bItems))
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    editF (dataType, item, index) {
      this.getDataId = item
      if (dataType === 'DATE') {
        this.dialogDateFiltrate = true
      }
      if (dataType === 'Integer') {
        this.dialogNumFiltrate = true
      }
      if (dataType === 'Text') {
        this.dialogTextFiltrate = true
      }
      // console.log(this.dialogTextFiltrate)
      console.log(dataType, item, index)
      // if (item) this.tempTextDialog = [item]
    },
    setSortTableOther () {
      let filtrate = document.querySelectorAll('.filtrateClass')
      let drawList = document.querySelectorAll('.drawListClass')
      let drawCol = document.querySelectorAll('.drawColClass')
      let drawVal = document.querySelectorAll('.drawValClass')
      const arr = [...filtrate, ...drawList, ...drawCol, ...drawVal]
      arr && arr.forEach(item => {
        Sortable.create(item, {
          group: 'shared', // set both lists to same group
          animation: 150,
          onAdd: function (evt) {
            evt.item.remove()
          }
        })
      })
    },
    // 日期筛选确认
    dateSure (filtratevisible) {
      this.dialogDateFiltrate = filtratevisible
      console.log('bItems dateSure', this.bItems)
    },
    // 日期筛选关闭
    dateClose (filtratevisible) {
      this.dialogDateFiltrate = filtratevisible
    },
    // 文本筛选确认
    textSure (filtratevisible) {
      this.dialogTextFiltrate = filtratevisible
      console.log('bItems textSure', this.bItems)
    },
    // 文本筛选关闭
    textClose (filtratevisible) {
      this.dialogTextFiltrate = filtratevisible
    },
    // 数值筛选确认
    numSure (filtratevisible) {
      this.dialogNumFiltrate = filtratevisible
      console.log('bItems numSure', this.bItems)
    },
    // 数值筛选确认
    numClose (filtratevisible) {
      this.dialogNumFiltrate = filtratevisible
    },
    // 维度查询
    handleShrinkSearch: _.debounce(function (val, search) {
      console.log(val, search)
    }, 500),
    // div设置拖拽
    setSortTable () {
      const dimen = document.querySelectorAll('#dimen')
      const measure = document.querySelectorAll('#measure')
      const arr = [...dimen, ...measure]
      arr && arr.forEach(item => {
        Sortable.create(item, {
          group: {
            name: 'shared',
            pull: 'clone',
            put: false // Do not allow items to be put into this list
          },
          sort: false,
          animation: 150,
          onEnd: evt => {
            console.log(evt.to.classList, evt.from.id)
            let index = evt.oldIndex
            let name = this[evt.from.id + 'Data'][index].name
            // let itemEl = evt.item
            // this.dataTypeF = itemEl.getAttribute('data-type')
            let dataId = this[evt.from.id + 'Data'][index].id
            let dataType = this[evt.from.id + 'Data'][index].type
            // 拖动进筛选
            if (evt.to.classList.contains('filtrateClass')) {
              // this.bItems.push({ 'name': name, 'dataType': dataType })
              // this.editF(dataType)
              const index = this.bItems.findIndex(_ => _.name === name)
              if (!~index) {
                // 如果不存在，那么插入
                if (dataType === 'DATE') {
                  this.dialogDateFiltrate = true
                  this.bItems.push({
                    name,
                    dataType,
                    dataId,
                    options: [{ 'region': '1', 'dateSpecific': '', 'dateStart': '', 'dateEnd': '', 'days': '' }]
                  })
                  this.editF(dataType, {
                    name,
                    dataType,
                    dataId,
                    options: [{ 'region': '1', 'dateSpecific': '', 'dateStart': '', 'dateEnd': '', 'days': '' }]
                  })
                }
                if (dataType === 'Integer') {
                  this.bItems.push({
                    name,
                    dataType,
                    dataId,
                    options: [{ region: '1', num: null, numMin: null, numMax: null }]
                  })
                  this.editF(dataType, {
                    name,
                    dataType,
                    dataId,
                    options: [{ region: '1', num: null, numMin: null, numMax: null }]
                  })
                }
                if (dataType === 'Text') {
                  this.bItems.push({ name, dataType, dataId, options: [{ region: '1', range: '' }] })
                  this.editF(dataType, { name, dataType, dataId, options: [{ region: '1', range: '' }] })
                }
              } else {
                //  如果存在
                this.$message({
                  type: 'error',
                  message: '已存在该字段筛选器，请不要重复拖拽!'
                })
              }
            }
            // 拖动到维度行
            if (evt.to.classList.contains('drawListClass')) {
              if (evt.from.id !== 'dimen') {
                this.$message.error('只支持维度数值')
                return
              }
              const index = this.rItems.findIndex(_ => _.name === name)
              if (!~index) {
                // 如果不存在，那么插入
                this.rItems.push({ name, dataType, dataId })
              } else {
                //  如果存在
                this.$message({
                  type: 'error',
                  message: '已存在该字段，请不要重复拖拽!'
                })
              }
            }
            // 拖动到维度列
            if (evt.to.classList.contains('drawColClass')) {
              if (evt.from.id !== 'dimen') {
                this.$message.error('只支持维度数值')
                return
              }
              const index = this.cItems.findIndex(_ => _.name === name)
              if (!~index) {
                // 如果不存在，那么插入
                this.cItems.push({ name, dataType, dataId })
              } else {
                //  如果存在
                this.$message({
                  type: 'error',
                  message: '已存在该字段，请不要重复拖拽!'
                })
              }
            }
            // 拖动到数值
            if (evt.to.classList.contains('drawValClass')) {
              if (evt.from.id !== 'measure') {
                this.$message.error('只支持度量数值')
                return
              }
              const index = this.nItems.findIndex(_ => _.name === name)
              if (!~index) {
                // 如果不存在，那么插入
                this.nItems.push({ name, dataType, dataId })
              } else {
                //  如果存在
                this.$message({
                  type: 'error',
                  message: '已存在该字段，请不要重复拖拽!'
                })
              }
            }
          }
        })
      })
      this.setSortTableOther()
    }
  }
}
</script>
<style lang="scss" scoped>
  @import '~@/styles/variables.scss';

  .chart-setting-box {
    flex-shrink: 0;
    margin-right: 20px;
    width: 400px;
    /*min-height: calc(100vh - 50px);*/
    .cus-drag-setting {
      overflow: auto;
      /*height: calc(100vh - 200px);*/
    }
    .filtrate-wrap {
      position: relative;
      #filtrate {
        //min-height: 200px;
      }
      ul {
        margin-bottom: 10px;
      }
      ul:last-child {
        margin-bottom: 0;
      }
    }
    .draw-list {
      margin-bottom: 10px;
      header {
        margin-bottom: 5px;
        color: #666666;
      }
    }
    .aside-right {
      width: 100% !important;
      > > >
      .el-aside {
        display: flex;
        flex-direction: column;
        div.shink-pannel:last-child {
          display: flex;
          flex: 1;
          .el-card {
            flex: 1;
            margin-bottom: 0;
          }
        }
      }
      .el-container {
        min-height: calc(100vh - 50px);
      }
    }

    .no-dataset {
      color: #cccccc;
      font-size: 13px;
      text-align: center;
      border: 1px dashed #ccc;
      padding: 5px;
    }
    .has-dataset {
      font-size: 14px;
      margin-block-start: 0;
      margin-block-end: 0;
      padding-inline-start: 0;
      .data-list {
        display: flex;
        flex-wrap: nowrap;
        justify-content: space-between;
        margin-bottom: 5px;
        i {
          color: #409EFF;
          &.close:hover {
            cursor: pointer;
            font-weight: bold;
          }
        }
        .filtrate-btn-box {
          .el-icon-close {
            color: #fff;
            &:hover {
              font-weight: bold;
            }
          }
        }
      }
      &.dimen {
        margin-top: 10px;
      }
      .dimen-list {
        cursor: move;
      }
      &.unvisable {
        li {
          display: none;
        }
      }
      &.filtrate-list {
        height: 114px;
        overflow: hidden;
        overflow-y: auto;

      }
    }
    .unvisable {
      li {
        display: none;
      }
    }
  }
</style>
