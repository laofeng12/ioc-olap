module.exports = {
  root: true,
  env: {
    node: true
  },
  'extends': [
    'plugin:vue/essential',
    '@vue/standard'
  ],
  rules: {
    'no-console': 'off',
    'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off',
    'camelcase': 0,
    'vue/no-use-v-if-with-v-for': 'off',
    "no-unused-expressions": 1
  },
  parserOptions: {
    parser: 'babel-eslint'
  }
}
