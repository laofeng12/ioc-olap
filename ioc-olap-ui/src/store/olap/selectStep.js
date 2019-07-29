import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'
import { filterArr, reduceObj, setLocalStorage, getLocalStorage } from '@/utils/index'
import Vue from 'vue'

const selectStep = {
  state: {
    treeList: [], // 树形数据
    serchTableList: [], // 获取的表数据
    searchType: 1, // 判断在数据湖还是本地 1 数据湖 2 本地
    saveSelectTable: [], // 数据湖选择的表
    saveLocalSelectTable: [], // 本地选择的表
    selectTableTotal: [], // 已选择的总表
    lastClickTab: '', // 存储最后一次点击的tabID
    saveSelctchckoutone: [],
    saveSelctchckouttwo: []
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
    // 获取第一步树列表
    GetTreeList ({ commit }) {
      return new Promise((resolve, reject) => {
        getResourcedirectoryCategory().then(res => {
          commit('GET_TREELIST', res)
          resolve(res)
        })
      })
    },
    // 根据树的id获取对应的表
    GetSerchTable ({ commit }, id) {
      return new Promise((resolve, reject) => {
        getResourcedirectory(id).then(res => {
          commit('GET_SERCHTABLE_LIST', res)
          resolve(res)
        })
      })
    },
    // 根据字段获取表头
    GetColumnList ({ commit }, params) {
      return new Promise((resolve, reject) => {
        getColumnList(params).then(res => {
          resolve(res)
        })
      })
    },
    // 根据字段获取表体
    GetTableData ({ commit }, params) {
      return new Promise((resolve, reject) => {
        getTableData(params).then(res => {
          resolve(res)
        })
      })
    },
    // 更新数据表
    GetdsUploadTable ({ commit }, params) {
      return new Promise((resolve, reject) => {
        getdsUploadTable(params).then(res => {
          resolve(res)
        })
      })
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
    // 设置已选择的表的总数据
    setSelectTableTotal ({ commit, state }) {
      let totalData = [...state.saveSelectTable, ...state.saveLocalSelectTable]
      commit('SETSELCT_TABLE_COUNT', totalData)
    }
  }
}

export default selectStep
