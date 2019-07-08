<template>
  <div class="folderAside">
    <div class="folder">
      <div class="folderName dis-flex l-h-35">
        <div class="name dis-flex">
          <i class="icon-folder"></i>
          <span class="f-w-b">文件夹名字</span>
        </div>
        <div class="menu"><i class="el-icon-more"></i></div>
        <div class="menuList" v-if="showMenuList">
          <div class="close"><i class="el-icon-close"></i></div>
          <div class="l-h-30 b-b text-center">编辑</div>
          <div class="l-h-30 b-b text-center">共享</div>
          <div class="l-h-30 text-center">删除</div>
        </div>
      </div>
      <div class="file dis-flex l-h-30">
        <div class="name">
          <span>文件名字</span>
        </div>
        <div class="menu"><i class="el-icon-more"></i></div>
      </div>
      <div class="file dis-flex l-h-30">
        <div class="name">
          <span>文件名字</span>
        </div>
        <div class="menu"><i class="el-icon-more"></i></div>
      </div>
    </div>
    <el-dialog class="visible" title="选择共享人" :visible.sync="shareVisible">
      <!--<el-tree :data="treeList" show-checkbox :props="defaultProps"></el-tree>-->
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="dialogFormVisible = false">确 定</el-button>
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
      shareVisible: true,
      treeList: [
        {
          id: 1,
          label: '一级 1',
          children: [{
            id: 4,
            label: '二级 1-1',
            children: [{
              id: 9,
              label: '三级 1-1-1'
            }, {
              id: 10,
              label: '三级 1-1-2'
            }]
          }]
        }, {
          id: 2,
          label: '一级 2',
          children: [{
            id: 5,
            label: '二级 2-1'
          }, {
            id: 6,
            label: '二级 2-2'
          }]
        }, {
          id: 3,
          label: '一级 3',
          children: [{
            id: 7,
            label: '二级 3-1'
          }, {
            id: 8,
            label: '二级 3-2'
          }]
        }
      ],
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  data () {
    return {
      showMenuList: false
    }
  },
  watch: {

  },
  methods: {}
}
</script>

<style lang="scss" scoped>
  .folderAside {
    width: 200px;
    flex-shrink: 0;
    background-color: white;
    margin-right: 20px;
    .folder {
      padding: 15px;
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
  }
</style>
