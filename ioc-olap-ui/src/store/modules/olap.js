import { setLocalStorage } from '@/utils/index'
const common = {
  state: {
    HeadNum: 1,
    ModelAllList: [], // 所有的数据集合
    /* 建立表关系 */
    totalSaveData: { // 总数据
      models: {
        modelDescData: {
          'name': '',
          'description': '',
          'uuid': '',
          'last_modified': '',
          'version': '',
          'fact_table': '',
          'lookups': [
            {
              'table': 'KYLIN.KYLIN_CAL_DT',
              'alias': 'KYLIN_CAL_DT',
              'joinTable': '',
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
            }
          ],
          'filter_condition': '',
          'dimensions': [ // 设置维度选择字段 （选择的维度）
            {
              'table': '',
              'realName': '',
              'columns': []
            },
            {
              'table': '',
              'realName': '',
              'columns': []
            }
          ],
          'metrics': [],
          'partition_desc': { // 刷新及过滤
            // 'partition_date_column': null, // 表
            // 'partition_type': 'APPEND' // 字段
            // // 'partition_date_format': null // 日期
          }
          // 'last_modified': 0
        }
      },
      cube: {
        'cubeDescData': {
          'name': '',
          'uuid': '',
          'model_name': '',
          'engine_type': '2',
          'description': '',
          'last_modified': '',
          'version': '',
          'dimensions': [ // 设置维度表
            {
              'name': 'USER_DEFINED_FIELD1',
              'table': 'KYLIN_CATEGORY_GROUPINGS',
              'realName': 'KYLIN_CATEGORY_GROUPINGS',
              'column': 'USER_DEFINED_FIELD1'
            },
            {
              'name': 'USER_DEFINED_FIELD3',
              'table': 'KYLIN_CATEGORY_GROUPINGS',
              'realName': 'KYLIN_CATEGORY_GROUPINGS',
              'column': 'USER_DEFINED_FIELD3'
            }
          ],
          'measures': [ // 度量
            {
              'name': 'PriceSum',
              'function': {
                'expression': 'SUM',
                'returntype': 'decimal(19,4)',
                'parameter': {
                  'type': 'column',
                  'value': 'KYLIN_SALES.PRICE'
                }
              },
              'showDim': true
            },
            {
              'name': 'TOP_SELLER',
              'function': {
                'expression': 'TOP_N',
                'returntype': 'topn(100)',
                'parameter': {
                  'type': 'column',
                  'value': 'KYLIN_SALES.PRICE',
                  'next_parameter': {
                    'type': 'column',
                    'value': 'KYLIN_SALES.SELLER_ID'
                  }
                },
                'configuration': {
                  'topn.encoding.KYLIN_SALES.SELLER_ID': 'dict',
                  'topn.encoding_version.KYLIN_SALES.SELLER_ID': '1'
                }
              },
              'showDim': false
            }
          ],
          'dictionaries': [],
          'rowkey': { // 高级设置过滤
            'rowkey_columns': [
              {
                'column': 'KYLIN_SALES.SELLER_ID',
                'encoding': 'integer:4',
                'isShardBy': false,
                'encoding_version': '1'
              }
            ]
          },
          'aggregation_groups': [ // 高级设置
            {
              'includes': [
              ],
              'select_rule': {
                'hierarchy_dims': [
                ],
                'mandatory_dims': [
                ],
                'joint_dims': [
                ]
              }
            }
          ],
          'mandatory_dimension_set_list': [], // 高级设置（黑白名单设置）
          // 'partition_date_start': 0,
          // 'notify_list': [
          //   '278549309@qq.com'
          // ],
          'hbase_mapping': { // 高级设置（高级列组合）
            'column_family': [
              {
                'name': 'F1',
                'columns': [
                  {
                    'qualifier': 'M',
                    'measure_refs': [
                      'PriceSum',
                      'Sell_Level_Sum',
                      'CNT'
                    ]
                  }
                ]
              },
              {
                'name': 'F2',
                'columns': [
                  {
                    'qualifier': 'M',
                    'measure_refs': [
                      'SELLER_CNT_HLL',
                      'TOP_SELLER'
                    ]
                  }
                ]
              }
            ]
          }
          // 'volatile_range': '0',
          // 'retention_range': '0',
          // 'status_need_notify': [
          //   'ERROR',
          //   'DISCARDED',
          //   'SUCCEED'
          // ],
          // 'storage_type': '2',
          // 'override_kylin_properties': {}
        }
      },
      filterCondidion: [], // 刷新过滤设置
      timingreFresh: {
        interval: '', // 更新频率
        frequencytype: '', // 更新方式
        autoReload: false, // 是否自动刷新
        dataMany: false // 日期是否存在多列
      },
      cubeDatalaketableNew: [],
      dimensionLength: '',
      dimensionFiledLength: '',
      measureFiledLength: ''
    }
  },
  actions: {
    resetList ({ state }) {
      state.totalSaveData.filterCondidion = []
      state.totalSaveData.timingreFresh = {
        interval: '',
        frequencytype: '',
        autoReload: false,
        dataMany: false
      }
      state.totalSaveData.cubeDatalaketableNew = []
      state.totalSaveData.cube.cubeDescData.name = ''
      state.totalSaveData.cube.cubeDescData.uuid = ''
      state.totalSaveData.cube.cubeDescData.model_name = ''
      state.totalSaveData.cube.cubeDescData.last_modified = ''
      state.totalSaveData.cube.cubeDescData.version = ''
      state.totalSaveData.cube.cubeDescData.description = ''
      state.totalSaveData.models.modelDescData.uuid = ''
      state.totalSaveData.models.modelDescData.name = ''
      state.totalSaveData.models.modelDescData.version = ''
      state.totalSaveData.models.modelDescData.last_modified = ''
      state.ModelAllList = []
    },
    // 合并设置的事实表到总表
    mergeFiledTable ({ state, getters, dispatch }, data) {
      getters.selectTableTotal.forEach((item, index) => {
        data[0].label === item.label ? getters.selectTableTotal[index]['filed'] = 1 : getters.selectTableTotal[index]['filed'] = 0
      })
      getters.jointResultData.fact_table = `${data[0].database}.${data[0].label}`
    },
    // 获取编辑的数据
    async SaveModelAllList ({ getters, store, state, dispatch }, data) {
      state.ModelAllList = data
      setLocalStorage('ModelAllList', data)
      // 赋值第一步已选择的表
      console.log(data, '编辑需要的数据')
      data.TableList.map((item, index) => {
        item.tableList.map(res => {
          getters.selectTableTotal.push({ label: res.table_name, id: res.table_id, resourceId: res.resourceId, database: item.orgName, orgId: item.orgId, ...res })
          getters.saveSelectTable.push({ label: res.table_name, id: res.table_id, resourceId: res.resourceId, database: item.orgName, orgId: item.orgId, ...res })
        })
      })
      // 判断是否是编辑进来的，如实编辑进来的需要主动调用存储第一步的方法
      state.ModelAllList.TableList && await dispatch('SavestepSelectData', getters.ModelAllList.TableList)
      // 赋值第二步模型的表
      getters.jointResultData.lookups = data.ModesList.lookups
      getters.jointResultData.fact_table = data.ModesList.fact_table
      // 赋值第三步
      data.ModesList.dimensions.map(res => {
        getters.saveNewSortListstructure.push(res)
      })
      data.CubeList.dimensions.map((res, i) => {
        getters.saveSelectFiled.push({
          titName: res.name,
          name: res.column ? res.column : res.name,
          dataType: res.column_type,
          tableName: res.table,
          filed: res.table === data.ModesList.fact_table.split('.')[1] ? '1' : '0',
          id: res.id,
          mode: res.derived ? '2' : '1'
        })
      })
      dispatch('SaveSelectFiled', getters.saveSelectFiled)
      // 赋值第四步
      data.CubeList.measures.map(item => {
        getters.measureTableList.push(item)
      })
      // 赋值第五步
      if (data.timingreFresh) {
        getters.reloadData.frequencytype = data.timingreFresh.frequencytype
        getters.reloadData.autoReload = !!data.timingreFresh.interval
        getters.reloadData.interval = Number(data.timingreFresh.interval)
      }
      let resultDate = data.ModesList.partition_desc.partition_date_column
      let resultTime = data.ModesList.partition_desc.partition_time_column
      getters.reloadData.data1a = resultDate.split('.')[0]
      getters.reloadData.data1b = resultDate.split('.')[1]
      getters.reloadData.partition_date_format = data.ModesList.partition_desc.partition_date_format
      if (resultTime) {
        getters.reloadData.partition_time_format = data.ModesList.partition_desc.partition_time_format
        getters.reloadData.data2a = resultTime.split('.')[0]
        getters.reloadData.data2b = resultTime.split('.')[1]
      }
      data.filterCondidion.map(item => { getters.relaodFilterList.push(item) })
      getters.reloadData.partition_type = !!resultTime
      // 赋值第六步
      data.CubeList.aggregation_groups.map((item, index) => {
        getters.aggregation_groups[index].includes = item.includes
        getters.aggregation_groups[index].select_rule = item.select_rule
        getters.selectDataidList[index].includesId = item.includes
        getters.selectDataidList[index].necessaryDataId = item.select_rule.mandatory_dims
        getters.selectDataidList[index].levelDataId = item.select_rule.hierarchy_dims
        getters.selectDataidList[index].jointDataId = item.select_rule.joint_dims
        item.includes.map(res => { getters.saveselectIncludesData.push(res) })
      })
      // hbase_mapping  mandatory_dimension_set_list
      data.CubeList.hbase_mapping.column_family.map((item, index) => {
        getters.hbase_mapping.column_family[index] = item
        item.columns.map((n, i) => {
          getters.savehetComposeDataId[index] = n.measure_refs
        })
      })
      // 赋值表名、字段数量、维度数量以及保存需要的uuid
      // Object.assign({}, state.totalSaveData, {
      //   cube: {
      //     cubeDescData: {
      //       name: data.CubeList.name,
      //       version: data.CubeList.version,
      //       last_modified: data.CubeList.last_modified,
      //       model_name: data.CubeList.model_name,
      //       description: data.CubeList.description,
      //       uuid: data.CubeList.uuid
      //     },
      //     engine_type: data.CubeList.engine_type
      //   },
      //   models: {
      //     modelDescData: {
      //       uuid: data.ModesList.uuid,
      //       name: data.ModesList.name,
      //       version: data.ModesList.version,
      //       last_modified: data.ModesList.last_modified
      //     }
      //   }
      // })
      state.totalSaveData.cube.cubeDescData.name = data.CubeList.name
      state.totalSaveData.cube.cubeDescData.version = data.CubeList.version
      state.totalSaveData.cube.cubeDescData.last_modified = data.CubeList.last_modified
      state.totalSaveData.cube.cubeDescData.model_name = data.CubeList.model_name
      state.totalSaveData.cube.cubeDescData.description = data.CubeList.description
      state.totalSaveData.cube.cubeDescData.uuid = data.CubeList.uuid
      state.totalSaveData.models.modelDescData.uuid = data.ModesList.uuid
      state.totalSaveData.models.modelDescData.name = data.ModesList.name
      state.totalSaveData.models.modelDescData.version = data.ModesList.version
      state.totalSaveData.models.modelDescData.last_modified = data.ModesList.last_modified
      state.totalSaveData.cube.engine_type = data.CubeList.engine_type
    }
  }
}

export default common
