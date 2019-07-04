// 电话
export function checkPhone (rule, value, callback) {
  const reg = /^1[3-9]\d{9}$/
  if (value && !reg.test(value)) {
    return callback(new Error('目前只支持中国大陆的手机号码'))
  } else {
    callback()
  }
}

// 服务名称校验
export function checkServiceName (rule, value, callback) {
  // 大于3位，小于30位
  const reg = /^[a-zA-Z]([-a-zA-Z0-9]{1,28})[a-zA-Z0-9]$/
  if (value && !reg.test(value)) {
    return callback(new Error('长度为3-30，只可含英文、数字或-符号，英文字母开头，英文字母或数字结尾'))
  } else {
    callback()
  }
  callback()
}

// 服务名称校验
export function isPasswd (rule, value, callback) {
  // 大于3位，小于30位
  if (value && (value.length > 20 || value.length < 6)) {
    return callback(new Error('长度6-20'))
  } else {
    callback()
  }
  callback()
}

export function checkPort (rule, value, callback) {
  if (value <= 1 || value > 65535) {
    callback(new Error('端口必须大于1且小于65535'))
  }
  callback()
}

export function checkGitUrl (rule, value, callback) {
  /* eslint-disable-next-line */
  const reg = /^((((ht|f)tps?):\/\/)|(git@github)|(git@gitee)|(git@git))([\w\-\.,@?^=%&:\/~\+#]+(\.[\w\-\.,@?^=%&:\/~\+#]+)*\/)*[\w\-\.,@?^=%&:\/~\+#]+(\.[\w\-\.,@?^=%&:\/~\+#]+)*\/?(\?([\w\-\.,@?^=%&:\/~\+#]*)+)?\.git$/
  if (value && !reg.test(value)) {
    return callback(new Error('请输入github/码云/gitee仓库地址'))
  } else {
    callback()
  }
}

export default {
  validateServicePort: { validator: checkPort, trigger: 'blur' },
  validateServiceName: { validator: checkServiceName, trigger: 'blur' },
  validateIsPasswd: { validator: isPasswd, trigger: 'blur' },
  validateGitUrl: { validator: checkGitUrl, trigger: 'blur' },
  requiredInput: { required: true, message: '不能为空', trigger: 'blur' },
  requiredChoose: { required: true, message: '不能为空', trigger: ['change', 'blur'] }
}
