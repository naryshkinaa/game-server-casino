const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true,
  // baseUrl:  '/',
  // devServer: {
  //   proxy: {
  //     '/connect': {
  //       target: 'http://localhost:8080/connect',
  //       changeOrigin: true
  //     },
  //     '/api': {
  //       target: 'http://localhost:8080',
  //       changeOrigin: true
  //     }
  //   }
  // }
  devServer: {
    proxy: 'http://localhost:8080'
  }
})

