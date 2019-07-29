import { getFolderWithQueryApi } from '../../api/instantInquiry'

const analysis = {
  state: {
    saveFolderList: []
  },

  mutations: {
    GET_SAVEFOLDERLIST: (state, saveFolderList) => {
      state.saveFolderList = saveFolderList
    }
  },

  actions: {
    getSaveFolderList ({ commit }, userInfo) {
      commit('GET_SAVEFOLDERLIST', userInfo)
      // setUserInfo(userInfo)
    }
  }
}

export default analysis
