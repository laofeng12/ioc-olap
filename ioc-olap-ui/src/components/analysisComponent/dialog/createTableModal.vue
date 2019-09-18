<template>
  <div class="createTableModal">
    <el-dialog title="查看数据表字段" :visible.sync="dialogFormVisible" @close="closeBtn">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane label="表数据" name="1">
          <div class="tableBox" v-if="managementHead && managementHead.length">
            <div class="tableBox_item headStep">
              <!-- <li v-if="managementHead && managementHead.length">序号</li> -->
              <span v-for="(item, index) in managementHead" :key="index">{{item.label}}</span>
            </div>
            <div class="tableBox_item">
              <span v-for="(n, index) in managementData[0]" :key="index">{{n}}</span>
            </div>
          </div>
          <div v-else style="text-align:center;margin-top:100px">暂无数据</div>
        </el-tab-pane>
        <el-tab-pane label="字段说明" name="2">
          <element-table v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
          <div v-else style="text-align:center;margin-top:100px">暂无数据</div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    elementTable
  },
  props: {
    border: {
      type: Boolean,
      default: false
    }
  },
  data () {
    return {
      activeName: '1',
      dialogFormVisible: false,
      loadingPlan: false,
      couponList: [],
      managementData: [],
      managementHead: [],
      descriptionData: [],
      descriptionHead: [
        { prop: 'name', label: '字段名称' },
        { prop: 'dataType', label: '字段类型' },
        { prop: 'dataType', label: '字段描述' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      this.tableOptions = this.selectTableTotal
    },
    closeBtn () {
      this.dialogFormVisible = false
    },
    dialog (id) {
      this.dialogFormVisible = true
      this.saveSelectAllList.forEach((item, index) => {
        let items = JSON.parse(item)
        if (items.resourceId === id) {
          this.descriptionData = items.data.columns || []
        }
      })
      console.log(this.descriptionData)
      // this.$store.dispatch('GetColumnList', { dsDataSourceId: 2, tableName: name }).then(res => {
      //   this.managementHead = []
      //   this.loadingPlan = true
      //   if (res.code === 200) {
      //     this.loadingPlan = false
      //     this.descriptionData = res.data
      //     res.data.map((res, index) => {
      //       this.managementHead.push({ label: res.comment })
      //     })
      //   }
      // })
      // this.$store.dispatch('GetTableData', { dsDataSourceId: 2, dbType: 0, tableName: name }).then(res => {
      //   this.loadingPlan = true
      //   if (res.code === 200) {
      //     this.loadingPlan = false
      //     this.managementData = res.data.data
      //   }
      // })
    },
    handleClick () {

    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectAllList: 'saveSelectAllList'
    })
  }
}
</script>

<style lang="stylus" scoped>
.createTableModal{
  padding 20px
  >>>.el-dialog{
  }
  >>>.el-tabs__header{
    margin-top 0px
    padding-left 10px
    border-top 0px solid #cccccc
  }
  >>>.el-tabs__content{
    padding 0px!important
    height 200px
    overflow-y auto
  }
  >>>.el-table{
    margin-top 10px
  }
  .tableBox{
    // height 200px
    overflow auto
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
  // .container::-webkit-scrollbar{
  //   width 8px
  //   height 8px
  //   background #fff
  // }
  // .container::-webkit-scrollbar-track{
  //   -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
  //   border-radius: 10px;
  //   background-color:#fff;
  // }
  // .container::-webkit-scrollbar-thumb{
  //   border-radius: 10px;
  //   -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
  //   background-color: #f0f0f0;
  // }
}
</style>
