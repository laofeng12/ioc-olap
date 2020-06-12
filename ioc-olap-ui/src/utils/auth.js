import Cookies from 'js-cookie'
import store from '@/store'
import { setSessionStorage } from '@/utils'
import { mySessionStorage } from './sessionStorage.js'
// import { GetQueryString } from './index'

const TokenKey = 'authority-token'

export function getToken () {
  // Cookies上带token进来 或者从本地获取token
  const cookiesToken = Cookies.get(TokenKey)

  cookiesToken && store.dispatch('SetToken', cookiesToken)
  const access_token = cookiesToken || localStorage.getItem('token')

  return access_token
}

export function setToken (token) {
  setSessionStorage('token', token)
}

export function removeToken () {
  Cookies.remove(TokenKey)
  localStorage.removeItem('token')
}

export function getUserInfo () {
  return mySessionStorage.getItem('userInfo')
}

export function setUserInfo (userInfo) {
  mySessionStorage.setItem('userInfo', userInfo)
}

export function removeUserInfo () {
  Cookies.remove('userInfo')
  mySessionStorage.removeItem('userInfo')
}
