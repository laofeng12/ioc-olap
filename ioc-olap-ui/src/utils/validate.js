/**
 * Created by jiachenpan on 16/11/18.
 */

export function isvalidUsername (str) {
  const valid_map = ['admin', 'editor']
  return valid_map.indexOf(str.trim()) >= 0
}

export function isExternal (path) {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export function isCharCodeAt (rule, value, callback) {
  let code = value.charCodeAt(0)
  if ((code >= 65 && code <= 90) || (code >= 97 && code <= 122)) {
    callback()
  } else {
    callback(new Error('首字符必须为字母开头'))
  }
}

export function isSpecialCharacter (rule, value, callback) {
  // let pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]") // 完整的
  let pattern = new RegExp("[`~@#$^*()|{}';'《》\\[\\]<>~@￥……*（）——|{}【】‘；”“']") // 兼容url的
  // if (!value.match(/^[a-zA-Z0-9_\u4e00-\u9fa5]+$/)) {
  if (pattern.test(value)) {
    callback(new Error('请勿输入特殊字符'))
  } else {
    callback()
  }
}

export function ishttporhttps (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!String(value).match(/(http|https):\/\/([\w.]+\/?)\S*/)) { // 需要协议
    callback(new Error('请输入http://或者https://开头的地址'))
  } else {
    callback()
  }
}

export function isnoContext (rule, value, callback) {
  if (!value) {
    callback()
  } else if (value.match(/^[\u4e00-\u9fa5]{0,}$/)) {
    callback(new Error('请勿输入中文字符'))
  } else {
    callback()
  }
}

export function isNumber (rule, value, callback) {
  console.log(value)
  if (!value) {
    callback()
  } else if (!String(value).match(/^[0-9]+$/)) {
    callback(new Error('请输入数字'))
  } else {
    callback()
  }
}

export function isIP (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!value.match(/^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/)) {
    callback(new Error('请输入合理的IP地址'))
  } else {
    callback()
  }
}

export function isServiceDomain (rule, value, callback) {
  if (!value) {
    callback()
  } else if (!value.match(/^(?!-)(?!.*-$)/)) {
    callback(new Error('不能以(-)开头或结尾'))
  } else if (value.indexOf('--') !== -1) {
    callback(new Error('不能出现相邻的(--)'))
  } else if (value.length < 3 || value.length > 63) {
    callback(new Error('长度范围为3到63个字符'))
  } else {
    callback()
  }
}
