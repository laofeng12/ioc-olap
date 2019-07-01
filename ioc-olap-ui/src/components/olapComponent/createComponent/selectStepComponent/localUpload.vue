<template>
 <div class="datalake">
  <serch-table></serch-table>
  <div class="step1_tab">
    <el-tabs v-model="activeName">
        <el-tab-pane label="表数据" name="1">
          <element-table :tableData="managementData" :colConfigs="managementHead"></element-table>
        </el-tab-pane>
        <el-tab-pane label="字段说明" name="2">
          <element-table :tableData="managementData2" :colConfigs="managementHead2"></element-table>
        </el-tab-pane>
      </el-tabs>
  </div>
 </div>
</template>

<script>
import serchTable from '@/components/olapComponent/common/serchTable'
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    serchTable, elementTable
  },
  data () {
    return {
      activeName: '1',
      managementData: [
        { index: '1', userAccount1: '啦啦啦', userAccount2: '啦啦啦', userAccount3: '啦啦啦', userAccount4: '啦啦啦', userAccount5: '啦啦啦', userAccount6: '啦啦啦', userAccount7: '啦啦啦', userAccount8: '啦啦啦', userAccount9: '啦啦啦', userAccount10: '啦啦啦' },
        { index: '2', userAccount1: '啦啦啦', userAccount2: '啦啦啦', userAccount3: '啦啦啦', userAccount4: '啦啦啦', userAccount5: '啦啦啦', userAccount6: '啦啦啦', userAccount7: '啦啦啦', userAccount8: '啦啦啦', userAccount9: '啦啦啦', userAccount10: '啦啦啦' },
        { index: '3', userAccount1: '啦啦啦', userAccount2: '啦啦啦', userAccount3: '啦啦啦', userAccount4: '啦啦啦', userAccount5: '啦啦啦', userAccount6: '啦啦啦', userAccount7: '啦啦啦', userAccount8: '啦啦啦', userAccount9: '啦啦啦', userAccount10: '啦啦啦' },
        { index: '4', userAccount1: '啦啦啦', userAccount2: '啦啦啦', userAccount3: '啦啦啦', userAccount4: '啦啦啦', userAccount5: '啦啦啦', userAccount6: '啦啦啦', userAccount7: '啦啦啦', userAccount8: '啦啦啦', userAccount9: '啦啦啦', userAccount10: '啦啦啦' },
        { index: '5', userAccount1: '啦啦啦', userAccount2: '啦啦啦', userAccount3: '啦啦啦', userAccount4: '啦啦啦', userAccount5: '啦啦啦', userAccount6: '啦啦啦', userAccount7: '啦啦啦', userAccount8: '啦啦啦', userAccount9: '啦啦啦', userAccount10: '啦啦啦' }
      
      ],
      managementHead: [
        { prop: 'index', label: '字段名称' },
        { prop: 'userAccount1', label: '字段类型' },
        { prop: 'userAccount2', label: '字段描述' },
        { prop: 'userAccount3', label: '广告版位' },
        { prop: 'userAccount4', label: '目标群体' },
        { prop: 'userAccount5', label: '展示时间' },
        { prop: 'userAccount6', label: '权重' },
        { prop: 'userAccount7', label: '排序' },
        { prop: 'userAccount8', label: '点击数' },
        { prop: 'userAccount9', label: '状态' },
        { prop: 'userAccount10', label: '添加时间' }
      ],
      managementData2: [
        { userAccount: '阿萨德', userAccount1: '啦啦啦', userAccount2: '啦啦啦' },
        { userAccount: '阿萨德', userAccount1: '啦啦啦', userAccount2: '啦啦啦' },
        { userAccount: '阿萨德', userAccount1: '啦啦啦', userAccount2: '啦啦啦' },
        { userAccount: '阿萨德', userAccount1: '啦啦啦', userAccount2: '啦啦啦' },
        { userAccount: '阿萨德', userAccount1: '啦啦啦', userAccount2: '啦啦啦' }
        ],
        managementHead2: [
        { prop: 'userAccount', label: '字段名称' },
        { prop: 'userAccount1', label: '字段类型' },
        { prop: 'userAccount2', label: '字段描述' }
      ],
    }
  }
}
</script>

<style lang="stylus" scoped>
.datalake {
  position absolute
  height 80%
  width 100%
  display flex
  padding-bottom 50px
  // overflow-x auto
  .step1_tab{
    flex 1
    >>>.el-tabs__header{
      margin-top 0px
      border-top 0px solid #cccccc
    }
    >>>.el-tabs__content{
      padding 10px
      // overflow hidden
      height auto!important
    }
  }
}
</style>
