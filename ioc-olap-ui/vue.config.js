// const baseUrl = process.env.NODE_ENV === 'production' ? '/platformweb' : '/platformweb'
const baseUrl = '/platformweb'
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
        target: 'http://183.6.55.26:31075', // 公司测试环境
        pathRewrite: { '^/platformweb/admin/': '/admin/' },
        changeOrigin: true
      },
      '/platformweb/gateway/': {
        target: 'http://183.6.55.26:31012', // 公司测试环境
        pathRewrite: { '^/platformweb/gateway/': '/gateway/' }
      },
      '/platformweb/ljdp/': {
        target: 'http://183.6.55.26:31012', // 公司测试环境
        // target: 'http://192.168.8.83:8081', // 中良本机
        pathRewrite: { '^/platformweb/ljdp/': '/ljdp/' },
        changeOrigin: true
      }
    }
  },
  // chainWebpack: config => {
  //   // 一个规则里的 基础Loader
  //   // svg是个基础loader
  //   const svgRule = config.module.rule('svg')

  //   // 清除已有的所有 loader。
  //   // 如果你不这样做，接下来的 loader 会附加在该规则现有的 loader 之后。
  //   svgRule.uses.clear()

  //   // 添加要替换的 loader
  //   svgRule
  //     .use('svg-sprite-loader')
  //     .loader('svg-sprite-loader')
  //     .options({
  //       symbolId: 'icon-[name]'
  //     })
  // }
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
