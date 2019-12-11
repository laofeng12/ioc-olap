<template>
 <div class="datalake">
   <!-- 资源目录列表 -->
  <trees class="trees"></trees>
  <!-- 表列表 -->
  <serch-table class="seachTable"></serch-table>
  <!-- 表数据、字段说明 -->
  <div class="step_tab" v-loading="loadingPlan">
    <el-tabs v-model="activeName">
      <!-- 数据湖 -->
      <el-tab-pane label="可用字段" name="3"  class="el-tab-box" v-if="isShow">
        <el-scrollbar style="height:100%">
          <div class="desc">说明：表字段同时拥有查阅权限、不脱敏权限、解密权限时，才可使用，若需要更多字段的使用权限，请到资源目录进行。
            <el-button type="text" @click="gotoSubscription">订阅</el-button>
          </div>
           <element-table v-if="powerPlanHead && powerPlanHead.length" :tableData="powerPlanHead" :colConfigs="descriptionHead"></element-table>
           <div v-else class="null_data">暂无数据</div>
        </el-scrollbar>
      </el-tab-pane>

      <el-tab-pane label="表详情" name="4" class="el-tab-box"  v-if="isShow">
        <el-scrollbar style="height:100%">
           <element-table v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHeadPower"></element-table>
           <div v-else class="null_data">暂无数据</div>
        </el-scrollbar>
      </el-tab-pane>

      <el-tab-pane label="字段说明" name="2" class="el-tab-box" v-if="!isShow">
        <el-scrollbar style="height:100%">
           <element-table v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
           <div v-else class="null_data">暂无数据</div>
        </el-scrollbar>
      </el-tab-pane>

        <el-tab-pane label="表数据" name="1" class="el-tab-box">
          <el-scrollbar style="height:100%">
            <div class="tableBox" v-if="managementHead && managementHead.length">
            <div class="tableBox_item headStep">
              <span v-for="(item, index) in managementHead" :key="index" class="item_column column_title">{{item.label}}</span>
            </div>
            <div class="tableBox_item contents" v-for="(n, index) in managementData" :key="index">
              <span v-for="(n, i) in managementData[index]" :key="i" class="item_column">{{n ? n : '-'}}</span>
            </div>
          </div>
          <div v-else class="null_data">暂无数据</div>
       </el-scrollbar>
      </el-tab-pane>

      </el-tabs>
  </div>
 </div>
</template>

<script>
import serchTable from '@/components/analysisComponent/modelCommon/serchTable'
import trees from '@/components/analysisComponent/modelCommon/trees'
import elementTable from '@/components/ElementTable/index'
import { getResourceData, getResourceInfo } from '@/api/newOlapModel'
import { mapGetters } from 'vuex'

