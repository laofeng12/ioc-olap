import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'

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
    relaodFilterList: []
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
    /* 维度 */
    // 存储已选择的维度
    saveSelectFiled ({ state }, data) {
      let datas = state.saveSelectFiled.concat(data)
      state.saveSelectFiled = datas
    },
    // 删除取消的selct
    removeSelectFiled ({ state }, data) {
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
      console.log(data)
      var map = {}
      var dest = []
      for (var i = 0; i < data.length; i++) {
        var ai = data[i]
        if (!map[ai.tableName]) {
          dest.push({
            comment: ai.comment,
            tableName: ai.tableName,
            columnName: ai.columnName,
            filed: ai.filed,
            list: [ai]
          })
          map[ai.tableName] = ai
        } else {
          for (var j = 0; j < dest.length; j++) {
            var dj = dest[j]
            if (dj.tableName === ai.tableName) {
              dj.list.push(ai)
              break
            }
          }
        }
      }
      let itemData = []
      dest.map(item => {
        let newData = {}
        newData.columnName = item.columnName
        newData.comment = item.comment
        newData.tableName = item.tableName
        newData.apiPaths = item.apiPaths
        newData.list = item.list
        itemData.push(newData)
      })
      state.saveNewSortList = itemData
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
    }
  }
}

export default common
