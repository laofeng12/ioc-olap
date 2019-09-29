<template>
  <div class="chart-setting-box">
    <el-form :model="dashBoardForm" :rules="rules" ref="addDashboardForm">
      <el-row :gutter="8">
        <el-col :span="12" class="cus-drag-setting" :style="`max-height: ${limitHeight}px`">
          <div class="grid-content">
            <!--报表名称-->
            <shirink-pannel name="OLAP模型">
              <div slot="content">
                <el-form-item class="m-b-0">
                  <el-select v-model="selectCubeId" placeholder="请选择olap模型" size="small">
                    <el-option v-for="(item, index) in menuList" :key="index" :label="item.name" :value="item.cubeId">
                    </el-option>
                  </el-select>
                </el-form-item>
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
                    <li :class="`data-list left-list ${(item.children && item.children.length > 0) ? 'parent' : ''}`"
                        v-for=" (item, index) in dimensuresList" :key="index">
                      <span class="line">
                        <i class="el-icon-notebook-2"></i>
                        <span>{{item.name}}</span>
                      </span>
                      <!--<ul v-if="item.children">-->
                        <!--<li v-for=" (v, i) in item.children" :key="`${index}-${i}`" >-->
                          <!--<span class="line">-->
                            <!--<i class="el-icon-notebook-2"></i>-->
                            <!--<span>{{v.name}}</span>-->
                          <!--</span>-->
                        <!--</li>-->
                      <!--</ul>-->
                    </li>
                    <!--<el-tree class="filter-tree" icon-class="el-icon-notebook-2" :data="dimensuresList" :props="menuDefault"-->
                             <!--default-expand-all :filter-node-method="filterAll" ref="alltree">-->
                      <!--<span class="custom-tree-node" slot-scope="{ node, data }">-->
                        <!--<span class="cus-node-title" :title="data.name">{{ data.name }}</span>-->
                       <!--</span>-->
                    <!--</el-tree>-->
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
                    <li :class="`data-list left-list ${(item.children && item.children.length > 0) ? 'parent' : ''}`"
                        v-for=" (item,index) in measuresList" :key="index">
                      <span class="line">
                        <i class="el-icon-notebook-2"></i>
                        <span>{{item.name}}</span>
                      </span>
                    </li>
                  </ul>
                </div>
              </div>
            </shirink-pannel>
          </div>
        </el-col>
        <el-col :span="12" class="cus-drag-setting" :style="`max-height: ${limitHeight}px`">
          <div class="grid-content">
            <!--维度设定-->
            <shirink-pannel name="维度设定">
              <div slot="content" class="filtrate-wrap">
                <ul class="has-dataset draw-list drawListClass" id="drawList">
                  <header>维度(X轴)：</header>
                  <div class="no-dataset drawListClass" v-if="rItems.length === 0">拖动维度字段到此处</div>
                  <filter-temp v-for="(item,index) in rItems" :name="item.columnChName" :key="index" :index="index"
                               :items="rItems" :showEdit="false" @deleteIndex="delRow" class="filtered">
                  </filter-temp>
                </ul>
                <ul class="has-dataset draw-list drawColClass">
                  <header>维度(Y轴)：</header>
                  <div class="no-dataset drawColClass" v-if="cItems.length === 0">拖动维度字段到此处</div>
                  <filter-temp v-for="(item,index) in cItems" :name="item.columnChName" :key="index" :index="index"
                               :items="cItems" :showEdit="false" @deleteIndex="delCol" class="filtered">
                  </filter-temp>
                </ul>
                <ul class="has-dataset draw-list drawValClass">
                  <header>数值：</header>
                  <div class="no-dataset drawValClass" v-if="nItems.length === 0">拖动度量字段到此处</div>
                  <filter-temp v-for="(item,index) in nItems" :name="item.columnChName" :key="index" :index="index"
                               :items="nItems" :showEdit="false" @deleteIndex="delVal" class="filtered">
                  </filter-temp>
                </ul>
              </div>
            </shirink-pannel>

            <!--筛选器-->
            <shirink-pannel name="筛选器">
              <div slot="content" class="filtrate-wrap">
                <ul class="has-dataset filtrate-list filtrateClass" id="filtrate">
                  <div class="no-dataset filtrateClass" id="no-filtrate" v-if="bItems.length === 0">拖动维度字段到此处</div>
                  <filter-temp v-else v-for="(item,index) in bItems" :name="item.columnChName" :key="index" :index="index"
                               :items="bItems" @editClick="editF(item, index)" @deleteIndex="delFiter" class="filtered">
                  </filter-temp>
                </ul>
              </div>
            </shirink-pannel>
          </div>
        </el-col>
      </el-row>
    </el-form>

    <el-dialog title="过滤设置" :visible.sync="filterVisible">
      <div class="filterBox dis-flex" v-loading="filterLoading">
        <div class="box">
          <div class="title">可用值</div>
          <div class="dis-flex">
            <el-input placeholder="请输入内容" size="mini" v-model="searchFilter"></el-input>
            <el-button size="mini" type="primary" icon="el-icon-search" @click="searchAllFunc()"></el-button>
          </div>
          <el-checkbox-group v-model="allCheckList" class="list">
            <div v-for="(item, index) in filterAllList" class="line" :key="index" v-if="!item.disabled">
              <el-checkbox :label="item.name">{{item.name}}</el-checkbox>
            </div>
          </el-checkbox-group>
          <div class="bottom dis-flex">
            <el-button size="mini" @click="lastPage" :disabled="filterPageIndex <= 1">上一页</el-button>
            <el-button size="mini" @click="nextPage" :disabled="filterPageIndex >= totalPage">下一页</el-button>
          </div>
        </div>
        <div class="centerButton">
          <el-button icon="el-icon-arrow-left" circle @click="reduceFilter"></el-button>
          <el-button icon="el-icon-arrow-right" circle @click="addFilter"></el-button>
        </div>
        <div class="box">
          <div class="title dis-flex">
            <div>使用值</div>
            <div class="radio">
              <el-radio class="radioItem" v-model="isException" :label="1">包含</el-radio>
              <el-radio class="radioItem" v-model="isException" :label="0">不包含</el-radio>
            </div>
          </div>
          <div class="dis-flex">
            <el-input placeholder="请输入内容" size="mini" v-model="searchFilterSelect"></el-input>
            <el-button size="mini" type="primary" icon="el-icon-search" @click="searchFilterSelectFunc"></el-button>
          </div>
          <el-checkbox-group v-model="filterSelectCheckList" class="list no-bottom">
            <div v-for="(item, index) in showFilterSelectList" class="line" :key="index">
              <el-checkbox :label="item">{{item}}</el-checkbox>
            </div>
          </el-checkbox-group>
          <!--<div class="bottom dis-flex">-->
          <!--<el-button size="mini">上一页</el-button>-->
          <!--<el-button size="mini">下一页</el-button>-->
          <!--</div>-->
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="filterVisible = false">取 消</el-button>
        <el-button type="primary" @click="filter">确 定</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import ShirinkPannel from '@/components/analysisComponent/olapAside/ShirinkPannel'
