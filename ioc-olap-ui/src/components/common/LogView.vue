<template>
  <div class="log-view-out-wrapper">
    <div class="follow-btn" v-if="isEndOfLog" @click="goToBottom">
      返回底部
    </div>
    <div v-else class="follow-btn" @click="follow">
      {{isFollow ? '停止跟随' : '跟随更新'}}
    </div>

    <div class="log-view-inner-wrapper" ref="logView">
      <ul class="log-view">
        <li class="log-item" v-for="(item, index) in logList" :key="index">
          <div class="line-num">{{index + 1}}</div>
          <div class="log-info">{{item}}</div>
        </li>
      </ul>

      <div class="go-top" @click="goToTop">
        返回顶部
      </div>
    </div>
  </div>
</template>

<script>
import DMSocket from '@/utils/DMSocket'

export default {
  props: {
    websocketUrl: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      isFollow: true,
      logList: [],
      websocket: null,
      isEndOfLog: false,
      maxLogLine: 3000
    }
  },
  created () {
    this.createSocket(this.websocketUrl)
  },
  methods: {
    goToTop () {
      this.$refs.logView.scrollTop = 0
    },
    goToBottom () {
      if (!this.$refs.logView) {
        return
      }
      this.$refs.logView.scrollTop = this.$refs.logView.scrollHeight
    },
    follow () {
      this.isFollow = !this.isFollow
      if (this.isFollow) {
        this.goToBottom()
      }
    },
    createSocket (websocketUrl) {
      this.websocket = new DMSocket(websocketUrl)

      this.websocket.onopen = this.websocketOnopen
      this.websocket.onerror = this.websocketOnerror
      this.websocket.onmessage = this.websocketOnmessage
      this.websocket.onclose = this.websocketOnclose
    },
    websocketOnopen () {
      console.log('websocket 链接成功')
    },
    websocketOnerror () {
      console.log('websocket 链接错误')
      this.isEndOfLog = true
    },
    websocketOnmessage (e) {
      const logs = e.data.split('\n')
      if (this.logList.length <= this.maxLogLine) {
        setTimeout(() => {
          this.logList = [...this.logList, ...logs]
          if (this.isFollow) {
            this.goToBottom()
          }
        }, 1000)
      } else {
        this.isEndOfLog = true
      }

      console.log('this.isFollow', this.isFollow)
    },
    websocketOnclose () {
      console.log('websocket 关闭')
    }
  }
}
</script>

<style lang="scss" scoped>
.log-view-out-wrapper {
  width: 100%;
  position: relative;
  height: calc(100vh - 285px);
  font-family: "Open Sans", Helvetica, Arial, sans-serif;
  .log-view-inner-wrapper {
    height: 100%;
    background: #000;
    color: #fff;
    font-size: 12px;
    line-height: 20px;
    font-family: Menlo, Monaco, Consolas, monospace;
    overflow: auto;
    .log-view {
      background: #000;
      color: #ffffff;
      padding: 40px 15px 40px 0;
      min-height: calc(100% - 45px);
      .log-item {
        display: flex;
        .line-num {
          border-right: 1px #272b30 solid;
          padding-right: 10px;
          vertical-align: top;
          white-space: nowrap;
          width: 60px;
          color: #72767b;
          text-align: right;
          -webkit-box-sizing: border-box;
          box-sizing: border-box;
        }
        .log-info {
          padding: 0 10px;
          white-space: pre-wrap;
          width: 100%;
          word-break: break-word;
          overflow-wrap: break-word;
          min-width: 0;
          word-wrap: break-word;
        }
      }
    }
    .go-top {
      position: absolute;
      right: 25px;
      bottom: 10px;
      background-color: #545e69;
      border-radius: 1px;
      -webkit-box-shadow: 0 0 5px 7px rgba(0, 0, 0, 0.9);
      box-shadow: 0 0 5px 7px rgba(0, 0, 0, 0.9);
      color: #fff;
      display: inline-block;
      font-size: 12px;
      padding: 2px 6px;
      cursor: pointer;
    }
  }
  .follow-btn {
    position: absolute;
    right: 25px;
    top: 10px;
    background-color: #545e69;
    border-radius: 1px;
    -webkit-box-shadow: 0 0 5px 7px rgba(0, 0, 0, 0.9);
    box-shadow: 0 0 5px 7px rgba(0, 0, 0, 0.9);
    color: #fff;
    display: inline-block;
    font-size: 12px;
    padding: 2px 6px;
    cursor: pointer;
    box-sizing: border-box;
    line-height: 20px;
  }
}

</style>
