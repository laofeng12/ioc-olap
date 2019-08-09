
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
    }
  }
}

export default setMeasure
