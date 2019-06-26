import { request, requestJson } from '@/utils/request'

// 服务列表
export function getCatalogs (params) {
  return request({
    url: '/category/services/spServiceCatalog/catalogs',
    method: 'GET',
    params
  })
}

// 根据id获取服务
export function spServiceCatalog (id) {
  return request({
    url: '/category/services/spServiceCatalog/' + id,
    method: 'GET'
  })
}

// 服务市场
export function getCatalogMatket (params) {
  return request({
    url: '/category/services/spServiceCatalog/catalogMatket',
    method: 'GET',
    params
  })
}

// 创建服务
export function createServiceCatalog (data) {
  return requestJson({
    url: '/category/services/spServiceCatalog',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 删除服务
export function deleteServe (data) {
  return request({
    url: '/category/services/spServiceCatalog/delete',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 批量删除
export function removeServe (data) {
  return request({
    url: '/category/services/spServiceCatalog/remove',
    method: 'post',
    data
  })
}

// 修改服务
export function editServe (data) {
  return requestJson({
    url: '/category/services/spServiceCatalog/edit',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 共享服务
export function publishServe (data) {
  return request({
    url: '/category/services/spServiceCatalog/publish',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 取消共享
export function unpublishServe (data) {
  return request({
    url: '/category/services/spServiceCatalog/unpublish',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 关联sso凭证
export function getlistSso () {
  return request({
    url: '/category/services/spServiceCatalog/listSso',
    method: 'GET'
  })
}

// 服务订阅审核列表
export function waitCatalogSubs (params) {
  return request({
    url: '/gateway/service/spCatalogSubscribe/waitCatalogSubs',
    method: 'GET',
    params
  })
}

// 我的订阅服务列表
export function myCatalogSubs (params) {
  return request({
    url: '/gateway/service/spCatalogSubscribe/myCatalogSubs',
    method: 'GET',
    params
  })
}

// 订阅
export function orderCatalog (data) {
  return request({
    url: '/gateway/service/spCatalogSubscribe/orderCatalog',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 订阅审核不通过
export function subAuditNotPassService (data) {
  return request({
    url: '/gateway/service/spCatalogSubscribe/subAuditNotPass',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 订阅审核通过
export function subAuditPassService (data) {
  return request({
    url: '/gateway/service/spCatalogSubscribe/subAuditPass',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 取消订阅
export function unorderCatalogService (data) {
  return request({
    url: '/gateway/service/spCatalogSubscribe/unorderCatalog',
    method: 'post',
    contentType: 'application/json',
    data
  })
}

// 我的服务下拉列表
export function getcatalogOption () {
  return requestJson({
    url: '/category/services/spServiceCatalog/catalogOption',
    method: 'post',
    contentType: 'application/json'
  })
}

// 服务发布审核列表
export function getallCatalogs (params) {
  return request({
    url: '/category/services/spServiceCatalog/allCatalogs',
    method: 'GET',
    params
  })
}

// 服务发布审核 ---- 通过
export function releasePass (data) {
  return request({
    url: '/category/services/spServiceCatalog/releasePass',
    method: 'post',
    data
  })
}

// 服务发布审核 ---- 不通过
export function releaseNotPass (data) {
  return request({
    url: '/category/services/spServiceCatalog/releaseNotPass',
    method: 'post',
    data
  })
}

// 服务统计
export function gecatalogReport (params) {
  return request({
    url: '/category/services/spServiceCatalog/catalogReport',
    method: 'GET',
    params
  })
}
