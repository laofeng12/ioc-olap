const common = {
  state: {
    /* 建立表关系 */
    savemousedownData: [], // 存储已拖拽的数据
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
          },
          'last_modified': 0
        }
      },
      cube: {
        'cubeDescData': {
          'name': 'myCube',
          'model_name': 'aaa',
          'description': 'cc',
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
              'showDim': false
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
                'KYLIN_SALES.TRANS_ID',
                'KYLIN_SALES.PART_DT'
              ],
              'select_rule': {
                'hierarchy_dims': [
                  [
                    'KYLIN_CATEGORY_GROUPINGS.USER_DEFINED_FIELD1',
                    'KYLIN_CATEGORY_GROUPINGS.USER_DEFINED_FIELD3'
                  ]
                ],
                'mandatory_dims': [
                  'KYLIN_SALES.PART_DT'
                ],
                'joint_dims': [
                  [
                    'KYLIN_SALES.OPS_USER_ID',
                    'KYLIN_SALES.OPS_REGION',
                    'KYLIN_COUNTRY.NAME',
                    'KYLIN_ACCOUNT.ACCOUNT_COUNTRY'
                  ]
                ]
              }
            }
          ],
          'mandatory_dimension_set_list': [], // 高级设置（黑白名单设置）
          'partition_date_start': 0,
          'notify_list': [
            '278549309@qq.com'
          ],
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
          },
          'volatile_range': '0',
          'retention_range': '0',
          'status_need_notify': [
            'ERROR',
            'DISCARDED',
            'SUCCEED'
          ],
          'engine_type': 2,
          'storage_type': '2',
          'override_kylin_properties': {}
        }
      },
      body: {
        filterCondidion: [], // 刷新过滤设置
        INTERVAL: '', // 更新频率
        frequencytype: '', // 更新方式
        autoReload: false, // 是否自动刷新
        dataMany: false // 日期是否存在多列
      }
    }
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
      state.saveSelectFiledTree = []
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
