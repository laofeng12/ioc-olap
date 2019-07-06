import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'

const common = {
  state: {
    treeList: [], // 树形数据
    serchTableList: [], // 获取的表数据
    searchType: 1, // 判断在数据湖还是本地 1 数据湖 2 本地
    saveSelectTable: [], // 数据湖选择的表
    saveLocalSelectTable: [], // 本地选择的表
    selectTableCount: ''
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
    },
    SETSELCT_TABLE_COUNT: (state, val) => {
      state.selectTableCount = val
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
    // 存储数据湖的数据
    getSelectTableList ({ state }, data) {
      state.saveSelectTable = []
      data.map(item => {
        if (!item.children) {
          state.saveSelectTable.push({
            id: item.id,
            label: item.label
          })
        }
      })
    },
    // 存储本地上传的数据
    getLocalSelectTableList ({ state }, data) {
      state.saveLocalSelectTable = []
      data.map(item => {
        if (!item.children) {
          state.saveLocalSelectTable.push({
            id: item.id,
            label: item.label
          })
        }
      })
    },
    // 设置已选择的表的数据
    setSelectTableCount ({ commit, state }) {
      let totalData = [...state.saveSelectTable, ...state.saveLocalSelectTable].length
      commit('SETSELCT_TABLE_COUNT', totalData)
    }
  }
}

export default common
