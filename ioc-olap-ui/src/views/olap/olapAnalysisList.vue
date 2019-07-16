<template>
  <el-container class="cus-box">
    <el-aside width="200px" class="cus-left">
      <el-row class="left-tabs">
        <el-tabs class="cus-tabs" v-model="activeTab" :stretch="true" @tab-click="tabClick">
          <el-tab-pane label="我的" name="my">
            <FolderAside :menuList="menuList" :menuDefault="menuDefault"></FolderAside>
          </el-tab-pane>
          <el-tab-pane label="分享" name="share">
            <FolderAside :menuList="menuList" :menuDefault="menuDefault"></FolderAside>
          </el-tab-pane>
        </el-tabs>
      </el-row>
    </el-aside>
    <div class="cus-right">
      <ResultBox v-if="tableData.length > 0" :theadData="theadData" :tableData="tableData"></ResultBox>
    </div>

    <!-- 分享弹窗 -->
    <!--<share-dialog-->
      <!--class="share-dialog"-->
      <!--ref="sdialog"-->
      <!--title="数据报表"-->
      <!--topNodeKey="dataId"-->
      <!--:defaultProps="defaultProps"-->
      <!--:departTreeProps="departTreeProps"-->
      <!--:shareDialogData="shareDialogData"-->
      <!--@clickDepartNode="clickDepartNode"-->
      <!--bottomNodeKey="orgId"-->
      <!--@confirmShare="confirmShare"-->
      <!--:show.sync="isShowSDialog">-->
    <!--</share-dialog>-->

    <!-- 移动弹窗 -->
    <!--<move-dialog-->
      <!--title="移动自助报表"-->
      <!--:FolderList="DialogFolderList"-->
      <!--@selectFolder="selectDialogFolder"-->
      <!--@confirmMove="confirmMove"-->
      <!--:show.sync="isShowMDialog">-->
    <!--</move-dialog>-->

  </el-container>
</template>

<script>
import FolderAside from '@/components/olapComponent/common/FolderAside1'
import ResultBox from '@/components/olapComponent/common/ResultBox'
// import ShareDialog from '../../components/ShareDialog'
// import MoveDialog from '../../components/MoveDialog'
// import StatementTable from '@/components/BITemp/StatementTable'
// import TableFilter from '../../components/TableFilter'
// import {
//   getStatementTree, editStatementTree, renameStatementTree, getFolderList, moveStatement,
//   delFolder, delStatement, newFile, getDepartmentTree, getDepartMember, shareStament, getTableData
// } from '../../api/statement'

