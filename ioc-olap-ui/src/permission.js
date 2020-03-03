import router from './router'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { getToken } from './utils/auth'
import store from '@/store'
import { getSessionStorage } from '@/utils'
import Cookies from 'js-cookie'

NProgress.configure({ showSpinner: false })// NProgress configuration

// single-spa 模式下，路由拦截只设置用户信息，路由判断由外围统一处理
if (window.singleSpaNavigate) {
  router.beforeEach((to, from, next) => {
    const objectList = [
      '/dataminingweb',
      '/datacollisionweb',
      '/datatransferweb',
      '/datatagweb',
      '/platformweb',
      '/datalakeweb',
      '/visualweb',
      '/olapweb',
      '/biweb',
      '/dsweb'
    ]
    for (let obj of objectList) {
      if (to.path.match(new RegExp('^' + obj))) {
        return
      }
    }
    // 把路由挂在 window，让外围拿到数据
    window.router = { to, baseUrl: '/olapweb' }
    const info = getSessionStorage('userInfo')
    let userInfo = info ? JSON.parse(info) : {}
    store.commit('SET_USERINFO', userInfo)
    next()
  })
} else {
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
        if (Object.keys(to.query).length) {
          to.matched[1].meta.title = '编辑OLAP模型'
        }
      // to.matched[1].meta.title = Object.keys(to.query).length ? '编辑OLAP模型' : '新建OLAP模型'
      }
      if (to.path === '/login') {
        next({ path: '/home' })
        NProgress.done() // if current page is dashboard will not trigger afterEach hook, so manually handle it
      } else {
      // 重置store 的信息
      // let data = { token: access_token, userInfo: userInfo }
      // store.dispatch('SetToken', data)
      // next()

        // 测试修改跳转
        const routerBase = router.options.base
        const toPath = to.fullPath
        const rootPath = toPath.substring(0, toPath.replace('/', '-').indexOf('/') + 1)
        if (rootPath === routerBase) {
          next(toPath.replace(routerBase, '/'))
        } else if (to.matched.length === 0) {
          if (Cookies.get(rootPath)) {
            Cookies.remove(rootPath)
            next('/404')
          } else {
            Cookies.set(rootPath, 1)
            window.location.replace(toPath)
          }
        } else {
          Cookies.remove(routerBase)
          next()
        }
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
}
