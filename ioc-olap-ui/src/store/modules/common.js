import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'
import { reduceObj } from '@/utils/index'

const common = {
  state: {
    treeList: [], // 树形数据
    serchTableList: [], // 获取的表数据
    searchType: 1, // 判断在数据湖还是本地 1 数据湖 2 本地
    saveSelectTable: [], // 数据湖选择的表
    saveLocalSelectTable: [], // 本地选择的表
    selectTableTotal: [], // 已选择的总表
    lastClickTab: '', // 存储最后一次点击的tabID
    saveSelctchckoutone: [],
    saveSelctchckouttwo: [],
    saveSelectFiled: [], // 存储已选择的维度
    saveSelectFiledTree: [] // 存储已选择的左侧维度菜单
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
    LSATCLICK_TAB: (state, data) => {
      state.lastClickTab = data
    },
    CHANGE_SERACHTYPE: (state, val) => {
      state.searchType = val
    },
    SETSELCT_TABLE_COUNT: (state, val) => {
      state.selectTableTotal = val
    },
    SAVESELECT_ONE: (state, val) => {
      state.saveSelctchckoutone = val
    },
    SAVESELECT_TWO: (state, val) => {
      state.saveSelctchckouttwo = val
    }
  },
  actions: {
    resetList ({ state }) {
      state.treeList = []
      state.serchTableList = []
      state.searchType = 1
      state.saveSelectTable = []
      state.saveLocalSelectTable = []
      state.selectTableTotal = []
      state.lastClickTab = ''
      state.saveSelctchckoutone = []
      state.saveSelctchckouttwo = []
      state.saveSelectFiled = []
      state.saveSelectFiledTree = []
    },
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
    // 存储已选择复选框
    saveSelctchckoutone ({ commit, state, dispatch }, data) {
      commit('SAVESELECT_ONE', data)
      state.saveSelectTable = []
      dispatch('setSelectTableTotal')
    },
    saveSelctchckouttwo ({ commit }, data) {
      commit('SAVESELECT_TWO', data)
    },
    // 存储数据源选择的表
    setSerchTable ({ commit }, data) {
      commit('SET_SERCHTABLE_LIST', data)
    },
    setLastClickTab ({ commit }, nodeId) {
      commit('LSATCLICK_TAB', nodeId)
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
    setSelectTableTotal ({ commit, state }) {
      let totalData = [...state.saveSelectTable, ...state.saveLocalSelectTable]
      commit('SETSELCT_TABLE_COUNT', totalData)
    },
    // 存储已选择的维度
    saveSelectFiled ({ state }, data) {
      let datas = reduceObj(state.saveSelectFiled.concat(data), 'comment')
      state.saveSelectFiled = datas
    },
    // 删除取消的selct
    removeSelectFiled ({ state }, data) {
      let datas = reduceObj(state.saveSelectFiled, 'comment') // 去重对象
      datas && datas.forEach((item, index) => {
        if (item.comment === data.comment) {
          datas.splice(index, 1)
        }
      })
      state.saveSelectFiled = datas
    },
    // 存储已选择的维度表
    saveSelectFiledTree ({ state }, data) {
      state.saveSelectFiledTree = data
    },
    // 合并设置的事实表到总表
    mergeFiledTable ({ state, dispatch }, data) {
      state.selectTableTotal.forEach((item, index) => {
        data[0].label === item.label ? state.selectTableTotal[index]['filed'] = 1 : state.selectTableTotal[index]['filed'] = 0
      })
      dispatch('setSelectTableTotal')
    }
  }
}

export default common
