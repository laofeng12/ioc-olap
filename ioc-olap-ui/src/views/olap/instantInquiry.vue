<template>
  <div>
    <el-tabs v-model="activeName">
      <el-tab-pane label="新建查询" name="1">
        <Queries></Queries>
      </el-tab-pane>
      <el-tab-pane label="已保存结果" name="2">
        <SaveResult></SaveResult>
      </el-tab-pane>
      <el-tab-pane label="共享的结果" name="3">
        <ShareResult></ShareResult>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Queries from '../../components/olapComponent/Queries'
import SaveResult from '../../components/olapComponent/SaveResult'
import ShareResult from '../../components/olapComponent/ShareResult'
// import { getFolderWithQueryApi } from '../../api/instantInquiry'

export default {
  components: { Queries, SaveResult, ShareResult },
  data () {
    return {
      activeName: '1'
      // folderList: []
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  mounted () {
    console.info('this.userInfo============', this.userInfo)
  },
  methods: {
    // async getFolderList () {
    //   const res = await getFolderWithQueryApi()
    //   const folderList = res.map(v => {
    //     return (
    //       { catalogList: v.leafs, dataId: v.folderId, dataName: v.name }
    //     )
    //   })
    //   console.info('folderList', folderList)
    //   this.folderList = folderList
    // }
  }
}
</script>
