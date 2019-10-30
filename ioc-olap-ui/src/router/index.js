import Vue from 'vue'
import Router from 'vue-router'
import Home from '@/views/home/index'
import ContainerWrapper from '@/views/ContainerWrapper'
import stepContainer from '@/views/analysisModel/createolap'
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
      path: '/analysisModel',
      name: 'analysisModel',
      component: ContainerWrapper,
      redirect: '/analysisModel/Configuration',
      children: [
        {
          path: 'Configuration',
          name: 'Configuration',
          meta: { title: 'OLAP模型' },
          component: () => import('@/views/analysisModel/Configuration.vue')
        },
        {
          path: 'createolap',
          name: 'createolap',
          meta: { title: '新建OLAP模型' },
          component: stepContainer,
          redirect: '/analysisModel/Configuration',
          children: [
            {
              path: 'selectStep',
              name: 'selectStep',
              meta: { title: '选择数据源' },
              component: () => import(`@/components/analysisComponent/createComponent/selectStep.vue`)
            },
            {
              path: 'createTableRelation',
              name: 'createTableRelation',
              meta: { title: '建立表关系' },
              component: () => import('@/components/analysisComponent/createComponent/createTableRelation.vue')
            },
            {
              path: 'setFiled',
              name: 'setFiled',
              meta: { title: '设置维度字段' },
              component: () => import('@/components/analysisComponent/createComponent/setFiled.vue')
            },
            {
              path: 'setMeasure',
              name: 'setMeasure',
              meta: { title: '设置度量字段' },
              component: () => import('@/components/analysisComponent/createComponent/setMeasure.vue')
            },
            {
              path: 'reloadSet',
              name: 'reloadSet',
              meta: { title: '刷新及过滤设置' },
              component: () => import('@/components/analysisComponent/createComponent/reloadSet.vue')
            },
            {
              path: 'advancedSet',
              name: 'advancedSet',
              meta: { title: '高级设置' },
              component: () => import('@/components/analysisComponent/createComponent/advancedSet.vue')
            },
            {
              path: 'completeCreate',
              name: 'completeCreate',
              meta: { title: '完成创建' },
              component: () => import('@/components/analysisComponent/createComponent/completeCreate.vue')
            }
          ]
        }
      ]
    },
    {
      path: '/',
      name: 'queries1',
      component: ContainerWrapper,
      redirect: '/instantInquiry',
      children: [
        {
          path: 'instantInquiry',
          name: 'instantInquiry',
          meta: { title: '即席分析' },
          component: () => import('@/views/analysisModel/instantInquiry.vue')
        }
        // {
        //   path: 'createolap',
        //   name: 'createolap',
        //   meta: { title: '新建OLAP模型' },
        //   component: () => import('@/views/analysisModel/createolap.vue')
        // }
      ]
    },
    {
      path: '/',
      name: 'queries2',
      component: ContainerWrapper,
      redirect: '/analysisList',
      children: [
        {
          path: 'analysisList',
          name: 'analysisList',
          meta: { title: 'olap分析' },
          component: () => import('@/views/analysisModel/analysisList.vue')
        },
        {
          path: 'newOlapAnalysis',
          name: 'newOlapAnalysis',
          meta: { title: '新建olap分析' },
          component: () => import('@/views/analysisModel/analysis.vue')
        }
      ]
    }
  ]
})
