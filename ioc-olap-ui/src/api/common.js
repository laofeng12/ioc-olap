import { request, requestJsonHttpCode, requestUpload } from '@/utils/request'

// 字典查询
export async function getBaseData (params) {
  return request({
    url: '/framework/sys/code/list2',
    method: 'GET',
    params
  })
}

// 上传文件
export async function uploadFile (data) {
  return requestUpload({
    url: '/ljdp/attach/memory/upload.act',
    method: 'post',
    data
  })
}
/**
 * 获取镜像列表
 * @param {object} params
 */
export async function getImagesList (params) {
  return requestJsonHttpCode({
    url: '/api/v1/images',
    method: 'GET',
    params
  })
}

/**
 * 获取跳转code
 */
export function getLinkCode (data) {
  return request({
    url: '/admin/user/Oauth2/getcode',
    method: 'POST',
    data
  })
}

// 导出
export async function exportsFn (params) {
  return requestJsonHttpCode({
    url: '/category/services/spServiceCatalog/export',
    method: 'GET',
    params
  })
}

// 对接重试获取
export async function retrys (data) {
  return request({
    url: '/category/services/spServiceCatalog/retry',
    method: 'post',
    data
  })
}

export function getApiList (params) {
  return request({
    url: '/gateway/apis/spInstanceApi/search',
    method: 'GET',
    params
  })
}
