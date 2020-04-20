import fetch from '@/utils/fetch'

/**
 * 获取菜单列表
 */
export function getMenuList () {
  return fetch({
    url: '/admin/res/sysRes/v2/myResources?systemids=10000082780009',
    method: 'get'
  })
}

/**
 * 获取服务目录&&第三方服务
 */
export function getCatalogMenu (params) {
  return fetch({
    url: '/category/services/spServiceCatalog/getCatalogMenu',
    method: 'GET',
    params
  })
}
