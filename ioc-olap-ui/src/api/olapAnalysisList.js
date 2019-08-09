import fetch from '@/utils/fetch'

/**
 * olap分析相关接口
 */

export function getFolderWithQueryApi () {
  return fetch({
    url: '/olap/apis/olapAnalyze/folderWithQuery',
    method: 'get'
  })
}

export function getQueryShareApi () {
  return fetch({
    url: '/olap/apis/olapAnalyze/queryShare',
    method: 'get'
  })
}

export function getQueryTableApi (params) {
  return fetch({
    url: '/olap/apis/olapAnalyze/query',
    method: 'get',
    params
  })
}

export function getCubesApi () {
  return fetch({
    url: '/olap/apis/olapAnalyze/Cubes',
    method: 'get'
  })
}

export function getFilterDetailsApi (params) {
  return fetch({
    url: '/olap/apis/olapAnalyze/queryDimension',
    method: 'get',
    params
  })
}

export function getOlapAnalyzeApi (params, data) {
  return fetch({
    url: '/olap/apis/olapAnalyze/query',
    method: 'post',
    params,
    data
  })
}

export function saveOlapAnalyzeApi (data) {
  return fetch({
    url: '/olap/apis/olapAnalyze/save',
    method: 'post',
    data
  })
}

export function getOlapAnalyzeDetailsApi (data) {
  return fetch({
    url: '/olap/apis/olapAnalyze/get',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}
