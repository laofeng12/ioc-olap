import { reduceObj } from '@/utils/index'
const setMeasure = {
  state: {
    /* 度量 */
    measureTableList: [],
    test: ''
  },
  mutations: {
    setTest (state, val) {
      state.test = val
    }
  },
  actions: {
    resetList ({ state }) {
      state.measureTableList = []
    },
    // 新增的table表
    MeasureTableList ({ state, dispatch }, data) {
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
        dispatch('GivehbaseMapping', data)
      })
    },
    // 根据生成的id删除对应表
    deleteMeasureTableList ({ state, dispatch }, val) {
      state.measureTableList.forEach((item, index) => {
        item.id === val.id && state.measureTableList.splice(index, 1)
      })
      dispatch('RemovehbaseMapping', val.name)
    },
    // 将新增的表添加到高级设置中的高级列组合中
    GivehbaseMapping ({ state, getters }, data) {
      let resultData = []
      // 添加对应的id
      getters.savehetComposeDataId[0].push(data.name)
      getters.hbase_mapping.column_family[0].columns[0].measure_refs = [...new Set(getters.hbase_mapping.column_family[0].columns[0].measure_refs)]
      state.measureTableList.map(res => { resultData.push(res.name) })
      getters.hbase_mapping.column_family[0].columns[0].measure_refs = resultData
    },
    // 删除对应高级设置中的高级列组合
    RemovehbaseMapping ({ getters }, name) {
      getters.hbase_mapping.column_family[0].columns[0].measure_refs.map((res, index) => {
        if (res === name) {
          getters.hbase_mapping.column_family[0].columns[0].measure_refs.splice(index, 1)
        }
      })
      getters.savehetComposeDataId[0].map((res, index) => {
        if (res === name) {
          getters.savehetComposeDataId[0].splice(index, 1)
        }
      })
    }
  }
}

export default setMeasure
