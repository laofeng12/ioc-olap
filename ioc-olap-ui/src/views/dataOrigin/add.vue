<template>
  <div class="container bg-fff">
    <div class="text-center">
      <h3 class="c-333 f-s-28">证数局数据库收集填报</h3>
    </div>
    <el-form ref="form" :model="form" :rules="rules" label-width="120px" >
      <el-form-item label="数据库类型" prop="databaseType">
        <ul class="dis-flex">
          <li class="typeBox text-center" v-for="(item, index) in databaseTypeList" :key="index" @click="selectType(item.codeDef)">
            <div class="img" :class="form.databaseType === item.codeDef && ' selected'">
              <img class="w-100" :src="item.iconPath" alt>
            </div>
            <p>{{item.codename}}</p>
          </li>
        </ul>
      </el-form-item>
      <el-form-item label="数据库类型" prop="databaseTypeName" v-if="form.databaseType === '99'"
                    :required="true">
        <el-input v-model="form.databaseTypeName" maxlength="20" placeholder="请输入数据库类型"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="数据库名称" prop="databaseName">
        <el-input v-model="form.databaseName" maxlength="20" placeholder="请输入数据库名称"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="数据库来源" prop="databaseSource">
        <TreeSelection
          class="w-100"
          ref="treedialog"
          clearable
          :value="form.databaseSource"
          :valueName="form.databaseSourceName"
          :options="treeData"
          :props="defaultProps"
          :loadingTree="!treeData.length"
          @change="handleOrgIdClick"
          @visible-change="handleVisibleClick"
        ></TreeSelection>
      </el-form-item>
      <el-form-item label="关联项目名称" prop="projectName">
        <el-input v-model="form.projectName" maxlength="20" placeholder="请输入关联项目名称"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="关联系统名称" prop="systemNames">
        <el-input v-model="form.systemNames" maxlength="20" placeholder="请输入关联系统名称"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="JDBC URL" prop="url">
        <el-input v-model="form.url" maxlength="100" placeholder="示例：jdbc:mysql://192.168.4.238:3306/test"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" maxlength="20" placeholder="请输入用户名"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="密码" prop="password">
        <el-input v-model="form.password" maxlength="30" placeholder="请输入密码" clearable auto-complete="new-password"
                  show-password></el-input>
      </el-form-item>
      <el-form-item label="连通测试" prop="linkStatus">
        <div class="dis-flex">
          <el-button type="primary" @click="testLink" :loading="linkLoading">连通测试</el-button>
          <div class="linkTip c-blue" v-if="form.linkStatus === 1">已连通</div>
          <div class="linkTip c-red" v-else-if="form.linkStatus === 2">未连通</div>
        </div>
      </el-form-item>
      <el-form-item label="数据库描述">
        <el-input v-model="form.description" maxlength="200" type="textarea"
                  placeholder="请输入数据库描述" clearable></el-input>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="form.contactPerson" maxlength="20" placeholder="请输入联系人"
                  clearable></el-input>
      </el-form-item>
      <el-form-item label="联系电话" prop="contactTelephone">
        <el-input v-model="form.contactTelephone" maxlength="11" placeholder="请输入联系电话"
                  clearable></el-input>
      </el-form-item>
    </el-form>
    <div class="text-center">
      <el-button type="primary" @click="submit" :loading="submitLoading">提交</el-button>
    </div>
  </div>
</template>

