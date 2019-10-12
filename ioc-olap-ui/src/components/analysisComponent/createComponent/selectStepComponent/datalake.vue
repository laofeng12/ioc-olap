<template>
 <div class="datalake">
  <trees class="trees"></trees>
  <serch-table class="seachTable"></serch-table>
  <div class="step_tab">
    <el-tabs v-model="activeName">
        <el-tab-pane label="表数据" name="1">
          <div class="tableBox" v-if="managementHead && managementHead.length">
            <div class="tableBox_item headStep">
              <!-- <li v-if="managementHead && managementHead.length">序号</li> -->
              <span v-for="(item, index) in managementHead" :key="index">{{item.label}}</span>
            </div>
            <div class="tableBox_item contents" v-for="(n, index) in managementData" :key="index">
              <span v-for="(n, i) in managementData[index]" :key="i">{{n}}</span>
            </div>
          </div>
          <div v-else style="text-align:center;margin-top:110px">暂无数据</div>
        </el-tab-pane>
        <el-tab-pane label="字段说明" name="2">
          <element-table v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
          <div v-else style="text-align:center;margin-top:110px">暂无数据</div>
          <!-- <element-table v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
          <div v-else style="text-align:center;margin-top:100px">暂无数据</div> -->
        </el-tab-pane>
      </el-tabs>
  </div>
 </div>
</template>

<script>
import serchTable from '@/components/analysisComponent/modelCommon/serchTable'
import trees from '@/components/analysisComponent/modelCommon/trees'
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    serchTable, trees, elementTable
  },
  data () {
    return {
      activeName: '1',
      loadingPlan: false,
      managementData: [],
      managementHead: [],
      descriptionData: [],
      descriptionHead: [
        { prop: 'columnAlias', label: '字段名称' },
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
    // kelin模拟数据
      this.$root.eventBus.$on('klinFetchData', res => {
        this.descriptionHead = [
          { prop: 'name', label: '字段名称' },
          { prop: 'dataType', label: '字段类型' },
          { prop: 'dataType', label: '字段描述' }
        ]
        this.descriptionData = res
      })
      // 数据湖获取表的数据
      this.$root.eventBus.$on('getTabdataList', (res, columnData) => {
        this.managementHead = []
        this.managementData = []
        if (res.code === 200) {
          res.data.columnList.map(n => {
            this.managementHead.push({ label: n.name })
          })
          this.managementData = res.data.data.splice(0, 14)
          this.descriptionData = columnData
        }
      })
      // 获取表格头部数据
      this.$root.eventBus.$on('getTableHeadList', res => {
        this.managementHead = []
        this.loadingPlan = true
        if (res.code === 200) {
          this.loadingPlan = false
          this.descriptionData = res.data
          res.data.map((res, index) => {
            this.managementHead.push({ label: res.comment })
          })
        }
      })
      // 获取表格数据
      this.$root.eventBus.$on('getTableContentList', res => {
        this.loadingPlan = true
        if (res.code === 200) {
          this.loadingPlan = false
          this.managementData = res.data.data
        }
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
.datalake {
  position absolute
  width 100%
  height 100%
  // margin-top 10px
  display flex
  .step_tab{
    overflow hidden
    box-shadow -5px 0 10px 0 rgba(0, 0, 0, 0.05)
    width 100%
    height 100%
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
      padding 0px!important
      overflow-y auto
      background #ffffff!important
      padding-bottom 100px!important
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
        padding 0 20px
        float left
        font-size 12px
        height 40px
        line-height 40px
        text-align center
        overflow hidden
      }
      .contents{
        text-align center
      }
    }
    .tableBox div:nth-child(even){
      background #ffffff!important
    }
  }
}
</style>
