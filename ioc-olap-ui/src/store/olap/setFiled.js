import { filterArr, filterArrData, reduceObj, reduceJson } from '@/utils/index'
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
    saveNewSortList: [], // 存储最新分类后的维度
    saveNewTitle: [] // 存储左侧列表组合的表名
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
    SaveSelectFiled ({ state, dispatch, getters }, data) {
      let datas = reduceObj(state.saveSelectFiled.concat(data), 'id')
      dispatch('changeFiled', datas)
    },
    // 设置别名后的维度(处理设置过别名的维度)
    changeFiled ({ state, dispatch }, data) {
      data.map(res => {
        if (!res.id) return
        let isId = res.tableName + '.' + res.titName
        if (isId !== res.id) {
          res.tableName = res.id.split('.')[0]
        }
      })
      state.saveSelectFiled = data
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
      // 删除对应的普通模式的表
      state.saveFiledNormalList = state.saveFiledNormalList.filter(item => {
        // 取消全选的时候
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
        }
      })
      // 删除对应的衍生模式的表
      state.saveFiledDerivativelList = state.saveFiledDerivativelList.filter(item => {
        // 取消全选的时候
        if (data.list) {
          return item.tableName !== data.id
        } else {
          return item.id !== data.id
        }
      })
      // 删除对应的整合过的数据（普通模式+衍生模式）
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
        modeType: '1',
        name: list.item.titName,
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
        modeType: '2',
        name: list.item.titName,
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
    changePushalias ({ state, dispatch }, val) {
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
      dispatch('SaveFiledData')
    },
    // 存储点击维度组合名称
    changePushSelectFiled ({ state, dispatch }, val) {
      // 遍历已选择的字段
      state.saveSelectFiled.map((item, index) => {
        // 如果为全选的时候 就需要遍历${val}取到对应的id
        // 如果已选择的字段的id===勾选过的id 就赋值勾选的mode到已存储的数据中
        if (val.length) {
          val.map(res => {
            if (res.id === item.id) {
              state.saveSelectFiled[index].mode = res.mode
            }
            // 如果为事实表的话 mode===1
            // if (res.filed === '1') {
            //   state.saveSelectFiled[index].mode = 1
            // } else {
            //   state.saveSelectFiled[index].mode = res.mode
            // }
            // 如果mode===1 或者为事实表的时候 就存储到普通模式列表中 否则的话就存储到衍生模式列表中
            if (String(res.mode) === '1' || res.filed === '1') {
              dispatch('normalFn', { item: item, val: res, mode: 1 })
            } else if (String(res.mode) === '2') {
              dispatch('derivativeFn', { item: item, val: res, mode: 2 })
            }
          })
        } else {
          // 单选勾选框的时候
          if (val.id === item.id) {
            state.saveSelectFiled[index].mode = val.mode
          }
          // 如果mode===1 或者为事实表的时候 就存储到普通模式列表中 否则的话就存储到衍生模式列表中
          if ((String(val.mode) === '1' && String(item.mode) === '1') || item.filed === '1') {
            dispatch('normalFn', { item: item, val: val, model: 1 })
          } else if (String(val.mode) === '2' && String(item.mode) === '2') {
            dispatch('derivativeFn', { item: item, val: val, model: 2 })
          }
        }
      })
      dispatch('filterFiledTable')
      dispatch('SaveFiledData')
    },
    // 整合正常模式或者衍生模式的数据
    filterFiledTable ({ state, getters, dispatch }) {
      let resultVal = reduceJson(state.saveFiledDerivativelList, 'tableName')
      // let resultVal = state.saveFiledDerivativelList
      // 筛选对应的foreign_key名
      let datas = []
      /**
       * 1、遍历已经连好线的表lookups 遍历已经选择的维度名称（普通或者衍生模式）
       * 2、${n.tableName} --- 当前选择的组合名称的表名 根据此表名去匹配lookups的表
       * 3、取出对应的表对应的foreign_key
       * 4、如果对应的${foreign_key}是多个的话就绪遍历
       */
      // console.log(resultVal, '获取已经选择的数据', state.saveSelectFiled, '========', getters.jointResultData.lookups)
      getters.jointResultData.lookups.map((item, index) => {
        resultVal.map((n, i) => {
          if (item.alias === n.tableName) {
            /*
             * 判断当前找的主表是否为普通模式
             * 1、如果是普通模式直接去除对应的字段
             * 2、否则就要继续往上找
            */
            if (item.join.foreign_key.length > 1) {
              item.join.foreign_key.forEach(res => {
                datas = datas.concat({
                  id: n.id,
                  type: n.dataType,
                  modeType: '2',
                  value: res
                })
              })
            } else {
              datas.push({
                id: n.id,
                type: n.dataType,
                modeType: '2',
                value: item.join.foreign_key.join(',')
              })
            }
          }
        })
      })
      // 整合正常模式数据
      let nomrlData = []
      state.saveFiledNormalList.map((res, index) => {
        nomrlData = nomrlData.concat({
          id: res.id,
          type: res.dataType,
          modeType: res.modeType,
          value: res.tableName + '.' + res.name
        })
      })
      // state.reloadNeedData = reduceObj([...nomrlData, ...datas], 'value')
      let result = reduceObj([...nomrlData, ...datas], 'value')
      dispatch('filterTableAlias', result)
      dispatch('setAdvanceData', result)
    },
    // 筛选出已经修改的表名
    filterTableAlias ({ state, getters }, data) {
      let ResultAlias = Array.from(getters.jointResultData.lookups, ({ alias }) => alias)
      let ResultJoinAlias = Array.from(getters.jointResultData.lookups, ({ joinAlias }) => joinAlias)
      let Result = [...new Set([...ResultAlias, ...ResultJoinAlias])]
      let val = data.filter(res => { return Result.includes(res.value.split('.')[0]) })
      state.reloadNeedData = [...val]
      // console.log('最终于需要的', val)
    },
    // 赋值给高级设置中默认显示的包含维度 以及rowkey
    setAdvanceData ({ state, getters, dispatch }, data) {
      let val = []
      let idval = []
      data.map(res => {
        val.push(res.value)
        idval.push(res.id)
      })
      getters.aggregation_groups[0].includes = val
      getters.selectDataidList[0].includesId = idval
      dispatch('SaveselectIncludesData', val)
      // debugger
      state.reloadNeedData = [...data]
    },
    // 存储洗选的维度（传给后端的)
    SaveFiledData ({ state }) {
      // 对接数据格式
      state.dimensions = []
      state.saveSelectFiled && state.saveSelectFiled.map((item, i) => {
        if (item.filed === '1') { item.mode = 1 }
        setTimeout(_ => {
          /**
           * ${mode} =1 就是普通模式
           * 普通模式需要传入column
           */
          if (String(item.mode) === '1') {
            state.dimensions.push({
              table: item.tableName,
              tableId: `${item.tableName}.${item.name}`,
              column: item.titName,
              id: item.id,
              column_type: item.dataType,
              name: item.name
            })
            /**
             * ${mode} = 2 就是衍生模式
             * 普通模式需要传入derived
             * derived 传入的数据为该字段的名称
            */
          } else {
            state.dimensions.push({
              table: item.tableName,
              tableId: `${item.tableName}.${item.name}`,
              column_type: item.dataType,
              id: item.id,
              derived: item.mode === '1' ? null : item.titName.split(','),
              // derived: item.mode === '1' ? null : item.titName,
              name: item.name
            })
          }
          state.dimensions = reduceObj(state.dimensions, 'tableId')
        }, 300)
      })
    },
    // 存储最新分类后的维度
    SaveNewSortList ({ state }, data) {
      // console.log('设置别名后', data)
      state.saveNewSortListstructure = filterArrData(data) // 需要传给后端的数据结构
      state.saveNewSortList = filterArr(data)
    },
    // 修改别名后需要重新整理数据
    SetAliasList ({ state }, data) {
      state.saveNewSortListstructure = data
    },
    SaveList ({ state }, data) {
      state.saveList = data
    },
    // 存储左侧组合的表名
    SaveLeftTitle ({ state }, data) {
      state.saveNewTitle = data
    },
    changeAlias ({ state, dispatch }, data) {
      let val = []
      // 如果修改了别名 就需要把原先的去掉  加入新的别名对应的数据
      state.saveNewSortListstructure.map(item => {
        data.map(res => {
          if (item.table === res) val.push(item)
        })
      })
      state.saveNewSortListstructure = [...val]
      // 删除对应已选择的字段
      let saveSelectFiledData = []
      state.saveSelectFiled.map(item => {
        data.map(res => {
          if (item.tableName === res) saveSelectFiledData.push(item)
        })
      })
      state.saveSelectFiled = [...saveSelectFiledData]
      state.saveFiledNormalList = []
      state.saveFiledDerivativelList = []
      dispatch('changePushSelectFiled', state.saveSelectFiled)
    }
  }
}

export default setFiled
