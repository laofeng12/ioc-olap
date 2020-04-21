<template>
  <div class="folderAside" v-loading="menuListLoading">
    <el-row class="left-search">
      <el-input size="small" suffix-icon="el-icon-search" placeholder="请输入关键词" maxlength="20"
                v-model="searchKey"></el-input>
    </el-row>
    <el-tree class="filter-tree" :icon-class="iconType === 'cube' ? 'icon-cube' : 'el-icon-folder'" :data="menuList"
             :props="menuDefault" default-expand-all :filter-node-method="filterAll" @node-click="clickTreeItem"
             :empty-text="emptyText" ref="alltree" :draggable="draggable" :allow-drag="allowDrag" :allow-drop="allowDrop"
             @node-drop="handleDrop">
      <span class="custom-tree-node" slot-scope="{ node, data }" @mouseenter="enterNode">
        <div class="flex-box">
           <i class="el-icon-folder diy-icon" v-show="node.level === 1"></i>
           <i class="el-icon-tickets diy-icon" v-show="node.level === 2"></i>
           <template v-if="!data.virtualTableName">
             <span  class="cus-node-title"  :title="data.name" v-if="!data.virtualTableName">{{ data.name }}</span>
           </template>
          <el-tooltip placement="top" v-else>
           <span slot="content" class="cus-node-title"  v-if="!data.virtualTableName">{{ data.name }}</span>
           <span slot="content" class="cus-node-title"  v-else>{{ data.virtualTableName }} - {{ data.name}}</span>
           <span  class="cus-node-title"  v-if="!data.virtualTableName">{{ data.name }}</span>
           <span  class="cus-node-title"  v-else>{{ data.virtualTableName }} - {{ data.name}}</span>
          </el-tooltip>
        </div>
        <span class="cus-node-content" v-if="showDo" @click.stop>
          <el-dropdown size="mini" @command="handleCommand($event, node, data)">
            <div style="color: #0486FE">操作</div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="4" v-if="vueType === 'myOlap' && !node.parent.parent">新建</el-dropdown-item>
              <el-dropdown-item command="0">编辑</el-dropdown-item>
              <el-dropdown-item command="1" v-if="node.parent.parent">分享</el-dropdown-item>
              <el-dropdown-item command="3">删除</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </span>
        <span class="cus-node-content1" v-else-if="data.showCheckbox" @click.stop>
          <el-checkbox v-model="checkName" :true-label="data.name" false-label=""
                       :checked="checkName === data.name"></el-checkbox>
        </span>
      </span>
    </el-tree>
    <div v-if="needNewFolder">
      <el-button style="width: 100%;padding: 0 16px;color: #0486FE; background: #fff!important;border: 1px solid #0486FE;" type="primary" size="small" @click="newFolder">新建文件夹</el-button>
    </div>
    <el-dialog :title="`${folderForm.isNew ? '新建' : '编辑'}文件夹`" :visible.sync="newVisible" width="30%">
      <el-form :model="folderForm" ref="folderForm" :rules="folderRules">
        <el-form-item label="文件夹名称" label-width="105px" prop="name">
          <el-input v-model="folderForm.name" maxlength="20" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="newVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitFolder">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="选择共享" :visible.sync="shareVisible">
      <div class="shareBox dis-flex" v-loading="shareLoading">
        <div class="box">
          <div class="title">未选择用户</div>
          <div class="dis-flex">
            <el-input placeholder="请输入用户账号" size="mini" v-model="searchUser"></el-input>
            <el-button size="mini" type="primary" icon="el-icon-search" @click="searchUserFunc('search')"></el-button>
          </div>
          <el-checkbox-group v-model="userCheckList" class="list">
            <div v-for="(item, index) in userList" class="line" :key="index" v-if="!item.disabled">
              <el-checkbox :label="item">{{item.label}}</el-checkbox>
            </div>
          </el-checkbox-group>
          <div class="bottom dis-flex">
            <el-button size="mini" @click="lastPage" :disabled="userPage <= 0">上一页</el-button>
            <el-button size="mini" @click="nextPage" :disabled="userPage >= totalPage">下一页</el-button>
          </div>
        </div>
        <div class="centerButton">
          <div>
            <el-button icon="el-icon-arrow-right" @click="addShare"></el-button>
          </div>
          <div class="m-t-20">
            <el-button icon="el-icon-arrow-left" @click="reduceShare"></el-button>
          </div>
        </div>
        <div class="box">
          <div class="title">已选择用户</div>
          <div class="dis-flex">
            <el-input placeholder="请输入内容" size="mini" v-model="searchShare"></el-input>
            <el-button size="mini" type="primary" icon="el-icon-search" @click="searchShareFunc"></el-button>
          </div>
          <el-checkbox-group v-model="shareCheckList" class="list no-bottom">
            <div v-for="(item, index) in showShareList" class="line" :key="index">
              <el-checkbox :label="item">{{item.label}}</el-checkbox>
            </div>
          </el-checkbox-group>
          <!--<div class="bottom dis-flex">-->
            <!--<el-button size="mini">上一页</el-button>-->
            <!--<el-button size="mini">下一页</el-button>-->
          <!--</div>-->
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shareVisible = false">取 消</el-button>
        <el-button type="primary" @click="share">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { searchCubeApi } from '../../../api/olapAnalysisList'
