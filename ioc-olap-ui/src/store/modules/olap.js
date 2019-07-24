import { getResourcedirectoryCategory, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/common'
import { filterArr, filterArrData, reduceObj } from '@/utils/index'
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
    saveLeftFiled: {
      'name': 'bb',
      'description': '',
      'fact_table': 'DS_U_GN9N1SADS',
      lookups: [{
        'table': 'DS_U_GN9N1SADS',
        'alias': 'DS_U_GN9N1SADS',
        'joinTable': 'DS_U_I66UXON7J',
        'kind': 'LOOKUP',
        'join': {
          'type': 'inner',
          'primary_key': [
            'KYLIN_CAL_DT.CAL_DT'
          ],
          'foreign_key': [
            'KYLIN_SALES.PART_DT' // 衍生模式需要取到包含维度里
          ],
          'isCompatible': [
            true
          ],
          'pk_type': [
            'date'
          ],
          'fk_type': [
            'date'
          ]
        }
      },
      {
        'table': 'DS_U_I66UXON7J',
        'alias': 'DS_U_I66UXON7J',
        'joinTable': 'DS_U_I66UXON7J',
        'kind': 'LOOKUP',
        'join': {
          'type': 'inner',
          'primary_key': [
            'KYLIN_CAL_DT.CAL_DT'
          ],
          'foreign_key': [
            'KYLIN_SALES.PART_DT'
          ],
          'isCompatible': [
            true
          ],
          'pk_type': [
            'date'
          ],
          'fk_type': [
            'date'
          ]
        }
      }]
    }, // 左侧的维度数据
    saveSelectFiled: [], // 存储已选择的维度
    saveFiledNormalList: [], // 存储正常模式下的数据(传给后端的结构)------------------------
    saveFiledDerivativelList: [], // 存储正常模式下的数据(传给后端的结构)------------------------
    saveFiledData: [], // 存储已选择的(传给后端的结构)------------------------
    reloadNeedData: [],
    saveNewSortListstructure: [], // 存储最新分类后的维度
    saveNewSortList: [], // 存储最新分类后的维度
    /* 度量 */
    measureTableList: [],
    /* 刷新过滤 */
    relaodFilterList: [],
    /* 高级设置 */
    aggregation_groups: [
      {
        includes: [], // 包含维度
        select_rule: {
          mandatory_dims: [], // 必要维度
          hierarchy_dims: [[]], // 层级维度
          joint_dims: [[]] // 联合维度
        }
      }
    ], // 存储总的数据
    savedimensionData: [[]], // 存储维度黑白名单
    savehetComposeData: [], // 存储高级设置组合
    NewDataList: [], // 根据id存储的
    /** 已选择的维度id 以及黑白名单/高级组合 */
    selectDataidList: [ // 已选择的id集合
      {
        includesId: [],
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
    // 合并设置的事实表到总表
    mergeFiledTable ({ state, dispatch }, data) {
      state.selectTableTotal.forEach((item, index) => {
        data[0].label === item.label ? state.selectTableTotal[index]['filed'] = 1 : state.selectTableTotal[index]['filed'] = 0
      })
      dispatch('setSelectTableTotal')
    },
    /**
     * 维度步骤
     */
    // 存储已选择的维度
    SaveSelectFiled ({ state, dispatch }, data) {
      let datas = reduceObj(state.saveSelectFiled.concat(data), 'id')
      state.saveSelectFiled = datas
      dispatch('changePushSelectFiled', data)
      console.log(state.reloadNeedData, '============', data)
    },
    // 删除取消的selct
    RemoveSelectFiled ({ state, dispatch }, data) {
      state.saveSelectFiled = state.saveSelectFiled.filter((item, index) => {
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
        }
      })
      dispatch('changePushSelectFiled', data)
      state.reloadNeedData.map((item, index) => {
        if (data.list) {

        } else {
          if (item.id === data.id) {
            state.reloadNeedData.splice(index, 1)
          }
        }
      })
      console.log(reduceObj(state.reloadNeedData, 'id'), '============', data)
      // state.reloadNeedData = state.reloadNeedData
    },
    // 存储加了显示名称的数据
    changePushSelectFiled ({ state, dispatch }, val) {
      state.saveSelectFiled.forEach((item, index) => {
        if (val.id === item.id) {
          state.saveSelectFiled[index].mode = val.mode
          state.saveSelectFiled[index].name = val.name
        }
        if (item.mode === '1') {
          // 普通模式
          state.saveFiledNormalList.push(item)
          state.saveFiledNormalList = reduceObj(state.saveFiledNormalList, 'id')
          // 删除选择的衍生模式
          state.saveFiledDerivativelList.forEach((item, index) => {
            if (item.id === val.id) {
              state.saveFiledDerivativelList.splice(index, 1)
            }
          })
        } else if (item.mode === '2') {
          // 衍生模式
          state.saveFiledDerivativelList.push(item)
          state.saveFiledDerivativelList = reduceObj(state.saveFiledDerivativelList, 'id')
          // 删除选择的普通模式
          state.saveFiledNormalList.forEach((item, index) => {
            if (item.id === val.id) {
              state.saveFiledNormalList.splice(index, 1)
            }
          })
        }
      })
      dispatch('filterFiledTable', state.saveFiledDerivativelList)
      // console.log(state.saveFiledDerivativelList, '啦啦啦啦啦啦啦')
      // console.log('李帆', state.saveFiledNormalList)
    },
    // 整合正常模式或者衍生模式的数据
    filterFiledTable ({ state }, data) {
      // console.log('万靓', state.saveSelectFiled)
      // 筛选对应的foreign_key名
      let datas = []
      state.saveLeftFiled.lookups.map((item, index) => {
        data.map((n, i) => {
          if (item.alias === n.tableName) {
            // datas.push(item.join.foreign_key.join(','))
            datas.push({
              id: n.id,
              value: item.join.foreign_key.join(',')
            })
          }
        })
      })
      // 整合正常模式数据
      let nomrlData = []
      state.saveFiledNormalList.map((res, index) => {
        // nomrlData.push(res.tableName + '.' + res.columnName)
        nomrlData.push({
          id: res.id,
          value: res.tableName + '.' + res.columnName
        })
      })
      state.reloadNeedData = [...nomrlData, ...datas]
      console.log(state.reloadNeedData)
    },
    // 存储洗选的维度（传给后端的）
    SaveFiledData ({ state }) {
      // 对接数据格式
      state.saveFiledData = []
      state.saveSelectFiled && state.saveSelectFiled.map((item, i) => {
        state.saveFiledData.push({
          table: item.tableName,
          column: item.columnName,
          derived: item.mode === '1' ? null : item.columnName.split(','),
          name: item.name ? item.name : item.columnName
        })
      })
      // console.log('需要的', state.saveFiledData)
    },
    // 存储最新分类后的维度
    SaveNewSortList ({ state }, data) {
      state.saveNewSortListstructure = filterArrData(data) // 需要传给后端的数据结构
      state.saveNewSortList = filterArr(data)
    },
    /* 度量 */
    // 新增的table表
    MeasureTableList ({ state }, data) {
      return new Promise((resolve, reject) => {
        if (data.isNew === 0) {
          state.measureTableList.push(data)
          resolve('ok')
        } else {
          state.measureTableList.map((item, index) => {
            if (data.id === item.id) {
              state.measureTableList[index] = data
              resolve('ok')
            }
          })
        }
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
        if (data.isNew === 0) {
          state.relaodFilterList.push(data)
          resolve('ok')
        } else {
          state.relaodFilterList.map((item, index) => {
            if (data.id === item.id) {
              state.relaodFilterList[index] = data
              resolve('ok')
            }
          })
        }
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
      // dispatch('WithidGetList', slectData.data)
      switch (slectData.type) {
        case 1:
          state.aggregation_groups[slectData.index].includes = slectData.data
          state.selectDataidList[slectData.index].includesId = slectData.data
          break
        case 2:
          state.aggregation_groups[slectData.index].select_rule.mandatory_dims = slectData.data
          state.selectDataidList[slectData.index].necessaryDataId = slectData.data
          break
        case 3:
          Vue.set(state.aggregation_groups[slectData.index].select_rule.hierarchy_dims, slectData.findIndex, slectData.data)
          Vue.set(state.selectDataidList[slectData.index].levelDataId, slectData.findIndex, slectData.data)
          break
        case 4:
          Vue.set(state.aggregation_groups[slectData.index].select_rule.joint_dims, slectData.findIndex, slectData.data)
          Vue.set(state.selectDataidList[slectData.index].jointDataId, slectData.findIndex, slectData.data)
          break
        case 5:
          Vue.set(state.savedimensionData, slectData.index, slectData.data)
          Vue.set(state.savedimensionDataId, slectData.index, slectData.data)
          break
        case 6:
          Vue.set(state.savehetComposeData, slectData.index, slectData.data)
          Vue.set(state.savehetComposeDataId, slectData.index, slectData.data)
          break
        default:
          break
      }
    },
    // 新增聚合小组
    addAggregationList ({ state }) {
      state.aggregation_groups.push({
        includes: [],
        select_rule: {
          mandatory_dims: [], // 必要维度
          hierarchy_dims: [[]], // 层级维度
          joint_dims: [[]] // 联合维度
        }
      })
      state.selectDataidList.push({
        includesId: [],
        necessaryDataId: [],
        levelDataId: [[]],
        jointDataId: [[]]
      })
    },
    // 新增层级维度
    AddlevelData ({ state }, index) {
      state.aggregation_groups[index].select_rule.hierarchy_dims.push([])
      state.selectDataidList[index].levelDataId.push([])
    },
    // 新增联合维度
    AddjointData ({ state }, index) {
      state.aggregation_groups[index].select_rule.joint_dims.push([])
      state.selectDataidList[index].jointDataId.push([])
    },
    // 新增维度黑白名单
    AddimensionData ({ state }) {
      state.savedimensionData.push([])
      state.savedimensionDataId.push([])
    },
    // 新增高级组合
    AddhetComposeData ({ state }) {
      state.savehetComposeData.push([])
      state.savehetComposeDataId.push([])
    },
    // 根据id筛选出需要的数据
    WithidGetList ({ state }, id) {
      state.NewDataList = []
      // let data = JSON.parse(getLocalStorage('saveNewSortList'))
      let data = state.saveNewSortList
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
          state.aggregation_groups[list.index].includes.filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].includes.splice(index, 1)
          })
          state.selectDataidList[list.index].includesId.map((item, index) => {
            item === list.id && state.selectDataidList[list.index].includesId.splice(index, 1)
          })
          break
        case 2:
          state.aggregation_groups[list.index].select_rule.mandatory_dims.filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].necessaryData.splice(index, 1)
          })
          state.selectDataidList[list.index].necessaryDataId.map((item, index) => {
            item === list.id && state.selectDataidList[list.index].necessaryDataId.splice(index, 1)
          })
          break
        case 3:
          state.aggregation_groups[list.index].select_rule.hierarchy_dims[list.findIndex].filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].select_rule.hierarchy_dims[list.findIndex].splice(index, 1)
          })
          state.selectDataidList[list.index].levelDataId[list.findIndex].map((item, index) => {
            item === list.id && state.selectDataidList[list.index].levelDataId[list.findIndex].splice(index, 1)
          })
          break
        case 4:
          state.aggregation_groups[list.index].select_rule.joint_dims[list.findIndex].filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].select_rule.joint_dims[list.findIndex].splice(index, 1)
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
