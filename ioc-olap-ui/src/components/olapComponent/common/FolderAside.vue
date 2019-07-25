<template>
  <div class="folderAside">
    <el-row class="left-search">
      <el-input
        size="small"
        suffix-icon="el-icon-search"
        placeholder="请输入关键词"
        v-model="searchKeyword_">
      </el-input>
    </el-row>
    <el-tree class="filter-tree" icon-class="el-icon-folder" :data="menuList" :props="menuDefault"
      default-expand-all :filter-node-method="filterAll" @node-click="clickTreeItem" ref="alltree">
      <span class="custom-tree-node" slot-scope="{ node, data }">
        <span class="cus-node-title" :title="data.name">{{ data.name }}</span>
          <span class="cus-node-content">
            <el-dropdown size="mini" @command="handleCommand($event, node, data)">
              <el-button type="primary" size="mini">
                操作<i class="el-icon-arrow-down el-icon--right"></i>
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="0">编辑</el-dropdown-item>
                <el-dropdown-item command="1">分享</el-dropdown-item>
                <el-dropdown-item command="2" v-if="node.level !== 1">移动</el-dropdown-item>
                <el-dropdown-item command="3">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span>
       </span>
    </el-tree>
    <div style="text-align: center;" v-if="needNewFolder">
      <el-button type="primary" size="small" @click="newFolder">新建文件夹</el-button>
    </div>
    <el-dialog :title="`${folderForm.isNew ? '新建' : '编辑'}文件夹`" :visible.sync="newVisible" width="30%">
      <el-form :model="folderForm" ref="folderForm" :rules="folderRules">
        <el-form-item label="文件夹名称" label-width="100px" prop="name">
          <el-input v-model="folderForm.name"></el-input>
        </el-form-item>
        <el-form-item label="文件夹序号" label-width="100px" prop="sort">
          <el-input type="number" v-model="folderForm.sort"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="newVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitFolder">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog title="选择共享" :visible.sync="shareVisible">
      <div class="">
        <el-transfer v-model="shareValue" :data="shareData"></el-transfer>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shareVisible = false">取 消</el-button>
        <el-button type="primary" @click="shareVisible = false">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { newOlapFolderApi, deleteOlapFolderApi } from '../../../api/instantInquiry'

export default {
  props: {
    menuList: {
      type: Array,
      required: true
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
    }
  },
  data () {
    const generateData = () => {
      const data = []
      for (let i = 1; i <= 15; i++) {
        data.push({
          key: i,
          label: `备选项${i}`,
          disabled: i % 4 === 0
        })
      }
      return data
    }
    return {
      searchKeyword_: '', // 分享搜索
      shareVisible: false,
      newVisible: false,
      folderForm: {
        isNew: true,
        name: '',
        sort: ''
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
        sort: [
          { required: true, message: '请输入序号', trigger: 'blur' }
        ]
      },
      shareData: generateData(),
      shareValue: [1, 4]
    }
  },
  watch: {
    searchKeyword (val) {
      this.$refs.alltree.filter(val)
    }
  },
  methods: {
    clickTreeItem (data, node, self) {
      let that = this
      if (node.parent.parent) {
        // 子节点才进入
        that.sheetTitle = data.name
        that.sheetDataId = data.folderId
        that.sheetShare = data.isShare
        // 渲染表格数据
        if (this.vueType === 'saveResult') {
          this.$emit('clickItem', data.sql, data.limit, data.realQueryId)
        }
      }
    },
    filterAll (value, data) {
      if (!value) return true
      return data.name.indexOf(value) !== -1
    },
    submitFolder () {
      const data = { // RealQuery（即席查询） DataAnalyze（Olap分析）
        flags: 0,
        parentId: 0,
        type: this.$route.name === 'instantInquiry' ? 'RealQuery' : 'DataAnalyze'
      }
      Object.assign(data, this.folderForm)
      console.info('data', data)
      debugger
      this.$refs.folderForm.validate(async (valid) => {
        if (valid) {
          try {
            const { createId } = await newOlapFolderApi(data)
            if (createId) {
              this.$message.success('新建成功')
              this.newVisible = false
            } else {
              this.$message.error('新建失败')
            }
          } catch (e) {
            this.$message.error('新建失败')
          }
        }
      })
    },
    handleCommand (dropIndex, node, data) {
      console.info('dropIndex', dropIndex)
      console.info('node', node)
      console.info('data', data)
      debugger
      switch (dropIndex) {
        case '0': // 编辑
          return this.edit(node, data)
        case '1': // 分享
          this.shareVisible = true
          break
        case '3': // 删除
          this.delete(data, node.isLeaf)
      }
    },
    newFolder () {
      this.newVisible = true
      this.folderForm.isNew = true
    },
    edit (node, data) {
      this.newVisible = true
      this.folderForm = {
        folderId: data.folderId,
        isNew: false,
        name: data.name,
        sort: data.sortNum
      }
      // this.$emit('editFunc', this.folderForm)
    },
    delete (data, isLeaf) {
      if (isLeaf) {
        if (data.realQueryId) {
          this.$emit('deleteFunc', data.realQueryId)
        } else {
          this.deleteFolder(data.folderId)
        }
      } else {
        this.$message.error('删除失败，请先删除文件夹中的文件')
      }
    },
    async deleteFolder (folderId) {
      try {
        await deleteOlapFolderApi({ id: folderId })
        this.$message.success('删除成功')
      } catch (e) {
        this.$message.error('删除失败')
      }
    }
  }
}
</script>

<style lang="scss">
  .folderAside {
    width: 200px;
    flex-shrink: 0;
    background-color: white;
    margin-right: 20px;
    position: relative;
    .left-search {
      padding: 0 5px 5px 5px;
    }
    .cus-node-content {
      //display: none;
      opacity: 0;
      filter:Alpha(opacity=0);
      transition: opacity .5s;
      position: absolute;
      right: 5px;
      top: 2px;
      .el-button {
        padding: 4px 6px;
      }
    }
    .custom-tree-node {
      width: 84%;
      overflow: hidden;
      text-overflow: ellipsis;
      .cus-node-title {
        color: #606266;
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
        filter:Alpha(opacity=100);
        transition: opacity .5s;
      }
    }
    .el-tree {
      height: calc(100vh - 299px);
      overflow: auto;
      .el-tree-node__expand-icon.expanded {
        transform: none;
      }
      .el-tree-node__expand-icon.expanded:before {
        content: "\e784";
      }
      .el-tree-node__expand-icon {
        color: #c0c4cc;
      }
    }
  }
  .el-tabs__content {
    padding: 0;
    margin-top: 10px;
    background: #ffffff;
  }
</style>
