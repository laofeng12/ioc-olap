import { filterArr, filterArrData, reduceObj, reduceJson } from '@/utils/index'
import Vue from 'vue'
const setFiled = {
  state: {
    /* 维度 */
    saveList: [],
    saveSelectFiled: [], // 存储已选择的维度
    saveFiledNormalList: [], // 存储正常模式下的数据
    saveFiledDerivativelList: [], // 存储正常模式下的数据
    dimensions: [], // 存储已选择的(传给后端的结构)------------------------
    reloadNeedData: [],
    saveNewSortListstructure: [], // 存储最新分类后的维度
    saveNewSortList: [] // 存储最新分类后的维度
  },
  actions: {
    resetList ({ state }) {
      state.saveSelectFiled = []
      state.saveFiledNormalList = []
      state.saveFiledDerivativelList = []
      state.dimensions = []
      state.reloadNeedData = []
      state.saveNewSortListstructure = []
      state.saveNewSortList = []
    },
    /**
     * 维度步骤
     */
    // 存储已选择的维度
    SaveSelectFiled ({ state, dispatch }, data) {
      let datas = reduceObj(state.saveSelectFiled.concat(data), 'id')
      state.saveSelectFiled = datas
      dispatch('changePushSelectFiled', data)
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
      // 删除对应的表
      state.saveFiledNormalList = state.saveFiledNormalList.filter(item => {
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
        }
      })
      state.saveFiledDerivativelList = state.saveFiledDerivativelList.filter(item => {
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
        }
      })
      state.reloadNeedData = state.reloadNeedData.filter(item => {
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
        }
      })
    },
    normalFn ({ state }, list) {
      // 普通模式
      state.saveFiledNormalList = state.saveFiledNormalList.concat({
        id: list.item.id,
        dataType: list.item.dataType,
        name: list.item.name,
        tableName: list.item.tableName
      })
      let data = reduceJson(state.saveFiledNormalList, 'id')
      state.saveFiledNormalList = data
      // 删除选择的衍生模式
      state.saveFiledDerivativelList && state.saveFiledDerivativelList.forEach((item, index) => {
        if (item.id === list.val.id) {
          state.saveFiledDerivativelList.splice(index, 1)
        }
      })
    },
    derivativeFn ({ state }, list) {
      // 衍生模式
      state.saveFiledDerivativelList = state.saveFiledDerivativelList.concat({
        id: list.item.id,
        dataType: list.item.dataType,
        name: list.item.name,
        tableName: list.item.tableName
      })
      let data = reduceJson(state.saveFiledDerivativelList, 'id')
      state.saveFiledDerivativelList = data
      // 删除选择的普通模式
      state.saveFiledNormalList && state.saveFiledNormalList.forEach((item, index) => {
        if (item.id === list.val.id) {
          state.saveFiledNormalList.splice(index, 1)
        }
      })
    },
    // 存储输入的显示名称
    changePushalias ({ state }, val) {
      console.log(val, '修改文字')
      state.saveSelectFiled.map((item, index) => {
        if (val.length) {
          val.map(res => {
            if (res.id === item.id) {
              state.saveSelectFiled[index].name = name
            }
          })
        } else {
          if (val.id === item.id) {
            state.saveSelectFiled[index].name = val.name
          }
        }
      })
    },
    // 存储点击维度组合名称
    changePushSelectFiled ({ state, dispatch }, val) {
      // console.log(state.saveSelectFiled, 'val====', val)
      state.saveSelectFiled.map((item, index) => {
        if (val.length) {
          val.map(res => {
            if (res.id === item.id) {
              state.saveSelectFiled[index].mode = res.mode
            }
            if (res.filed === '1') {
              state.saveSelectFiled[index].mode = 1
            }
            if (String(res.mode) === '1' || res.filed === '1') {
              dispatch('normalFn', { item: item, val: res })
            } else if (String(res.mode) === '2') {
              dispatch('derivativeFn', { item: item, val: res })
            }
          })
        } else {
          if (val.id === item.id) {
            state.saveSelectFiled[index].mode = val.mode
          }
          if ((String(val.mode) === '1' && String(item.mode) === '1') || item.filed === '1') {
            dispatch('normalFn', { item: item, val: val })
          } else if (String(val.mode) === '2' && String(item.mode) === '2') {
            dispatch('derivativeFn', { item: item, val: val })
          }
        }
      })
      dispatch('filterFiledTable')
    },
    // 整合正常模式或者衍生模式的数据
    filterFiledTable ({ state, getters }) {
      let resultVal = reduceJson(state.saveFiledDerivativelList, 'tableName')
      // 筛选对应的foreign_key名
      let datas = []
      getters.jointResultData.lookups.map((item, index) => {
        resultVal.map((n, i) => {
          if (item.alias.substring(item.alias.indexOf('.') + 1) === n.tableName) {
            datas.push({
              id: n.id,
              type: n.dataType,
              value: item.join.foreign_key.join(',')
            })
          }
        })
      })
      // 整合正常模式数据
      let nomrlData = []
      state.saveFiledNormalList.map((res, index) => {
        nomrlData = nomrlData.concat({
          id: res.id,
          type: res.dataType,
          value: res.tableName + '.' + res.name
        })
      })
      state.reloadNeedData = reduceObj([...nomrlData, ...datas], 'value')
      console.log('啦啦啦啦', state.reloadNeedData)
    },
    // 存储洗选的维度（传给后端的)
    SaveFiledData ({ state }) {
      // 对接数据格式
      state.dimensions = []
      state.saveSelectFiled && state.saveSelectFiled.map((item, i) => {
        if (item.filed === '1') { item.mode = 1 }
        setTimeout(_ => {
          if (String(item.mode) === '1') {
            state.dimensions.push({
              table: item.tableName,
              column: item.name,
              // columnType: item.dataType,
              column_type: item.dataType,
              name: item.name ? item.name : item.name
            })
          } else {
            state.dimensions.push({
              table: item.tableName,
              // columnType: item.dataType,
              column_type: item.dataType,
              derived: item.mode === '1' ? null : item.name.split(','),
              name: item.name ? item.name : item.name
            })
          }
        }, 300)
      })
    },
    // 存储最新分类后的维度
    SaveNewSortList ({ state }, data) {
      state.saveNewSortListstructure = filterArrData(data) // 需要传给后端的数据结构
      state.saveNewSortList = filterArr(data)
    },
    SaveList ({ state }, data) {
      state.saveList = data
    }
  }
}

export default setFiled
