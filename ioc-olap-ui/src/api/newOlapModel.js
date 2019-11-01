import fetch from '@/utils/fetch'

// 资源目录列表（数据湖、数据集、自定义、本地上传）
export function getCatalog (params) {
  return fetch({
    url: '/pds/resource/catalog/',
    method: 'get',
    params
  })
}
// 获取数据湖资源目录树
export function getDtalakeTreeList ({ orgId, databaseType }) {
  return fetch({
    url: `/pds/resource/catalog/dataLakeTree/${orgId}-${databaseType}`,
    method: 'get'
  })
}
// 获取资源列表
export function getResourceList ({ orgId, type, databaseType }) {
  return fetch({
    url: `/pds/resource/catalog/resourceList/${orgId}-${type}-${databaseType}`,
    method: 'get'
  })
}
// 获取资源数据
export function getResourceData (params) {
  return fetch({
    url: `/pds/resource/catalog/resourceData/${params.resourceId}/${params.type}-${params.databaseId}`,
    method: 'post',
    data: params
  })
}
// 获取资源信息
export function getResourceInfo ({ resourceId, type, databaseId, isOnlyPermitted }) {
  return fetch({
    url: `/pds/resource/catalog/resourceInfo/${resourceId}/${type}-${databaseId}-${isOnlyPermitted}`,
    method: 'get'
  })
}
// 批量创建同步任务
export function batchCreateJob (params) {
  return fetch({
    url: `/pds/datasync/dataSync/batchCreateJob`,
    method: 'post',
    data: params
  })
}


