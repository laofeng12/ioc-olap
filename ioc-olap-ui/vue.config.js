// const baseUrl = process.env.NODE_ENV === 'production' ? '/platformweb' : '/platformweb'
const baseUrl = '/'
const path = require('path')
function resolve (dir) {
  return path.join(__dirname, '.', dir)
}
module.exports = {
  lintOnSave: false,
  publicPath: baseUrl,
  productionSourceMap: false,
  devServer: {
    proxy: {
      '/platformweb/admin/': {
        target: 'http://192.168.6.104:30003', // 公司测试环境
        pathRewrite: { '^/platformweb/admin/': '/admin/' },
        changeOrigin: true
      },
      '/platformweb/gateway/': {
        target: 'http://192.168.6.104:30003', // 公司测试环境
        pathRewrite: { '^/platformweb/gateway/': '/gateway/' }
      },
      '/platformweb/ljdp/': {
        target: 'http://192.168.6.104:30003', // 公司测试环境
        // target: 'http://192.168.8.83:8081', // 中良本机
        pathRewrite: { '^/platformweb/ljdp/': '/ljdp/' },
        changeOrigin: true
      },
      '/pds/': {
        target: 'http://192.168.6.104:30003', // 公司测试环境
        pathRewrite: { '^/pds/': '/pds/' },
        changeOrigin: true
      }
    }
  },
  chainWebpack: config => {
    config.module
      .rule('svg')
      .exclude.add(resolve('src/icons'))
      .end()

    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(resolve('src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })
  }
}
