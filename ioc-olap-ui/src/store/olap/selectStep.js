import { getselectCatalog, getselectTable, getselectColumn, getTreeoneList, getTreetwoList, getTreethreeList, getResourceInfo, getResourceData, getResourcedirectory, getColumnList, getTableData, getdsUploadTable } from '@/api/olapModel'
import { reduceObj, setLocalStorage } from '@/utils/index'
const selectStep = {
  state: {
    treeList: [], // 树形数据
    serchTableList: [], // 获取的表数据
    searchType: 1, // 判断在数据湖还是本地 1 数据湖 2 本地
    saveSelectTable: [], // 数据湖选择的表
    saveLocalSelectTable: [], // 本地选择的表
    selectTableTotal: [], // 已选择的总表
    lastClickTab: '', // 存储最后一次点击的tabID
    lateData: [],
    saveSelectAllList: [], // 保存选择的表的所有字段
    saveSelectAllListFiled: [], // 保存连表后对应的所有字段
    SaveFactData: [], // 事实表对应的所有字段
    selectStepList:
    [
      {
        orgId: '11',
        orgName: '',
        tableList: []
      }
      // {
      //   'orgId': 2,
      //   'orgName': 'KYLIN',
      //   'tableList': [
      //     {
      //       'table_id': 'f86af73f-c96a-4eb8-9de7-3cca85aae998', // 表id
      //       'table_name': 'KYLIN_SALES', // 表名称
      //       'resourceId': 'f86af73f-c96a-4eb8-9de7-3cca85aae998',
      //       'database': 'KYLIN',
      //       'type': 1, // 表类型（数据湖/本地上传）
      //       'filed': 0
      //     }
      //   ]
      // },
      // {
      //   'orgId': 2,
      //   'orgName': 'KYLIN',
      //   'tableList': [
      //     {
      //       'table_id': 'f86af73f-c96a-4eb8-9de7-3cca85aae998',
      //       'table_name': 'KYLIN_SALES',
      //       'resourceId': 'f86af73f-c96a-4eb8-9de7-3cca85aae998',
      //       'database': 'KYLIN',
      //       'type': 1,
      //       'filed': 0
      //     }
      //   ]
      // }
    ]
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
      state.selectTableTotal = val.filter(item => { return item.label })
    },
    // 存储所有选择的表对应的字段
    SaveSelectAllListone (state, val) {
      let columId = val.map(item => { return item.resourceId })
      getselectColumn(columId).then(res => {
        state.saveSelectAllList = res
      })
    },
    SaveSelectAllListtwo (state, val) {
      // let columId = val.map(item => { return item.resourceId })
      getselectColumn(val).then(res => {
        state.saveSelectAllListFiled = res
      })
    },
    // 存储事实表对应的字段
    SaveFactData (state, list) {
      state.SaveFactData = list.data.map(item => {
        item.filed = '1'
        item.tableName = list.list.joinTable
        return item
      })
    },
    ClearTableRelation (getters) {
      getters.jointResultData = {
        name: 'joint',
        description: '',
        fact_table: '',
        lookups: []
      }
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
      state.saveSelectFiled = []
      state.saveSelectFiledTree = []
      state.selectStepList = [{
        orgId: '',
        orgName: '',
        tableList: []
      }]
    },
    // 获取第一步树列表
    GetTreeList ({ commit }) {
      return new Promise((resolve, reject) => {
        getTreeoneList().then(res => {
          // commit('GET_TREELIST', res)
          resolve(res)
        })
      })
    },
    // 获取下一级列表
    GetTreeTwoList ({ commit }, obj) {
      return new Promise((resolve, reject) => {
        getTreetwoList(obj).then(res => {
          resolve(res)
        })
      })
    },
    // 获取第三级列表
    GetThreeList ({ commit }, obj) {
      return new Promise((resolve, reject) => {
        // getTreethreeList(obj).then(res => {
        getselectTable(obj).then(res => { // kelin
          commit('GET_SERCHTABLE_LIST', res)
          resolve(res)
        })
      })
    },
    // 获取列表获取资源信息
    GetResourceInfo ({ commit }, obj) {
      return new Promise((resolve, reject) => {
        // getResourceInfo(obj).then(res => {
        getselectColumn(obj).then(res => { // kelin
          resolve(res)
        })
      })
    },
    // 根据资源信息获取对应详情
    getResourceData ({ commit }, obj) {
      return new Promise((resolve, reject) => {
        getResourceData(obj.params, obj.data).then(res => {
          resolve(res)
        })
      })
    },
    // ----------------------------------------
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
    // 存储数据源选择的表
    setSerchTable ({ commit }, data) {
      commit('SET_SERCHTABLE_LIST', data)
    },
    setLastClickTab ({ state, commit }, nodeId) {
      commit('LSATCLICK_TAB', nodeId)
      state.selectStepList.nodeId = nodeId
    },
    // 切换数据湖--本地上传控制
    changeSerachtype ({ commit, state }, val) {
      commit('CHANGE_SERACHTYPE', val)
    },
    // 存储选择后的数据（传给后端的）
    SelectStepList ({ state }, data) {
      let map = {}
      let dest = []
      let localData = []
      data.map((item, i) => {
        if (item.type === 2) {
          localData.push({
            orgId: item.orgId,
            table_name: item.label,
            type: item.type,
            table_id: item.id
          })
        } else {
          if (!map[item.database]) {
            dest.push({
              orgId: item.orgId,
              orgName: item.database,
              tableList: [item]
            })
            map[item.database] = item
          } else {
            for (var j = 0; j < dest.length; j++) {
              var dj = dest[j]
              if (dj.orgName === item.database) {
                dj.tableList.push(item)
                break
              }
            }
          }
        }
      })
      dest.map(res => {
        res.tableList.map(n => {
          n.table_name = n.label
          n.table_id = n.id
        })
      })
      state.selectStepList = dest
    },
    // 存储数据湖的数据
    getSelectTableList ({ state, dispatch, getters }, data) {
      // state.saveSelectTable = []
      data.map(item => {
        if (!item.children) {
          state.saveSelectTable = state.saveSelectTable.concat({
            id: item.id,
            label: item.label,
            orgId: item.orgId,
            resourceId: item.resourceId,
            database: item.database,
            type: state.searchType
          })
        }
      })
      state.saveSelectTable = reduceObj(state.saveSelectTable, 'id')
      dispatch('SelectStepList', state.saveSelectTable).then(_ => {
        getters.ModelAllList.TableList && dispatch('SavestepSelectData', getters.ModelAllList.TableList)
      })
    },
    // 删除数据胡对应的数据
    delSelectTableList ({ state, dispatch, getters }, data) {
      state.saveSelectTable.map((item, index) => {
        if (data.delData.id === item.id) {
          state.saveSelectTable.splice(index, 1)
        }
      })
      dispatch('SelectStepList', state.saveSelectTable).then(_ => {
        getters.ModelAllList.TableList && dispatch('SavestepSelectData', getters.ModelAllList.TableList)
      })
    },
    // 存储本地上传的数据
    getLocalSelectTableList ({ state, dispatch }, data) {
      state.saveLocalSelectTable = []
      data.map(item => {
        if (!item.children && !item.database) {
          state.saveLocalSelectTable.push({
            id: item.id,
            label: item.label,
            database: 'fan',
            type: 2
          })
        }
      })
      dispatch('SelectStepList', state.saveLocalSelectTable)
    },
    // 删除本地上传的数据
    delLocalSelectTableList ({ state, dispatch, getters }, data) {
      state.saveLocalSelectTable.map((item, index) => {
        if (data.delData.id === item.id) {
          state.saveLocalSelectTable.splice(index, 1)
        }
      })
      dispatch('SelectStepList', state.saveLocalSelectTable)
    },
    // 设置已选择的表的总数据
    setSelectTableTotal ({ commit, state }) {
      let totalData = [...state.saveSelectTable, ...state.saveLocalSelectTable]
      commit('SETSELCT_TABLE_COUNT', totalData)
    },
    // 存储数据湖点击的表
    SavedataData ({ commit, state }, data) {
      state.lateData = data
    },
    // 存储第一步要保存的数据
    SaveSelectData ({ state }, data) {
      state.selectStepList.orgId = data.orgId
      state.selectStepList.orgName = data.orgName
    },
    // 赋值保存过来的数据
    SavestepSelectData ({ state }, data) {
      state.selectStepList = data
    }
  }
}

export default selectStep