<script>
import { linkTestApi, getTreeApi, doSaveApi } from '@/api/dataOrigin'
import { encrypt, decrypt } from '../../utils/pass.js'
import TreeSelection from './TreeSelection'
const oracleIconPath = require('@/assets/oracle.png')
const mysqlIconPath = require('@/assets/mysql.png')
const postgresqlIconPath = require('@/assets/postgresql.png')
const gaussdbIconPath = require('@/assets/gaussdb.png')
const sqlserverIconPath = require('@/assets/sqlserver.png')
const otherPath = require('@/assets/other.png')
export default {
  components: { TreeSelection },
  data () {
    const typeRule = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请选择数据库类型'))
      } else {
        callback()
      }
    }
    const newTypeRule = (rule, value, callback) => {
      if (this.form.databaseType === '99') {
        if (value === '') {
          callback(new Error('请选择数据库类型'))
        } else {
          callback()
        }
      } else {
        callback()
      }
    }
    const phoneRule = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入联系电话'))
      } else {
        const reg = /^1[3-9]\d{9}$/
        if (value && !reg.test(value)) {
          callback(new Error('目前只支持中国大陆的手机号码'))
        } else {
          callback()
        }
      }
    }
    const linkRule = (rule, value, callback) => {
      if (value === 0) {
        callback(new Error('请点击连通测试'))
      } else {
        callback()
      }
    }
    return {
      form: {
        databaseType: '0',
        databaseTypeName: '',
        databaseName: '',
        databaseSource: '',
        databaseSourceName: '',
        projectName: '',
        systemNames: '',
        url: '',
        username: '',
        password: '',
        description: '',
        contactPerson: '',
        contactTelephone: '',
        linkStatus: 0
      },
      databaseTypeList: [
        {
          codeDef: '0',
          codename: 'Oracle',
          iconPath: oracleIconPath,
          pcodeid: 0
        },
        {
          codeDef: '1',
          codename: 'MySQL',
          iconPath: mysqlIconPath,
          pcodeid: 0
        },
        {
          codeDef: '3',
          codename: 'PostgreSQL',
          iconPath: postgresqlIconPath,
          pcodeid: 0
        },
        {
          codeDef: '7',
          codename: 'GaussDB',
          iconPath: gaussdbIconPath,
          pcodeid: 0
        },
        {
          codeDef: '5',
          codename: 'SQLServer',
          iconPath: sqlserverIconPath,
          pcodeid: 0
        },
        {
          codeDef: '99',
          codename: 'other',
          iconPath: otherPath,
          pcodeid: 0
        }
      ],
      rules: {
        databaseType: [
          { validator: typeRule },
          { required: true, trigger: 'blur' }
        ],
        databaseTypeName: [
          { validator: newTypeRule }
        ],
        databaseName: [
          { required: true, message: '请输入数据库名称', trigger: 'blur' }
        ],
        databaseSource: [
          { required: true, message: '请选择数据库来源', trigger: 'change' }
        ],
        projectName: [
          { required: true, message: '请输入关联项目名称', trigger: 'blur' }
        ],
        systemNames: [
          { required: true, message: '请输入关联系统名称', trigger: 'blur' }
        ],
        url: [
          { required: true, message: '请输入JDBC URL', trigger: 'blur' }
        ],
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        linkStatus: [
          { validator: linkRule },
          { required: true }
        ],
        contactPerson: [
          { required: true, message: '请输入联系人', trigger: 'blur' }
        ],
        contactTelephone: [
          { validator: phoneRule },
          { required: true, message: '请输入联系电话', trigger: 'blur' }
        ]
      },
      treeData: [],
      defaultProps: Object({
        value: 'orgId',
        label: 'orgName',
        children: 'children',
        isLeaf: 'isLeaf'
      }),
      linkLoading: false,
      submitLoading: false
    }
  },
  mounted () {
    this.getTreeData()
  },
  methods: {
    async getTreeData () {
      const { data } = await getTreeApi()
      this.treeData = JSON.parse(data)
    },
    selectType (databaseType) {
      this.form.databaseType = databaseType
    },
    async testLink () {
      this.linkLoading = true
      let testPassword = encrypt(this.form.password)
      this.form.password = testPassword
      const data = {
        databaseType: this.form.databaseType,
        url: this.form.url,
        username: this.form.username,
        password: this.form.password
      }
      try {
        const { message } = await linkTestApi(data)
        if (message === '连通成功') {
          this.$message.success(message)
          this.form.linkStatus = 1
          const res = decrypt(this.form.password)
          this.form.password = res
        } else {
          this.$message.error(message)
          this.form.linkStatus = 2
          const res = decrypt(this.form.password)
          this.form.password = res
        }
        this.linkLoading = false
      } catch (e) {
        this.form.linkStatus = 2
        this.linkLoading = false
        const res = decrypt(this.form.password)
        this.form.password = res
      }
    },
    handleOrgIdClick (orgId, orgName) {
      this.form.databaseSource = orgId
      this.form.databaseSourceName = orgName
    },
    handleVisibleClick (isVisible) {
      if (isVisible && !this.treeData.length) {
        this.getTreeData()
      }
    },
    submit () {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          this.submitLoading = true
          // form.password
          let password = encrypt(this.form.password)
          this.form.password = password
          try {
            const { message } = await doSaveApi(this.form)
            if (message === '保存成功') {
              this.$message.success(message)
              this.$router.back()
            } else {
              this.$message.error(message)
            }
            this.submitLoading = false
          } catch (e) {
            this.submitLoading = false
          }
        } else {
          this.$message.error('请完成所有选项')
          return false
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
  .f-s-28 {
    font-size: 28px;
  }
  .bg-fff {
    background-color: #FFFFFF;
  }
  .container {
    max-width: 1000px;
    margin: 16px auto 40px;
    padding: 32px 16px;
    box-sizing: border-box;
    .typeBox {
      width: 110px;
      margin: 8px 16px 0;
      cursor: pointer;
      .img {
        border: 1px #ddd solid;
        padding: 8px 8px 0;
        box-sizing: border-box;
      }
      .selected {
        border: 1px #1877F1 solid;
      }
    }
    .linkTip {
      height: 32px;
      line-height: 32px;
      margin-left: 16px;
    }
    .c-blue {
      color: #1877F1;
    }
    .c-red {
      color: red;
    }
  }

</style>
