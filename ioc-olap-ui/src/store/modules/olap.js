const common = {
  state: {
    /* 建立表关系 */
    savemousedownData: [] // 存储已拖拽的数据
  },
  mutations: {
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
    /*
     *建立表关系
     */
    // 存储已拖拽到画布的表
    SaveMousedownData ({ state }, data) {
      state.savemousedownData.push(data)
    },
    // 合并设置的事实表到总表
    mergeFiledTable ({ state, getters, dispatch }, data) {
      getters.selectTableTotal.forEach((item, index) => {
        data[0].label === item.label ? getters.selectTableTotal[index]['filed'] = 1 : getters.selectTableTotal[index]['filed'] = 0
      })
      // dispatch('setSelectTableTotal')
    }
  }
}

export default common
