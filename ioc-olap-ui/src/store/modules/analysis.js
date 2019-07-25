import { login, logout } from '@/api/login'

const defaultAvatar = require('@/assets/img/administrator.png')

const analysis = {
  state: {
    folderList: []
  },

  mutations: {
    GET_FOLDERLIST: (state, folderList) => {
      state.folderList = folderList
    }
  },

  actions: {
    GetFolderList ({ commit, dispatch }, userInfo) {
      commit('GET_FOLDERLIST', userInfo)
      setUserInfo(userInfo)
    }
  }
}

export default analysis
