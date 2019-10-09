<template>
 <div class="datalake">
  <serch-table></serch-table>
  <div class="step_tab">
    <el-tabs v-model="activeName">
        <el-tab-pane label="表数据" name="1">
          <div class="tableBox" v-if="managementHead && managementHead.length">
            <div class="tableBox_item headStep" v-loading="loadingPlanHead">
              <!-- <li v-if="managementHead && managementHead.length">序号</li> -->
              <span v-for="(item, index) in managementHead" :key="index">{{item.label}}</span>
            </div>
            <div class="tableBox_item" v-for="(n, index) in managementData" v-loading="loadingPlanBody" :key="index">
              <span v-for="(n, i) in managementData[index]" :key="i">{{n}}</span>
            </div>
          </div>
          <div v-else style="text-align:center;margin-top:100px">暂无数据</div>
        </el-tab-pane>
        <el-tab-pane label="字段说明" name="2">
          <element-table  v-loading="loadingPlanBody" v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
          <div v-else style="text-align:center;margin-top:100px">暂无数据</div>
        </el-tab-pane>
      </el-tabs>
  </div>
 </div>
</template>

<script>
import serchTable from '@/components/analysisComponent/modelCommon/serchTable'
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    serchTable, elementTable
  },
  data () {
    return {
      activeName: '1',
      loadingPlanHead: false,
      loadingPlanBody: false,
      managementData: [],
      managementHead: [],
      descriptionData: [],
      descriptionHead: [
        { prop: 'comment', label: '字段名称' },
        { prop: 'dataType', label: '字段类型' },
        { prop: 'columnName', label: '字段描述' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.$root.eventBus.$on('getLocalTableHeadList', res => {
        this.managementHead = []
        this.loadingPlanHead = true
        this.$store.dispatch('GetColumnList', res).then(res => {
          if (res.code === 200) {
            this.descriptionData = res.data
            res.data.map((res, index) => {
              this.managementHead.push({ label: res.comment })
            })
            setTimeout(() => {
              this.loadingPlanHead = false
            }, 300)
          }
        })
      })
      // 获取表格数据
      this.$root.eventBus.$on('getLocalTableContentList', res => {
        this.managementData = []
        this.loadingPlanBody = true
        this.$store.dispatch('GetTableData', res).then(res => {
          if (res.code === 200) {
            this.managementData = res.data.data
            setTimeout(() => {
              this.loadingPlanBody = false
            }, 300)
          }
        })
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
.datalake {
  position absolute
  height 100%
  // margin-top 10px
  width 100%
  display flex
  .trees{

  }
  .step_tab{
	box-shadow: -5px 0 10px 0 rgba(0,0,0,0.05);
	height: 100%;
	background: #fff;
    flex 1
    // margin-left 10px!important
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
      height: calc(100vh - 280px)!important;
    }
    >>>.el-table{
      margin-top 10px
    }
    .tableBox_item{
      width 100%
      display flex
      background #F2F2F2
      span{
        width 120px
        float left
        height 40px
        line-height 40px
        text-align center
      }
    }
    .tableBox div:nth-child(even){
      background #ffffff!important
    }
  }
}
</style>
