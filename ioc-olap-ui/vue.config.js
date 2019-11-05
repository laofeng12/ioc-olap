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
        target: 'http://219.135.182.2:31075', // 公司测试环境
        // target: 'http://19.104.40.36', // 政务内网
        pathRewrite: { '^/olapweb/admin/': '/admin/' },
        changeOrigin: true
      },
      '/olapweb/gateway/': {
        target: 'http://219.135.182.2:31012', // 公司测试环境
        pathRewrite: { '^/olapweb/gateway/': '/gateway/' }
      },
      '/olapweb/ljdp/': {
        target: 'http://219.135.182.2:31012', // 公司测试环境
        // target: 'http://192.168.8.83:8081', // 中良本机
        pathRewrite: { '^/olapweb/ljdp/': '/ljdp/' },
        changeOrigin: true
      },
      '/olapweb/pds/': {
        target: 'http://219.135.182.2:31013', // 公司测试环境
        pathRewrite: { '^/olapweb/pds/': '/pds/' },
        changeOrigin: true
      },
      '/olapweb/olap/apis': {
        target: 'http://219.135.182.2:30003', // 麒麟测试环境
        changeOrigin: true
      },
      '/olapweb/olap/': {
        target: 'http://219.135.182.2:30003', // 麒麟测试环境
        // target: 'http://192.168.4.211:8081', // 左烨本地
        pathRewrite: { '^/olapweb/olap/': '/olap/' },
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

    config.resolve.alias
    .set('@P', resolve('./node_modules/nhc-portal/packages'))
    }
}
