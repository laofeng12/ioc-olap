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
    saveList: [],
    saveSelectFiled: [], // 存储已选择的维度
    saveFiledNormalList: [], // 存储正常模式下的数据(传给后端的结构)------------------------
    saveFiledDerivativelList: [], // 存储正常模式下的数据(传给后端的结构)------------------------
    saveFiledData: [], // 存储已选择的(传给后端的结构)------------------------
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
      state.saveFiledDerivativelList = state.saveFiledDerivativelList.filter(item => {
        return item.id !== data.id
      })
      state.saveFiledNormalList = state.saveFiledNormalList.filter(item => {
        return item.id !== data.id
      })
      state.reloadNeedData = state.reloadNeedData.filter(item => {
        return item.id !== data.id
      })
    },
    // 存储加了显示名称的数据
    changePushSelectFiled ({ state, dispatch }, val) {
      state.saveSelectFiled.forEach((item, index) => {
        if (val.id === item.id) {
          state.saveSelectFiled[index].mode = val.mode
          state.saveSelectFiled[index].name = val.name
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
        }
      })
      dispatch('filterFiledTable', state.saveFiledDerivativelList)
    },
    // 整合正常模式或者衍生模式的数据
    filterFiledTable ({ state }, data) {
      let resultVal = reduceObj(data, 'tableName')
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
        nomrlData.push({
          id: res.id,
          type: res.dataType,
          value: res.tableName + '.' + res.columnName
        })
      })
      state.reloadNeedData = [...nomrlData, ...datas]
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
    SaveList ({ state }, data) {
      state.saveList = data
    }
  }
}

export default setFiled
