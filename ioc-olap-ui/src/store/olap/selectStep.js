import { getselectCatalog,
  getselectTable,
  getselectColumn,
  getTreeoneList,
  getTreetwoList,
  getTreethreeList,
  getResourceInfo,
  getResourceData,
  getResourcedirectory,
  getColumnList,
  getTableData,
  getdsUploadTable
} from '@/api/olapModel'
import {
  batchCreateJob,
  getResourceInfo as getNewResourceInfo
} from '@/api/newOlapModel'
import { reduceObj, setLocalStorage, removeLocalStorage } from '@/utils/index'

const selectStep = {
  state: {
    tableJoinType: '',
    batchCreateJob: [], // 批量创建同步任务
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
    ]
  },
  mutations: {
    SET_TABLE_JOINTYPE (state, data) {
      state.tableJoinType = data
    },
    // 批量创建同步任务
    SET_BATCH_CREATEJOB (state, data = []) {
      state.batchCreateJob = data
    },
    // 初始化所有的列
    SET_ALLLIISTONE(state) {
      state.saveSelectAllList = []
    },
    GET_TREELIST: (state, data) => {
      state.treeList = data
    },
    GET_SERCHTABLE_LIST: (state, data) => {
      state.serchTableList = data
      setLocalStorage('serchTableList', data)
    },
    SET_SERCHTABLE_LIST: (state, data) => {
      state.serchTableList = data
      setLocalStorage('serchTableList', data)
    },
    LSATCLICK_TAB: (state, data) => {
      state.lastClickTab = data
    },
    CHANGE_SERACHTYPE: (state, val) => {
      state.searchType = val
    },
    // 选择的表相关逻辑初始化
    SETSELCT_TABLE_INIT () {
      state.selectTableTotal = []
      state.state.selectTableTotal  =[]
    },
    // 存储选择数据表
    SETSELCT_TABLE_COUNT: (state, val) => {
      state.selectTableTotal = val.filter(item => { return item.label })
      // removeLocalStorage('selectTableTotal')
      // setLocalStorage('selectTableTotal', state.selectTableTotal)
    },
    // 移除选择的数据
    REMOVE_SETSELCT_TABLE_COUNT: (state, { id }) => {
      const index = state.selectTableTotal.findIndex(t => t.id === id)
      index !== -1  && state.selectTableTotal.splice(index, 1)
      // removeLocalStorage('selectTableTotal')
      // setLocalStorage('selectTableTotal', state.selectTableTotal)
    },
    // 存储所有选择的表对应的字段
    SaveSelectAllListone (state, val = {}) {
      // let columId = val.map(item => { return item.resourceId })
      // getselectColumn(columId).then(res => {
      //   state.saveSelectAllList = res
      // })
      state.saveSelectAllList.push(val)
    },
    // 替换type
    SET_SElECTAll_LISTONE (state, data = []) {
      state.saveSelectAllList.forEach(t => {
        data.forEach(d => {
          if ( t.resourceTableName === d.virtualTableName) {
            t.column.forEach(c => {
              const target = d.meta.columns.find(m => c.definition.toUpperCase() === m.name.toUpperCase() )
              if (target) {
                c.type = target.datatype
              }
            })
          }
        })
      })
    },
    // 存储已经建表对应的所有字段
    SaveSelectAllListtwo (state, val) {
      // let columId = val.map(item => { return item.resourceId })
      // getselectColumn(val).then(res => {
      //   state.saveSelectAllListFiled = res
      // })
      state.saveSelectAllListFiled = []
      val.forEach(t => {
        const target = state.saveSelectAllList.find(item => String(item.resourceId)  === String(t))
        if (target) {
          state.saveSelectAllListFiled.push(target)
        }
      })
    },
    // 存储事实表对应的字段
    SaveFactData (state, list) {
      state.SaveFactData = list.data && list.data.map(item => {
        item.filed = '1'
        item.tableName = list.list.joinTable
        return item
      })
    },
    // 存储选择的表
    SET_SELECT_TALBE (state, data = []) {
      // 合并现在跟之前的数据
      // const tempData = data.concat(state.saveSelectTable)
      const tempData = data
      // 去重在赋值
      state.saveSelectTable = reduceObj(tempData, 'id')
    },
    // 存储下一步已选择的表
    SET_SELECT_STEP_LIST (state, data) {
      state.selectStepList = data
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
    // 获取所有选择表的列
    async getAllColumnInfo ({ state, commit }) {
      commit('SET_ALLLIISTONE')
      state.saveSelectTable.forEach(async ({resourceId, type, databaseId, isOnlyPermitted  = 2}) => {
        // isOnlyPermitted  = 2 只显示有最大权限
        const { data } = await getNewResourceInfo({resourceId, type, databaseId, isOnlyPermitted})
        commit('SaveSelectAllListone', data)
      })
    },
     // 批量创建同步接口
     async batchCreateJob ({ commit }, params) {
       const data = await batchCreateJob(params)
       commit('SET_BATCH_CREATEJOB', data.rows)
       return data
    },
    // 获取第一步树列表
    GetTreeList ({ commit }) {
      return new Promise((resolve, reject) => {
        getTreeoneList().then(res => {
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
    // 获取表数据以及列资源信息
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
    // 根据树的id获取对应的
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
    SelectStepList ({ state, commit }, data) {
      let map = {}
      let dest = []
      data.forEach((item, i) => {
          // 如果是数据湖的数据
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
        // }
      })
      dest.forEach(res => {
        res.tableList.map(n => {
          n.table_name = n.label
          n.table_id = n.id
          n.databaseId = n.databaseId
          n.virtualTableName = n.label // 虚拟表名
        })
      })
      // commit mutions
      commit('SET_SELECT_STEP_LIST', dest)
      // state.selectStepList = dest
    },
    // 勾选触发--存储数据湖的数据
    getSelectTableList ({ state, dispatch, getters, commit }, data) {
      const tempSaveSelectTable = []
      data.forEach(item => {
        tempSaveSelectTable.push({
          id: item.id,
          label: item.label,
          orgId: item.orgId,
          resourceId: item.resourceId,
          resourceName: item.resourceName,
          database: item.database,
          type: state.searchType,
          databaseId: item.databaseId,
          virtualTableName: item.label,
          ...item
        })
      })
      // commit vuex
      commit('SET_SELECT_TALBE', tempSaveSelectTable)
      dispatch('SelectStepList', state.saveSelectTable).then(_ => {
        // 判断是否是编辑进来的，如实编辑进来的需要主动调用存储第一步的方法
        getters.ModelAllList.TableList && dispatch('SavestepSelectData', getters.ModelAllList.TableList)
      })
    },
    // 删除数据胡对应的数据
    delSelectTableList ({ state, dispatch, getters }, data) {
      state.saveSelectTable.map((item, index) => {
        if (data.delData && data.delData.id === item.id) {
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
        if (data.delData && data.delData.id === item.id) {
          state.saveLocalSelectTable.splice(index, 1)
        }
      })
      dispatch('SelectStepList', state.saveLocalSelectTable)
    },
    // 设置已选择的表的总数据
    setSelectTableTotal ({ commit, state }) {
      // let totalData = [...state.saveSelectTable, ...state.saveLocalSelectTable]
      state.saveSelectTable = reduceObj(state.saveSelectTable, 'id')
      let totalData = [...state.saveSelectTable]
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
