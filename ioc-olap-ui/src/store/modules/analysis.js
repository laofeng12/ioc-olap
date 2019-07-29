import { getFolderWithQueryApi } from '../../api/instantInquiry'

export const analysis = {
  state: {
    saveFolderList: []
  },

  mutations: {
    GET_SAVE_FOLDER_LIST: (state, saveFolderList) => {
      state.saveFolderList = saveFolderList
    }
  },

  actions: {
    async getSaveFolderListAction ({ commit }) {
      const res = await getFolderWithQueryApi()
      const list = res.map(v => {
        return (
          { children: v.children, id: v.id, name: v.name, sortNum: v.sortNum }
        )
      })
      commit('GET_SAVE_FOLDER_LIST', list)
    }
  }
}

export default analysis
