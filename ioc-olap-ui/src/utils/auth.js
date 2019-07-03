import Cookies from 'js-cookie'
import store from '@/store'
import { setSessionStorage } from '@/utils'
// import { GetQueryString } from './index'

const TokenKey = 'authority-token'

export function getToken () {
  // Cookies上带token进来 或者从本地获取token
  const cookiesToken = Cookies.get(TokenKey)

  cookiesToken && store.dispatch('SetToken', cookiesToken)
  const access_token = cookiesToken || sessionStorage.getItem('token')

  return access_token
}

export function setToken (token) {
  setSessionStorage('token', token)
}

export function removeToken () {
  Cookies.remove(TokenKey)
  sessionStorage.removeItem('token')
}

export function setUserInfo (userInfo) {
  setSessionStorage('userInfo', userInfo)
}

export function removeUserInfo () {
  sessionStorage.removeItem('userInfo')
}
