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
 * 资源目录分类查询
 * @param {object} params
 *
 */
export function getResourcedirectoryCategory (params) {
  return fetch({
    url: '/pds/datasource/dsResourcedirectoryCategory/search',
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