import ElementPagination from '@/components/analysisComponent/olapAside/ElementPagination'
import FilterTemp from '@/components/BITemp/FilterTemp'
import FiltrateDialog from '@/components/FiltrateDialogBywzh'
import _ from 'lodash'
import Sortable from 'sortablejs'

import { getCubesApi, getFilterDetailsApi } from '../../../api/olapAnalysisList'

export default {
  components: { ShirinkPannel, FilterTemp, ElementPagination, FiltrateDialog },
  props: {
    changeRowAndCol: {
      type: Boolean,
      default: false
    },
    auto: {
      type: Boolean,
      default: false
    },
    editData: {
      type: Object,
      default: () => ({})
    }
  },
  data () {
    return {
      limitHeight: 0,
      menuList: [],
      menuDefault: {
        children: 'children', // 子集的属性
        label: 'name', // 标题的属性
        disabled: function (resData) {
          if (resData.isShare === 0) {
            return false
          } else {
            return true
          }
        }
      },
      dimensuresList: [],
      measuresList: [],
      selectCubeId: '',
      dialogDataSet: false,
      dataSetData: [{ name: '南城交通数据源1' }],
      formInline: {
        name: ''
      },
      dashBoardForm: {
        searchKey: '',
        measureSearch: '',
        name: ''
      },
      tableData: [
        {
          date: '2016-05-02',
          name: '王小虎',
          address: '上海市普陀区金沙江路 1518 弄'
        }
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
      // dialogFormVisible: false,
      ruleForm: {
        name: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入文件夹名称', trigger: 'blur' },
          { min: 1, max: 20, message: '文件夹名称不能超过20个字，请重新输入', trigger: 'blur' }
        ]
      },
      filterPageIndex: 1,
      totalPage: 0,
      searchFilter: '',
      searchFilterSelect: '',
      filterVisible: false,
      filterLoading: false,
      filterAllList: [],
      allCheckList: [],
      filterSelectList: [],
      filterSelectCheckList: [],
      showFilterSelectList: [],
      filterParams: {},
      isException: 1
    }
  },
  computed: {
    ...mapGetters([
      'cubeData'
    ])
  },
  watch: {
    changeRowAndCol (val) {
      const newCol = [...this.rItems]
      const newRow = [...this.cItems]
      this.rItems = newRow
      this.cItems = newCol
    },
    nItems (val) {
      this.$store.dispatch('getNewValueListAction', val)
      if (this.auto && (val.length > 0 && this.rItems.length > 0)) this.autoFunc()
    },
    bItems (val) {
      this.$store.dispatch('getNewFilterListAction', val)
      if (this.auto && (this.nItems.length > 0 && this.rItems.length > 0)) this.autoFunc()
    },
    rItems (val) {
      this.$store.dispatch('getNewRowListAction', val)
      if (this.auto && (val.length > 0 && this.nItems.length > 0)) this.autoFunc()
    },
    cItems (val) {
      this.$store.dispatch('getNewColListAction', val)
      if (this.auto && (this.nItems.length > 0 && this.rItems.length > 0)) this.autoFunc()
    },
    'dashBoardForm.searchKey' (val) {
      if (val) this.handleShrinkSearch(val, 'searchKey')
    },
    'dashBoardForm.measureSearch' (val) {
      if (val) this.handleShrinkSearch(val, 'measureSearch')
    },
    selectCubeId (val) {
      this.changeCubeId(val)
      this.cleanAllList()
    },
    editData (val) {
      this.selectCubeId = val.cubeId
      this.rItems = []
      this.cItems = []
      this.nItems = []
      this.bItems = []
      val.olapAnalyzeAxes.forEach(v => {
        switch (v.type) {
          case 1:
            this.rItems.push(v)
            break
          case 2:
            this.cItems.push(v)
            break
          case 3:
            this.nItems.push(v)
            break
          case 4:
            this.bItems.push(v)
            break
        }
      })
      const headLimit = {
        rItems: this.rItems,
        cItems: this.cItems
      }
      this.$emit('searchFunc', val.olapAnalyzeAxes, val.cubeId, headLimit)
    }
  },
  mounted () {
    this.limitHeight = document.body.offsetHeight - 123
    this.setSortTable()
    this.getCubes()
  },
  methods: {
    async getCubes () {
      const menuList = await getCubesApi()
      this.menuList = menuList
      this.changeCubeId(this.selectCubeId)
    },
    changeCubeId (val) {
      const { dimensures, measures, cubeId } = this.menuList.filter(v => v.cubeId === val)[0]
      this.$store.dispatch('getCubeDataAction', { dimensures, measures, cubeId })
      let dimensuresList = []
      let measuresList = []
      dimensures.forEach(item => {
        dimensuresList.push(item)
        item.children && item.children.forEach(v => {
          dimensuresList.push(v)
        })
      })
      measures.forEach(item => {
        measuresList.push(item)
        item.children && item.children.forEach(v => {
          measuresList.push(v)
        })
      })
      this.dimensuresList = dimensuresList
      this.measuresList = measuresList
    },
    // 删除维度行
    delRow (index) {
      this.rItems.splice(index, 1)
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    // 删除维度列
    delCol (index) {
      this.cItems.splice(index, 1)
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    // 删除数值
    delVal (index) {
      this.nItems.splice(index, 1)
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    // 删除筛选器
    delFiter (index) {
      this.bItems.splice(index, 1)
      setTimeout(() => {
        this.setSortTableOther()
      }, 0)
    },
    editF (item, index) {
      this.getDataId = item
      this.filterVisible = true
      this.getFilterDetails(item, index)
    },
    searchAllFunc () {
      this.filterPageIndex = 1
      this.getFilterDetails()
    },
    searchFilterSelectFunc () {
      const showFilterSelectList = this.filterSelectList.filter(v => v.includes(this.searchFilterSelect))
      this.showFilterSelectList = showFilterSelectList
    },
    async getFilterDetails (item, index) {
      this.filterLoading = true
      if (item) {
        this.filterParams = {
          columnId: item.columnId,
          index,
          tableId: item.tableId
        }
        this.filterSelectList = item.selectValues ? item.selectValues.split(',') : []
        this.filterSelectCheckList = []
        this.showFilterSelectList = item.selectValues ? item.selectValues.split(',') : []
        this.isException = item.isInclude === 0 ? 0 : 1
      }
      const params = {
        columnId: this.filterParams.columnId,
        key: this.searchFilter,
        pageIndex: this.filterPageIndex,
        pageSize: 20,
        tableId: this.filterParams.tableId
      }
      const { results, totalRecord } = await getFilterDetailsApi(params)
      const filterAllList = results.map(v => ({ name: v[0], disabled: false }))
      this.totalPage = Math.ceil(totalRecord / 20)
      this.filterAllList = filterAllList
      this.cleanFilter()
      this.filterLoading = false
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
          filter: '.filtered',
          animation: 150,
          onAdd: function (evt) {
            evt.item.remove()
          }
        })
      })
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
          filter: '.parent',
          onEnd: evt => {
            let obj = {}
            if (evt.from.id === 'dimen') {
              obj = this.dimensuresList[evt.oldIndex].attrs
            } else {
              obj = this.measuresList[evt.oldIndex].attrs
            }
            // 拖动进筛选
            if (evt.to.classList.contains('filtrateClass')) {
              if (evt.from.id !== 'dimen') {
                return this.$message.error('只支持维度数值')
              }
              const index = this.bItems.findIndex(_ => _.columnId === obj.columnId)
              if (!~index) {
                // 如果不存在，那么插入
                this.bItems.push(obj)
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
                return this.$message.error('只支持维度数值')
              }
              const rIndex = this.rItems.findIndex(_ => {
                if (_.columnId === obj.columnId) {
                  return _
                }
              })
              const cIndex = this.cItems.findIndex(_ => _.columnId === obj.columnId)
              if (!~cIndex && !~rIndex) {
                // 如果不存在，那么插入
                this.rItems.push(obj)
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
              const rIndex = this.rItems.findIndex(_ => _.columnId === obj.columnId)
              const cIndex = this.cItems.findIndex(_ => _.columnId === obj.columnId)
              if (!~cIndex && !~rIndex) {
                // 如果不存在，那么插入
                this.cItems.push(obj)
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
              const index = this.nItems.findIndex(_ => _.columnId === obj.columnId)
              if (!~index) {
                // 如果不存在，那么插入
                this.nItems.push(obj)
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
    },
    lastPage () {
      if (this.filterPageIndex <= 1) return false
      this.filterPageIndex = this.filterPageIndex - 1
      this.getFilterDetails()
    },
    nextPage () {
      if (this.filterPageIndex >= this.totalPage) return false
      this.filterPageIndex = this.filterPageIndex + 1
      this.getFilterDetails()
    },
    addFilter () {
      this.searchFilter = ''
      this.filterSelectList = [...this.filterSelectList, ...this.allCheckList]
      this.showFilterSelectList = this.filterSelectList
      this.allCheckList = []
      this.cleanFilter()
    },
    reduceFilter () {
      this.searchFilter = ''
      let list = []
      this.filterSelectList.forEach((item, index) => {
        this.filterSelectCheckList.forEach(v => {
          if (item === v) {
            list.push(index)
          }
        })
      })
      list.reverse()
      list.forEach(v => this.filterSelectList.splice(v, 1))
      this.showFilterSelectList = this.filterSelectList
      this.filterSelectCheckList = []
      this.cleanFilter()
    },
    cleanFilter () {
      const filterAllList = this.filterAllList.map(item => {
        let disabled = false
        this.filterSelectList.forEach(v => {
          if (v === item.name) {
            disabled = true
          }
        })
        return { name: item.name, disabled }
      })
      this.filterAllList = filterAllList
    },
    filter () {
      Object.assign(this.bItems[this.filterParams.index], {
        selectValues: this.filterSelectList.join(),
        isInclude: this.isException
      })
      this.filterVisible = false
      this.$message.success('筛选成功')
    },
    autoFunc () {
      const newValueList = this.nItems.length > 0 ? this.nItems.map(v => Object.assign({}, v, { type: 3 })) : []
      const newFilterList = this.bItems.length > 0 ? this.bItems.map(v => Object.assign({}, v, { type: 4 })) : []
      const newRowList = this.rItems.length > 0 ? this.rItems.map(v => Object.assign({}, v, { type: 1 })) : []
      const newColList = this.cItems.length > 0 ? this.cItems.map(v => Object.assign({}, v, { type: 2 })) : []
      const list = [...newValueList, ...newFilterList, ...newRowList, ...newColList]
      this.$emit('searchFunc', list, this.cubeData.cubeId, { rItems: this.rItems, cItems: this.cItems })
    },
    cleanAllList () {
      this.nItems = []
      this.bItems = []
      this.rItems = []
      this.cItems = []
    }
  }
}
</script>
<style lang="scss" scoped>
  @import '~@/styles/variables.scss';
  .grid-content {
    padding-left: 0!important;
  }
  .chart-setting-box {
    flex-shrink: 0;
    /*margin-right: 20px;*/
    width: 480px;
    .cus-drag-setting {
      overflow: auto;
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
      height: 32px;
      line-height: 32px;
      color: #cccccc;
      font-size: 13px;
      text-align: center;
      border: 1px dashed #ccc;
      /*padding: 5px;*/
      /*>>> .custom-tree-node { // 小图标*/
        /*width: 84%;*/
        /*overflow: hidden;*/
        /*text-overflow: ellipsis;*/
        /*.cus-node-title {*/
          /*color: #606266;*/
          /*font-size: 14px;*/
        /*}*/
      /*}*/
      /*>>> .el-tree-node__content {*/
        /*position: relative;*/
        /*overflow: hidden;*/
        /*text-overflow: ellipsis;*/
        /*white-space: nowrap;*/
      /*}*/
      /*>>> .el-tree-node__content:hover {*/
        /*.cus-node-content {*/
          /*display: inline-block;*/
          /*opacity: 1;*/
          /*filter:Alpha(opacity=100);*/
          /*transition: opacity .5s;*/
        /*}*/
      /*}*/
      /*>>> .el-tree {*/
        /*height: calc(100vh - 299px);*/
        /*overflow: auto;*/
        /*>>> .el-tree-node__expand-icon.expanded {*/
          /*transform: none;*/
        /*}*/
        /*>>> .el-tree-node__expand-icon.expanded:before {*/
          /*content: "\e784";*/
        /*}*/
        /*>>> .el-tree-node__expand-icon {*/
          /*color: #c0c4cc;*/
        /*}*/
      /*}*/
      .share .el-transfer-panel .el-transfer-panel__body .el-checkbox {
        display: block!important;
      }
    }
    .has-dataset {
      font-size: 14px;
      margin-block-start: 0;
      margin-block-end: 0;
      padding-inline-start: 0;
      .data-list {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;
        span {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
        .line {
          display: flex;
          align-items: center;
          width: 100%;
          span {
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }
        }
        i {
          flex-shrink: 0;
          color: #409EFF;
          margin-right: 5px;
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
        /*cursor: move;*/
      }
      .left-list {
        display: block;
        margin-left: 15px;
        cursor: move;
        height: 18px;
      }
      .parent {
        margin-left: 0;
        cursor: default;
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
  .filterBox {
    justify-content: space-between;
    align-items: center;
    width: 100%;
    .box {
      width: 35%;
      padding: 10px;
      box-sizing: border-box;
      border: 1px #ddd solid;
      border-radius: 5px;
      .title {
        margin-bottom: 10px;
        .radio {
          .radioItem {
            margin: 0 10px;
          }
        }
      }
      .list {
        height: 200px;
        margin: 10px 0;
        overflow: auto;
        .line {
          height: 30px;
          line-height: 30px;
        }
      }
      .no-bottom {
        height: 230px;
      }
      .bottom {
        justify-content: space-between;
        height: 30px;
      }
    }
  }
</style>
