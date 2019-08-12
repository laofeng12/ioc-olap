import fetch from '@/utils/fetch'

// 模型列表
export function getModelDataList (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/cubeList',
    method: 'get',
    params
  })
}

// 查看立方体
export function descDataList (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/desc',
    method: 'get',
    params
  })
}

// 构建
export function buildModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/build',
    method: 'PUT',
    contentType: 'application/json',
    params
  })
}

// 复制
export function cloneModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/clone',
    method: 'PUT',
    contentType: 'application/json',
    params
  })
}

// 禁用
export function disableModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/disable',
    method: 'PUT',
    contentType: 'application/json',
    params
  })
}
// 启用
export function enableModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/enable',
    method: 'PUT',
    contentType: 'application/json',
    params
  })
}

// 刷新
// export function reloadModel (params) {
//   return fetch({
//     url: '/olap/apis/OlapModeling/cubeList',
//     method: 'PUT',
//     contentType: 'application/json',
//     params
//   })
// }

// 构建列表
export function jobsListModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/jobsList',
    method: 'post',
    contentType: 'application/json',
    params
  })
}
