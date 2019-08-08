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

export function getLocalStorage (item) {
  return localStorage.getItem(item)
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

// 去重对象
export function reduceObj (arr, name) {
  let obj = {}
  let arrs = arr.reduce((item, next) => {
    obj[next[name]] ? '' : obj[next[name]] = true && item.push(next)
    return item
  }, [])
  return arrs
}

export function reduceJson (arr, name) {
  let obj = {}
  let arrs = arr.reduce((item, next) => {
    obj[next[name]] ? '' : obj[next[name]] = true && item.push({
      id: next[name],
      dataType: next.dataType,
      name: next.name,
      tableName: next.tableName
    })
    return item
  }, [])
  return arrs
}

// 过滤分类组合---- 选择的维度数据结构
export function filterArrData (data) {
  var map = {}
  var dest = []
  for (var i = 0; i < data.length; i++) {
    var ai = data[i]
    // 根据相同的tableName来进行分类
    if (!map[ai.tableName]) {
      dest.push({
        table: ai.tableName,
        columns: [ai.name]
      })
      map[ai.tableName] = ai
    } else {
      for (var j = 0; j < dest.length; j++) {
        var dj = dest[j]
        if (dj.table === ai.tableName) {
          dj.columns.push(ai.name)
          break
        }
      }
    }
  }
  return dest
}

export function filterArr (data) {
  var map = {}
  var dest = []
  for (var i = 0; i < data.length; i++) {
    var ai = data[i]
    // 根据相同的tableName来进行分类
    if (!map[ai.tableName]) {
      dest.push({
        comment: ai.comment,
        tableName: ai.tableName,
        name: ai.name,
        apiPaths: ai.apiPaths,
        filed: ai.filed,
        list: [ai]
      })
      map[ai.tableName] = ai
    } else {
      for (var j = 0; j < dest.length; j++) {
        var dj = dest[j]
        if (dj.tableName === ai.tableName) {
          dj.list.push(ai)
          break
        }
      }
    }
  }
  return dest
}
