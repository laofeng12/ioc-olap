<template>
  <div class="md-box">
    <el-dialog
      :title="title"
      :visible.sync="visible"
      :show="show"
      width="width"
      @close="$emit('update:show', false)">
      <div class="md-inner-box">
        <h3>请选择目标文件夹</h3>
        <template v-if="FolderList.length === 0">
          <span class="no-folder"><i class="el-icon-folder-opened"></i> 暂无文件夹</span>
        </template>
        <template v-else>
          <el-radio-group v-model="selectedFolder" @change="$emit('selectFolder', $event)">
            <el-radio
              v-for="(item, index) in FolderList"
              :label="item.categoryId" :key="index" :id="item.categoryId" :type="item.categoryType"><i class="el-icon-folder"></i> {{item.categoryName}}</el-radio>
          </el-radio-group>
        </template>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button size="small" @click="visible = false">取 消</el-button>
        <el-button v-if="FolderList.length > 0" size="small" type="primary" @click="$emit('confirmMove', selectedFolder)">确 定</el-button>
      </span>
      <!--<slot name="footer"></slot>-->
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'index',
  props: {
    title: {
      type: String
    },
    width: {
      type: String,
      default: '60%'
    },
    show: {
      type: Boolean,
      default: false
    },
    FolderList: {
      type: Array
    }
  },
  data () {
    return {
      visible: this.show,
      selectedFolder: ''
    }
  },
  watch: {
    show () {
      this.visible = this.show
    }
  },
  methods: {}
}
</script>

<style lang="scss">
  .md-box {
    .el-dialog__header {
      border-bottom: 1px solid #f9f9f9;
    }
    .el-dialog__body {
      padding: 20px 15px;
      max-height: 360px;
      overflow: auto;
    }
    .md-inner-box {
      h3 {
        margin: 0;
        padding-left: 10px;
        border-left: 7px solid #409eff;
        margin-bottom: 8px;
      }
      .no-folder {
        margin-left: 20px;
        color: #888888;
      }
      .el-radio-group {
        margin: 10px 15px;
        label {
          display: block;
          margin-bottom: 10px;
        }
      }
    }

    /**滚动条样式**/
    ::-webkit-scrollbar {
      width: 14px;
      height: 14px;
    }

    ::-webkit-scrollbar-track,
    ::-webkit-scrollbar-thumb {
      border-radius: 999px;
      border: 5px solid transparent;
    }

    ::-webkit-scrollbar-track {
      box-shadow: 1px 1px 5px rgba(0,0,0,.2) inset;
    }

    ::-webkit-scrollbar-thumb {
      min-height: 20px;
      background-clip: content-box;
      box-shadow: 0 0 0 5px rgba(0,0,0,.2) inset;
    }

    ::-webkit-scrollbar-corner {
      background: transparent;
    }
  }
</style>
