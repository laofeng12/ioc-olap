<template>
  <div class="mechanism">
    <el-input type="text" placeholder="输入机构名称筛选" v-model="serachvalue" clearable></el-input>
    <div class="trees">
      <el-tree
      :data="treeList"
        ref="tree"
        v-loading="treeLoading"
        :expand-on-click-node="false"
        node-key="id"
        @node-expand="nodeExpand"
        :highlight-current="showTree"
        @node-click="getCurrents"
        :default-expanded-keys="defaultOpenKeys"
        :render-content="renderContent"
        :filter-node-method="filterNode"
        :props="defaultProps">
      </el-tree>
    </div>
  </div>
</template>

<script>
import { setTimeout } from 'timers'
import { mapGetters } from 'vuex'
export default {
  data () {
    return {
      treeLoading: false,
      showTree: true,
      serachvalue: '',
      // treeList: [
      //   {
      //     label: '数据湖',
      //     id: '1337',
      //     children: []
      //   },
      //   {
      //     label: '自建目录',
      //     id: '1338',
      //     children: []
      //   }
      // ],
      treeList: [
        {
          label: 'DEFAULT',
          id: '1337',
          children: []
        },
        {
          label: 'KYLIN',
          id: '1338',
          children: []
        }
      ],
      defaultOpenKeys: [], // 默认展开的key
      defaultProps: {
        children: 'children',
        label: 'label'
      },
      jsonData: [
        {
          'columns': [
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_ACCOUNT',
              'column_NAME': 'ACCOUNT_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 1,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_ACCOUNT',
              'column_NAME': 'ACCOUNT_BUYER_LEVEL',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'INTEGER',
              'data_TYPE': 4,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 2,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_ACCOUNT',
              'column_NAME': 'ACCOUNT_SELLER_LEVEL',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'INTEGER',
              'data_TYPE': 4,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 3,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_ACCOUNT',
              'column_NAME': 'ACCOUNT_COUNTRY',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 4,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            }
          ],
          'table_SCHEM': 'DEFAULT',
          'table_NAME': 'KYLIN_ACCOUNT',
          'table_CAT': 'defaultCatalog',
          'table_TYPE': 'TABLE',
          'remarks': null,
          'type_CAT': null,
          'type_SCHEM': null,
          'type_NAME': null,
          'self_REFERENCING_COL_NAME': null,
          'ref_GENERATION': null
        },
        {
          'columns': [
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CAL_DT',
              'column_NAME': 'CAL_DT',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DATE',
              'data_TYPE': 91,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 1,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CAL_DT',
              'column_NAME': 'YEAR_BEG_DT',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DATE',
              'data_TYPE': 91,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 2,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CAL_DT',
              'column_NAME': 'MONTH_BEG_DT',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DATE',
              'data_TYPE': 91,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 3,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CAL_DT',
              'column_NAME': 'WEEK_BEG_DT',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DATE',
              'data_TYPE': 91,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 4,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            }
          ],
          'table_SCHEM': 'DEFAULT',
          'table_NAME': 'KYLIN_CAL_DT',
          'table_CAT': 'defaultCatalog',
          'table_TYPE': 'TABLE',
          'remarks': null,
          'type_CAT': null,
          'type_SCHEM': null,
          'type_NAME': null,
          'self_REFERENCING_COL_NAME': null,
          'ref_GENERATION': null
        },
        {
          'columns': [
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'LEAF_CATEG_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 1,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'SITE_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'INTEGER',
              'data_TYPE': 4,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 2,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'USER_DEFINED_FIELD1',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 3,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'USER_DEFINED_FIELD3',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 4,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'META_CATEG_NAME',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 5,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'CATEG_LVL2_NAME',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 6,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
              'column_NAME': 'CATEG_LVL3_NAME',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 7,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            }
          ],
          'table_SCHEM': 'DEFAULT',
          'table_NAME': 'KYLIN_CATEGORY_GROUPINGS',
          'table_CAT': 'defaultCatalog',
          'table_TYPE': 'TABLE',
          'remarks': null,
          'type_CAT': null,
          'type_SCHEM': null,
          'type_NAME': null,
          'self_REFERENCING_COL_NAME': null,
          'ref_GENERATION': null
        },
        {
          'columns': [
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_COUNTRY',
              'column_NAME': 'COUNTRY',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 1,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_COUNTRY',
              'column_NAME': 'NAME',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 2,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            }
          ],
          'table_SCHEM': 'DEFAULT',
          'table_NAME': 'KYLIN_COUNTRY',
          'table_CAT': 'defaultCatalog',
          'table_TYPE': 'TABLE',
          'remarks': null,
          'type_CAT': null,
          'type_SCHEM': null,
          'type_NAME': null,
          'self_REFERENCING_COL_NAME': null,
          'ref_GENERATION': null
        },
        {
          'columns': [
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'TRANS_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 1,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'PART_DT',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DATE',
              'data_TYPE': 91,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 2,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'LSTG_FORMAT_NAME',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 3,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'LEAF_CATEG_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 4,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'LSTG_SITE_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'INTEGER',
              'data_TYPE': 4,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 5,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'PRICE',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DECIMAL(19, 4)',
              'data_TYPE': 3,
              'column_SIZE': 19,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 4,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 19,
              'ordinal_POSITION': 6,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'SELLER_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 7,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'BUYER_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 8,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'OPS_USER_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 9,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'DEFAULT',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'OPS_REGION',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 10,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            }
          ],
          'table_SCHEM': 'DEFAULT',
          'table_NAME': 'KYLIN_SALES',
          'table_CAT': 'defaultCatalog',
          'table_TYPE': 'TABLE',
          'remarks': null,
          'type_CAT': null,
          'type_SCHEM': null,
          'type_NAME': null,
          'self_REFERENCING_COL_NAME': null,
          'ref_GENERATION': null
        },
        {
          'columns': [
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'TRANS_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 1,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'PART_DT',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DATE',
              'data_TYPE': 91,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 2,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'LSTG_FORMAT_NAME',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 3,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'LEAF_CATEG_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 4,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'LSTG_SITE_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'INTEGER',
              'data_TYPE': 4,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 5,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'PRICE',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'DECIMAL(19, 4)',
              'data_TYPE': 3,
              'column_SIZE': 19,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 4,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 19,
              'ordinal_POSITION': 6,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'SELLER_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 7,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'BUYER_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'BIGINT',
              'data_TYPE': -5,
              'column_SIZE': -1,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': -1,
              'ordinal_POSITION': 8,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'OPS_USER_ID',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 9,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            },
            {
              'table_SCHEM': 'KYLIN',
              'table_NAME': 'KYLIN_SALES',
              'column_NAME': 'OPS_REGION',
              'table_CAT': 'defaultCatalog',
              'remarks': null,
              'type_NAME': 'VARCHAR(256) CHARACTER SET "UTF-16LE" COLLATE "UTF-16LE$en_US$primary"',
              'data_TYPE': 12,
              'column_SIZE': 256,
              'buffer_LENGTH': -1,
              'decimal_DIGITS': 0,
              'num_PREC_RADIX': 10,
              'nullable': 1,
              'column_DEF': null,
              'sql_DATA_TYPE': -1,
              'sql_DATETIME_SUB': -1,
              'char_OCTET_LENGTH': 256,
              'ordinal_POSITION': 10,
              'is_NULLABLE': 'YES',
              'scope_CATLOG': null,
              'scope_SCHEMA': null,
              'scope_TABLE': null,
              'source_DATA_TYPE': -1,
              'is_AUTOINCREMENT': ''
            }
          ],
          'table_SCHEM': 'KYLIN',
          'table_NAME': 'KYLIN_SALES',
          'table_CAT': 'defaultCatalog',
          'table_TYPE': 'TABLE',
          'remarks': null,
          'type_CAT': null,
          'type_SCHEM': null,
          'type_NAME': null,
          'self_REFERENCING_COL_NAME': null,
          'ref_GENERATION': null
        }
      ]
    }
  },
  watch: {
    serachvalue (val) {
      this.$refs.tree.filter(val)
    }
  },
  mounted () {
    this.init()
    this.sortDataList()
  },
  methods: {
    init () {
      this.$root.eventBus.$on('openDefaultTree', res => {
        setTimeout(() => {
          this.$root.eventBus.$emit('getserchTableList', this.serchTableList)
          this.$store.dispatch('changeSerachtype', 1)
          this.$store.dispatch('saveSelctchckoutone', this.saveSelectTable)
          this.$root.eventBus.$emit('saveSelectTables')
        }, 1000)
      })
    },
    // 整理模拟数据
    sortDataList () {
      let data = this.jsonData || []
      data.forEach((item, index) => {
        if (item.table_SCHEM === 'DEFAULT') {
          // this.treeList[0].id.(item)
        } else {
          // this.treeList[1].children.push(item)
        }
      })
      console.log(this.treeList)
      // let map = {}
      // let dest = []
      // for (var i = 0; i < data.length; i++) {
      //   var ai = data[i]
      //   if (!map[ai.table_SCHEM]) {
      //     dest.push({
      //       table_SCHEM: ai.table_SCHEM,
      //       table_NAME: ai.table_NAME,
      //       list: [ai]
      //     })
      //     map.table_SCHEM = ai
      //   } else {
      //     for (var j = 0; j < dest.length; j++) {
      //       var dj = dest[j]
      //       if (dj.table_SCHEM === ai.table_SCHEM) {
      //         dj.list.push(ai)
      //         break
      //       }
      //     }
      //   }
      // }
      // console.log(dest)
      // return dest
    },
    fetchTreeList (val) {
      this.treeLoading = true
      this.$store.dispatch('GetTreeList').then(res => {
        if (res && res.code === 200) {
          this.treeLoading = false
          this.setTree(res.data.dataLakeDirectoryTree, 1)
          this.setTree(res.data.dataSetDirectoryTree, 2)
          const ids = val || this.treeList[0].id
          const newids = ids.length > 10 ? this.treeList[0].id : val
          newids && setTimeout(() => {
            this.$refs.tree.store.nodesMap[newids].expanded = true
          }, 500)
          this.defaultFrist(this.treeList)
        }
      }).finally(() => {
        this.treeLoading = false
      })
    },
    // 默认点击第一项的递归计算
    defaultFrist (val) {
      // console.log(val)
    },
    setTree (val, type) {
      let item = []
      val.map((list, i) => {
        let newData = {}
        newData.label = list.orgName
        newData.databaseType = list.databaseType
        newData.orgId = list.orgId
        newData.resNum = list.resNum
        newData.id = list.id
        newData.parentId = list.parentId
        newData.is_show_add = true
        newData.isLeaf = list.isLeaf
        newData.type = list.type
        newData.orgtype = list.orgtype
        newData.isTable = list.isTable
        // newData.children = list.children ? this.setTree(list.children) : []
        newData.children = list.isLeaf === true ? [] : [{}]
        item.push(newData)
      })
      type === 1 ? this.treeList[0].children = item : this.treeList[1].children = item
      return item
    },
    filterNode (value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
    renderContent (h, { node, data, store }) {
      return h('span', {
        class: `tree${node.id}`
      }, [
        h('span', {
        }, node.label)
      ])
    },
    // 展开列表
    nodeExpand (data, node, me) {
      if (!data.orgId) return
      this.fetchTree(data, node)
    },
    // 选中对应的表
    getCurrents (data, node, me) {
      // if (data.isLeaf === true) {
      //   if (data.databaseType !== '1') {
      //     this.$message.warning('暂只支持HIVE类型数据查询')
      //   } else {
      //     this.fetchResourceList(data, node.parent.key)
      //   }
      // }
      // // 为资源列表的时候
      // if (data.isTable === true) {
      //   // this.fetchResourceInfo(data)
      // }
      // 模拟数据
      let datas = this.jsonData || []
      let listData = []
      datas.forEach((item, index) => {
        if (data.label === item.table_SCHEM) {
          listData.push(item)
        }
      })
      this.fetchResourceList(listData)
    },
    fetchTree (data) {
      this.treeLoading = true
      this.$store.dispatch('GetTreeTwoList', { orgId: data.orgId, databaseType: data.databaseType }).then(res => {
        if (res.code === 200) {
          this.treeLoading = false
          res.data.map(res => {
            res.label = res.orgName
            res.children = res.isLeaf === true || res.isTable === true ? [] : [{}]
          })
          if (res.data.length) data.children = res.data
        }
      }).finally(() => {
        this.treeLoading = false
      })
    },
    fetchResourceList (data, nodeId) {
      this.$root.eventBus.$emit('getserchTableList', data, 1)
      // 点击时清除其他选择框
      this.$root.eventBus.$emit('clearSelect')
      // 存储当前点击的父节点的id
      this.$store.dispatch('setLastClickTab', nodeId)
      // 保存选择的数据源数据
      this.$store.dispatch('saveSelctchckoutone', this.saveSelectTable)
    },
    fetchResourceInfo (data, nodeId) {
      this.$store.dispatch('GetResourceInfo', { resourceId: data.resourceId, type: data.type }).then(res => {
        if (res.code === 200) {
          this.$root.eventBus.$emit('getserchTableList', res)
          // 点击时清除其他选择框
          this.$root.eventBus.$emit('clearSelect')
          // 存储当前点击的父节点的id
          this.$store.dispatch('setLastClickTab', nodeId)
          // 保存选择的数据源数据
          this.$store.dispatch('saveSelctchckoutone', this.saveSelectTable)
        }
      })
    }
    // fetchTree (id, nodeId) {
    //   this.$store.dispatch('GetSerchTable', id).then(res => {
    //     if (res.code === 200) {
    //       this.$root.eventBus.$emit('getserchTableList', res)
    //       // 点击时清除其他选择框
    //       this.$root.eventBus.$emit('clearSelect')
    //       // 存储当前选择数据源
    //       this.$store.dispatch('setSerchTable', res)
    //       // 存储当前点击的父节点的id
    //       this.$store.dispatch('setLastClickTab', nodeId)
    //       // 保存选择的数据源数据
    //       this.$store.dispatch('saveSelctchckoutone', this.saveSelectTable)
    //     }
    //   })
    // }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      saveLocalSelectTable: 'saveLocalSelectTable',
      lastClickTab: 'lastClickTab',
      serchTableList: 'serchTableList'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('clearSelect')
    this.$root.eventBus.$off('saveSelectTables')
  },
  created () {
    // this.fetchTreeList(this.lastClickTab)
  }
}
</script>