import { newOlapFolderApi, deleteOlapFolderApi, getShareUserApi, saveShareApi } from '../../../api/instantInquiry'
import { getUserListApi } from '../../../api/login'

export default {
  props: {
    menuList: {
      type: Array,
      required: true
    },
    menuListLoading: {
      type: Boolean,
      default: false
    },
    showDo: {
      type: Boolean,
      default: true
    },
    showCheckbox: {
      type: Boolean,
      default: true
    },
    menuDefault: {
      // type: Array,
      required: true
    },
    needNewFolder: {
      type: Boolean,
      default: true
    },
    vueType: {
      type: String,
      required: true
    },
    iconType: {
      type: String,
      required: false
    },
    emptyText: {
      type: String,
      required: false,
      default: '暂无数据'
    },
    cubeName: {
      type: String,
      required: false
    },
    draggable: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      searchKey: '', // 分享搜索
      shareVisible: false,
      newVisible: false,
      folderForm: {
        isNew: true,
        name: '',
        sortNum: ''
      },
      folderRules: {
        name: [
          {
            required: true,
            message: '请输入文件夹名称',
            trigger: 'blur'
          },
          { max: 50, message: '请不要超过50个字', trigger: 'blur' }
        ],
        sortNum: [
          { required: true, message: '请输入序号', trigger: 'blur' }
        ]
      },
      // userList: [],
      // shareList: [],
      shareId: null,
      shareLoading: false,
      userRows: [],
      userList: [],
      shareList: [],
      showShareList: [],
      userCheckList: [],
      shareCheckList: [],
      shareData: {},
      searchUser: '',
      searchShare: '',
      userPage: 0,
      totalPage: 0,
      sheetTitle: '',
      sheetDataId: '',
      sheetShare: '',
      checkName: ''
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  watch: {
    searchKey (val) {
      this.$refs.alltree.filter(val)
    },
    checkName (val) {
      this.$emit('clickItem', val)
    },
    cubeName (val) {
      this.checkName = val
    }
  },
  activated () {
    this.checkName = this.cubeName
  },
  methods: {
    enterNode(e) {
      e.target.parentElement.parentElement.classList.add('hideLongText')
    },
    clickTreeItem (data, node, self) {
      if (node.parent.parent || this.vueType === 'shareResult' || this.vueType === 'shareOlap') {
        // 子节点才进入
        this.sheetTitle = data.name
        this.sheetDataId = data.id
        this.sheetShare = data.isShare
        // 渲染表格数据
        this.$emit('clickItem', data, 'search')
      // } else if (this.vueType === 'queries' && this.checkName === data.name) {
      //   this.$emit('clickItem', data.name)
      }
    },
    filterAll (value, data, node) {
      if (!value) return true
      if (data.attrs.name.indexOf(value) !== -1 || (data.attrs.queryCode && data.attrs.queryCode.indexOf(value) !== -1)) {
        return true
      }
      return this.checkBelongToChooseNode(value, data, node)
    },
    checkBelongToChooseNode (value, data, node) {
      const level = node.level
      // 如果传入的节点本身就是一级节点就不用校验了
      if (level === 1) {
        return false
      }
    // 先取当前节点的父节点
    let parentData = node.parent
    // 遍历当前节点的父节点
    let index = 0
    while (index < level - 1) {
      // 如果匹配到直接返回
      if (parentData.data.name.indexOf(value) !== -1) {
        return true
      }
      // 否则的话再往上一层做匹配
      parentData = parentData.parent
      index ++
    }
    // 没匹配到返回false
    return false
  },
    allowDrag (draggingNode) {
      return draggingNode.level === 1
    },
    allowDrop (draggingNode, dropNode, type) {
      return !!dropNode.data.attrs.canDrop && type !== 'inner'
    },
    handleDrop () {
      const list = []
      this.menuList.forEach((v, i) => {
        const obj = {
          folderId: v.id,
          sortNum: i + 1
        }
        list.push(obj)
      })
      this.$emit('changeSortNum', list)
    },
    submitFolder () {
      const data = { // RealQuery（即席查询） Analyze（Olap分析）
        flags: 0,
        parentId: 0,
        type: this.$route.name === 'instantInquiry' ? 'RealQuery' : 'Analyze'
      }
      Object.assign(data, this.folderForm)
      this.$refs.folderForm.validate(async (valid) => {
        if (valid) {
          try {
            await newOlapFolderApi(data)
            this.$message.success('操作成功')
            this.newVisible = false
            if (this.$route.name === 'instantInquiry') {
              await this.$store.dispatch('getSaveFolderListAction')
            } else {
              this.$emit('getAnalysisList')
            }
          } catch (e) {
            console.error(e)
          }
        }
      })
    },
    handleCommand (dropIndex, node, data) {
      switch (dropIndex) {
        case '0': // 编辑
          return this.edit(node, data)
        case '1': // 分享
          return this.getShareUserList(node, data)
        case '3': // 删除
          return this.delete(data, node.isLeaf)
        case '4': // 新建olap
          return this.$router.push(`/newOlapAnalysis?folderId=${data.id}`)
      }
    },
    newFolder () {
      this.newVisible = true
      this.folderForm = { isNew: true,  name: '', sortNum: '' }
    },
    edit (node, data) {
      if (node.parent.parent) {
        this.$emit('editFunc', data)
      } else {
        this.newVisible = true
        this.folderForm = {
          folderId: data.id,
          isNew: false,
          name: data.name,
          sortNum: data.attrs.sortNum
        }
      }
      // this.$emit('editFunc', this.folderForm)
    },
    delete (data, isLeaf) {
      if (isLeaf) {
        if (data.attrs.realQueryId || data.attrs.analyzeId) {
          data.attrs.realQueryId ? this.$emit('deleteFunc', data.attrs.realQueryId)
            : this.$emit('deleteFunc', data.attrs.analyzeId)
        } else {
          this.deleteFolder(data.id)
        }
      } else {
        this.$message.error('删除失败，请先删除文件夹中的文件')
      }
    },
    async deleteFolder (id) {
      await deleteOlapFolderApi({ id })
      this.$message.success('删除成功')
      this.$emit('getAnalysisList')
      await this.$store.dispatch('getSaveFolderListAction')
    },
    async getShareUserList (node, data) {
      this.shareLoading = true
      this.shareData = data
      const params = { id: data.attrs ? data.attrs.cubeId : data.cubeId }
      let res = ''
      // 即席查询的时候不需要调用该接口
      if (this.$route.name !== 'instantInquiry') {
        res = await searchCubeApi(params)
      }
      if (res.flags || this.$route.name === 'instantInquiry') {
        const shareId = data.attrs.realQueryId || data.attrs.analyzeId
        if (shareId) {
          this.shareId = shareId
          this.shareVisible = true
          const { rows, totalPage } = await getUserListApi()
          this.totalPage = totalPage
          this.userRows = rows
          const res = await getShareUserApi({
            sourceId: this.shareId,
            sourceType: this.$route.name === 'instantInquiry' ? 'RealQuery' : 'Analyze'
          })
          let shareList = []
          res.forEach(v => shareList.push({ key: v.shareUserId, label: v.shareUserName, disabled: false }))
          this.shareList = shareList
          this.showShareList = shareList
          const userList = rows.map(item => {
            let disabled = false
            res.forEach(v => {
              if (v.shareUserId === item.userid || item.userid == this.userInfo.userId) {
                disabled = true
              }
            })
            return Object.assign(item, { key: item.userid, label: item.fullname, disabled })
          })
          this.userList = userList
          this.shareLoading = false
        } else {
          this.$message.error('分享失败')
          this.shareLoading = false
        }
      } else {
        this.$message.error('该立方体已禁用')
      }
    },
    async share () {
      const data = { // RealQuery（即席查询） Analyze（Olap分析）
        sourceId: this.shareId,
        sourceType: this.$route.name === 'instantInquiry' ? 'RealQuery' : 'Analyze'
      }
      let url = '/olap/apis/olapShare/save?'
      this.shareList.forEach((v, i) => {
        if (i === 0) {
          url += `userIds=${v.key}`
        } else {
          url += `&userIds=${v.key}`
        }
      })
      await saveShareApi(url, data)
      this.$message.success('分享成功')
      this.shareVisible = false
      this.$emit('clickItem', this.shareData, 'share')
    },
    addShare () {
      this.searchShare = ''
      this.shareList = [...this.shareList, ...this.userCheckList]
      this.showShareList = this.shareList
      this.userCheckList = []
      this.cleanShare()
    },
    reduceShare () {
      this.searchShare = ''
      let list = []
      this.shareList.forEach((item, index) => {
        this.shareCheckList.forEach(v => {
          if (item.key === v.key) {
            list.push(index)
          }
        })
      })
      list.reverse()
      list.forEach(v => this.shareList.splice(v, 1))
      this.showShareList = this.shareList
      this.shareCheckList = []
      this.cleanShare()
    },
    cleanShare () {
      const userList = this.userRows.map(item => {
        let disabled = false
        this.shareList.forEach(v => {
          if (v.key === item.userid || item.userid == this.userInfo.userId) {
            disabled = true
          }
        })
        return Object.assign(item, { key: item.userid, label: item.fullname, disabled })
      })
      this.userList = userList
    },
    searchShareFunc () {
      const showShareList = this.shareList.filter(v => v.label.includes(this.searchShare))
      this.showShareList = showShareList
    },
    async searchUserFunc (type) {
      this.shareLoading = true
      if (type === 'search') {
        this.userPage = 0
      }
      const data = this.shareData
      const shareId = data.attrs.realQueryId || data.attrs.analyzeId
      if (shareId) {
        this.shareId = shareId
        this.shareVisible = true
        const params = { page: this.userPage, like_account: this.searchUser }
        const { rows, totalPage } = await getUserListApi(params)
        this.userRows = rows
        this.totalPage = totalPage
        // const res = await getShareUserApi({ sourceId: this.shareId, sourceType: 'RealQuery' })
        // let shareList = []
        // res.forEach(v => shareList.push({ key: v.shareUserId, label: v.shareUserName, disabled: false }))
        // this.shareList = shareList
        const userList = rows.map(item => {
          let disabled = false
          this.shareList.forEach(v => {
            if (v.key === item.userid || item.userid == this.userInfo.userId) {
              disabled = true
            }
          })
          return Object.assign(item, { key: item.userid, label: item.fullname, disabled })
        })
        this.userList = userList
        this.shareLoading = false
      } else {
        this.$message.error('分享失败')
        this.shareLoading = false
      }
    },
    lastPage () {
      if (this.userPage <= 0) return false
      this.userPage = this.userPage - 1
      this.searchUserFunc()
    },
    nextPage () {
      if (this.userPage >= this.totalPage) return false
      this.userPage = this.userPage + 1
      this.searchUserFunc()
    }
  }
}
</script>

<style lang="scss">
  .folderAside {
    width: 240px;
    flex-shrink: 0;
    background: #FFFFFF;
    box-shadow: 5px 0 10px 0 rgba(0,0,0,0.05);
    margin-right: 20px;
    position: relative;
    .el-tree{
      /deep/ .el-tree-node{
        &.hideLongText:hover{
          .el-tree-node__content{
            .custom-tree-node{
              .flex-box{
                display: flex;
                vertical-align: middle;
                .diy-icon{
                  margin-top: 9px;
                }
              }
              /deep/ .cus-node-title{
                display: block;
                width: 180px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }
            }
          }
        }
        /deep/ .el-tree-node__expand-icon{
          display: none;
        }
        /deep/ .diy-icon{
          font-size: 14px;
          margin-right: 6px;
        }
      }
    }
    .cus-node-content {
      //display: none;
      opacity: 0;
      filter: Alpha(opacity=0);
      transition: opacity .5s;
      position: absolute;
      right: 5px;
      top: 2px;
      .el-button {
        padding: 4px 6px;
      }
    }
    .cus-node-content1 {
      position: absolute;
      right: 5px;
      top: 2px;
      .el-button {
        padding: 4px 6px;
      }
    }
    .custom-tree-node {
      width: 90%;
      overflow: hidden;
      text-overflow: ellipsis;
      .cus-node-title {
        color: #262626;
        font-size: 14px;
      }
    }
    .el-tree-node__content {
      position: relative;
    }
    .el-tree-node__content:hover {
      .cus-node-content {
        display: inline-block;
        opacity: 1;
        filter: Alpha(opacity=100);
        transition: opacity .5s;
      }
    }
    .el-tree {
      height: calc(100vh - 299px);
      overflow: auto;
      .icon-cube {
        background-image: url("../../../icons/png/cube.png");
        background-repeat: no-repeat;
        background-size: 100% 100%;
      }
      .el-tree-node__expand-icon.expanded {
        transform: none;
      }
      .el-icon-folder:before {
        content: "\e784";
      }
      .icon-cube:before {
        background-image: url("../../../icons/png/cube.png");
        background-repeat: no-repeat;
        background-size: 100% 100%;
      }
      .el-tree-node__expand-icon {
        color: #c0c4cc;
        margin: 0 5px;
      }
    }
    .share .el-transfer-panel .el-transfer-panel__body .el-checkbox {
      display: block!important;
    }
  }
  .el-tabs__content {
    padding: 0;
    margin-top: 10px;
    background: #ffffff;
    margin-bottom: 24px;
  }
  .shareBox {
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
