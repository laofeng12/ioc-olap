import fetch from '@/utils/fetch'

export function getApiList (params) {
  return fetch({
    url: '/platformweb/gateway/apis/spInstanceApi/search',
    method: 'GET',
    params
  })
}

// 获取组织机构树列表
export function getMechanismTree (params) {
  return fetch({
    url: '/platformweb/admin/org/sysOrg/doZTreeToElement',
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
