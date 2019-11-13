<template>
 <article class="publish-box">
   <el-dialog class="dialog" :title="title" :visible.sync="showDialog" :close-on-click-modal="false" @close="closeBtn">
      <el-form :model="form" :rules="rules"  ref="form" label-width="100px">
        <el-form-item label="数据服务：" prop="moduleType">
          <el-input v-model="form.moduleType" placeholder="输入数据服务" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="请求协议："  prop="apiProtocols">
          <el-input v-model="form.apiProtocols" placeholder="输入请求协议" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="请求方式："  prop="apiMethod">
          <el-input v-model="form.apiMethod" placeholder="输入请求方式" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="接口名称："  prop="apiName">
          <el-input v-model="form.apiName" placeholder="输入接口名称" ></el-input>
        </el-form-item>

        <el-form-item label="接口地址："  prop="apiUrl">
          <el-input v-model="form.apiUrl" placeholder="输入接口地址" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="提交方式："  prop="enctype">
          <el-input v-model="form.enctype" placeholder="输入提交方式" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="接口描述：" >
          <el-input type="textarea" v-model="form.apiDesc" :rows="3" placeholder="输入接口描述" ></el-input>
        </el-form-item>

        <el-form-item label="发布状态:">
          <el-switch v-model="form.status" > </el-switch>
        </el-form-item>
      </el-form>
       <div slot="footer" class="dialog-footer">
        <el-button  @click="closeBtn">取消</el-button>
        <el-button type="primary" @click="submitBtn" >确定</el-button>
      </div>
    </el-dialog>
 </article>
</template>

<script>
import { 
  getPublishInfo,
  publish,
  delPublish
  } from '@/api/newOlapModel'

export default {
  name: 'publish',
  components: {},
  props: {
    // 标题
    title: {
      type: String,
      default: '发布数据查询接口'
    }
  },
  data () {
    return {
      showDialog: false,
      analyzeId: '',
      form: {
        apiDesc: '', // 接口描述
        apiMethod: '', // 接口请求方法
        apiName: '', // 接口名称
        apiPaths: '', // 	接口地址
        apiProtocols: '', // 接口协议
        apiUrl: '', // 接口对外访问地址
        customApiId: '', // 关联业务系统的接口Id
        enctype: '', // 请求方式 默认application/json
        moduleType: '', // 接口所属模块 1:智能API数据查询；6:olap分析；9:数据湖数据推送；10:即席查询
        token: '', // 接口对外使用token
        status: false
      },
      rules: {
        moduleType: [
            { required: true, message: '请输入数据服务', trigger: 'blur' }
          ],
          apiMethod: [
            { required: true, message: '请输入接口请求方法', trigger: 'blur' }
          ],
          apiName: [
            { required: true, message: '请输入接口名称', trigger: 'blur' }
          ],
          apiPaths: [
           { required: true, message: '请输入接口地址', trigger: 'blur' }
          ],
          apiProtocols: [
            { required: true, message: '请输入接口协议', trigger: 'blur' }
          ],
          apiUrl: [
            { required: true, message: '请输入接口对外访问地址', trigger: 'blur' }
          ],
          enctype: [
           { required: true, message: '请输入请求方式 ', trigger: 'blur' }
          ]
        }
    }
  },
  computed: {},
  watch: {},
  created () {},
  mounted () {},
  methods: {
    // 初始化
    init (val) {
      this.showDialog = !!val
      this.analyzeId = val
      this.getPublish()
    },
    // 查看发布接口
    async getPublish () {
      let params = {
        analyzeId: this.analyzeId
      }
      const data = await getPublishInfo(params)
      this.form = Object.assign(this.form, data)
      // debugger
    },
    // 发布接口
    submitBtn () {
      if (!this.form.status) {

      } else {

      }
    },
    // 新增/编辑
    editPublish () {
    },
    // 删除
    deletePublish () {

    },
    // 关闭弹窗
    closeBtn () {
      this.showDialog = false
      // this.$refs.formData.clearValidate()
      // this.$parent.init()
    }
  },
  filters: {}
}
</script>

<style lang='scss' scoped>
.publish-box {
  & .dialog /deep/ {
     .el-dialog{
      width: 620px;
    }
    .el-dialog__footer {
      text-align: right;
    }
  }
}
</style>
