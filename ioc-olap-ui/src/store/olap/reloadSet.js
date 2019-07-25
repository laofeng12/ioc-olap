
const reloadSet = {
  state: {
    relaodFilterList: []
  },
  actions: {
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
    }
  }
}

export default reloadSet
