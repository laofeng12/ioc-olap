
const reloadSet = {
  state: {
    // 刷新过滤设置
    relaodFilterList: [],
    /**
     * {autoReload} 是否开启自动刷新
     * partition_date_column 第一条数据表加字段
     * partition_date_format 第一条的时间格式
     * interval 刷新频率
     * frequencytype 频率方式
     */
    reloadData: {
      autoReload: false,
      dataMany: false,
      partition_date_column: '',
      partition_date_format: '',
      partition_time_format: '',
      interval: '',
      frequencytype: 1
    }
  },
  actions: {
    resetList ({ state }) {
      state.relaodFilterList = []
      state.reloadData = {
        autoReload: false,
        dataMany: false,
        partition_date_column: '',
        partition_date_format: '',
        partition_time_format: '',
        interval: '',
        frequencytype: 1
      }
    },
    // 新增的过滤表
    ReloadFilterTableList ({ state }, data) {
      return new Promise((resolve, reject) => {
        if (data.isNew === 0) {
          state.relaodFilterList.push(data)
          resolve('ok')
        } else {
          state.relaodFilterList.map((item, index) => {
            if (data.ids === item.ids) {
              state.relaodFilterList[index] = data
              resolve('ok')
            }
          })
        }
      })
    },
    // 根据生成的id删除对应表
    deleteReloadFilterTableList ({ state }, ids) {
      state.relaodFilterList.forEach((item, index) => {
        item.ids === ids && state.relaodFilterList.splice(index, 1)
      })
    }
  }
}

export default reloadSet
