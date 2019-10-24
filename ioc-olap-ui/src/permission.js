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
    let PATH = to.path.split('/')
    if (!PATH.includes('createolap')) store.dispatch('resetList')
    // 判断当前停留在模型哪个页面
    let pathindex = ['', 'selectStep', 'createTableRelation', 'setFiled', 'setMeasure', 'reloadSet', 'advancedSet', 'completeCreate']
    store.state.olap.HeadNum = pathindex.indexOf(to.name)
    // 判断当前是新建还是编辑（根据ModelAllList是否为对象来判断是否为编辑）
    if (pathindex.includes(to.name)) {
      // to.matched[1].meta.title = to.query.cubeName ? '编辑OLAP模型' : '新建OLAP模型'
      to.matched[1].meta.title = Object.keys(to.query).length ? '编辑OLAP模型' : '新建OLAP模型'
    }
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
