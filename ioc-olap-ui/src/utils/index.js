export const WEBSOCKET_TOKEN_PREFIX = 'bearer.authorization.platform.dgzsj.com'

export function removeSessionStorage (item) {
  sessionStorage.removeItem(item)
}

export function removeLocalStorage (item) {
  localStorage.removeItem(item)
}

export function setSessionStorage (item, value) {
  let stringValue = typeof value === 'string' ? value : JSON.stringify(value)
  return sessionStorage.setItem(item, stringValue)
}

export function setLocalStorage (item, value) {
  let stringValue = typeof value === 'string' ? value : JSON.stringify(value)
  return localStorage.setItem(item, stringValue)
}

export function getSessionStorage (item) {
  return sessionStorage.getItem(item)
}

export function GetQueryString (name) {
  var reg = new RegExp('(^|&)' + name + '=([^&]*)(&|$)')
  var r = window.location.search.substr(1).match(reg) // search,查询？后面的参数，并匹配正则
  if (r != null) return unescape(r[2]); return null
}

/*
 防抖，防止快速重复点击
 */
export function throttle (fn, wait = 500, scope) {
  clearTimeout(throttle.timer)
  throttle.timer = setTimeout(function () {
    fn.apply(scope)
  }, wait)
}

// 图片转为base64
export function getBase64 (file) {
  return new Promise(function (resolve, reject) {
    let reader = new FileReader()
    let imgResult = ''
    reader.readAsDataURL(file)
    reader.onload = function () {
      imgResult = reader.result
    }
    reader.onerror = function (error) {
      reject(error)
    }
    reader.onloadend = function () {
      resolve(imgResult)
    }
  })
}

// json格式化 显示到html
export function syntaxHighlight (json) {
  if (typeof json !== 'string') {
    json = JSON.stringify(json, undefined, 2)
  }
  json = json.replace(/&/g, '&').replace(/</g, '<').replace(/>/g, '>')
  return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
    var cls = 'number'
    if (/^"/.test(match)) {
      if (/:$/.test(match)) {
        cls = 'key'
      } else {
        cls = 'string'
      }
    } else if (/true|false/.test(match)) {
      cls = 'boolean'
    } else if (/null/.test(match)) {
      cls = 'null'
    }
    // return '<span class="' + cls + '">' + match + '</span>' // v-html
    return match
  })
}

// 下拉滚动加载
export function scrollHandleModel (flag, currentPage, getData, pageSize, more, tableData, callback) {
  // 获取页面页面的滚动高度
  let scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight
  // 获取页面滚动距离顶部的高度,  window.pageYOffse 兼容苹果
  let scrollTop = document.documentElement.scrollTop || window.pageYOffset || document.body.scrollTop
  // 获取页面的可视高度
  let clientHeight = document.documentElement.clientHeight || document.body.clientHeight
  // 我们可以在这里判断页面的滚动是否到了底部
  if (scrollTop + clientHeight === scrollHeight) {
    console.log('到达了！！！')
    if (flag) return
    flag = true
    currentPage++ // 加载下一页
    // 根据noMore 看是否还有数据  默认为false
    console.log(tableData)
    if (getData.length < pageSize) more = false
    if (getData.length > 0) {
      // 滚动到底部执行数据加载
      callback && callback()
      flag = false
      if (tableData) {
      } else {
        tableData = []
        more = false
      }
    } else {
      flag = true
      more = false
    }
  }
  // 当然我们也可以滚动距离底部还有一定距离的时候执行加载
  if (scrollTop + clientHeight >= scrollHeight - 20) {
    // 距离底部还有20的时候执行数据加载
    flag = false
  }
}

// 状态显示判断
export function statusReviewFilter (status, type) {
  // 共享状态
  const statusMap1 = {
    0: 'red',
    1: 'green'
  }
  // 审核状态
  const statusMap2 = {
    1: 'red',
    2: 'green',
    3: 'block'
  }
  // 创建状态
  const statusMap3 = {
    0: 'red',
    1: 'green'
  }
  if (type === 1) {
    return statusMap1[status]
  }
  if (type === 2) {
    return statusMap2[status]
  }
  if (type === 3) {
    return statusMap3[status]
  }
}

// 获取当前月份第一天
export function getCurrentMonthFirstDay () {
  var date = new Date()
  date.setDate(1)
  var month = parseInt(date.getMonth() + 1)
  var day = date.getDate()
  if (month < 10) {
    month = '0' + month
  }
  if (day < 10) {
    day = '0' + day
  }
  return date.getFullYear() + '-' + month + '-' + day + ' ' + '00:00:00'
}

// 获取当前月份最后一天
export function getCurrentMonthLastDay () {
  var date = new Date()
  var year = date.getFullYear()
  var month = date.getMonth() + 1
  month = month < 10 ? '0' + month : month
  var day = new Date(year, month, 0)
  return year + '-' + month + '-' + day.getDate() + ' ' + '23:59:59'
}

// 获取近七天日期
export function getsevenData (count) {
  // 拼接时间
  const time1 = new Date()
  const time2 = new Date()
  if (count === 1) {
    time1.setTime(time1.getTime() - (24 * 60 * 60 * 1000))
  } else {
    if (count >= 0) {
      time1.setTime(time1.getTime())
    } else {
      if (count === -2) {
        time1.setTime(time1.getTime() + (24 * 60 * 60 * 1000) * 2)
      } else {
        time1.setTime(time1.getTime() + (24 * 60 * 60 * 1000))
      }
    }
  }

  const Y1 = time1.getFullYear()
  const M1 = ((time1.getMonth() + 1) > 9 ? (time1.getMonth() + 1) : '0' + (time1.getMonth() + 1))
  const D1 = (time1.getDate() > 9 ? time1.getDate() : '0' + time1.getDate())
  const timer1 = Y1 + '-' + M1 + '-' + D1 + ' ' + '23:59:59' // 当前时间

  time2.setTime(time2.getTime() - (24 * 60 * 60 * 1000 * count))
  const Y2 = time2.getFullYear()
  const M2 = ((time2.getMonth() + 1) > 9 ? (time2.getMonth() + 1) : '0' + (time2.getMonth() + 1))
  const D2 = (time2.getDate() > 9 ? time2.getDate() : '0' + time2.getDate())
  const timer2 = Y2 + '-' + M2 + '-' + D2 + ' ' + '00:00:00' // 之前的7天或者30天
  return [timer2, timer1]
}
