<template>
  <div>
    <ch-layout
      :toggleSideBar="toggleSideBar"
      @handleClickOutside="handleClickOutside"
      :logout="logout"
      :sidebar="sidebar"
      :device="device"
      :routes="menuList"
      :userInfo="userInfo"
      :dropdownItemList="dropdownItemList"
      :portalInfo="portalInfo"
      :svgArr="svgArr"
      v-if="!singleSpaNavigate"
    >
      <div class="container-wrapper">
        <transition name="fade-transform" mode="out-in">
          <router-view></router-view>
        </transition>
      </div>
    </ch-layout>
    <div v-else class="container-wrapper">
      <router-view></router-view>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import ResizeMixin from '../../mixin/ResizeHandler'
export default {
  // 这个mixins是为了初始化状态栏的状态，具体见原文件
  mixins: [ResizeMixin],
  data () {
    return {
      dropdownItemList: [],
      // dropdownItemList: [{ icon: 'notice', itemName: '公告', itemChild: [{ itemChildName: '消息', itemChildNum: 3, path: '/', link: 'https://www.baidu.com' }] }],
      portalInfo: { logo: require('../../assets/img/logo.png'), portalTitle: '政务数据服务平台' },
      svgArr: []
    }
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'device', // 设备类型，mobile或者desktop
      'menuList', // 后台请求回来的菜单
      'userInfo' // 用户信息
    ]),
    singleSpaNavigate () {
      return window.singleSpaNavigate
    }
  },
  created () {
    this.getMenuList()
    const requireAll = requireContext => requireContext.keys().map(requireContext => {
      const startI = requireContext.indexOf('/') + 1
      const endI = requireContext.indexOf('svg') - 1
      return requireContext.substring(startI, endI)
    })

    const req = require.context('../../icons/svg', false, /\.svg$/)
    this.svgArr = requireAll(req)
  },
  methods: {
    toggleSideBar () {
      this.$store.dispatch('ToggleSideBar')
    },
    getMenuList () {
      this.$store.dispatch('GetMenuList')
    },
    handleClickOutside () {
      this.$store.dispatch('CloseSideBar', { withoutAnimation: false })
    },
    logout () {
      this.$store.dispatch('logout').then((res) => {
        this.$message.success(res.message)
        setTimeout(() => {
          location.reload() // 为了重新实例化vue-router对象 避免bug
        }, 1000)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import '@/styles/variables.scss';

.container-wrapper {
  padding: 24px;
  background:#F2F2F2;
  min-height: $containerWrapperMinHeight;
}
.app-container {
  min-height: $containerMinHeight;
  box-sizing: border-box;
}
</style>
