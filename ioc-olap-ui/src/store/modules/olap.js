import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'
import { filterArr, filterArrData, reduceObj, setLocalStorage, getLocalStorage } from '@/utils/index'
import Vue from 'vue'
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
    /* 建立表关系 */
    savemousedownData: [], // 存储已拖拽的数据
    /* 维度 */
    saveSelectFiled: [], // 存储已选择的维度
    saveSelectFiledTree: [], // 存储已选择的左侧维度菜单
    saveNewSortListstructure: [], // 存储最新分类后的维度(传给后端的数据接口)
    saveNewSortList: [], // 存储最新分类后的维度
    saveList: [], // 存储维度输入框以及维度组合
    saveRightTableList: [], // 维度对应的表
    /* 度量 */
    measureTableList: [],
    /* 刷新过滤 */
    relaodFilterList: [],
    /* 高级设置 */
    totalSaveList: [
      {
        containData: [], // 包含维度
        necessaryData: [], // 必要维度
        levelData: [{}], // 层级维度
        jointData: [ {} ] // 联合维度
      }
    ], // 存储总的数据
    savedimensionData: [{}], // 存储维度黑白名单
    savehetComposeData: [], // 存储高级设置组合
    NewDataList: [], // 根据id存储的
    /** 已选择的维度id 以及黑白名单/高级组合 */
    selectDataidList: [ // 已选择的id集合
      {
        containDataId: [],
        necessaryDataId: [],
        levelDataId: [[]],
        jointDataId: [[]]
      }
    ],
    savedimensionDataId: [[]],
    savehetComposeDataId: [[]]
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
    },
    /*
     *建立表关系
     */
    // 存储已拖拽到画布的表
    SaveMousedownData ({ state }, data) {
      state.savemousedownData.push(data)
    },
    /**
     * 维度步骤
     */
    // 存储已选择的维度
    SaveSelectFiled ({ state }, data) {
      let datas = reduceObj(state.saveSelectFiled.concat(data), 'id')
      state.saveSelectFiled = datas
      // setLocalStorage('saveSelectFiled', datas) // 存储未分类的数据
    },
    // 删除取消的selct
    RemoveSelectFiled ({ state }, data) {
      state.saveSelectFiled = state.saveSelectFiled.filter((item, index) => {
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
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
      state.saveNewSortListstructure = filterArrData(data) // 需要传给后端的数据结构
      state.saveNewSortList = filterArr(data) // 需要传给后端的数据结构
      // setLocalStorage('saveNewSortList', filterArr(data))
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
    SaveAggregationWD ({ state, dispatch }, slectData) {
      dispatch('WithidGetList', slectData.data)
      switch (slectData.type) {
        case 1:
          state.totalSaveList[slectData.index].containData = state.NewDataList
          state.selectDataidList[slectData.index].containDataId = slectData.data
          break
        case 2:
          state.totalSaveList[slectData.index].necessaryData = state.NewDataList
          state.selectDataidList[slectData.index].necessaryDataId = slectData.data
          break
        case 3:
          Vue.set(state.totalSaveList[slectData.index].levelData, slectData.findIndex, state.NewDataList)
          Vue.set(state.selectDataidList[slectData.index].levelDataId, slectData.findIndex, slectData.data)
          break
        case 4:
          Vue.set(state.totalSaveList[slectData.index].jointData, slectData.findIndex, state.NewDataList)
          Vue.set(state.selectDataidList[slectData.index].jointDataId, slectData.findIndex, slectData.data)
          break
        case 5:
          Vue.set(state.savedimensionData, slectData.index, state.NewDataList)
          Vue.set(state.savedimensionDataId, slectData.index, slectData.data)
          break
        case 6:
          Vue.set(state.savehetComposeData, slectData.index, state.NewDataList)
          Vue.set(state.savehetComposeDataId, slectData.index, slectData.data)
          break
        default:
          break
      }
    },
    // 新增聚合小组
    addAggregationList ({ state }) {
      state.totalSaveList.push({
        containData: [],
        necessaryData: [],
        levelData: [{}],
        jointData: [{}]
      })
      state.selectDataidList.push({
        containDataId: [],
        necessaryDataId: [],
        levelDataId: [],
        jointDataId: []
      })
    },
    // 新增维度黑白名单
    AddimensionData ({ state }) {
      state.savedimensionData.push({})
      state.savedimensionDataId.push([])
    },
    // 新增高级组合
    AddhetComposeData ({ state }) {
      state.savehetComposeData.push({})
      state.savehetComposeDataId.push([])
    },
    // 根据id筛选出需要的数据
    WithidGetList ({ state }, id) {
      state.NewDataList = []
      let data = JSON.parse(getLocalStorage('saveNewSortList'))
      data.map(item => {
        item.list.map((n, i) => {
          id.map((k) => {
            if (n.id === k) {
              state.NewDataList.push(item.list[i])
            }
          })
        })
      })
    },
    // 根据id删除对应的标签以及弹框的标签
    RmtagList ({ state }, list) {
      switch (list.type) {
        case 1:
          state.totalSaveList[list.index].containData.filter((item, index) => {
            item.id === list.id && state.totalSaveList[list.index].containData.splice(index, 1)
          })
          state.selectDataidList[list.index].containDataId.map((item, index) => {
            item === list.id && state.selectDataidList[list.index].containDataId.splice(index, 1)
          })
          break
        case 2:
          state.totalSaveList[list.index].necessaryData.filter((item, index) => {
            item.id === list.id && state.totalSaveList[list.index].necessaryData.splice(index, 1)
          })
          state.selectDataidList[list.index].necessaryDataId.map((item, index) => {
            item === list.id && state.selectDataidList[list.index].necessaryDataId.splice(index, 1)
          })
          break
        case 3:
          state.totalSaveList[list.index].levelData[list.findIndex].filter((item, index) => {
            item.id === list.id && state.totalSaveList[list.index].levelData[list.findIndex].splice(index, 1)
          })
          state.selectDataidList[list.index].levelDataId[list.findIndex].map((item, index) => {
            item === list.id && state.selectDataidList[list.index].levelDataId[list.findIndex].splice(index, 1)
          })
          break
        case 4:
          state.totalSaveList[list.index].jointData[list.findIndex].filter((item, index) => {
            item.id === list.id && state.totalSaveList[list.index].jointData[list.findIndex].splice(index, 1)
          })
          state.selectDataidList[list.index].jointDataId[list.findIndex].map((item, index) => {
            item === list.id && state.selectDataidList[list.index].jointDataId[list.findIndex].splice(index, 1)
          })
          break
        case 5:
          state.savedimensionData[list.findIndex].filter((item, index) => {
            item.id === list.id && state.savedimensionData[list.findIndex].splice(index, 1)
          })
          state.savedimensionDataId[list.findIndex].map((item, index) => {
            item === list.id && state.savedimensionDataId[list.findIndex].splice(index, 1)
          })
          break
        case 6:
          state.savehetComposeData[list.findIndex].filter((item, index) => {
            item.id === list.id && state.savehetComposeData[list.findIndex].splice(index, 1)
          })
          state.savehetComposeDataId[list.findIndex].map((item, index) => {
            item === list.id && state.savehetComposeDataId[list.findIndex].splice(index, 1)
          })
          break
        default:
          break
      }
    }
  }
}

export default common
