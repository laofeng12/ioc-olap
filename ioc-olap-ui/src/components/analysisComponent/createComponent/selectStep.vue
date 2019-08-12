<template>
  <div class="selectStep">
    <div class="containers">
        <el-tabs v-model="activeName" type="border-card" @tab-click="tabClick">
          <el-tab-pane label="数据湖" name="1">
            <data-lake></data-lake>
          </el-tab-pane>
          <el-tab-pane label="本地上传" name="2">
            <local-upload></local-upload>
          </el-tab-pane>
          <el-tab-pane label="已选择" name="3" :disabled="true" class="selctNum">
            <span slot="label" style="cursor:pointer" @click="changes" class="selctNum">已选择：<i>{{selectTableTotal.length || 0}}</i></span>
          </el-tab-pane>
        </el-tabs>
    </div>
    <select-modal ref="dialog"></select-modal>
    <steps class="steps" :step="1" @nextModel="nextModel"></steps>
  </div>
</template>

<script>
import dataLake from '@/components/analysisComponent/createComponent/selectStepComponent/datalake'
import localUpload from '@/components/analysisComponent/createComponent/selectStepComponent/localUpload'
import selectModal from '@/components/analysisComponent/createComponent/selectStepComponent/selectModal'
import steps from '@/components/analysisComponent/modelCommon/steps'
import { mapGetters } from 'vuex'
export default {
  components: {
    dataLake, localUpload, selectModal, steps
  },
  data () {
    return {
      activeName: '1'
    }
  },
  methods: {
    changes (val) {
      this.$refs.dialog.dialog()
    },
    nextModel (val) {
      if (this.selectTableTotal.length === 0) return this.$message.warning('请选择创建模型的数据源')
      this.$parent.getStepCountAdd(val)
      this.$router.push('/analysisModel/createolap/createTableRelation')
      this.$store.commit('SaveSelectAllList', this.saveSelectTable)
    },
    tabClick (val) {
      val.name === '2'
        ? this.$root.eventBus.$emit('getUploadTable') && this.$store.dispatch('changeSerachtype', 2) && this.$store.dispatch('saveSelctchckouttwo', this.saveLocalSelectTable)
        : this.$root.eventBus.$emit('getserchTableList', this.serchTableList) && this.$store.dispatch('changeSerachtype', 1) && this.$store.dispatch('saveSelctchckoutone', this.saveSelectTable)
        // ? this.$store.dispatch('GetdsUploadTable').then(res => {
        //   this.$root.eventBus.$emit('getUploadTable', res)
        // }) && this.$store.dispatch('changeSerachtype', 2) && this.$store.dispatch('saveSelctchckouttwo', this.saveLocalSelectTable)
        // : this.$root.eventBus.$emit('getserchTableList', this.serchTableList) && this.$store.dispatch('changeSerachtype', 1) && this.$store.dispatch('saveSelctchckoutone', this.saveSelectTable)
      // 推送已选择的复选框按钮到serachTable
      this.$root.eventBus.$emit('saveSelectTables')
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable',
      saveLocalSelectTable: 'saveLocalSelectTable',
      selectTableTotal: 'selectTableTotal',
      serchTableList: 'serchTableList'
    })
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
  margin-top 20px
  background #ffffff
  position relattive
  .containers{
    height calc(100vh - 150px)
    >>>.el-tabs__content{
      height calc(100vh - 200px)
      padding 10px 5px
      overflow-x auto
      background #ffffff
    }
    >>>.selctNum{
      i{
        color green
      }
    }
  }
  .steps{
    left 60%
  }
}
</style>
