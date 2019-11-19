<template>
  <div class="createTableModal" v-loading="loadingPlan">
    <el-dialog title="查看数据表字段" :visible.sync="dialogFormVisible" @close="closeBtn" >
      <el-tabs v-model="activeName" @tab-click="handleClick" >

        <el-tab-pane label="表数据" name="1">
          <div class="tableBox" >
            <div class="tableBox_item headStep">
              <span v-for="(item, index) in managementHead" :key="index">{{item.label}}</span>
            </div>
            <div v-if="managementData && managementData.length">
               <div class="tableBox_item contents" v-for="(n, index) in managementData" :key="index"  >
                  <span v-for="(n, i) in managementData[index]" :key="i" class="item_column">{{n ? n : '-'}}</span>
               </div>
            </div>
            <div class="null_data" v-else >暂无数据</div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="字段说明" name="2">
          <element-table 
          v-if="descriptionData && descriptionData.length" :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
          <div  class="null_data" >暂无数据</div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import elementTable from '@/components/ElementTable/index'
import { getResourceData } from '@/api/newOlapModel'

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
      managementData: [],  // 表数据
      managementHead: [], // 表数据-头
      descriptionData: [],
      descriptionHead: [
        { prop: 'definition', label: '字段名称' },
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
      this.tableOptions = this.selectTableTotal
    },
    closeBtn () {
      this.dialogFormVisible = false
    },
    // 显示弹窗
    dialog (id) {
      try {
        this.managementData = []
        this.managementHead = []
        this.descriptionData = []
        this.dialogFormVisible = true
        this.saveSelectAllList.forEach(async (item, index) => {
          // let items = JSON.parse(item)
          if (item.resourceId === id) {
            this.descriptionData = item.column || []
            let tempHeand = []
            Array.isArray(this.descriptionData) && this.descriptionData.forEach(t => {
              tempHeand.push({ label: t.comment, ...t })
            })
            this.managementHead = tempHeand 
            await this.getResourceData(item.resourceId,item.type,item.databaseId)
          }
        })
      } catch (e) {
      }
    },
    handleClick () {
    },
    // 获取资源数据
    async getResourceData (resourceId, type, databaseId) {
      try {
        this.loadingPlan = true
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
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectAllList: 'saveSelectAllList'
    })
  }
}
</script>
<style lang="scss" scoped>
.createTableModal{
  & /deep/ .el-tab-pane {
    height: 100%;
    background-color: white;
    padding: 16px !important;
  }
}
</style>

<style lang="stylus" scoped>
.createTableModal{
  padding 20px
  >>>.el-dialog{
    width 70%
  }
  >>>.el-tabs__header{
    padding: 0!important;
    margin-top 0px
    // padding-left 10px
    border-top 0px solid #cccccc
  }
  >>>.el-tabs__content{
    padding 0px!important
    height 400px
    overflow-y auto
  }
  >>>.el-table{
    margin-top 10px
    padding: 0!important
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

</style>
