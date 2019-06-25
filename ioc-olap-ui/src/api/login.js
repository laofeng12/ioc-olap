import { request, requestJsonHttpCode } from '@/utils/request'

/**
 * 登录admin项目
 * @param {string} userAccount
 * @param {string} userPwd
 */
export function login (userAccount, userPwd) {
  return request({
    url: '/admin/user/sysUser/login',
    method: 'post',
    data: {
      userAccount,
      userPwd
    }
  })
}

/**
 * 登录api/v1项目
 * @param {string} userAccount
 * @param {string} userPwd
 */
export function loginApiV1 (userAccount, userPwd) {
  return requestJsonHttpCode({
    url: '/api/v1/account/login',
    method: 'post',
    data: {
      username: userAccount,
      password: userPwd
    }
  })
}

/**
 * 退出登录
 */
export function logout () {
  return request({
    url: '/admin/user/OA/loginOut',
    method: 'post'
  })
}
