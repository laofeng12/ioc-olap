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
            <span slot="label" style="cursor:pointer" @click="cahngges" class="selctNum">已选择：<i>10</i></span>
          </el-tab-pane>
        </el-tabs>
    </div>
    <steps class="steps" :step="1" @nextModel="nextModel"></steps>
  </div>
</template>

<script>
import dataLake from '@/components/olapComponent/createComponent/selectStepComponent/datalake'
import localUpload from '@/components/olapComponent/createComponent/selectStepComponent/localUpload'
import steps from '@/components/olapComponent/common/steps'
import { mapGetters } from 'vuex'
export default {
  components: {
    dataLake, localUpload, steps
  },
  data () {
    return {
      activeName: '1'
    }
  },
  methods: {
    cahngges (val) {
      this.$message.success('还剩10条哦~~~~')
    },
    nextModel (val) {
      this.$parent.getStepCountAdd(val)
      this.$router.push('/olap/createolap/createTableRelation')
    },
    tabClick (val) {
      // 推送已选择的复选框按钮到serachTable
      this.$root.eventBus.$emit('saveSelectTable', this.saveSelectTable)
      val.name === '2'
        ? this.$store.dispatch('GetdsUploadTable').then(res => {
          this.$root.eventBus.$emit('getUploadTable', res)
        }) && this.$store.dispatch('changeSerachtype', 2)
        : this.$root.eventBus.$emit('getserchTableList', this.$store.state.common.serchTableList) && this.$store.dispatch('changeSerachtype', 1)
    }
  },
  computed: {
    ...mapGetters({
      saveSelectTable: 'saveSelectTable'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getUploadTable')
    this.$root.eventBus.$off('saveSelectTable')
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
