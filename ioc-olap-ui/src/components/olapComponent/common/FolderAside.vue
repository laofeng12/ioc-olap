<template>
  <div class="folderAside">
    <div class="folder" v-for="(item, index) in menuList" :key="index">
      <div class="folderName dis-flex l-h-35">
        <div class="name dis-flex">
          <i class="icon-folder"></i>
          <span class="f-w-b">{{item.title}}</span>
        </div>
        <div class="menu" @click="showShareVisible"><i class="el-icon-more"></i></div>
        <div class="menuList" v-if="showMenuList">
          <div class="close"><i class="el-icon-close"></i></div>
          <div class="l-h-30 b-b text-center">编辑</div>
          <div class="l-h-30 b-b text-center">共享</div>
          <div class="l-h-30 text-center">删除</div>
        </div>
      </div>
      <div class="file dis-flex l-h-30" v-for="(v, i) in item.row" :key="i">
        <div class="name">
          <span>{{v.name}}</span>
        </div>
        <div class="menu"><i class="el-icon-more"></i></div>
      </div>
    </div>
    <el-button class="button" type="primary" size="mini" @click="showNewVisible">新建文件夹</el-button>
    <el-dialog class="visible" title="选择共享人" :visible.sync="shareVisible" width="40%">
      <el-tree :data="treeList" show-checkbox ></el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shareVisible = false">取 消</el-button>
        <el-button type="primary" @click="shareVisible = false">确 定</el-button>
      </div>
    </el-dialog>
    <el-dialog :title="`${folderForm.isNew ? '新增' : '修改'}文件夹`" :visible.sync="newVisible" width="30%">
    <el-form :model="folderForm" :rules="folderRules" ref="folderForm">
        <el-form-item label="名称" label-width="70px" prop="name">
          <el-input v-model="folderForm.name" auto-complete="off"></el-input>
        </el-form-item>
        <el-form-item class="m-t-30" label="排序号" label-width="70px" prop="sort">
          <el-input v-model.number="folderForm.sort" auto-complete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFolderForm">
          确认{{folderForm.isNew ? '新增' : '修改'}}
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  props: {
    menuList: {
      type: Array,
      required: true,
    }
  },
  data () {
    return {
      showMenuList: false,
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
    }
  }
}
</script>

<style lang="scss" scoped>
  .folderAside {
    width: 200px;
    flex-shrink: 0;
    background-color: white;
    margin-right: 20px;
    .folder {
      padding: 0 15px;
      box-sizing: border-box;
      cursor: pointer;
      .folderName {
        justify-content: space-between;
        position: relative;
        .name {
          align-items: center;
          .icon-folder {
            display: inline-block;
            width: 23px;
            height: 20px;
            margin-right: 10px;
            background-image: url("../../../assets/img/icon_folder.png");
            background-size: 100% 100%;
            background-repeat: no-repeat;
          }
        }
        .menuList {
          width: 100px;
          position: absolute;
          right: -70px;
          top: 0;
          background: #ffffff;
          border-radius: 5px;
          border: 1px #ddd solid;
          z-index: 1;
          .close {
            width: 5px;
            height: 5px;
            position: relative;
            top: -8px;
            right: -80px;
          }
        }
      }
      .file {
        justify-content: space-between;
        padding-left: 40px;
        box-sizing: border-box;
      }
    }
    .button {
      display: block;
      margin: 10px auto;
    }
  }
</style>
