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
    <el-tree
      class="filter-tree"
      icon-class="el-icon-folder"
      :data="menuList"
      :props="menuDefault"
      default-expand-all
      :filter-node-method="filterAll"
      @node-click="clickTreeItem"
      ref="alltree_">
              <span class="custom-tree-node" slot-scope="{ node, data }">
                <span class="cus-node-title" :title="data.dataName">{{ data.dataName }}</span>
                <span class="cus-node-content">
                  <el-dropdown size="mini" @command="handleItemShare($event, node, data)">
                    <el-button type="primary" size="mini">
                      操作<i class="el-icon-arrow-down el-icon--right"></i>
                    </el-button>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item command="0">重命名</el-dropdown-item>
                      <!--<el-dropdown-item v-if="!node.isLeaf" command="1">刷新</el-dropdown-item>-->
                      <el-dropdown-item command="2" v-if="node.level !== 1">移动</el-dropdown-item>
                      <el-dropdown-item command="3">删除</el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </span>
              </span>
    </el-tree>
    <div style="text-align: center;">
      <el-button type="primary" size="small" @click="newFolder()">新建文件夹</el-button>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    menuList: {
      type: Array,
      required: true
    },
    menuDefault: {
      // type: Array,
      required: true
    }
  },
  data () {
    return {
      showMenuList: false,
      searchKeyword_: '', // 分享搜索
      treeList: [
        {
          id: 1,
          label: '交通局',
          children: [{
            id: 4,
            label: '检查组',
            children: [{
              id: 9,
              label: '张三'
            }, {
              id: 10,
              label: '李四'
            }]
          }]
        }, {
          id: 2,
          label: '教育局',
          children: [{
            id: 5,
            label: '张三'
          }, {
            id: 6,
            label: '李四'
          }]
        }
      ],
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
          { max: 50, message: '请不要超过50个字符', trigger: 'blur' }
        ],
        sort: [
          { required: true, message: '请输入序号', trigger: 'blur' },
          { type: 'number', message: '序号必须为数字值' }
        ]
      }
    }
  },
  watch: {
    searchKeyword (val) {
      this.$refs.alltree.filter(val)
    },
    searchKeyword_ (val) {
      this.$refs.alltree_.filter(val)
    }
  },
  methods: {
    showShareVisible () {
      this.shareVisible = true
    },
    submitFolderForm () {
      console.info('this.folderForm', this.folderForm)
    },
    showNewVisible () {
      this.newVisible = true
    },
    clickTreeItem (data, node, self) {
      let that = this
      if (node.parent.parent) {
        // 子节点才进入
        that.sheetTitle = data.dataName
        that.sheetDataId = data.dataId
        that.sheetShare = data.isShare
        // 渲染表格数据
        this.getTableData_(data.dataId)
      }
    },
    filterAll (value, data) {
      if (!value) return true
      return data.dataName.indexOf(value) !== -1
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
