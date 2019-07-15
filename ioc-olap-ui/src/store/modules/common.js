import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'
import { filterArr } from '@/utils/index'
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
    /* 维度 */
    saveSelectFiled: [], // 存储已选择的维度
    saveSelectFiledTree: [], // 存储已选择的左侧维度菜单
    saveNewSortList: [], // 存储最新分类后的维度
    saveList: [], // 存储维度输入框以及维度组合
    saveRightTableList: [], // 维度对应的表
    /* 度量 */
    measureTableList: [],
    /* 刷新过滤 */
    relaodFilterList: [],
    /* 高级设置 */
    saveAggregationWD: [], // 存储已选择包含维度
    saveAggregationnecessaryWD: [], // 存储已选择必要维度
    saveAggregationlevelWD: [], // 存储已选择层级维度
    saveAggregationjointWD: [] // 存储已选择联合维度
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
    GetTreeList ({ commit }) {
      return new Promise((resolve, reject) => {
        getResourcedirectoryCategory().then(res => {
          commit('GET_TREELIST', res)
          resolve(res)
        })
      })
    },
    GetSerchTable ({ commit }, id) {
      return new Promise((resolve, reject) => {
        getResourcedirectory(id).then(res => {
          commit('GET_SERCHTABLE_LIST', res)
          resolve(res)
        })
      })
    },
    GetColumnList ({ commit }, params) {
      return new Promise((resolve, reject) => {
        getColumnList(params).then(res => {
          resolve(res)
        })
      })
    },
    GetTableData ({ commit }, params) {
      return new Promise((resolve, reject) => {
        getTableData(params).then(res => {
          resolve(res)
        })
      })
    },
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
    },
    /**
     * 维度步骤
     */
    // 存储已选择的维度
    SaveSelectFiled ({ state }, data) {
      let datas = state.saveSelectFiled.concat(data)
      state.saveSelectFiled = datas
    },
    // 删除取消的selct
    RemoveSelectFiled ({ state }, data) {
      state.saveSelectFiled && state.saveSelectFiled.forEach((item, index) => {
        if (item.id === data.id) {
          state.saveSelectFiled.splice(index, 1)
        }
      })
    },
    // 存储已选择对应的表
    SaveRightTableList ({ state }, data) {
      state.saveRightTableList = data
    },
    // 存储已选择的维度表
    saveSelectFiledTree ({ state }, data) {
      state.saveSelectFiledTree = data
    },
    // 存储加了显示名称的数据
    changePushSelectFiled ({ state }, index) {
      let datas = state.saveList.concat(state.saveRightTableList)
      state.saveList = datas
    },
    // 存储最新分类后的维度
    SaveNewSortList ({ state }, data) {
      state.saveNewSortList = filterArr(data)
    },
    // 合并设置的事实表到总表
    mergeFiledTable ({ state, dispatch }, data) {
      state.selectTableTotal.forEach((item, index) => {
        data[0].label === item.label ? state.selectTableTotal[index]['filed'] = 1 : state.selectTableTotal[index]['filed'] = 0
      })
      dispatch('setSelectTableTotal')
    },
    /* 度量 */
    // 新增的table表
    MeasureTableList ({ state }, data) {
      return new Promise((resolve, reject) => {
        state.measureTableList.push(data)
        resolve('ok')
      })
    },
    // 根据生成的id删除对应表
    deleteMeasureTableList ({ state }, id) {
      state.measureTableList.forEach((item, index) => {
        item.id === id && state.measureTableList.splice(index, 1)
      })
    },
    /* 刷新过滤 */
    // 新增的过滤表
    ReloadFilterTableList ({ state }, data) {
      return new Promise((resolve, reject) => {
        state.relaodFilterList.push(data)
        resolve('ok')
      })
    },
    // 根据生成的id删除对应表
    deleteReloadFilterTableList ({ state }, id) {
      state.relaodFilterList.forEach((item, index) => {
        item.id === id && state.relaodFilterList.splice(index, 1)
      })
    },
    // 存储聚合小组选择的维度
    SaveAggregationWD ({ state }, slectData) {
      switch (slectData.type) {
        case 1:
          state.saveAggregationWD = slectData.data
          break
        case 2:
          state.saveAggregationnecessaryWD = slectData.data
          break
        case 3:
          state.saveAggregationlevelWD = slectData.data
          break
        case 4:
          state.saveAggregationjointWD = slectData.data
          break
        default:
          break
      }
    }
  }
}

export default common
