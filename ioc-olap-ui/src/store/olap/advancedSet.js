import Vue from 'vue'
import { getArrDifference } from '@/utils/index'
const advancedSet = {
  state: {
    aggregation_groups: [ // 存储总的数据
      {
        includes: [], // 包含维度
        select_rule: {
          mandatory_dims: [], // 必要维度
          hierarchy_dims: [[]], // 层级维度
          joint_dims: [[]] // 联合维度
        }
      }
    ],
    saveselectIncludesData: [], // 存储已选择的包含维度
    filterMappingData: [], // 过滤已经选择的高级组合
    mandatory_dimension_set_list: [[]], // 存储维度黑白名单
    hbase_mapping: { // 存储高级设置组合
      'column_family': [
        {
          'name': 'F1', // 序号
          'columns': [
            {
              'qualifier': 'M', // 写死
              'measure_refs': []
            }
          ]
        }
      ]
    },
    NewDataList: [], // 根据id存储的
    /** 已选择的维度id 以及黑白名单/高级组合 */
    selectDataidList: [ // 已选择的id集合
      {
        includesId: [],
        necessaryDataId: [],
        levelDataId: [[]],
        jointDataId: [[]]
      }
    ],
    savedimensionDataId: [[]], // 黑白名单id
    savehetComposeDataId: [[]], // 高级列id
    recordingData: [], // 记录已选择的
    engine_types: '2', // 构建引擎
    rowkeyData: {
      rowkey_columns: []
    } // rowkeys设置
  },
  actions: {
    resetList ({ state }) {
      state.relaodFilterList = []
      state.aggregation_groups = [
        {
          includes: [],
          select_rule: {
            mandatory_dims: [],
            hierarchy_dims: [[]],
            joint_dims: [[]]
          }
        }
      ]
      state.saveselectIncludesData = []
      state.filterMappingData = []
      state.mandatory_dimension_set_list = [[]]
      state.hbase_mapping = {
        column_family: [
          {
            'name': 'F1', // 序号
            'columns': [
              {
                'qualifier': 'M', // 写死
                'measure_refs': []
              }
            ]
          }
        ]
      }
      state.NewDataList = []
      state.selectDataidList = [
        {
          includesId: [],
          necessaryDataId: [],
          levelDataId: [[]],
          jointDataId: [[]]
        }
      ]
      state.savedimensionDataId = [[]]
      state.savehetComposeDataId = [[]]
      state.recordingData = []
      state.engine_types = '2'
    },
    // 存储聚合小组选择的维度
    SaveAggregationWD ({ state, dispatch }, slectData) {
      // dispatch('WithidGetList', slectData.data)
      switch (slectData.type) {
        case 1:
          state.aggregation_groups[slectData.index].includes = slectData.data
          state.selectDataidList[slectData.index].includesId = slectData.data
          dispatch('SaveselectIncludesData', slectData.data)
          break
        case 2:
          state.aggregation_groups[slectData.index].select_rule.mandatory_dims = slectData.data
          state.selectDataidList[slectData.index].necessaryDataId = slectData.data
          state.recordingData = state.recordingData.concat(slectData.data)
          break
        case 3:
          Vue.set(state.aggregation_groups[slectData.index].select_rule.hierarchy_dims, slectData.findIndex, slectData.data)
          Vue.set(state.selectDataidList[slectData.index].levelDataId, slectData.findIndex, slectData.data)
          state.recordingData = state.recordingData.concat(slectData.data)
          break
        case 4:
          Vue.set(state.aggregation_groups[slectData.index].select_rule.joint_dims, slectData.findIndex, slectData.data)
          Vue.set(state.selectDataidList[slectData.index].jointDataId, slectData.findIndex, slectData.data)
          state.recordingData = state.recordingData.concat(slectData.data)
          break
        case 5:
          Vue.set(state.mandatory_dimension_set_list, slectData.index, slectData.data)
          Vue.set(state.savedimensionDataId, slectData.index, slectData.data)
          break
        case 6:
          Vue.set(state.hbase_mapping.column_family[slectData.index].columns[0], 'measure_refs', slectData.data)
          Vue.set(state.savehetComposeDataId, slectData.index, slectData.data)
          dispatch('FilterMapping', slectData.data)
          break
        default:
          break
      }
    },
    // 新增聚合小组
    addAggregationList ({ state }) {
      state.aggregation_groups.push({
        includes: [],
        select_rule: {
          mandatory_dims: [], // 必要维度
          hierarchy_dims: [[]], // 层级维度
          joint_dims: [[]] // 联合维度
        }
      })
      state.selectDataidList.push({
        includesId: [],
        necessaryDataId: [],
        levelDataId: [[]],
        jointDataId: [[]]
      })
    },
    // 新增层级维度
    AddlevelData ({ state }, index) {
      state.aggregation_groups[index].select_rule.hierarchy_dims.push([])
      state.selectDataidList[index].levelDataId.push([])
    },
    // 新增联合维度
    AddjointData ({ state }, index) {
      state.aggregation_groups[index].select_rule.joint_dims.push([])
      state.selectDataidList[index].jointDataId.push([])
    },
    // 新增维度黑白名单
    AddimensionData ({ state }) {
      state.mandatory_dimension_set_list.push([])
      state.savedimensionDataId.push([])
    },
    // 新增高级组合
    AddhetComposeData ({ state }) {
      state.hbase_mapping.column_family.push({
        'name': `F${state.hbase_mapping.column_family.length + 1}`,
        'columns': [
          {
            'qualifier': 'M',
            'measure_refs': []
          }
        ]
      })
      state.savehetComposeDataId.push([])
    },
    // 根据id筛选出需要的数据
    WithidGetList ({ state }, id) {
      state.NewDataList = []
      let data = state.saveNewSortList
      data.map(item => {
        item.list.map((n, i) => {
          id.map((k) => {
            if (n.id === k) {
              state.NewDataList.push(item.list[i])
            }
          })
        })
      })
    },
    // 根据id删除对应的标签以及弹框的标签
    RmtagList ({ state }, list) {
      switch (list.type) {
        case 1:
          state.aggregation_groups[list.index].includes.filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].includes.splice(index, 1)
          })
          state.selectDataidList[list.index].includesId.map((item, index) => {
            item === list.id && state.selectDataidList[list.index].includesId.splice(index, 1)
          })
          break
        case 2:
          state.recordingData = getArrDifference(state.saveselectIncludesData, state.recordingData).concat(list.id)
          state.aggregation_groups[list.index].select_rule.mandatory_dims.filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].necessaryData.splice(index, 1)
          })
          state.selectDataidList[list.index].necessaryDataId.map((item, index) => {
            item === list.id && state.selectDataidList[list.index].necessaryDataId.splice(index, 1)
          })
          state.recordingData = getArrDifference(state.saveselectIncludesData, state.recordingData)
          break
        case 3:
          state.recordingData = getArrDifference(state.saveselectIncludesData, state.recordingData).concat(list.id)
          state.aggregation_groups[list.index].select_rule.hierarchy_dims[list.findIndex].filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].select_rule.hierarchy_dims[list.findIndex].splice(index, 1)
          })
          state.selectDataidList[list.index].levelDataId[list.findIndex].map((item, index) => {
            item === list.id && state.selectDataidList[list.index].levelDataId[list.findIndex].splice(index, 1)
          })
          state.recordingData = getArrDifference(state.saveselectIncludesData, state.recordingData)
          break
        case 4:
          state.recordingData = getArrDifference(state.saveselectIncludesData, state.recordingData).concat(list.id)
          state.aggregation_groups[list.index].select_rule.joint_dims[list.findIndex].filter((item, index) => {
            item.id === list.id && state.aggregation_groups[list.index].select_rule.joint_dims[list.findIndex].splice(index, 1)
          })
          state.selectDataidList[list.index].jointDataId[list.findIndex].map((item, index) => {
            item === list.id && state.selectDataidList[list.index].jointDataId[list.findIndex].splice(index, 1)
          })
          state.recordingData = getArrDifference(state.saveselectIncludesData, state.recordingData)
          break
        case 5:
          state.mandatory_dimension_set_list[list.findIndex].filter((item, index) => {
            item.id === list.id && state.mandatory_dimension_set_list[list.findIndex].splice(index, 1)
          })
          state.savedimensionDataId[list.findIndex].map((item, index) => {
            item === list.id && state.savedimensionDataId[list.findIndex].splice(index, 1)
          })
          break
        case 6:
          state.hbase_mapping.column_family[list.findIndex].columns[0].measure_refs.filter((item, index) => {
            item === list.id && state.hbase_mapping.column_family[list.findIndex].columns[0].measure_refs.splice(index, 1)
          })
          state.savehetComposeDataId[list.findIndex].map((item, index) => {
            item === list.id && state.savehetComposeDataId[list.findIndex].splice(index, 1)
          })
          break
        default:
          break
      }
    },
    // 设置模型引擎
    SetEngine ({ state }, data) {
      state.engine_types = data
    },
    // 存储选择的包含维度
    SaveselectIncludesData ({ state }, data) {
      state.saveselectIncludesData = data
    },
    // 过滤掉已经选择的组合数据
    FilterMapping ({ state, getters }, data) {
      getters.measureTableList.map((item, index) => {
        data.forEach(n => {
          if (item.name !== n) {
            state.filterMappingData.push({
              value: item.name, id: item.id
            })
          }
        })
      })
      // state.filterMappingData = data
    },
    // 设置剩余的list
    SetrecordingData ({ state }, data) {
      state.recordingData = data
    }
  }
}

export default advancedSet
