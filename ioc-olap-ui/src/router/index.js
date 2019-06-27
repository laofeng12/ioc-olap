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
    },
    {
      path: '/system',
      name: 'system',
      component: ContainerWrapper,
      redirect: '/system/role',
      meta: { title: '系统管理' },
      children: [
        {
          path: 'user-list',
          name: 'userList',
          meta: { title: '用户管理' },
          component: () => import(/* webpackChunkName: "user" */ '@/views/system/UserList.vue')
        },
        {
          path: 'role',
          meta: { title: '角色管理' },
          name: 'role',
          // route level code-splitting
          // this generates a separate chunk (about.[hash].js) for this route
          // which is lazy-loaded when the route is visited.
          component: () => import(/* webpackChunkName: "role" */ '@/views/system/Role.vue')
        },
        {
          path: 'resource',
          name: 'resource',
          meta: { title: '资源管理' },
          component: () => import(/* webpackChunkName: "resource" */ '@/views/system/Resource.vue')
        },
        {
          path: 'mechanismManage',
          name: 'mechanismManage',
          meta: { title: '组织机构管理' },
          component: () => import(/* webpackChunkName: "resource" */ '@/views/system/mechanismManage.vue')
        },
        {
          path: 'user-edit/:id',
          name: 'eidtUser',
          meta: { title: '编辑用户' },
          component: () => import(/* webpackChunkName: "eidtUser" */ '@/views/system/AddUser.vue')
        },
        {
          path: 'user-info/:id',
          name: 'userInfo',
          meta: { title: '编辑用户' },
          component: () => import(/* webpackChunkName: "userInfo" */ '@/views/system/UserInfo.vue')
        },
        {
          path: 'user-add',
          name: 'addUser',
          meta: { title: '编辑用户' },
          component: () => import(/* webpackChunkName: "addUser" */ '@/views/system/AddUser.vue')
        },
        {
          path: 'child-system',
          name: 'childSystem',
          meta: { title: '子系统管理' },
          component: () => import(/* webpackChunkName: "childSystem" */ '@/views/system/ChildSystem.vue')
        }
      ]
    }
  ]
})
