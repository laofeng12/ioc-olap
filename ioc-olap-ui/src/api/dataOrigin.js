import fetch from '@/utils/fetch'

export function searchApi () {
  return fetch({
    url: '/dts/dtsDatasource/findAll',
    method: 'get'
  })
}

export function linkTestApi (data) {
  return fetch({
    url: '/dts/dtsDatasource/linkTest',
    method: 'post',
    data
  })
}

export function getTreeApi () {
  return fetch({
    url: '/dts/dtsDatasource/getTree',
    method: 'get'
  })
}

export function doSaveApi (data) {
  return fetch({
    url: '/dts/dtsDatasource/doSave',
    method: 'post',
    data
  })
}
