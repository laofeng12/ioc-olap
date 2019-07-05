import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'

const common = {
  state: {
    treeList: [],
    serchTableList: [],
    searchType: 1,
    saveSelectTable: []
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
    // 存储数据源选择的表
    setSerchTable ({ commit }, data) {
      commit('SET_SERCHTABLE_LIST', data)
    },
    // 切换数据湖--本地上传控制
    changeSerachtype ({ commit, state }, val) {
      commit('CHANGE_SERACHTYPE', val)
    },
    // 保存已勾选的表
    saveSelectTable ({ state }, data) {
      if (data.id !== 1 || data.id === undefined) {
        state.saveSelectTable.push({
          id: data.id,
          label: data.label
        })
      }
    },
    // 去掉取消勾选的表
    deleteSelectTable ({ state }, data) {
      if (data.id !== 1 || data.id === undefined) {
        state.saveSelectTable.forEach((item, _index) => {
          if (item.id === data.id) {
            state.saveSelectTable.splice(_index, 1)
          }
        })
      }
    }
  }
}

export default common
