
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
    deleteMeasureTableList ({ state }, id) {
      state.measureTableList.forEach((item, index) => {
        item.id === id && state.measureTableList.splice(index, 1)
      })
    },
    // 将新增的表添加到高级设置中的高级列组合中
    GivehbaseMapping ({ state, getters }, data) {
      // 添加对应的id
      getters.savehetComposeDataId[0].push(data.name)
      getters.hbase_mapping.column_family[0].columns[0].measure_refs.push(data.name)
    }
  }
}

export default setMeasure
