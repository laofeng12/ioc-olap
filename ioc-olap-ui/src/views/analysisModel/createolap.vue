<template>
  <div class="creates">
    <header>
      <!--<el-form>-->
        <!--<el-button icon="el-icon-arrow-left" @click='tobackList'></el-button>-->
        <!-- <el-input v-model="totalSaveData.cube.cubeDescData.name" :disabled="!Array.isArray(ModelAllList)" @blur="blurIpt" maxlength="20" placeholder="请输入模型名称(1~20字)"></el-input> -->
      <!--</el-form>-->
    </header>
    <head-box :selectId="selectStep"></head-box>
    <div v-loading="isLoading">
      <!-- <transition name="fade-transform" mode="out-in"> -->
        <keep-alive>
          <router-view></router-view>
        </keep-alive>
      <!-- </transition> -->
    </div>
  </div>
</template>

<script>
import headBox from '@/components/analysisComponent/modelCommon/head'
import { mapGetters } from 'vuex'
import { isChineseChar } from '@/utils/validate'
import { descDataList } from '@/api/modelList'
export default {
  components: { headBox },
  data () {
    return {
      selectStep: 1,
      isLoading: false,
      isEdit: false,
      value: '',
      rules: {
        'cube.cubeDescData.name': [
          { required: true, message: '请输入模型名称', trigger: 'blur' }
        ]
      },
      json: {}
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.$route.query.cubeName) {
        this.isLoading = true
        let { cubeName, models } = this.$route.query
        const params = {
          cubeName, models
        }
        // 编辑
        descDataList(params).then(async res => {
          if (res.CubeList) {
            this.isLoading = false
            await this.$store.dispatch('SaveModelAllList', res)
            const tempModelAllObj = res.TableList[0].tableList[0]
            if (this.$route.query.cubeName) {
              await this.$root.eventBus.$emit('getserchTableList', {
              orgId: tempModelAllObj.orgId,
              type: Number(tempModelAllObj.type),
              databaseType: Number(tempModelAllObj.databaseType) }, 1)
              // this.$root.eventBus.$emit('saveSelectTables')
            }
          }
        })
      }
    },
    getStepCountAdd (val) {
      this.selectStep += 1
    },
    getStepCountReduce (val) {
      this.selectStep -= 1
    },
    // 暂时不用 longbensong
    // tobackList () {
    //   this.$router.push('/analysisModel/Configuration')
    //   this.$store.dispatch('resetList')
    // },
    blurIpt (val) {
      let result = val.target.value
      let flag = isChineseChar(val.target.value)
      if (!result) return this.$message.warning('请填写模型名称~')
      if (flag) return this.$message.warning('请勿输入中文~')
    }
  },
  computed: {
    ...mapGetters({
      totalSaveData: 'totalSaveData',
      ModelAllList: 'ModelAllList'
    })
  }
}
</script>
<style lang="stylus" scoped>
.creates{
  position relative
  width 100%
  >>>.el-form{
    padding:0px!important;
  }
  header{
    width 100%
    // margin-bottom 16px
	.el-form {
		overflow: hidden;
		background: #f2f2f2;
		padding: 0 !important;
	}
    >>>.el-button{
		background: #f2f2f2;
		border: none;
		padding: 0 !important;
		.el-icon-arrow-left{font-size:18px}
    }
    >>>.el-input{
      width 200px
      margin-left 48px
      height 32px
      .el-input__inner{
        border-radius 0
        height 32px
      }
    }
  }
  .showBox{
    opacity 1
    >>>.serchTable{
      height auto
    }
    >>>.selectStep >>>.el-tabs__content{
     height calc(100vh - 195px)
    }
  }
  .showBox.selectStep >>>.el-tabs__content{
    height calc(100vh - 195px)
  }
  .hideBox{
    opacity 0
    height 0
    >>>.serchTable{
      height 0
      width 0
    }
    >>>.containers{
      height 0
    }
  }
  .hideBox.selectStep >>>.el-tabs__content{
    height 0
  }
}
</style>
