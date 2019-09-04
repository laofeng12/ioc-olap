const common = {
  state: {
    ModelAllList: [], // 所有的数据集合
    modelSelectData: [],
    /* 建立表关系 */
    totalSaveData: { // 总数据
      models: {
        modelDescData: {
          'name': 'model1',
          'description': '',
          'fact_table': 'KYLIN.KYLIN_SALES',
          'lookups': [
            {
              'table': 'KYLIN.KYLIN_CAL_DT',
              'alias': 'KYLIN_CAL_DT',
              'joinTable': 'KYLIN.KYLIN_SALES',
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
          'engine_type': 2,
          'description': '',
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
      cubeDatalaketableNew: {},
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
      state.totalSaveData.cubeDatalaketableNew = {}
      state.totalSaveData.cube.cubeDescData.name = ''
      state.totalSaveData.cube.cubeDescData.description = ''
    },
    // 合并设置的事实表到总表
    mergeFiledTable ({ state, getters, dispatch }, data) {
      getters.selectTableTotal.forEach((item, index) => {
        data[0].label === item.label ? getters.selectTableTotal[index]['filed'] = 1 : getters.selectTableTotal[index]['filed'] = 0
      })
      // dispatch('setSelectTableTotal')
    },
    // 获取编辑的数据
    SaveModelAllList ({ getters, store, state, dispatch }, data) {
      state.ModelAllList = data
      // 赋值第一步已选择的表
      console.log(data, '编辑需要的数据')
      data.TableList.map(item => {
        item.tableList.map(res => {
          getters.selectTableTotal.push({ label: res.table_name, id: res.table_id, resourceId: res.resourceId })
          getters.saveSelectTable.push({ label: res.table_name, id: res.table_id, resourceId: res.resourceId })
        })
      })
      // 赋值第二步模型的表
      getters.jointResultData.lookups = data.ModesList.lookups
      getters.jointResultData.fact_table = data.ModesList.fact_table
      // console.log('表的数据', getters.jointResultData.lookups)
      // 赋值第三步
      data.ModesList.dimensions.map(res => {
        getters.saveNewSortListstructure.push(res)
      })
      data.CubeList[0].dimensions.map((res, i) => {
        getters.saveSelectFiled.push({
          titName: res.name,
          name: res.column ? res.column : res.name,
          dataType: res.column_type,
          tableName: res.table,
          id: res.id,
          mode: res.derived ? '2' : '1'
        })
      })
      dispatch('SaveSelectFiled', getters.saveSelectFiled)
      // 赋值第四步
      data.CubeList[0].measures.map(item => {
        getters.measureTableList.push(item)
      })
      // 赋值第五步
      getters.reloadData.frequencytype = data.timingreFresh.frequencytype
      getters.reloadData.autoReload = !!data.timingreFresh.interval
      getters.reloadData.interval = Number(data.timingreFresh.interval)
      getters.reloadData.partition_type = !!data.ModesList.partition_desc.partition_time_format
      let result = data.ModesList.partition_desc.partition_date_column
      getters.reloadData.data1a = result.split('.')[0]
      getters.reloadData.data1b = result.split('.')[1]
      getters.reloadData.partition_date_format = data.ModesList.partition_desc.partition_date_format
      getters.reloadData.partition_time_format = data.ModesList.partition_desc.partition_time_format
      console.log(data.filterCondidion, '1')
      data.filterCondidion.map(item => { getters.relaodFilterList.push(item) })

      // 赋值第六步
      data.CubeList[0].aggregation_groups.map((item, index) => {
        getters.aggregation_groups[index].includes = item.includes
        getters.aggregation_groups[index].select_rule = item.select_rule
        getters.selectDataidList[index].includesId = item.includes
        getters.selectDataidList[index].necessaryDataId = item.select_rule.mandatory_dims
        getters.selectDataidList[index].levelDataId = item.select_rule.hierarchy_dims
        getters.selectDataidList[index].jointDataId = item.select_rule.joint_dims
      })
      // hbase_mapping  mandatory_dimension_set_list
      data.CubeList[0].hbase_mapping.column_family.map((item, index) => {
        getters.hbase_mapping.column_family[index] = item
        getters.savehetComposeDataId[index] = item
      })
      state.totalSaveData.cube.cubeDescData.name = data.CubeList[0].name
      state.totalSaveData.cube.cubeDescData.description = data.CubeList[0].description
      state.totalSaveData.cube.cubeDescData.uuid = data.ModesList.uuid
      console.log(getters)
      state.totalSaveData.cube.engine_type = data.CubeList[0].engine_type
      // console.log('第六步===', getters.aggregation_groups)
    }
  }
}

export default common
