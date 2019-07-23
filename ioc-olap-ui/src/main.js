import Vue from 'vue'
import App from '@/App.vue'
import router from '@/router'
import store from '@/store'
import 'normalize.css/normalize.css'
import Layout from 'ch-vue-portal-layout'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import 'ch-vue-portal-layout/lib/ch-vue-portal-layout.css'
import '@/styles/element-variables.scss'
import '@/permission' // permission control
import './icons'
import '@/styles/index.scss'
import 'vue-task-node/dist/css/vnode.css'
import fullscreen from 'vue-fullscreen'

Vue.use(ElementUI)
Vue.use(Layout)
Vue.use(fullscreen)
Vue.config.productionTip = false

new Vue({
  router,
  store,
  data: {
    eventBus: new Vue()
  },
  render: h => h(App)
}).$mount('#app')
