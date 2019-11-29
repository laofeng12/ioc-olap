
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
      // autoReload: false,
      // dataMany: false,
      // partition_date_column: '',
      // partition_date_format: '',
      // partition_time_format: '',
      // interval: '',
      // frequencytype: 1,
      // 我看代码在最后一步有拿这个东西做处理, 为了返回上一步回显, 我把这个对象跟view 里面的formData 对象同步一样了
        autoReload: false,
        ispartition_type: false,
        dataMany: false,
        idx: 0,
        data1a: '',
        data1b: '',
        data2a: '',
        data2b: '',
        partition_date_column: '', // 第一条数据表加字段
        partition_date_format: '', // 第一条数据
        partition_time_column: '',
        partition_time_format: '',
        interval: null,
        frequencytype: 1
    }
  },
  actions: {
    resetList ({ state }) {
      state.relaodFilterList = []
      // 同理这里也是一样处理
      state.reloadData = {
        autoReload: false,
        ispartition_type: false,
        dataMany: false,
        data1a: '',
        data1b: '',
        data2a: '',
        data2b: '',
        partition_date_column: '',
        partition_date_format: '',
        partition_time_format: '',
        partition_time_column: '',
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
  },
  mutations: {
    SET_RELAOD_FILTER (state, data) {
      state.reloadData = data
    }
  }
}

export default reloadSet
