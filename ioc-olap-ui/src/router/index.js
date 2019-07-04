import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/home/index'
import ContainerWrapper from '@/views/ContainerWrapper'
import Login from '@/views/login/index'
Vue.use(Router)

export default new Router({
  mode: 'history',
  base: process.env.BASE_URL,
  routes: [
    { path: '/login', component: Login, hidden: true },
    { path: '/404', component: () => import(/* webpackChunkName: "404" */ '@/views/404'), hidden: true },
    {
      path: '/',
      name: 'home',
      redirect: '/home',
      component: ContainerWrapper,
      children: [
        {
          path: '/home',
          name: 'indexHome',
          meta: { title: '首页' },
          component: Home
        }
      ]
    },
    {
      path: '/olap',
      name: 'olap',
      component: ContainerWrapper,
      redirect: '/olap/olapList',
      children: [
        {
          path: 'olapList',
          name: 'olapList',
          meta: { title: 'OLAP分析' },
          component: () => import('@/views/olap/olapList.vue')
        },
        {
          path: 'createolap',
          name: 'createolap',
          meta: { title: '新建OLAP模型' },
          component: () => import('@/views/olap/createolap.vue')
        }
      ]
    }
  ]
})