<style lang="stylus" scoped>
  .mechanism{
    height 98%
    width 200px
    // height calc(100vh - 150px)
    overflow auto
    border-right 1px solid #f0f0f0
    .trees{
      width 198px
      // height 100%
      // overflow-y auto
    }
    >>>.el-tree{
      // min-width 500px
      overflow: initial
      width 600px
    }
    >>>.el-input{
      width 80%
      margin-left 10%
    }
    >>>.el-input__suffix{
      margin-top -10px
    }
    >>>.el-tree{
    .el-tree-node__content{
      font-size: 14px;
      height 25px
      line-height 25px
      color: rgba(0,0,0,0.85);
    }
    .el-tree-node__children{
      .el-tree-node__content{
      font-size: 12px;
      color: rgba(0,0,0,0.65)!important;
      }
    }
  }
  >>>.el-loading-spinner{
    width 30%
    top 30%
    .circular{
      width 30px
      height 30px
    }
  }
    >>>.el-input{
      // width 300px
      height 50px
      .el-input__inner{
        height 30px
      }
    }
    >>>.is-leaf{
      background none
      width 0
      height 0
      color transparent
      cursor default
    }
  .trees::-webkit-scrollbar{
    width 8px
    height 8px
    background #fff
  }
  .trees::-webkit-scrollbar-track{
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color:#fff;
  }
  .trees::-webkit-scrollbar-thumb{
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #B5D2DE;
  }
}
</style>
