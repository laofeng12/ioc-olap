<template>
  <el-select
    :class="{ 'm-custom-left':!isBody }"
    ref="selectEl"
    :value="valueName"
    :clearable="clearable"
    @clear="clearHandle"
    :popper-append-to-body="isBody"
    v-bind="$attrs"
    v-on="$listeners"
  >
    <el-option :value="valueName" :label="valueName">
      <el-tree
        id="tree-option"
        v-loading="loadingTree"
        element-loading-spinner="el-icon-loading"
        ref="selectTree"
        :accordion="accordion"
        :data="options"
        :props="props"
        :node-key="props.value"
        :expand-on-click-node="false"
        icon-class="el-icon-arrow-down"
        @node-click="handleNodeClick"
        :filter-node-method="filterNode"
      ></el-tree>
    </el-option>
  </el-select>
</template>

<script>
// import { getTreeListLazy } from "@/api/common";

export default {
  name: "TreeSelection",
  props: {
    /* 配置项 */
    props: {
      type: Object,
      default: () => {
        return {
          value: "value", // ID字段名
          label: "label", // 显示名称
          children: "children" // 子级字段名
        };
      }
    },
    /* 选项列表数据(树形结构的对象数组) */
    callback: {
      type: Object,
      default: () => {
        return {};
      }
    },
    options: {
      type: Array,
      default: () => {
        return [];
      }
    },
    /* 初始值 */
    value: {
      type: String,
      default: () => {
        return null;
      }
    },
    /* 初始值名称 */
    valueName: {
      type: String,
      default: () => {
        return "";
      }
    },
    /* 可清空选项 */
    clearable: {
      type: Boolean,
      default: () => {
        return true;
      }
    },
    /* 自动收起 */
    accordion: {
      type: Boolean,
      default: () => {
        return false;
      }
    },
    /* loading 树 */
    loadingTree: {
      type: Boolean,
      default: () => {
        return false;
      }
    },
    // 是否将弹出框插入至 body 元素
    isBody: {
      type: Boolean,
      default: () => {
        return true;
      }
    },
    // 是否开启异步加载
    lazy: {
      type: Boolean,
      default: () => {
        return false;
      }
    }
  },
  data() {
    return {
      // valueId: "", // 初始值
      // valueTitle: "",
      selectListeners: {}
    };
  },

  mounted() {
  },
  methods: {
    // 切换选项
    handleNodeClick(node) {
      // console.log(this.$refs.selectEl)
      this.$refs.selectEl.blur(); // select-option 收起
      let valueTitle = node[this.props.label];
      let valueId = node[this.props.value];
      this.$emit("change", valueId, valueTitle, this.callback);
    },
    // 清除选中
    clearHandle() {
      // this.valueTitle = "";
      // this.valueId = null;
      // this.clearSelected()
      this.$emit("change", "", "", this.callback);
    },

    //树形节点过滤
    filterNode(value, data) {
      if (!value) return true;
      return data.orgName.indexOf(value) !== -1;
    },
    //select触发搜索回掉
    selectFilterHandler(text) {
      this.$refs.selectTree.filter(text);
    }

  }

  // watch: {
  //   value: {
  //     handler(nw) {
  //       this.valueId = nw;
  //       this.initHandle();
  //     },
  //     immediate: true
  //   },
  //   valueName: {
  //     handler(newV) {
  //       this.valueTitle = newV;
  //     },
  //     immediate: true
  //   }
  // }
};
</script>

<style lang="stylus" scoped>
>>>.el-tree-node__content {
  margin: 0 0 4px
}

.m-custom-left {
  >>>.el-select-dropdown {
    left: auto !important;
    right: 0 !important;
  }

  >>>.popper__arrow {
    left: 90% !important;
  }
}

.el-scrollbar {
  .el-scrollbar__view {
    .el-select-dropdown__item {
      height: auto;
      min-width: 400px;
      padding: 0;
      overflow: hidden;
      overflow-y: auto;
    }
  }
}

.el-select-dropdown__item.selected {
  font-weight: normal;
}

ul {
  li {
    >>>.el-tree .el-tree-node__content {
      height: auto;
    }
  }
}

.el-tree-node__label {
  font-weight: normal;
}

.el-tree {
  width: 100%;

  >>>.is-current {
    .el-tree-node__label {
      color: #0486FE;
      font-weight: 700;
    }

    .el-tree-node__children {
      .el-tree-node__label {
        color: #606266;
        font-weight: normal;
      }
    }
  }

  >>>.el-tree-node__expand-icon.el-icon-arrow-down {
    border: 1px solid rgba(0, 0, 0, 0.65);
    box-sizing: border-box;
    width: 14px;
    height: 14px;
    padding: 0;
    margin: 0 10px;
    font-size: 12px;
    border-radius: 2px;
    color: rgba(0, 0, 0, 0.65);
  }

  >>>.el-tree-node__expand-icon.is-leaf {
    border: none;
    color: transparent;
    cursor: default;
  }

  >>>.expanded {
    transform: rotate(180deg);
  }
}
</style>
