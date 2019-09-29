<template>
  <div class="creates">
    <header>
      <!-- <el-form :rules="rules" :model="totalSaveData"> -->
      <el-form>
        <el-button icon="el-icon-arrow-left" @click='tobackList'></el-button>
        <!-- <el-form-item prop="cube.cubeDescData.name"> -->
          <el-input v-model="totalSaveData.cube.cubeDescData.name" @blur="blurIpt" maxlength="20" placeholder="请输入模型名称(1~20字)"></el-input>
        <!-- </el-form-item> -->
      </el-form>
    </header>
    <head-box :selectId="selectStep"></head-box>
    <div v-loading="isLoading">
      <transition name="fade-transform" mode="out-in">
        <router-view></router-view>
      </transition>
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
        descDataList(params).then(res => {
          if (res.CubeList) {
            this.isLoading = false
            this.$store.dispatch('SaveModelAllList', res)
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
    tobackList () {
      this.$router.push('/analysisModel/Configuration')
      this.$store.dispatch('resetList')
    },
    blurIpt (val) {
      let result = val.target.value
      let flag = isChineseChar(val.target.value)
      if (!result) return this.$message.warning('请填写模型名称~')
      if (flag) return this.$message.warning('请勿输入中文~')
    }
  },
  computed: {
    ...mapGetters({
      totalSaveData: 'totalSaveData'
    })
  }
}
</script>
<style lang="stylus" scoped>
.creates{
  position relative
  // margin-top -25px
  width 100%
  >>>.el-form{
    padding:0px!important;
  }
  header{
    height 40px!important
    width 100%
    background: #333333;
    margin-bottom 15px
    >>>.el-button{
      background #333333
      float left
      border none
      font-size 20px
      height 40px!important
      color #ffffff
      border-right 1px solid #ccc
    }
    >>>.el-input{
      float left
      width 200px
      margin-left 20px
      height 30px
      margin-top 5px
      .el-input__inner{
        border-radius 0
        height 30px
      }
    }
  }
  .showBox{
    opacity 1
    >>>.serchTable{
      height auto
    }
    >>>.selectStep >>>.el-tabs__content{
     height calc(100vh - 200px)
    }
  }
  .showBox.selectStep >>>.el-tabs__content{
    height calc(100vh - 200px)
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
