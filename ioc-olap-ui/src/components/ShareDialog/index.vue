<template>
  <div class="sd-box">
    <el-dialog
      :title="`共享${title}`"
      :visible.sync="visible"
      :show="show"
      width="width"
      @open="init"
      @close="$emit('update:show', false)">
      <div class="sd-tree-box">
        <h3>选择{{title}}</h3>
        <el-row :gutter="12" class="sd-box-top">
          <el-col :span="11">
            <el-card shadow="hover">
              <el-tree
                ref="topLeftTree"
                :data="shareDialogData.topLeftData"
                show-checkbox
                :node-key="topNodeKey"
                @node-click="clickTopNode"
                @check="checkedNode"
                :default-expanded-keys="[shareDialogData.topSelected]"
                :props="defaultProps">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                  <span class="cus-node-title" :title="data.dataName">{{ data.dataName }}</span>
                </span>
              </el-tree>
            </el-card>
          </el-col>
          <el-col :span="2" style="text-align: center;">
            <i class="el-icon-sort"></i>
          </el-col>
          <el-col :span="11">
            <el-card shadow="hover">
              <div style="text-align: center; color: #909399;" v-if="selectedData.length === 0">暂无数据</div>
              <div v-else>
                <div style="font-size: 16px; color: #303133; font-weight: 400;">已添加的{{title}}: </div>
                <div  class="node-selected-item" v-for="(item, index) in selectedData" :key="index" :title="item.dataName">
                  {{Number(index+1)}}. {{item.dataName}}
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <div class="sd-tree-box" style="margin-bottom: 50px;">
        <h3>选择共享人</h3>
        <el-row :gutter="12" class="sd-box-top">
          <el-col :span="10">
            <el-card shadow="hover">
              <el-tree
                ref="bottomLeftTree"
                :data="shareDialogData.bottomLeftData"
                :node-key="bottomNodeKey"
                @node-click="$emit('clickDepartNode', $event)"
                :props="departTreeProps">
              </el-tree>
            </el-card>
          </el-col>
          <el-col :span="1" style="text-align: center;">
            <i class="el-icon-sort"></i>
          </el-col>
          <el-col :span="13">
            <el-transfer
              v-loading="shareDialogData.showDepartLoading"
              element-loading-text="加载中..."
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(0, 0, 0, 0.8)"
              :titles="['待添加人员', '已添加人员']"
              v-model="memberVal"
              :data="memberData"></el-transfer>
          </el-col>
        </el-row>
      </div>
      <div slot="footer" class="dialog-footer">
        <!--<el-button type="primary" size="small" icon="el-icon-full-screen" @click="toggleFull('.el-dialog')">{{isFullScreen ? '退出全屏' : '全屏操作'}}</el-button>-->
        <el-button size="small" @click="visible = false">取 消</el-button>
        <el-button size="small" type="primary" @click="$emit('confirmShare', selectedDataList, memberVal)">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'shareDialog',
  data () {
    return {
      visible: this.show,
      memberVal: [],
      memberData: [],
      selectedData: [],
      selectedDataList: []
    }
  },
  watch: {
    show () {
      this.visible = this.show
    },
    'shareDialogData.midwareMember': {
      handler: function (newVal, oldVal) {
        this.memberData = newVal
      },
      deep: true
    },
    'shareDialogData.topRightData': {
      handler: function (newVal, oldVal) {
        this.selectedData = newVal
      },
      deep: true
    },
    'selectedData': {
      handler: function (newVal, oldVal) {
        this.selectedDataList = newVal.map(function (item) {
          return item.dataId
        })
      },
      deep: true
    }
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    width: {
      type: String,
      default: '60%'
    },
    show: {
      type: Boolean,
      default: false
    },
    topNodeKey: {
      type: String,
      default: 'dataId'
    },
    bottomNodeKey: {
      type: String,
      default: 'dataId'
    },
    defaultProps: {
      type: Object,
      default: () => {}
    },
    departTreeProps: {
      type: Object,
      default: () => {}
    },
    shareDialogData: {}
  },
  methods: {
    clickTopNode () {
    },
    refreshData () {
      this.memberVal = []
      this.memberData = []
      this.selectedData = []
    },
    checkedNode (data, node) {
      let temp = []
      node.checkedNodes.map(function (item) {
        if (item.isShare === 0) {
          temp.push(item)
        }
      })
      this.selectedData = temp
    },
    init () {
      let seft = this
      this.$nextTick(() => {
        let temp = []
        temp.push(seft.shareDialogData.topSelected)
        seft.$refs.topLeftTree.setCheckedKeys(temp)
      })
    }
  }
}
</script>

<style lang="scss">
  .sd-box {
    .el-dialog {
      position: relative;
      width: 1000px;
      overflow: hidden;
    }
    .el-dialog__header {
      border-bottom: 1px solid #f9f9f9;
    }
    .el-dialog__body {
      padding: 20px 15px;
      /*height: 470px;*/
      overflow: auto;
      height: 100%;
    }
    .sd-tree-box {
      margin-bottom: 20px;
      height: 40%;
      h3 {
        margin: 0;
        padding-left: 10px;
        border-left: 7px solid #409eff;
        margin-bottom: 8px;
      }
    }
    .sd-box-top {
      display: flex;
      align-items: center;
      .node-selected-item {
        color: #606266;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        margin: 5px 0 0 10px;
      }
      .el-icon-sort {
        transform: rotate(90deg);
        font-size: 20px;
        color: #888;
      }
      .el-card__body {
        height: 192px;
        overflow: auto;
      }
      .el-transfer-panel {
        height: 192px;
        width: 206px;
      }
    }
    .dialog-footer {
      text-align: right;
      position: absolute;
      bottom: 0;
      right: 0;
      width: 100%;
      padding: 7px 10px;
      background-color: #f9f9f9;
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
