import { getFolderWithQueryApi } from '../../api/instantInquiry'

export const analysis = {
  state: {
    saveFolderList: [],
    cubeData: {},
    newValueList: [],
    newFilterList: [],
    newRowList: [],
    newColList: []
  },

  mutations: {
    GET_SAVE_FOLDER_LIST: (state, saveFolderList) => {
      state.saveFolderList = saveFolderList
    },
    GET_CUBE_DATA: (state, cubeData) => {
      state.cubeData = cubeData
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
      let list = []
      res.forEach(v => {
        const attrs = Object.assign({}, v.attrs, { canDrop: true })
        const obj = {
          attrs,
          children: v.children,
          id: v.id,
          name: v.name,
          virtualTableName: v.virtualTableName
        }
        list.push(obj)
      })
      commit('GET_SAVE_FOLDER_LIST', list)
    },
    getCubeDataAction ({ commit }, cubeData) {
      commit('GET_CUBE_DATA', cubeData)
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
