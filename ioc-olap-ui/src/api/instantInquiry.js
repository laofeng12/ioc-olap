import fetch from '@/utils/fetch'

/**
 * 即席查询相关接口
 */

export function getCubeTreeApi () {
  return fetch({
    url: '/olapweb/olap/apis/olapRealQuery/CubeTree',
    method: 'get'
  })
}

export function getFolderWithQueryApi () {
  return fetch({
    url: '/olapweb/olap/apis/olapRealQuery/folderWithQuery',
    method: 'get'
  })
}

export function getQueryShareApi () {
  return fetch({
    url: '/olapweb/olap/apis/olapRealQuery/queryShare',
    method: 'get'
  })
}

export function searchOlapApi (data) {
  return fetch({
    url: '/olapweb/olap/apis/olapRealQuery/query',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function saveOlapApi (data) {
  return fetch({
    url: '/olapweb/olap/apis/olapRealQuery',
    method: 'post',
    data
  })
}

export function deleteOlapApi (data) {
  return fetch({
    url: '/olapweb/olap/apis/olapRealQuery/delete',
    method: 'post',
    data
  })
}

export function newOlapFolderApi (data) {
  return fetch({
    url: '/olapweb/olap/apis/olapFolder',
    method: 'post',
    data
  })
}

export function getTableByIdApi (id) {
  return fetch({
    url: `/olapweb/olap/apis/olapRealQuery/${id}`,
    method: 'get'
  })
}

export function deleteOlapFolderApi (data) {
  return fetch({
    url: '/olapweb/olap/apis/olapFolder/delete',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function getShareUserApi (params) {
  return fetch({
    url: '/olapweb/olap/apis/olapShare/get',
    method: 'get',
    params
  })
}

export function saveShareApi (params, data) {
  debugger
  return fetch({
    url: '/olapweb/olap/apis/olapShare/save',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    params,
    data
  })
}
