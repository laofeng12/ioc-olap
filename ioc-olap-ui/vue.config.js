// const baseUrl = process.env.NODE_ENV === 'production' ? '/' : '/'
const baseUrl = '/olapweb'
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
      '/olapweb/admin/': {
        target: 'http://183.6.55.26:31075', // 公司测试环境
        // target: 'http://19.104.40.36', // 政务内网
        pathRewrite: { '^/olapweb/admin/': '/admin/' },
        changeOrigin: true
      },
      '/olapweb/gateway/': {
        target: 'http://183.6.55.26:31012', // 公司测试环境
        pathRewrite: { '^/olapweb/gateway/': '/gateway/' }
      },
      '/olapweb/ljdp/': {
        target: 'http://183.6.55.26:31012', // 公司测试环境
        // target: 'http://192.168.8.83:8081', // 中良本机
        pathRewrite: { '^/olapweb/ljdp/': '/ljdp/' },
        changeOrigin: true
      },
      '/olapweb/pds/': {
        target: 'http://183.6.55.26:31013', // 公司测试环境
        pathRewrite: { '^/olapweb/pds/': '/pds/' },
        changeOrigin: true
      },
      '/olapweb/olap/': {
        // target: 'http://19.104.40.36', // 麒麟测试环境
        // target: 'http://172.16.60.82:8081', // 麒麟地址（new）
        target: 'http://172.16.60.82:8081', // 左烨本地
        // target: 'http://19.104.40.36', // 测试服务
        // target: 'http://172.16.60.99:8080', // 沛城本地
        // pathRewrite: { '^/olapweb/olap/': '/olap/' },
        changeOrigin: true
      }
      // '/olapweb/olap': {
      //   target: 'http://172.16.60.99:9090', // 沛辰本机
      //   // pathRewrite: { '^/olapweb/olap/': '/olap/' },
      //   changeOrigin: true
      // }
      // '/olapweb/': {
      //   target: 'http://172.16.60.5:8080', // 宝珠本机
      //   changeOrigin: true
      // }
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
