import { getFolderWithQueryApi } from '../../api/instantInquiry'

export const analysis = {
  state: {
    saveFolderList: [],
    cubeId: '',
    newValueList: [],
    newFilterList: [],
    newRowList: [],
    newColList: []
  },

  mutations: {
    GET_SAVE_FOLDER_LIST: (state, saveFolderList) => {
      state.saveFolderList = saveFolderList
    },
    GET_CUBE_ID: (state, cubeId) => {
      state.cubeId = cubeId
    },
    GET_NEW_VALUE_LIST: (state, newValueList) => {
      state.newValueList = newValueList
    },
    GET_NEW_FILTER_LIST: (state, newFilterList) => {
      state.newFilterList = newFilterList
    },
    GET_NEW_ROW_LIST: (state, newRowList) => {
      state.newRowList = newRowList
    },
    GET_NEW_COL_LIST: (state, newColList) => {
      state.newColList = newColList
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
    },
    getCubeIdAction ({ commit }, cubeId) {
      commit('GET_CUBE_ID', cubeId)
    },
    getNewValueListAction ({ commit }, list) {
      commit('GET_NEW_VALUE_LIST', list)
    },
    getNewFilterListAction ({ commit }, list) {
      commit('GET_NEW_FILTER_LIST', list)
    },
    getNewRowListAction ({ commit }, list) {
      commit('GET_NEW_ROW_LIST', list)
    },
    getNewColListAction ({ commit }, list) {
      commit('GET_NEW_COL_LIST', list)
    }
  }
}

export default analysis
