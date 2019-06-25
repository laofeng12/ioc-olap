<template>
  <div class="editModal" v-if="resid" v-loading="loadingData">
    <header>
      <span v-for="(item, index) in navTitleList" :key="index" v-text="item+' >'"></span>
      <span style="color:#409EFF;">{{head_title}}</span>
    </header>
    <el-form :model="formData" :rules="rules" ref="formData" :inline="true">
      <el-form-item label="资源名称" :label-width="formLabelWidth" prop="resname">
        <el-input v-model="formData.resname" autocomplete="off" maxlength="20" :disabled="isdisabled"></el-input>
      </el-form-item>
      <el-form-item label="资源别名" :label-width="formLabelWidth" prop="alias">
        <el-input v-model="formData.alias" autocomplete="off" maxlength="20" :disabled="isdisabled"></el-input>
      </el-form-item>
      <el-form-item label="图标" :label-width="formLabelWidth" prop="icon">
        <el-input v-model="formData.icon" autocomplete="off" maxlength="20" :disabled="isdisabled"></el-input>
      </el-form-item>
      <el-form-item label="默认URL" :label-width="formLabelWidth" prop="defaulturl">
        <el-input v-model="formData.defaulturl" autocomplete="off" maxlength="50" :disabled="isdisabled"></el-input>
      </el-form-item>
      <el-form-item label="可否有子节点" :label-width="formLabelWidth" prop="isdisplayinmenu">
        <el-select v-model="formData.isdisplayinmenu" placeholder="请选择" :disabled="isdisabled">
          <el-option v-for="item in isRequiredOption" :key="item.bid" :label="item.basename" :value="item.bid"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="显示到菜单" :label-width="formLabelWidth" prop="isfolderName">
        <el-select v-model="formData.isfolder" placeholder="请选择" :disabled="isdisabled">
          <el-option v-for="item in isRequiredOption" :key="item.bid" :label="item.basename" :value="item.bid"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="默认打开" :label-width="formLabelWidth" prop="isopenName">
        <el-select v-model="formData.isopen" placeholder="请选择" :disabled="isdisabled">
          <el-option v-for="item in isRequiredOption" :key="item.bid" :label="item.basename" :value="item.bid"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="排序" :label-width="formLabelWidth" prop="sorts">
        <el-input v-model="formData.sort" autocomplete="off" maxlength="3" :disabled="isdisabled"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitBtn('formData')" v-loading="btnLoading">确 定</el-button>
    </div>
  </div>
  <div v-else>
    <img src="@/assets/img/logo.png" style="margin-left:50%;margin-top:5%;transform:translateX(-50%);width:50%;">
  </div>
</template>
<script>
import { setTimeout } from 'timers'
import { throttle } from '@/utils'
export default {
  data () {
    return {
      rate: 3,
      dialogFormVisible: false,
      isdisabled: false,
      loadingData: false,
      btnLoading: false,
      isNew: false,
      resid: '',
      navTitleList: [],
      head_title: '编辑',
      formLabelWidth: '120px',
      formData: {},
      isRequiredOption: [
        { basename: '是', bid: 1 }, { basename: '否', bid: 0 }
      ],
      rules: {
        resname: [
          { required: true, message: '请输入角色名称', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        alias: [
          { required: true, message: '请输入别名', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        icon: [
          { required: false, message: '请输入图标地址', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        defaulturl: [
          { required: false, message: '请输入默认url', trigger: 'blur' },
          { validator: this.$isSpecialCharacter, trigger: 'blur' }
        ],
        isdisplayinmenu: [
          { required: false, message: '是否有子节点', trigger: 'blur' }
        ],
        isfolderName: [
          { required: false, message: '是否显示到菜单', trigger: 'blur' }
        ],
        isopenName: [
          { required: false, message: '请选择是否默认打开', trigger: 'blur' }
        ],
        sorts: [
          { required: false, type: 'number', message: '请填写排序', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    this.$root.eventBus.$on('giveId', (data, type, $parent) => {
      this.resid = data.id
      this.navTitleList = $parent
      if (!type) {
        this.loadingData = true
        this.head_title = '编辑'
        this.fetchData(data.id)
      } else {
        this.head_title = '新增'
        this.loadingData = true
        setTimeout(() => { this.loadingData = false }, 200)
        this.formData = {}
        this.isNew = true
      }
      this.$refs.formData.clearValidate()
    })
  },
  methods: {
    fetchData (id) {
      this.$api.getResourseTreeDetail(id).then(res => {
        this.formData = res
        this.loadingData = false
      })
    },
    submitBtn (formData) {
      this.$refs[formData].validate((valid) => {
        if (valid) {
          this.btnLoading = true
          throttle(() => {
            try {
              const params = {
                // isNew: this.isNew,
                parentid: this.resid,
                ...this.formData
              }
              this.$api.saveResourseTree(params, this.isNew).then(res => {
                if (res.code === 200) {
                  this.$message.success('保存成功')
                  this.$refs.formData.clearValidate()
                  this.$root.eventBus.$emit('saveok')
                  this.$store.dispatch('GetMenuList')
                  this.btnLoading = false
                  this.resid = ''
                }
              })
            } catch (error) {
              this.$message.error(error || '服务异常')
            }
          }, 500)
        }
      })
    }
  }
}
</script>

<style lang="stylus" scoped>
.editModal{
  header{
    height 50px
    line-height 50px
    border-bottom 1px solid #f0f0f0
    margin-bottom 10px
    color #686B6D
    font-size 16px
    span{
      font-size 14px
      color #787878
      margin-left 3px
    }
  }
  >>>.el-dialog{
    padding-right:50px;
  }
  >>>.el-select{
    width:100%;
  }
  >>>.el-form-item__label{
    text-align:center;
  }
  >>>.el-form-item{
      margin-bottom:15px!important;
      width 40%
      margin-right 10%
      .el-form-item__label{
        display block
        font-size 14px
        text-align left
        color: rgba(0,0,0,0.85)
        font-weight 500
      }
      .el-form-item__content{
        width 100%
        height 35px
        .el-input__inner{
          height 35px
          line-height 35px
        }
      }
    }
    .el-input__suffix{
      display:none;
    }
    .el-input__inner{
      background:#ffffff;
      border:none;
      color:#000000;
    }
  >>>.dialog-footer{
    position absolute
    bottom 50px
    right 0
    button {
      width 120px
    }
  }
  >>>.el-input.is-disabled .el-input__inner{
    color #000000
  }
}
</style>
