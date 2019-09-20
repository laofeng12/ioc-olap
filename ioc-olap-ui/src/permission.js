import router from './router'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from './utils/auth'
import store from '@/store'
import { getSessionStorage } from '@/utils'

NProgress.configure({ showSpinner: false })// NProgress configuration

const whiteList = ['/login'] // 不重定向白名单
router.beforeEach((to, from, next) => {
  NProgress.start()

  const info = getSessionStorage('userInfo')
  let userInfo = info ? JSON.parse(info) : {}
  store.commit('SET_USERINFO', userInfo)

  const access_token = getToken()
  // 如果有token
  if (access_token) {
    // 判断是否在创建模型
    console.log('当前路由', to.path.split('/'))
    let PATH = to.path.split('/')
    if (!PATH.includes('createolap')) store.dispatch('resetList')
    if (to.path === '/login') {
      next({ path: '/home' })
      NProgress.done() // if current page is dashboard will not trigger afterEach hook, so manually handle it
    } else {
      next()
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      next()
    } else {
      next(`/login?redirect=${to.path}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done() // 结束Progress
})
