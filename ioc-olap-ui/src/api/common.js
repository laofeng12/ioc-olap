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
