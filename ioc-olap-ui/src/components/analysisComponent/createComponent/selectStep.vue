<template>
  <div class="selectStep">
    <div class="containers" v-loading="isLoading">
        <el-tabs v-model="activeName" @tab-click="tabClick">
          <el-tab-pane label="数据湖" name="1">
            <data-lake></data-lake>
          </el-tab-pane>
          <el-tab-pane label="本地上传" name="2">
            <local-upload></local-upload>
          </el-tab-pane>
          <el-tab-pane label="已选择" name="3" :disabled="true" class="selctNum" v-if="selectTableTotal.length > 0">
            <span slot="label" style="cursor:pointer" @click="changes" class="selctNum">已选择：<i>{{selectTableTotal.length || 0}}</i></span>
          </el-tab-pane>
        </el-tabs>
    </div>
    <select-modal ref="dialog"></select-modal>
    <steps :step="1" @nextModel="nextModel"></steps>
  </div>
</template>

<script>
import dataLake from '@/components/analysisComponent/createComponent/selectStepComponent/datalake'
import localUpload from '@/components/analysisComponent/createComponent/selectStepComponent/localUpload'
import selectModal from '@/components/analysisComponent/createComponent/selectStepComponent/selectModal'
import steps from '@/components/analysisComponent/modelCommon/steps'
import { mapGetters } from 'vuex'
import { getLocalStorage } from '@/utils/index'
export default {
  components: {
    dataLake, localUpload, selectModal, steps
  },
  data () {
    return {
      isLoading: false,
      activeName: '1',
      dataList: []
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      let totalData = this.selectTableTotal.length ? this.selectTableTotal : JSON.parse(getLocalStorage('selectTableTotal'))
      totalData && totalData.map(res => {
        this.selectTableTotal.push(res.label)
      })
    },
    changes (val) {
      this.$refs.dialog.dialog()
    },
    nextModel (val) {
      if (this.selectTableTotal.length === 0) return this.$message.warning('请选择创建模型的数据源')
      this.$parent.getStepCountAdd(val)
      this.$router.push('/analysisModel/createolap/createTableRelation')
      this.$store.commit('SaveSelectAllListone', this.saveSelectTable)
      // 清掉第二步创建的表
      this.$store.commit('ClearTableRelation')
    },
    tabClick (val) {
      val.name === '2'
        ? this.$root.eventBus.$emit('getUploadTable') && this.$store.dispatch('changeSerachtype', 2)
        : this.$root.eventBus.$emit('getserchTableList', this.serchTableList) && this.$store.dispatch('changeSerachtype', 1)
      // 推送已选择的复选框按钮到serachTable
      this.$root.eventBus.$emit('saveSelectTables')
    }
  },
  computed: {
    ...mapGetters(['saveSelectTable', 'saveLocalSelectTable', 'selectTableTotal', 'selectStepList', 'serchTableList'])
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getUploadTable')
    this.$root.eventBus.$off('getserchTableList')
    this.$root.eventBus.$off('saveSelectTables')
  }
}
</script>

<style lang="stylus" scoped>
.selectStep{
  padding-bottom 67px
  margin-top 20px
  // background #ffffff
  position relattive
  .containers{
    background #F2F2F2
    height calc(100vh - 150px)
    >>>.el-tabs__content{
      height calc(100vh - 200px)
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
    >>>.selctNum{
      i{
        color green
      }
    }
    >>>.el-tabs__content{
      background: #F2F2F2;
    }
    >>>.el-tabs__nav-wrap::after{
    }
  }
}
</style>
