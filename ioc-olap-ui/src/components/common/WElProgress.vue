<template>
  <div v-if="instanceStatus">
    <el-popover
      placement="top-start"
      width="150"
      title="进程详情"
      trigger="hover">
      <el-progress  slot="reference"
        :percentage="successInstance === 0 ? 100: successInstance/failedInstance * 100"
        :format="format" :color="successInstance === 0 ? '#ff4949' : '#13ce66'"
      >
      </el-progress>

      <div class="instances-wrapper">
        <p class="instances-status">
          <el-badge type="primary" is-dot class="item">总实例数 {{instanceStatus.totalInstances}}</el-badge>
        </p>
        <p class="instances-status">
          <el-badge type="success" is-dot class="item">处理成功 {{instanceStatus.succeededInstances}}</el-badge>
          </p>
        <p class="instances-status">
          <el-badge type="success" is-dot class="item">正在运行 {{instanceStatus.runningInstances}}</el-badge>
          </p>
        <p class="instances-status">
          <el-badge type="danger" is-dot class="item">处理错误 {{instanceStatus.failedInstances}}</el-badge>
          </p>
        <p class="instances-status">
          <el-badge type="warning" is-dot class="item">正在等待 {{instanceStatus.waitingInstances}}</el-badge>
        </p>
      </div>
    </el-popover>
  </div>

  <div v-else>
    <el-button type="text" icon="el-icon-loading" style="color: #409EFF">正在处理</el-button>
  </div>
</template>

<script>
export default {
  props: {
    successInstance: {
      type: Number,
      default: 0
    },
    failedInstance: {
      type: Number,
      default: 0
    },
    instanceStatus: {
      type: Object,
      default: () => ({})
    }
  },
  methods: {
    format (percentage) {
      return `${this.successInstance}/${this.instanceStatus.totalInstances}`
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.success-progress-span {
  color: #13ce66;
}
.failed-progress-span {
  color: #ff4949
}
.instances-wrapper {
  padding-left: 20px;
  /deep/ .instances-status {
    color: $chTableFont;

    /deep/ .el-badge__content.is-fixed.is-dot {
      left: -25px;
      top: 9px;
    }
  }
}
</style>
