<template>
  <div class="console" id="terminal">
    <svg-icon class="fullscreen-btn" :class="{'is-full': isFullscreen}" style="color: #9c9c9c" @click="handleResize()" :iconClass="isFullscreen ? 'shrink' : 'arrawsalt'"></svg-icon>
  </div>
</template>
<script>
import Terminal from './Xterm'
import DMSocket from '@/utils/DMSocket'

export default {
  name: 'Console',
  props: {
    terminal: {
      type: Object,
      default: () => {}
    },
    websocketUrl: {
      type: String,
      default: ''
    }
  },
  data () {
    return {
      term: null,
      websocket: '',
      isFullscreen: false,
      show: false
    }
  },
  mounted () {
    let terminalContainer = document.getElementById('terminal')
    let height = document.body.scrollHeight - 285
    let rows = height / 18

    this.term = new Terminal({ cursorBlink: true, rows: parseInt(rows) })

    this.term.open(terminalContainer)

    this.term.on('key', (key, ev) => {
      if (ev.keyCode === 27) {
        this.term.focus()
        this.handleResize()
      }
    })

    this.createSocket(this.websocketUrl)
  },
  methods: {
    runRealTerminal () {
      console.log('webSocket is finished')
    },
    errorRealTerminal () {
      console.log('error')
    },
    closeRealTerminal () {
      console.log('close')
    },
    createSocket (websocketUrl) {
      this.websocket = new DMSocket(websocketUrl)

      this.websocket.onopen = this.socketOnopen
      this.websocket.onerror = this.socketOnerror
      this.websocket.onmessage = this.socketOnmessage
      this.websocket.onclose = this.socketOnclose
    },
    socketOnopen () {
      console.log('链接成功')

      this.term.attach(this.websocket)
      this.term.fit()
    },
    socketOnerror () {
      console.log('链接出错')
    },
    socketOnmessage (data) {
      console.log('socket DATA' + data)
    },
    socketOnclose (data) {
      console.log('关闭')
    },
    handleResize () {
      this.isFullscreen = !this.isFullscreen
      if (this.isFullscreen) {
        this.$message({
          type: '',
          message: '按ESC可退出全屏',
          center: true
        })
      }
      this.term.toggleFullScreen(this.isFullscreen)
    }
  },
  beforeDestroy () {
    this.term.destroy()
  }
}
</script>

<style lang="scss" scoped>
#terminal {
  position: relative;
  z-index: 1002;
  .fullscreen-btn {
    position: absolute;
    cursor: pointer;
    right: 30px;
    top: 15px;
    z-index: 999;
    font-size: 20px;
    &.is-full {
      position: fixed;

    }
  }
}
</style>
