import CryptoJS from 'crypto-js'

// 加密
export function encrypt (word) {
  var key = CryptoJS.enc.Utf8.parse('OeLGqDdTTayXtH88')
  var srcs = CryptoJS.enc.Utf8.parse(word)
  var encrypted = CryptoJS.AES.encrypt(srcs, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 })
  return encrypted.toString()
}
// 解密
export function decrypt (word) {
  var key = CryptoJS.enc.Utf8.parse('OeLGqDdTTayXtH88')
  var decrypt = CryptoJS.AES.decrypt(word, key, { mode: CryptoJS.mode.ECB, padding: CryptoJS.pad.Pkcs7 })
  return CryptoJS.enc.Utf8.stringify(decrypt).toString()
}
