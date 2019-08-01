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
