<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane label="新建查询" name="1">
        <Queries :editInfo="editInfo"></Queries>
      </el-tab-pane>
      <el-tab-pane label="已保存结果" name="2">
        <SaveResult @changeActive="changeActive"></SaveResult>
      </el-tab-pane>
      <el-tab-pane label="共享的结果" name="3">
        <ShareResult></ShareResult>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Queries from '../../components/analysisComponent/Queries'
import SaveResult from '../../components/analysisComponent/SaveResult'
import ShareResult from '../../components/analysisComponent/ShareResult'

export default {
  components: { Queries, SaveResult, ShareResult },
  data () {
    return {
      activeName: '1',
      editInfo: {}
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  methods: {
    changeActive (index, data) {
      this.activeName = index
      this.editInfo = data.attrs
    }
  }
}
</script>
