<template>
 <article class="publish-box" >
   <el-dialog class="dialog"   :title="title" :visible.sync="showDialog" :close-on-click-modal="false" @close="closeBtn">
      <el-form v-loading="loading" :model="form" :rules="rules"  ref="form" label-width="100px">
        <el-form-item label="数据服务：" prop="moduleType">
          <el-input :value="form.moduleTypeName" placeholder="输入数据服务" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="请求协议："  prop="apiProtocols">
          <el-input :value="form.apiProtocols" placeholder="输入请求协议" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="请求方式："  prop="apiMethod">
          <el-input :value="form.apiMethod" placeholder="输入请求方式" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="接口名称："  prop="apiName">
          <el-input v-model="form.apiName" placeholder="输入接口名称" ></el-input>
        </el-form-item>

        <el-form-item label="接口地址："  prop="apiUrl">
          <el-input :value="form.apiUrl" placeholder="输入接口地址" :disabled="true"></el-input>
        </el-form-item>
        <el-form-item label="提交方式："  prop="enctype">
          <el-input :value="form.enctype" placeholder="输入提交方式" :disabled="true"></el-input>
        </el-form-item>

        <el-form-item label="接口描述：" >
          <el-input type="textarea" v-model="form.apiDesc" :rows="3" placeholder="输入接口描述" ></el-input>
        </el-form-item>

        <el-form-item label="发布状态:" v-if="form.token">
          <el-switch v-model="form.status" > </el-switch>
        </el-form-item>
      </el-form>
       <div slot="footer" class="dialog-footer">
        <el-button  @click="closeBtn">取消</el-button>
        <el-button type="primary" @click="submitBtn" :loading="btnLoading">确定</el-button>
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
  import { moduleTypeStr } from './constant'

export default {
  name: 'publish',
  components: {},
  props: {
    // 标题
    title: {
      type: String,
      default: '发布数据查询接口'
    },
    // 操作模块（及时分析/查询都是用同一个，接口地址前缀不同）
    operateModule: {
      type: String,
      required: true
    }
  },
  data () {
    return {
      loading: false,
      showDialog: false,
      btnLoading: false,
      analyzeId: '',
      operateType: '', // edit add
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
      try {
        this.loading = true
        let params = {
        analyzeId: this.analyzeId,
        operateModule: this.operateModule
      }
      const data = await getPublishInfo(params)
      this.form = Object.assign(this.form, data)
      data.token ? this.operateType = 'edit' :  this.operateType = 'add'
      this.form.status = !!data.token
      } catch(e) {
      } finally {
        this.loading = false
      }
    },
    // 发布接口(edit or delete)
    submitBtn () {
      this.btnLoading = true
      if (this.operateType === 'add') {
        this.editPublish()
      } else {
        // 编辑时如果状态为false 则调用发布的删除接口
        !this.form.status ? this.deletePublish() : this.editPublish()
      }
    },
    // 新增/编辑
    async editPublish () {
      try {
        let params = { ...this.form }
        params.operateModule = this.operateModule
        await publish(params)
        this.$message.success('接口发布成功')
        this.showDialog = false
      } catch(e) {
      } finally {
        this.btnLoading = false
      }
    },
    // 删除
    async deletePublish () {
      try {
        let params = {
          analyzeId: this.analyzeId,
          operateModule: this.operateModule
        }
        await delPublish(params)
        this.$message.success('操作成功')
        this.showDialog = false
      } catch(e) {
      } finally {
        this.btnLoading = false
      }
    },
    // 关闭弹窗
    closeBtn () {
      this.showDialog = false
      // this.$refs.formData.clearValidate()
      // this.$parent.init()
    }
  },
  filters: {
    moduleTypeStr (val) {
      if (val === null || val === void 0) return ''
      let obj = moduleTypeStr(val)
      return obj ? obj.value : ''
    }
  }
}
</script>

<style lang='scss' scoped>
.publish-box {
  & .dialog /deep/ {
     .el-dialog{
      width: 620px;
    }
    .el-dialog__footer {
      text-align: right !important;
    }
  }
}
</style>
