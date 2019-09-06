<template>
  <div class="creates">
    <header>
      <!-- <el-form :rules="rules" :model="totalSaveData"> -->
      <el-form>
        <el-button icon="el-icon-arrow-left" @click='tobackList'></el-button>
        <!-- <el-form-item prop="cube.cubeDescData.name"> -->
          <el-input v-model="totalSaveData.cube.cubeDescData.name" @blur="blurIpt" maxlength="20" placeholder="请输入模型名称(1~20字)"></el-input>
        <!-- </el-form-item> -->
      </el-form>
    </header>
    <head-box :selectId="selectStep"></head-box>
    <div v-loading="isLoading">
      <transition name="fade-transform" mode="out-in">
        <router-view></router-view>
      </transition>
    </div>
  </div>
</template>

<script>
import headBox from '@/components/analysisComponent/modelCommon/head'
import { mapGetters } from 'vuex'
import { isChineseChar } from '@/utils/validate'
import { descDataList } from '@/api/modelList'
export default {
  components: { headBox },
  data () {
    return {
      selectStep: 1,
      isLoading: false,
      value: '',
      rules: {
        'cube.cubeDescData.name': [
          { required: true, message: '请输入模型名称', trigger: 'blur' }
        ]
      },
      json: {
        'measureFiledLength': null,
        'CubeList': [
          {
            'uuid': '4ed079e5-4af3-49bd-8f9d-2503533b6cbb',
            'last_modified': '1566995318966',
            'version': '2.3.1.0',
            'is_draft': false,
            'name': 'cezuobiao1',
            'model_name': '837954180900010',
            'description': 'asdasdasdasdasdasdasd',
            'dimensions': [
              {
                'name': 'TRANS_ID',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_SALES',
                'column': 'TRANS_ID',
                'columnType': null,
                'column_type': 'bigint',
                'id': null
              },
              {
                'name': 'SELLER_ID',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_SALES',
                'column': 'SELLER_ID',
                'columnType': null,
                'column_type': 'bigint',
                'id': null
              },
              {
                'name': 'BUYER_ID',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_SALES',
                'column': 'BUYER_ID',
                'columnType': null,
                'column_type': 'bigint',
                'id': null
              },
              {
                'name': 'OPS_USER_ID',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_SALES',
                'column': 'OPS_USER_ID',
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'OPS_REGION',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_SALES',
                'column': 'OPS_REGION',
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'LSTG_FORMAT_NAME',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_SALES',
                'column': 'LSTG_FORMAT_NAME',
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'YEAR_BEG_DT',
                'realName': null,
                'derived': [
                  'YEAR_BEG_DT'
                ],
                'table': 'KYLIN_CAL_DT',
                'column': null,
                'columnType': null,
                'column_type': 'date',
                'id': null
              },
              {
                'name': 'MONTH_BEG_DT',
                'realName': null,
                'derived': [
                  'MONTH_BEG_DT'
                ],
                'table': 'KYLIN_CAL_DT',
                'column': null,
                'columnType': null,
                'column_type': 'date',
                'id': null
              },
              {
                'name': 'WEEK_BEG_DT',
                'realName': null,
                'derived': [
                  'WEEK_BEG_DT'
                ],
                'table': 'KYLIN_CAL_DT',
                'column': null,
                'columnType': null,
                'column_type': 'date',
                'id': null
              },
              {
                'name': 'USER_DEFINED_FIELD1',
                'realName': null,
                'derived': [
                  'USER_DEFINED_FIELD1'
                ],
                'table': 'KYLIN_CATEGORY_GROUPINGS',
                'column': null,
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'USER_DEFINED_FIELD3',
                'realName': null,
                'derived': [
                  'USER_DEFINED_FIELD3'
                ],
                'table': 'KYLIN_CATEGORY_GROUPINGS',
                'column': null,
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'META_CATEG_NAME',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_CATEGORY_GROUPINGS',
                'column': 'META_CATEG_NAME',
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'CATEG_LVL2_NAME',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_CATEGORY_GROUPINGS',
                'column': 'CATEG_LVL2_NAME',
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'CATEG_LVL3_NAME',
                'realName': null,
                'derived': null,
                'table': 'KYLIN_CATEGORY_GROUPINGS',
                'column': 'CATEG_LVL3_NAME',
                'columnType': null,
                'column_type': 'varchar(256)',
                'id': null
              },
              {
                'name': 'ACCOUNT_BUYER_LEVEL',
                'realName': null,
                'derived': null,
                'table': 'BUYER_ACCOUNT',
                'column': 'ACCOUNT_BUYER_LEVEL',
                'columnType': null,
                'column_type': 'integer',
                'id': null
              },
              {
                'name': 'BUYER_COUNTRY',
                'realName': null,
                'derived': null,
                'table': 'BUYER_ACCOUNT',
                'column': 'ACCOUNT_COUNTRY',
                'columnType': null,
                'column_type': null,
                'id': null
              },
              {
                'name': 'ACCOUNT_SELLER_LEVEL',
                'realName': null,
                'derived': null,
                'table': 'SELLER_ACCOUNT',
                'column': 'ACCOUNT_SELLER_LEVEL',
                'columnType': null,
                'column_type': 'integer',
                'id': null
              },
              {
                'name': 'SELLER_COUNTRY',
                'realName': null,
                'derived': null,
                'table': 'SELLER_ACCOUNT',
                'column': 'ACCOUNT_COUNTRY',
                'columnType': null,
                'column_type': null,
                'id': null
              },
              {
                'name': 'BUYER_COUNTRY_NAME',
                'realName': null,
                'derived': null,
                'table': 'BUYER_COUNTRY',
                'column': 'NAME',
                'columnType': null,
                'column_type': null,
                'id': null
              },
              {
                'name': 'SELLER_COUNTRY_NAME',
                'realName': null,
                'derived': null,
                'table': 'SELLER_COUNTRY',
                'column': 'NAME',
                'columnType': null,
                'column_type': null,
                'id': null
              }
            ],
            'measures': [
              {
                'name': 'GMV_SUM',
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
                'name': 'BUYER_LEVEL_SUM',
                'function': {
                  'expression': 'SUM',
                  'returntype': 'integer',
                  'parameter': {
                    'type': 'column',
                    'value': 'BUYER_ACCOUNT.ACCOUNT_BUYER_LEVEL'
                  }
                },
                'showDim': false
              },
              {
                'name': 'SELLER_LEVEL_SUM',
                'function': {
                  'expression': 'SUM',
                  'returntype': 'integer',
                  'parameter': {
                    'type': 'column',
                    'value': 'SELLER_ACCOUNT.ACCOUNT_SELLER_LEVEL'
                  }
                },
                'showDim': false
              }
            ],
            'dictionaries': [],
            'rowkey': {
              'rowkey_columns': [
                {
                  'column': 'KYLIN_SALES.TRANS_ID',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.SELLER_ID',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.BUYER_ID',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.OPS_USER_ID',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.OPS_REGION',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.LSTG_FORMAT_NAME',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_CATEGORY_GROUPINGS.META_CATEG_NAME',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_CATEGORY_GROUPINGS.CATEG_LVL2_NAME',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_CATEGORY_GROUPINGS.CATEG_LVL3_NAME',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'BUYER_ACCOUNT.ACCOUNT_COUNTRY',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'BUYER_ACCOUNT.ACCOUNT_BUYER_LEVEL',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'SELLER_ACCOUNT.ACCOUNT_COUNTRY',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'SELLER_ACCOUNT.ACCOUNT_SELLER_LEVEL',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'BUYER_COUNTRY.NAME',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'SELLER_COUNTRY.NAME',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.PART_DT',
                  'encoding': 'date',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.LEAF_CATEG_ID',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                },
                {
                  'column': 'KYLIN_SALES.LSTG_SITE_ID',
                  'encoding': 'dict',
                  'isShardBy': 'false',
                  'encoding_version': null
                }
              ]
            },
            'aggregation_groups': [
              {
                'includes': [
                  'KYLIN_SALES.TRANS_ID',
                  'KYLIN_SALES.SELLER_ID',
                  'KYLIN_SALES.BUYER_ID',
                  'KYLIN_SALES.OPS_USER_ID',
                  'KYLIN_SALES.OPS_REGION',
                  'KYLIN_SALES.LSTG_FORMAT_NAME',
                  'KYLIN_CATEGORY_GROUPINGS.META_CATEG_NAME',
                  'KYLIN_CATEGORY_GROUPINGS.CATEG_LVL2_NAME',
                  'KYLIN_CATEGORY_GROUPINGS.CATEG_LVL3_NAME',
                  'BUYER_ACCOUNT.ACCOUNT_COUNTRY',
                  'BUYER_ACCOUNT.ACCOUNT_BUYER_LEVEL',
                  'SELLER_ACCOUNT.ACCOUNT_COUNTRY',
                  'SELLER_ACCOUNT.ACCOUNT_SELLER_LEVEL',
                  'BUYER_COUNTRY.NAME',
                  'SELLER_COUNTRY.NAME',
                  'KYLIN_SALES.PART_DT',
                  'KYLIN_SALES.LEAF_CATEG_ID',
                  'KYLIN_SALES.LSTG_SITE_ID'
                ],
                'select_rule': {
                  'hierarchy_dims': [
                    [
                      'KYLIN_CATEGORY_GROUPINGS.META_CATEG_NAME',
                      'KYLIN_CATEGORY_GROUPINGS.CATEG_LVL2_NAME',
                      'KYLIN_CATEGORY_GROUPINGS.CATEG_LVL3_NAME',
                      'KYLIN_SALES.LEAF_CATEG_ID'
                    ]
                  ],
                  'mandatory_dims': [
                    'KYLIN_SALES.PART_DT'
                  ],
                  'joint_dims': [
                    [
                      'BUYER_ACCOUNT.ACCOUNT_COUNTRY',
                      'BUYER_COUNTRY.NAME'
                    ],
                    [
                      'SELLER_ACCOUNT.ACCOUNT_COUNTRY',
                      'SELLER_COUNTRY.NAME'
                    ],
                    [
                      'BUYER_ACCOUNT.ACCOUNT_BUYER_LEVEL',
                      'SELLER_ACCOUNT.ACCOUNT_SELLER_LEVEL'
                    ],
                    [
                      'KYLIN_SALES.LSTG_FORMAT_NAME',
                      'KYLIN_SALES.LSTG_SITE_ID'
                    ],
                    [
                      'KYLIN_SALES.OPS_REGION',
                      'KYLIN_SALES.OPS_USER_ID'
                    ]
                  ]
                }
              }
            ],
            'mandatory_dimension_set_list': [],
            'partition_date_start': 0,
            'notify_list': null,
            'hbase_mapping': {
              'column_family': [
                {
                  'name': 'F1',
                  'columns': [
                    {
                      'qualifier': 'M',
                      'measure_refs': [
                        'GMV_SUM',
                        'SELLER_LEVEL_SUM',
                        'BUYER_LEVEL_SUM',
                        '_COUNT_'
                      ]
                    }
                  ]
                }
              ]
            },
            'volatile_range': '0',
            'retention_range': '0',
            'status_need_notify': [],
            'auto_merge_time_ranges': null,
            'engine_type': 2,
            'storage_type': '0',
            'columns_Type': null,
            'override_kylin_properties': {
              'kylin.cube.aggrgroup.is-mandatory-only-valid': 'true',
              'kylin.cube.aggrgroup.max-combination': '2147483647'
            },
            '_draft': false
          }
        ],
        'ModesList': {
          'uuid': 'c382dc72-4f88-4b6c-a87a-579f9d221d99',
          'last_modified': 1566995318495,
          'version': '2.3.1.0',
          'name': '837954180900010',
          'owner': 'ADMIN',
          'description': '',
          'fact_table': 'DEFAULT.KYLIN_SALES',
          'lookups': [
            {
              'table': 'DEFAULT.KYLIN_CAL_DT',
              'kind': 'LOOKUP',
              'alias': 'KYLIN_CAL_DT',
              'joinTable': 'KYLIN_SALES',
              'joinAlias': 'KYLIN_SALES',
              'joinId': '3fa28668-b02c-49cf-aa53-008e06c49cb7',
              'join': {
                'type': 'inner',
                'primary_key': [
                  'KYLIN_CAL_DT.CAL_DT'
                ],
                'foreign_key': [
                  'KYLIN_SALES.PART_DT'
                ],
                'isCompatible': null,
                'pk_type': [
                  'date'
                ],
                'fk_type': [
                  'date'
                ]
              },
              'SAxis': 259,
              'YAxis': 126,
              'joinSAxis': 272,
              'joinYAxis': 335,
              'id': 'a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972',
              'yaxis': 126,
              'saxis': 259
            },
            {
              'table': 'DEFAULT.KYLIN_CATEGORY_GROUPINGS',
              'kind': 'LOOKUP',
              'alias': 'KYLIN_CATEGORY_GROUPINGS',
              'joinTable': 'KYLIN_SALES',
              'joinAlias': 'KYLIN_SALES',
              'joinId': '3fa28668-b02c-49cf-aa53-008e06c49cb7',
              'join': {
                'type': 'inner',
                'primary_key': [
                  'KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID',
                  'KYLIN_CATEGORY_GROUPINGS.SITE_ID'
                ],
                'foreign_key': [
                  'KYLIN_SALES.LEAF_CATEG_ID',
                  'KYLIN_SALES.LSTG_SITE_ID'
                ],
                'isCompatible': null,
                'pk_type': [
                  'bigint',
                  'integer'
                ],
                'fk_type': [
                  'bigint',
                  'integer'
                ]
              },
              'SAxis': -17,
              'YAxis': 331,
              'joinSAxis': 272,
              'joinYAxis': 335,
              'id': 'c65b5c62-6d14-4456-bc37-2f7bff28fcca',
              'yaxis': 331,
              'saxis': -17
            },
            {
              'table': 'DEFAULT.KYLIN_ACCOUNT',
              'kind': 'LOOKUP',
              'alias': 'BUYER_ACCOUNT',
              'joinTable': 'KYLIN_SALES',
              'joinAlias': 'KYLIN_SALES',
              'joinId': '3fa28668-b02c-49cf-aa53-008e06c49cb7',
              'join': {
                'type': 'inner',
                'primary_key': [
                  'BUYER_ACCOUNT.ACCOUNT_ID'
                ],
                'foreign_key': [
                  'KYLIN_SALES.BUYER_ID'
                ],
                'isCompatible': null,
                'pk_type': [
                  'bigint'
                ],
                'fk_type': [
                  'bigint'
                ]
              },
              'SAxis': 463,
              'YAxis': 336,
              'joinSAxis': 272,
              'joinYAxis': 335,
              'id': '2abeafff-b218-46de-b06c-abd10365d273',
              'yaxis': 336,
              'saxis': 463
            },
            {
              'table': 'DEFAULT.KYLIN_ACCOUNT',
              'kind': 'LOOKUP',
              'alias': 'SELLER_ACCOUNT',
              'joinTable': 'KYLIN_SALES',
              'joinAlias': 'KYLIN_SALES',
              'joinId': '3fa28668-b02c-49cf-aa53-008e06c49cb7',
              'join': {
                'type': 'inner',
                'primary_key': [
                  'SELLER_ACCOUNT.ACCOUNT_ID'
                ],
                'foreign_key': [
                  'KYLIN_SALES.SELLER_ID'
                ],
                'isCompatible': null,
                'pk_type': [
                  'bigint'
                ],
                'fk_type': [
                  'bigint'
                ]
              },
              'SAxis': 267,
              'YAxis': 427,
              'joinSAxis': 272,
              'joinYAxis': 335,
              'id': '2abeafff-b218-46de-b06c-abd10365d273',
              'yaxis': 427,
              'saxis': 267
            },
            {
              'table': 'DEFAULT.KYLIN_COUNTRY',
              'kind': 'LOOKUP',
              'alias': 'BUYER_COUNTRY',
              'joinTable': 'KYLIN_ACCOUNT',
              'joinAlias': 'BUYER_ACCOUNT',
              'joinId': '2abeafff-b218-46de-b06c-abd10365d273',
              'join': {
                'type': 'inner',
                'primary_key': [
                  'BUYER_COUNTRY.COUNTRY'
                ],
                'foreign_key': [
                  'BUYER_ACCOUNT.ACCOUNT_COUNTRY'
                ],
                'isCompatible': null,
                'pk_type': [
                  'varchar(256)'
                ],
                'fk_type': [
                  'varchar(256)'
                ]
              },
              'SAxis': 527,
              'YAxis': 167,
              'joinSAxis': 463,
              'joinYAxis': 336,
              'id': '34ac809f-7728-423b-81af-29f931c2da39',
              'yaxis': 167,
              'saxis': 527
            },
            {
              'table': 'DEFAULT.KYLIN_COUNTRY',
              'kind': 'LOOKUP',
              'alias': 'SELLER_COUNTRY',
              'joinTable': 'KYLIN_ACCOUNT',
              'joinAlias': 'SELLER_ACCOUNT',
              'joinId': '2abeafff-b218-46de-b06c-abd10365d273',
              'join': {
                'type': 'inner',
                'primary_key': [
                  'SELLER_COUNTRY.COUNTRY'
                ],
                'foreign_key': [
                  'SELLER_ACCOUNT.ACCOUNT_COUNTRY'
                ],
                'isCompatible': null,
                'pk_type': [
                  'varchar(256)'
                ],
                'fk_type': [
                  'varchar(256)'
                ]
              },
              'SAxis': 322,
              'YAxis': 533,
              'joinSAxis': 267,
              'joinYAxis': 427,
              'id': '34ac809f-7728-423b-81af-29f931c2da39',
              'yaxis': 533,
              'saxis': 322
            }
          ],
          'dimensions': [
            {
              'table': 'KYLIN_CAL_DT',
              'columns': [
                'YEAR_BEG_DT',
                'MONTH_BEG_DT',
                'WEEK_BEG_DT',
                'CAL_DT'
              ]
            },
            {
              'table': 'KYLIN_CATEGORY_GROUPINGS',
              'columns': [
                'USER_DEFINED_FIELD1',
                'USER_DEFINED_FIELD3',
                'META_CATEG_NAME',
                'CATEG_LVL2_NAME',
                'CATEG_LVL3_NAME',
                'LEAF_CATEG_ID',
                'SITE_ID'
              ]
            },
            {
              'table': 'KYLIN_SALES',
              'columns': [
                'TRANS_ID',
                'SELLER_ID',
                'BUYER_ID',
                'OPS_USER_ID',
                'OPS_REGION',
                'LSTG_FORMAT_NAME',
                'PART_DT',
                'LEAF_CATEG_ID',
                'LSTG_SITE_ID'
              ]
            },
            {
              'table': 'BUYER_ACCOUNT',
              'columns': [
                'ACCOUNT_COUNTRY',
                'ACCOUNT_BUYER_LEVEL',
                'ACCOUNT_ID'
              ]
            },
            {
              'table': 'SELLER_ACCOUNT',
              'columns': [
                'ACCOUNT_COUNTRY',
                'ACCOUNT_SELLER_LEVEL',
                'ACCOUNT_ID'
              ]
            },
            {
              'table': 'BUYER_COUNTRY',
              'columns': [
                'NAME',
                'COUNTRY'
              ]
            },
            {
              'table': 'SELLER_COUNTRY',
              'columns': [
                'NAME',
                'COUNTRY'
              ]
            }
          ],
          'metrics': [],
          'filter_condition': '',
          'partition_desc': {
            'partition_date_column': '',
            'partition_date_start': 0,
            'partition_date_format': '',
            'partition_time_format': 'HH:mm:ss',
            'partition_type': 'APPEND',
            'partition_condition_builder': 'org.apache.kylin.metadata.model.PartitionDesc$DefaultPartitionConditionBuilder',
            'interval': null,
            'frequencytype': null
          },
          'capacity': 'MEDIUM',
          'SAxis': 259,
          'YAxis': 126,
          'joinSAxis': 272,
          'joinYAxis': 335,
          'joinAlias': null,
          'joinId': null,
          'joinTable': null,
          'yaxis': 126,
          'saxis': 259
        },
        'dimensionLength': null,
        'TableList': [
          {
            'orgId': 1,
            'orgName': 'DEFAULT',
            'tableList': [
              {
                'cubeName': 'cezuobiao1',
                'orgName': 'DEFAULT',
                'orgId': 1,
                'table_id': 'c65b5c62-6d14-4456-bc37-2f7bff28fcca',
                'table_name': 'KYLIN_CATEGORY_GROUPINGS',
                'resourceId': 'c65b5c62-6d14-4456-bc37-2f7bff28fcca',
                'database': 'DEFAULT',
                'type': '1',
                'filed': 0,
                'isNew': null
              },
              {
                'cubeName': 'cezuobiao1',
                'orgName': 'DEFAULT',
                'orgId': 1,
                'table_id': '3fa28668-b02c-49cf-aa53-008e06c49cb7',
                'table_name': 'KYLIN_SALES',
                'resourceId': '3fa28668-b02c-49cf-aa53-008e06c49cb7',
                'database': 'DEFAULT',
                'type': '1',
                'filed': 1,
                'isNew': null
              },
              {
                'cubeName': 'cezuobiao1',
                'orgName': 'DEFAULT',
                'orgId': 1,
                'table_id': '2abeafff-b218-46de-b06c-abd10365d273',
                'table_name': 'KYLIN_ACCOUNT',
                'resourceId': '2abeafff-b218-46de-b06c-abd10365d273',
                'database': 'DEFAULT',
                'type': '1',
                'filed': 0,
                'isNew': null
              },
              {
                'cubeName': 'cezuobiao1',
                'orgName': 'DEFAULT',
                'orgId': 1,
                'table_id': 'a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972',
                'table_name': 'KYLIN_CAL_DT',
                'resourceId': 'a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972',
                'database': 'DEFAULT',
                'type': '1',
                'filed': 0,
                'isNew': null
              },
              {
                'cubeName': 'cezuobiao1',
                'orgName': 'DEFAULT',
                'orgId': 1,
                'table_id': '34ac809f-7728-423b-81af-29f931c2da39',
                'table_name': 'KYLIN_COUNTRY',
                'resourceId': '34ac809f-7728-423b-81af-29f931c2da39',
                'database': 'DEFAULT',
                'type': '1',
                'filed': 0,
                'isNew': null
              }
            ]
          }
        ],
        'dimensionFiledLength': null,
        'timingrefresh': null
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      console.log(this.totalSaveData.cube.cubeDescData.name)
      // this.$store.dispatch('SaveModelAllList', this.json)
      if (this.$route.query.cubeName) {
        this.isLoading = true
        let { cubeName, models } = this.$route.query
        const params = {
          cubeName, models
        }
        descDataList(params).then(res => {
          if (res.CubeList) {
            this.isLoading = false
            this.$store.dispatch('SaveModelAllList', res)
          }
        })
      }
    },
    getStepCountAdd (val) {
      this.selectStep += 1
    },
    getStepCountReduce (val) {
      this.selectStep -= 1
    },
    tobackList () {
      this.$router.push('/analysisModel/Configuration')
      this.$store.dispatch('resetList')
    },
    blurIpt (val) {
      let result = val.target.value
      let flag = isChineseChar(val.target.value)
      if (!result) return this.$message.warning('请填写模型名称~')
      if (flag) return this.$message.warning('请勿输入中文~')
    }
  },
  computed: {
    ...mapGetters({
      totalSaveData: 'totalSaveData'
    })
  }
}
</script>
<style lang="stylus" scoped>
.creates{
  position relative
  margin-top -25px
  width 100%
  header{
    height 40px
    width 100%
    background #F2F2F2
    margin-bottom 15px
    >>>.el-button{
      float left
      background #F2F2F2
      border none
      font-size 20px
      height 38px
      border-right 1px solid #ccc
    }
    >>>.el-input{
      float left
      width 200px
      margin-left 20px
      height 30px
      margin-top 5px
      .el-input__inner{
        height 30px
      }
    }
  }
  .showBox{
    opacity 1
    >>>.serchTable{
      height auto
    }
    >>>.selectStep >>>.el-tabs__content{
     height calc(100vh - 200px)
    }
  }
  .showBox.selectStep >>>.el-tabs__content{
    height calc(100vh - 200px)
  }
  .hideBox{
    opacity 0
    height 0
    >>>.serchTable{
      height 0
      width 0
    }
    >>>.containers{
      height 0
    }
  }
  .hideBox.selectStep >>>.el-tabs__content{
    height 0
  }
}
</style>