export default {
  components: { FolderAside, ResultBox },
  watch: {
    searchKeyword (val) {
      this.$refs.alltree.filter(val)
    },
    searchKeyword_ (val) {
      this.$refs.alltree_.filter(val)
    }
  },
  data () {
    return {
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
      },
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
      ]
    }
  },
  created () {
    // this.getStatementTree(0) // 初始化时默认拿取我的
    // this.getFolderList_(1) // 初始化时默认拿取我的文件夹列表
  },
  methods: {
  //   async getStatementTree (type) { // type 0-我的 1-共享
  //     try {
  //       const { data } = await getStatementTree({
  //         eq_isShare: type,
  //         like_dataName: ''
  //       })
  //       if (data) {
  //         this.treeDataAll = data
  //         if (this.treeDataAll.length > 0) {
  //           for (let item of this.treeDataAll) {
  //             if (item.catalogList.length > 0) {
  //               this.sheetTitle = item.catalogList[0].dataName || '报表' // 默认情况下
  //               this.sheetDataId = item.catalogList[0].dataId || ''
  //               this.sheetShare = item.catalogList[0].isShare
  //               this.getTableData_(this.sheetDataId) // 初始化第一个报表
  //
  //               return
  //             }
  //           }
  //         }
  //       }
  //     } catch (e) {
  //       // this.$message.error('服务器异常')
  //     }
  //   },
  //   async getFolderList_ (type) {
  //     try {
  //       const cbData = await getFolderList({
  //         type
  //       })
  //       if (cbData) {
  //         this.DialogFolderList = cbData.data
  //       }
  //     } catch (e) {
  //     }
  //   },
    tabClick (tab, event) {
      tab.index === '0' ? this.isShowFieldTable = true : this.isShowFieldTable = false
      if (tab.index === '1') {
        this.activeSheetTab = 'first'
        // this.getStatementTree(1)
        // this.getFolderList_(2)
      } else if (tab.index === '0') {
        // this.getStatementTree(0)
        // this.getFolderList_(1)
      }
      this.searchKeyword = ''
      this.searchKeyword_ = ''
    }
  //   filterAll (value, data) {
  //     if (!value) return true
  //     return data.dataName.indexOf(value) !== -1
  //   },
  //   newFolder (categoryType) {
  //     this.$prompt('文件夹名称', '新建文件夹', {
  //       confirmButtonText: '确定',
  //       cancelButtonText: '取消',
  //       inputPattern: /^.{1,20}$/,
  //       inputErrorMessage: '请输入1-20位字符'
  //     }).then(({ value }) => {
  //       let labelList = this.treeDataAll.map(item => item.dataName)
  //       if (!labelList.includes(value)) {
  //         // 新建文件夹
  //         this.newFolder_(categoryType, value)
  //       } else {
  //         this.$message({
  //           type: 'info',
  //           message: '文件夹名称重复, 请重新添加!'
  //         })
  //       }
  //     }).catch(() => {
  //     })
  //   },
  //   async newFolder_ (categoryType, categoryName) {
  //     const cbData = await newFile({
  //       categoryType,
  //       categoryName,
  //       isNew: true
  //     })
  //     if (cbData.code === 200) {
  //       this.$message.success('创建文件夹成功')
  //       this.getFolderList_(categoryType)
  //       categoryType === 1 ? this.getStatementTree(0) : this.getStatementTree(1)
  //     }
  //   },
  //   handleItem (dropIndex, node, data) {
  //     console.log(node)
  //     switch (dropIndex) {
  //       case '0': // 新建自助报表
  //         this.$router.push({ path: '/newStatement' })
  //         break
  //       case '1': // 重命名
  //         this.renameTreeItem(node, data, 0)
  //         break
  //       case '2': // 编辑
  //         this.$router.push({ path: `/editStatement/${data.dataId}` })
  //         break
  //       case '3': // 刷新
  //         this.refreshTreeItem()
  //         break
  //       case '4': // 分享
  //         this.shareTreeItem(data, node)
  //         break
  //       case '5': // 移动
  //         this.moveTreeItem(data, 0)
  //         break
  //       case '6': // 删除
  //         this.delTreeItem(node, data, 0)
  //         break
  //     }
  //   },
  //   handleItemShare: function (dropIndex, node, data) {
  //     switch (dropIndex) {
  //       case '0': // 重命名
  //         this.renameTreeItem(node, data, 1)
  //         break
  //       case '1': // 刷新
  //         this.refreshTreeItem()
  //         break
  //       case '2': // 移动
  //         this.moveTreeItem(data, 1)
  //         break
  //       case '3': // 删除
  //         this.delTreeItem(node, data, 1)
  //         break
  //     }
  //   },
  //   delTreeItem (node, data, type) {
  //     let txt = node.isLeaf ? `确定删除“${data.dataName}”吗？删除后无法恢复！` : `确定删除“${data.dataName}”文件夹吗？文件夹下的自助报表将会被一起删除，且无法恢复`
  //     let self = this
  //     this.$confirm(txt, '提示', {
  //       confirmButtonText: '确定',
  //       cancelButtonText: '取消',
  //       type: 'warning'
  //     }).then(() => {
  //       // 软删
  //       // const parent = node.parent
  //       // const children = parent.data.children || parent.data
  //       // const index = children.findIndex(d => d.id === data.id)
  //       // children.splice(index, 1)
  //       // 硬删
  //       self.delFolderOrSta(node, data, type)
  //     }).catch(() => {
  //     })
  //   },
  //   async delFolderOrSta (node, data, type) {
  //     let cbData
  //     if (!node.isLeaf || node.level === 1) { // 删除文件夹
  //       cbData = await delFolder({
  //         id: data.dataId
  //       })
  //     } else { // 删除报表
  //       cbData = await delStatement({
  //         id: data.dataId,
  //         type
  //       })
  //     }
  //     if (cbData.code === 200) {
  //       this.$message.success('删除成功')
  //       if (this.activeTab === 'my') {
  //         this.getStatementTree(0)
  //         this.getFolderList_(1)
  //       } else {
  //         this.getStatementTree(1)
  //         this.getFolderList_(2)
  //       }
  //     }
  //   },
  //   renameTreeItem (node, data, type) {
  //     let defaultVal = ''
  //     let categoryId = ''
  //     let self = this
  //     defaultVal = data.dataName
  //     categoryId = data.dataId
  //     this.$prompt('请输入新名称', '提示', {
  //       confirmButtonText: '确定',
  //       cancelButtonText: '取消',
  //       inputValue: defaultVal,
  //       inputPattern: /^.{1,20}$/,
  //       inputErrorMessage: '请输入1-20位字符'
  //     }).then(({ value }) => {
  //       self.editTreeName(value, defaultVal, categoryId, node, type)
  //     }).catch(() => {
  //     })
  //   },
  //   async editTreeName (categoryName, defaultVal, categoryId, node, type) {
  //     if (categoryName && categoryName !== defaultVal) {
  //       let cbData
  //       if (!node.isLeaf || node.level === 1) { // 重命名文件夹
  //         cbData = await editStatementTree({
  //           id: categoryId,
  //           name: categoryName
  //         })
  //       } else { // 重命名报表
  //         cbData = await renameStatementTree({
  //           id: categoryId,
  //           name: categoryName,
  //           type
  //         })
  //       }
  //       if (cbData.code === 200) {
  //         this.$message.success('重命名成功')
  //         if (this.activeTab === 'my') {
  //           this.getStatementTree(0)
  //         } else {
  //           this.getStatementTree(1)
  //         }
  //       }
  //     } else {
  //       this.$message.warning('名称没变动')
  //     }
  //   },
  //   refreshTreeItem () {
  //     this.$message({
  //       message: '数据刷新成功',
  //       type: 'success'
  //     })
  //     // this.$message.error('数据刷新失败，请重新操作或联系管理员')
  //   },
  //   async shareTreeItem (data, node) {
  //     this.shareDialogData.topLeftData = this.treeDataAll
  //     this.isShowSDialog = true
  //     this.tempArray = [] // 每次打开弹窗清一下临时数组
  //     this.$refs.sdialog.refreshData() // 刷新子组件数据
  //     this.shareDialogData.topSelected = data.dataId
  //     this.shareDialogData.topRightData = [data]
  //     if (this.shareDialogData.bottomLeftData.length > 0) {
  //       return
  //     }
  //     const cbData = await getDepartmentTree()
  //     if (cbData && cbData.resources.length > 0) {
  //       this.shareDialogData.bottomLeftData = cbData.resources[0].subOrgList
  //     }
  //   },
  //   async clickDepartNode (data) {
  //     if (data.subOrgList === null) {
  //       // 最底层子集才去请求人员数据
  //       this.shareDialogData.showDepartLoading = true
  //       const cbData = await getDepartMember({
  //         orgid: data.orgId,
  //         page: 0,
  //         size: 1000
  //       })
  //       if (cbData) {
  //         this.shareDialogData.showDepartLoading = false
  //         const temp = cbData.rows
  //         temp.map((item) => {
  //           this.tempArray.push({ 'key': item.userid, 'label': `${item.fullname}-[${item.orgname}]` })
  //         })
  //         const key = 'key'
  //         const r = this.tempArray.reduce((cur, next) => cur.some((atom) => atom[key] === next[key]) ? cur : [...cur, next], [])
  //         this.shareDialogData.midwareMember = r
  //       }
  //     }
  //   },
  //   async confirmShare (reportList, userList) {
  //     if (reportList.length === 0) {
  //       this.$message.warning('请添加报表')
  //     } else if (userList.length === 0) {
  //       this.$message.warning('请添加人员')
  //     } else {
  //       const cbData = await shareStament({
  //         reportList,
  //         userList
  //       })
  //       if (cbData) {
  //         this.$message.success('报表分享成功')
  //         this.isShowSDialog = false
  //       }
  //     }
  //   },
  //   moveTreeItem (data, type) {
  //     this.isShowMDialog = true
  //     this.tempDataId = data.dataId
  //     this.tempType = type
  //   },
  //   selectDialogFolder (data) {
  //     console.log('dialog folder==', data)
  //   },
  //   async confirmMove (val) {
  //     if (val) {
  //       try {
  //         const cbData = await moveStatement({
  //           categoryId: val,
  //           id: this.tempDataId,
  //           type: this.tempType
  //         })
  //         if (cbData) {
  //           this.$message.success('报表移动成功')
  //           this.isShowMDialog = false
  //           if (this.activeTab === 'my') {
  //             this.getStatementTree(0)
  //           } else {
  //             this.getStatementTree(1)
  //           }
  //         }
  //       } catch (e) {
  //       }
  //     } else {
  //       this.$message.warning('请选择文件夹')
  //     }
  //   },
  //   clickTreeItem (data, node, self) {
  //     let that = this
  //     if (node.parent.parent) {
  //       // 子节点才进入
  //       that.sheetTitle = data.dataName
  //       that.sheetDataId = data.dataId
  //       that.sheetShare = data.isShare
  //       // 渲染表格数据
  //       this.getTableData_(data.dataId)
  //     }
  //   },
  //   async getTableData_ (dataId) {
  //     this.isLoadingTable = true
  //     try {
  //       const { data } = await getTableData({ dataId })
  //       console.log('表格数据===', data)
  //       this.reportTableData = data.dataList
  //       this.reportTableType = data.model.dataType
  //       this.isLoadingTable = false
  //     } catch (e) {
  //       this.isLoadingTable = false
  //       this.reportTableData = []
  //     }
  //   },
  //   toggleFull (clxName) {
  //     let seft = this
  //     this.$fullscreen.toggle(this.$el.querySelector(clxName), {
  //       wrap: false,
  //       callback: (fullscreen) => { seft.isFullScreen = fullscreen }
  //     })
  //   },
  //   handleSheetTab (curObj) {
  //
  //   },
  //   // 数据字段表格操作↓↓↓↓↓↓
  //   editRowEvent (row) {
  //     this.$refs.xTable.validate(valid => {
  //       if (valid) {
  //         this.$refs.xTable.setActiveRow(row)
  //       }
  //     })
  //   },
  //   saveRowEvent (row) {
  //     this.$refs.xTable.validate(valid => {
  //       if (valid) {
  //         this.cancelRowEvent()
  //       }
  //     })
  //   },
  //   cancelRowEvent (row) {
  //     this.$refs.xTable.validate(valid => {
  //       if (valid) {
  //         this.$refs.xTable.clearActived()
  //       }
  //     })
  //   },
  //   shareStatement () { // 分享
  //     this.isShowSDialog = true
  //     this.$refs.sdialog.refreshData() // 刷新子组件数据
  //   }
  }
}
</script>
<style lang="scss" scoped>
  .cus-box {
    .cus-left {
      padding: 15px 0;
      .el-tabs__content {
        padding: 0;
        margin-top: 10px;
        background: #ffffff;
      }
      .left-tabs {
        position: relative;
        .cus-tabs {
          .no-tree-data {
            height: calc(100vh - 267px);
            text-align: center;
          }
          .cus-empty {
            text-align: center;
          }
        }
      }
    }
    .cus-right {
      margin-left: 20px;
    }
  }
</style>
