import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'

const common = {
  state: {
    treeList: [],
    serchTableList: [],
    searchType: 1
  },
  mutations: {
    GET_TREELIST: (state, data) => {
      state.treeList = data
    },
    GET_SERCHTABLE_LIST: (state, data) => {
      state.serchTableList = data
    },
    SET_SERCHTABLE_LIST: (state, data) => {
      state.serchTableList = data
    },
    CHANGE_SERACHTYPE: (state, val) => {
      state.searchType = val
    }
  },
  actions: {
    async GetTreeList ({ commit }) {
      let data = await getResourcedirectoryCategory()
      commit('GET_TREELIST', data)
      return data
    },
    async GetSerchTable ({ commit }, id) {
      let data = await getResourcedirectory(id)
      commit('GET_SERCHTABLE_LIST', data)
      return data
    },
    async GetColumnList ({ commit }, params) {
      let data = await getColumnList(params)
      return data
    },
    async GetTableData ({ commit }, params) {
      let data = await getTableData(params)
      return data
    },
    async GetdsUploadTable ({ commit }, params) {
      let data = await getdsUploadTable(params)
      return data
    },
    setSerchTable ({ commit }, data) {
      commit('SET_SERCHTABLE_LIST', data)
    },
    changeSerachtype ({ commit }, val) {
      commit('CHANGE_SERACHTYPE', val)
    }
  }
}

export default common
