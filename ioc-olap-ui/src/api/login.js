import fetch from '@/utils/fetch'

/**
 * 登录admin项目
 * @param {string} userAccount
 * @param {string} userPwd
 */
export function login (userAccount, userPwd) {
  return fetch({
    url: '/admin/user/sysUser/v2/login',
    method: 'post',
    contentType: 'application/x-www-form-urlencoded',
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
  return fetch({
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
  return fetch({
    url: '/admin/user/OA/loginOut',
    method: 'post'
  })
}

export function getUserListApi (params) {
  return fetch({
    url: '/admin/user/sysUser/search',
    method: 'get',
    params
  })
}