export default {
  name: 'datalake',
  components: {
    serchTable,
    trees,
    elementTable
  },
  computed: {
    ...mapGetters({
      ModelAllList: 'ModelAllList'
    }),
    // 是否显示权限部分, type 类型（0:数据湖,1:数据集,2:自定义源;3:本地空间）数据湖才显示
    isShow () {
      return this.dataType === 0
    }
  },
  data () {
    return {
      activeName: '2',
      dataType: '',
      loadingPlan: false,
      loadingPowerPlan: false, // 可用panel 加载
      powerPlanHead: [], // 数据湖可用字段
      managementHead: [], // 表数据-头
      managementData: [], // 表数据
      descriptionData: [], // 字段说明
      // 表格字段说明
      descriptionHead: [
        { prop: 'columnAlias', label: '字段名称', align: 'center' },
        { prop: 'type', label: '字段类型', align: 'center' },
        { prop: 'comment', label: '字段描述', align: 'center' }
      ],
      // 表详情
      descriptionHeadPower: [ 
        { prop: 'columnAlias', label: '字段名称', align: 'center' },
        { prop: 'type', label: '字段类型', align: 'center' },
        { prop: 'comment', label: '字段描述', align: 'center' },
        { prop: 'viewable', label: '查阅权限', formatter: row => row.viewable ? '是' : '否', align: 'center' },
        { prop: 'sensitived', label: '不脱敏权限', formatter: row => row.sensitived ? '是' : '否', align: 'center' },
        { prop: 'decryption', label: '解密权限', formatter: row => row.decryption ? '是' : '否', align: 'center' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      // 获取资源信息
      this.$root.eventBus.$on('getTableHeadList', (params) => this.getTableHeadList(params))
    },
    // 订阅
    gotoSubscription () {
    },
    // 获取 非数据胡资 源信息-表头 权限（0：显示全部，1：只显示有权限部分 2 只显示有最大权限）
    async getTableHeadList ({resourceId, type, databaseId, isOnlyPermitted = 0 }) {
        this.dataType = Number(type)
        this.activeName = this.dataType === 0 ? '3' : '2'
        try {
          this.loadingPlan = true
          let params = {
            resourceId,
            type,
            databaseId,
            isOnlyPermitted
          }
          const { data } = await getResourceInfo(params)
          // this.$root.eventBus.$emit('klinFetchData', data.columns)
          let tempHeand = []
          Array.isArray(data.column) && data.column.forEach(t => {
            tempHeand.push({ label: t.comment, ...t })
          })
          this.managementHead = tempHeand
          // 字段说明
          this.descriptionData = this.managementHead
          // 获取数据
          if (this.managementHead && this.managementHead.length) {
            this.getResourceData(resourceId, type, databaseId)
          }
          // 如果是数据湖还要查询可用字段列表（权限）
          if (this.isShow) {
            let tempParams = JSON.parse(JSON.stringify(params))
            tempParams.isOnlyPermitted = 2
            this.getTableHeadPowerList(tempParams)
          }
        } catch (e) {
          console.log(e)
        } finally {
          this.loadingPlan = false
        }
    },
    // 获取数据湖资源信息-表头 权限（0：显示全部，1：只显示有权限部分 2 只显示有最大权限）
    async getTableHeadPowerList ({resourceId, type, databaseId, isOnlyPermitted = 2 }) {
        try {
          this.loadingPowerPlan = true
          let params = {
            resourceId,
            type,
            databaseId,
            isOnlyPermitted
          }
          const { data } = await getResourceInfo(params)
          // this.$root.eventBus.$emit('klinFetchData', data.columns)
          let tempHeand = []
          Array.isArray(data.column) && data.column.forEach(t => {
            tempHeand.push({ label: t.comment, ...t })
          })
          // 可用字段列表
          this.powerPlanHead = tempHeand
        } catch (e) {
          console.log(e)
        } finally {
          this.loadingPowerPlan = false
        }
    },
    // 获取资源数据
    async getResourceData (resourceId, type, databaseId) {
      try {
        let params = {
          databaseId,
          columnIdList: [],
          page: 0,
          size: 15, // 默认展示15条数据
          resourceId,
          type
        }
        // 要传列集合查询数据
        this.managementHead.forEach(t => {
          params.columnIdList.push(t.id)
        })
        const { data } = await getResourceData(params)
        // 表数据
        this.managementData = data.data
      } catch (e) {
      } finally {
        this.loadingPlan = false
      }
    }
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getTableHeadList')
  }
}
</script>

<style lang="stylus" scoped>
.desc {
  padding: 0 20px;
}
.desc >>>.el-button--text {
  padding: 0 !important;
}
.datalake {
  position absolute
  width 100%
  height 100%
  display flex
  .column_title {
    background-color:#f2f2f2;
  }
  .el-tab-box {
    height: 100%;
  }
  .step_tab{
    overflow hidden
    box-shadow -5px 0 10px 0 rgba(0, 0, 0, 0.05)
    height 100%
    flex:1
	font-size: 14px;
    >>>.el-tabs {
      height 100%
    }
    >>>.el-tabs__header{
      margin-top 0px
      padding-left 10px
      border-top 0px solid #cccccc
    }
    >>>.el-tabs__content{
      height: calc(100vh - 190px);
      padding 0px!important
      overflow-y auto
      background #ffffff!important
    }
    >>>.el-table{
      margin-top 10px;
      padding: 0!important;
      .el-table__header tr th{
        padding 10px 0  10px 30px!important;
        text-align left;
        font-weight: 400 !important;
      }
      .el-table__body td{
        padding 10px 0  10px 30px!important;
        text-align left
      }
    }
    .tableBox_item{
      width 100%
      display flex
      background #F2F2F2
      span{
        width 140px
        min-width: 90px;
        padding 0 12px
        font-size 12px
        height 40px
        line-height 40px
        text-align center
        flex: 1
        overflow: hidden;
        background #F2F2F2
      }
      .contents{
        text-align center
      }
    }
    .tableBox div:nth-child(even){
      background #ffffff!important
      .item_column {
        background #ffffff!important
      }
    }
  }
}

</style>
