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
export function getTreetwoList (orgId, databaseType) {
  return fetch({
    url: `/pds/datalake/dataLake/dataLakeResourceTree/${orgId}-${databaseType}`,
    method: 'get'
  })
}
// 查询资源列表（数据湖+数据集
export function getTreethreeList (orgId, type, databaseType) {
  return fetch({
    url: `/pds/datalake/dataLake/resourceList/${orgId}-${type}-${databaseType}`,
    method: 'get'
  })
}
// 获取资源信息
export function getResourceInfo (resourceId, type) {
  return fetch({
    url: `/pds/datalake/dataLake/resourceList/${resourceId}-${type}`,
    method: 'get'
  })
}
// 资源数据查询
export function getResourceData (data, resourceId, type) {
  return fetch({
    url: `/pds/datalake/dataLake/resourceList/${resourceId}-${type}`,
    method: 'post',
    data
  })
}

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
