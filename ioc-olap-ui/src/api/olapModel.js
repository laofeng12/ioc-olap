import fetch from '@/utils/fetch'

export function getApiList (params) {
  return fetch({
    url: '/gateway/apis/spInstanceApi/search',
    method: 'GET',
    params
  })
}

// 获取组织机构树列表
export function getMechanismTree (params) {
  return fetch({
    url: '/admin/org/sysOrg/doZTreeToElement',
    method: 'get',
    params
  })
}

/**
 * 资源目录分类查询(树)
 * @param {object} params
 *
 */
// 获取资源目录树（数据湖+数据集）
export function getTreeoneList (params) {
  return fetch({
    url: '/pds/datalake/dataLake/allResourceTree',
    method: 'get',
    params
  })
}
// 获取数据湖资源目录树
export function getTreetwoList (obj) {
  return fetch({
    url: `/pds/datalake/dataLake/dataLakeResourceTree/${obj.orgId}-${obj.databaseType}`,
    method: 'get'
  })
}
// 查询资源列表（数据湖+数据集）
export function getTreethreeList (obj) {
  return fetch({
    url: `/pds/datalake/dataLake/resourceList/${obj.orgId}-${obj.type}-${obj.databaseType}`,
    method: 'get'
  })
}
// 获取资源信息
export function getResourceInfo (obj) {
  return fetch({
    url: `/pds/datalake/dataLake/resourceInfo/${obj.resourceId}-${obj.type}`,
    method: 'get'
  })
}
// 资源数据查询
export function getResourceData (data, obj) {
  return fetch({
    url: `/pds/datalake/dataLake/resourceData/${obj.resourceId}-${obj.type}`,
    method: 'post',
    data
  })
}

// --------------------------------------------------

export function getResourcedirectoryCategory (params) {
  return fetch({
    url: '/pds/datalake/dataLake/allResourceTree',
    // url: '/pds/datasource/dsResourcedirectoryCategory/search',
    method: 'get',
    params
  })
}

/**
 * 根据id获取列表
 * @param String params
 */
export function getResourcedirectory (id) {
  return fetch({
    url: '/pds/datasource/dsResourcedirectory/' + id,
    method: 'get'
  })
}

/**
 * 获取数据源表的列名及备注
 * @param {Object} params
 */
export function getColumnList (params) {
  return fetch({
    url: '/pds/datasource/dsDataSource/getColumnList',
    method: 'get',
    params
  })
}

/**
 * 获取列名称table表数据
 * @param {Object} params
 */
export function getTableData (params) {
  return fetch({
    url: '/pds/dataset/dsDataSet/getTableData',
    method: 'get',
    params
  })
}

/**
 * 本地上传左侧数据
 * @param {Object} params
 */
export function getdsUploadTable (params) {
  return fetch({
    url: '/pds/dataset/dsUploadTable/search',
    method: 'get',
    params
  })
}

/**
 * 保存步骤
 * @param { Object } param
 */
export function saveolapModeldata (data) {
  return fetch({
    url: '/olap/apis/OlapModeling/createModeling',
    method: 'post',
    contentType: 'application/json;charset=UTF-8',
    data
  })
}

// 模型列表
export function getModelDataList (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/cubeList',
    method: 'get',
    params
  })
}

// 获取Encoding
export function getEncodingList () {
  return fetch({
    url: '/olap/apis/OlapModeling/encodingList',
    method: 'get'
  })
}

// kelin
export function getselectCatalog (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/selectCatalog',
    method: 'get',
    params
  })
}
export function getselectTable (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/selectTable',
    method: 'get',
    params
  })
}
export function getselectColumn (params) {
  return fetch({
    url: '/olap/apis/OlapModeling/selectColumn?resourceIds=' + params,
    method: 'get'
  })
}
