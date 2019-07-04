import { request } from '@/utils/request'

/**
 * 获取菜单列表
 * @param {object} params
 */
export function getMenuList (params) {
  return request({
    url: '/admin/res/sysRes/myResources',
    method: 'get',
    params
  })
}

/**
 * 获取服务目录&&第三方服务
 * @param {object} params
 */
export function getCatalogMenu (params) {
  return request({
    url: '/category/services/spServiceCatalog/getCatalogMenu',
    method: 'GET',
    params
  })
}
