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

// 删除
export function deleteCubeModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/deleteCube',
    method: 'DELETE',
    params
  })
}

// 合并
export function mergeCubeModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/merge',
    method: 'PUT',
    params
  })
}

// 刷新
export function reloadModel (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/refresh',
    method: 'PUT',
    params
  })
}

// 构建列表
export function jobsListModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/jobsList',
    method: 'get',
    params
  })
}

// 停止
export function cancelJobListModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/cancelJob',
    method: 'PUT',
    params
  })
}

// 暂停
export function pauseJobListModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/pauseJob',
    method: 'PUT',
    params
  })
}

// 运行
export function resumeJobListModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/resumeJob',
    method: 'PUT',
    params
  })
}

// 删除
export function deleteJobListModeling (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/deleteJob',
    method: 'DELETE',
    params
  })
}
