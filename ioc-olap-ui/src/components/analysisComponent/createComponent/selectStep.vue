<template>
  <div class="selectStep">
    <div class="containers" v-loading="isLoading">
       <data-lake></data-lake>
        <!-- <el-tabs v-model="activeName" @tab-click="tabClick">
          <el-tab-pane label="数据湖" name="1">
            <data-lake></data-lake>
          </el-tab-pane>
          <el-tab-pane label="本地上传" name="2">
            <local-upload></local-upload>
          </el-tab-pane>
          <el-tab-pane label="已选择" name="3" :disabled="true" class="selctNum" v-if="selectTableTotal.length > 0">
            <span slot="label"  style="cursor:pointer" @click="changes" class="selctNum">已选择：<i>{{selectTableTotal.length || 0}}</i></span>
          </el-tab-pane>
        </el-tabs> -->
    </div>
    <select-modal ref="dialog"></select-modal>
    <steps :step="1" @nextModel="nextModel"></steps>
  </div>
</template>

<script>
import dataLake from '@/components/analysisComponent/createComponent/selectStepComponent/datalake'
import selectModal from '@/components/analysisComponent/createComponent/selectStepComponent/selectModal'
import steps from '@/components/analysisComponent/modelCommon/steps'

import { mapGetters } from 'vuex'

export default {
  name: 'selectStep',
  components: {
    dataLake, 
    selectModal, 
    steps
  },
  data () {
    return {
      isLoading: false,
      activeName: '1',
      dataList: []
    }
  },
  created () {
    this.initEvent()
    this.init()
  },
  methods: {
    init () {
      // let totalData = this.selectTableTotal.length ? this.selectTableTotal : JSON.parse(getLocalStorage('selectTableTotal'))
      // totalData && totalData.map(res => {
      //   this.selectTableTotal.push(res.label)
      // })
    },
    initEvent () {
      window.addEventListener('beforeunload', e => this.beforeunloadFn(e))
    },
     beforeunloadFn (e) {
    },
    // 下一步
    async nextModel (val) {
      if (this.selectTableTotal.length < 1) {
        this.$message.warning('请选择创建模型的数据源，且不少于两张表')
        return
      }
      this.$parent.getStepCountAdd(val)
      let params = []
      this.selectTableTotal.forEach(({ databaseId,resourceTableName, resourceId, resourceName, type } )=> {
        params.push({
          cron:'0 0 2 * * ? *', // 定时任务的正则表达式，看你们的定时任务是多久同步一次
          hiveDbName: 'async',
          writerTableComment: 'olap',
          writerTableSource: `id_${resourceId}`,
          databaseId,
          resourceId,
          resourceName,
          syncSource: type,
          type
        })
      })
      await this.$store.dispatch('batchCreateJob', params)
      // 清掉第二步创建的表
      this.$store.commit('ClearTableRelation')
      this.$store.dispatch('getAllColumnInfo')
      this.$router.push('/analysisModel/createolap/createTableRelation')
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      selectTableTotal: 'selectTableTotal',
      serchTableList: 'serchTableList'
    })
  },
  beforeDestroy () {
  }
}
</script>

<style lang="stylus" scoped>
.selectStep{
  margin-top 16px
  position relative
  .containers{
    background #F2F2F2
    height calc(100vh - 235px)
    >>>.el-tabs__content{
      height calc(100% - 40px)
      padding 10px 5px
      overflow-x auto
      background #ffffff
    }
    >>>.el-tabs__header{
      background #ffffff
      padding-left:10px;
      border:none;
      height 40px!important
    }
    >>>.el-tabs__content{
      background: #F2F2F2;
    }
  }
}
</style>
