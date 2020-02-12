import fetch from '@/utils/fetch'

/**
 * 即席查询相关接口
 */

export function getCubeTreeApi () {
  return fetch({
    url: '/olap/apis/olapRealQuery/CubeTree',
    method: 'get'
  })
}

export function getFolderWithQueryApi () {
  return fetch({
    url: '/olap/apis/olapRealQuery/folderWithQuery',
    method: 'get'
  })
}

export function getQueryShareApi () {
  return fetch({
    url: '/olap/apis/olapRealQuery/queryShare',
    method: 'get'
  })
}

export function searchOlapApi (data) {
  return fetch({
    url: '/olap/apis/olapRealQuery/query',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function saveOlapApi (data) {
  return fetch({
    url: '/olap/apis/olapRealQuery',
    method: 'post',
    data
  })
}

export function deleteOlapApi (data) {
  return fetch({
    url: '/olap/apis/olapRealQuery/delete',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function newOlapFolderApi (data) {
  return fetch({
    url: '/olap/apis/olapFolder',
    method: 'post',
    data
  })
}

export function deleteOlapFolderApi (data) {
  return fetch({
    url: '/olap/apis/olapFolder/delete',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function getShareUserApi (params) {
  return fetch({
    url: '/olap/apis/olapShare/get',
    method: 'get',
    params
  })
}

export function saveShareApi (url, data) {
  return fetch({
    url,
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function searchOlapByIdApi (data) {
  return fetch({
    url: '/olap/apis/olapRealQuery/queryById',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function exportExcelApi (data) {
  return fetch({
    url: '/olap/apis/olapRealQuery/export',
    method: 'post',
    responseType: 'blob',
    contentType: 'application/x-www-form-urlencoded',
    data
  })
}

export function changeSortNumApi (data) {
  return fetch({
    url: '/olap/apis/olapFolder/sortNum/batchUpdate',
    method: 'post',
    data
  })
}
