<template>
 <div class="datalake">
  <trees class="trees"></trees>
  <serch-table class="seachTable"></serch-table>
  <div class="step_tab" v-loading="loadingPlan">
    <el-tabs v-model="activeName">
        <el-tab-pane label="表数据" name="1" class="el-tab-box">
          <el-scrollbar style="height:100%">
            <div class="tableBox" v-if="managementHead && managementHead.length">
            <div class="tableBox_item headStep">
              <!-- <li v-if="managementHead && managementHead.length">序号</li> -->
              <span v-for="(item, index) in managementHead" :key="index" class="item_column column_title">{{item.label}}</span>
            </div>
            <div class="tableBox_item contents" v-for="(n, index) in managementData" :key="index">
              <span v-for="(n, i) in managementData[index]" :key="i" class="item_column">{{n ? n : '-'}}</span>
            </div>
          </div>
          <div v-else style="text-align:center;margin-top:110px">暂无数据</div>
       </el-scrollbar>
      </el-tab-pane>
      <el-tab-pane label="字段说明" name="2" class="el-tab-box">
        <el-scrollbar style="height:100%">
           <element-table v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
           <div v-else style="text-align:center;margin-top:110px">暂无数据</div>
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

export default {
  name: 'datalake',
  components: {
    serchTable, 
    trees, 
    elementTable
  },
  data () {
    return {
      activeName: '1',
      loadingPlan: false,
      managementHead: [], // 表数据-头
      managementData: [], // 表数据
      descriptionData: [], // 字段说明
      descriptionHead: [ // 表格字段说明
        { prop: 'name', label: '字段名称' },
        { prop: 'type', label: '字段类型' },
        { prop: 'comment', label: '字段描述' }
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
      // kelin模拟数据
      this.$root.eventBus.$on('klinFetchData', res => {
        this.descriptionHead = [
          { prop: 'name', label: '字段名称' },
          { prop: 'dataType', label: '字段类型' },
          { prop: 'dataType', label: '字段描述' }
        ]
        this.descriptionData = res
      })
    },
    // 获取资源信息-表头 isOnlyPermitted:0：显示全部，1：只显示有权限部分
    async getTableHeadList ({resourceId, type, databaseId, isOnlyPermitted = 1 }) {
        try {
          this.loadingPlan = true
          let params = {
            resourceId,
            type,
            databaseId,
            isOnlyPermitted
          }
          const { data } = await getResourceInfo(params)
          this.$root.eventBus.$emit('klinFetchData', data.columns)
          let tempHeand = []
          Array.isArray(data.column) && data.column.forEach(t => {
            tempHeand.push({ label: t.comment, ...t })
          })
          this.managementHead = tempHeand 
          // 获取数据
          if (this.managementHead && this.managementHead.length) {
            this.getResourceData(resourceId, type, databaseId)
          }
        } catch (e) {
          console.log(e)
        } finally {
          this.loadingPlan = false
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
        // 列集合
        this.managementHead.forEach(t => {
          params.columnIdList.push(t.id)
        })
        const { data } = await getResourceData(params)
        this.managementData = data.data
        this.descriptionData = this.managementHead
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
      margin-top 10px
      .el-table__header tr th{
        padding 10px 0  10px 30px!important;
        text-align left
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
