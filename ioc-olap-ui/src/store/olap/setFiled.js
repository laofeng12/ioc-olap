import { filterArr, filterArrData, reduceObj } from '@/utils/index'

const setFiled = {
  state: {
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
            'KYLIN_SALES.PART_DT1' // 衍生模式需要取到包含维度里
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
            'KYLIN_SALES.PART_DT2'
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
    // 存储加了显示名称的数据
    changePushSelectFiled ({ state, dispatch }, val) {
      state.saveSelectFiled.forEach((item, index) => {
        if (val.length) {
          val.map(res => {
            if (res.id === item.id) {
              state.saveSelectFiled[index].mode = res.mode
              state.saveSelectFiled[index].name = res.name
            }
          })
        } else {
          if (val.id === item.id) {
            state.saveSelectFiled[index].mode = val.mode
            state.saveSelectFiled[index].name = val.name
          }
        }
        if (item.mode === '1') {
          // 普通模式
          state.saveFiledNormalList = state.saveFiledNormalList.concat(item)
          state.saveFiledNormalList = reduceObj(state.saveFiledNormalList, 'id')
          // 删除选择的衍生模式
          state.saveFiledDerivativelList && state.saveFiledDerivativelList.forEach((item, index) => {
            if (item.id === val.id) {
              state.saveFiledDerivativelList.splice(index, 1)
            }
          })
        } else if (item.mode === '2') {
          // 衍生模式
          state.saveFiledDerivativelList = state.saveFiledDerivativelList.concat(item)
          state.saveFiledDerivativelList = reduceObj(state.saveFiledDerivativelList, 'id')
          // 删除选择的普通模式
          state.saveFiledNormalList && state.saveFiledNormalList.forEach((item, index) => {
            if (item.id === val.id) {
              state.saveFiledNormalList.splice(index, 1)
            }
          })
        }
      })
      dispatch('filterFiledTable')
      // console.log(state.saveFiledDerivativelList, '衍生模式')
      // console.log(state.saveFiledNormalList, '普通模式')
    },
    // 整合正常模式或者衍生模式的数据
    filterFiledTable ({ state }) {
      let resultVal = reduceObj(state.saveFiledDerivativelList, 'tableName')
      // 筛选对应的foreign_key名
      let datas = []
      state.saveLeftFiled.lookups.map((item, index) => {
        resultVal.map((n, i) => {
          if (item.alias === n.tableName) {
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
          value: res.tableName + '.' + res.columnName
        })
      })
      state.reloadNeedData = [...nomrlData, ...datas]
      // console.log('啦啦啦啦', state.reloadNeedData)
    },
    // 存储洗选的维度（传给后端的)
    SaveFiledData ({ state }) {
      // 对接数据格式
      state.dimensions = []
      state.saveSelectFiled && state.saveSelectFiled.map((item, i) => {
        state.dimensions.push({
          table: item.tableName,
          column: item.columnName,
          derived: item.mode === '1' ? null : item.columnName.split(','),
          name: item.name ? item.name : item.columnName
        })
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
