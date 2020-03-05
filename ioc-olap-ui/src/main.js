import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import store from '@/store'
import 'normalize.css/normalize.css'
// import Layout from 'ch-vue-portal-layout'
import Layout from 'nhc-portal'
import ElementUI from 'element-ui'
import 'nhc-portal/lib/nhc-portal.css'
// import 'element-ui/lib/theme-chalk/index.css'
// import 'ch-vue-portal-layout/lib/ch-vue-portal-layout.css'
import '@/styles/element-variables.scss'
import { throttle } from '@/utils/index'
import './icons'
import '@/styles/index.scss'
import 'vue-task-node/dist/css/vnode.css'
import fullscreen from 'vue-fullscreen'
import '@/permission' // permission control
import singleSpaVue from './single-spa-vue.js'

Vue.use(ElementUI)
Vue.use(Layout)
Vue.use(fullscreen)
Vue.config.productionTip = false
Vue.prototype.$throttle = throttle

// new Vue({
//   router,
//   store,
//   data: {
//     eventBus: new Vue()
//   },
//   render: h => h(App)
// }).$mount('#app')

// ---------------------------------------------------
const vueOptions = {
  el: '#app',
  router,
  store,
  data: {
    eventBus: new Vue()
  },
  render: h => h(App)
}

// 判断当前页面使用singleSpa应用,不是就渲染
if (!window.singleSpaNavigate) {
  delete vueOptions.el
  new Vue(vueOptions).$mount('#app')
}

// singleSpaVue包装一个vue微前端服务对象
const vueLifecycles = singleSpaVue({
  Vue,
  appOptions: vueOptions
})

export const bootstrap = vueLifecycles.bootstrap // 启动时
export const mount = vueLifecycles.mount // 挂载时
export const unmount = vueLifecycles.unmount // 卸载时

export default vueLifecycles
