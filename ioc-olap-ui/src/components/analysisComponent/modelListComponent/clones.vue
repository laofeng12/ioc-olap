<template>
  <div class="rename">
    <el-dialog title="克隆" :visible.sync="dialogFormVisible" v-loading="cloneLoading">
      <el-form :model="form">
        <el-form-item>
          <el-input placeholder="请输入新名称" v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="handlebtn">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { cloneModeling } from '@/api/modelList'
export default {
  data () {
    return {
      cloneLoading: false,
      cubeName: '',
      form: {
        name: ''
      },
      dialogFormVisible: false
    }
  },
  methods: {
    async handlebtn () {
      this.cloneLoading = true
      this.dialogFormVisible = false
      this.$parent.changeLoading()
      await cloneModeling({ cubeNameClone: this.form.name, cubeName: this.cubeName }).then(res => {
        this.$message.success('复制成功~')
        this.cloneLoading = false
        this.$parent.closeChangeLoading()
      }).catch(_ => {
        this.cloneLoading = false
        this.$parent.closeChangeLoading()
      })
    },
    dialog (data) {
      this.dialogFormVisible = true
      let { name } = JSON.parse(JSON.stringify(data))
      this.cubeName = name
      this.form.name = `${name}_clone`
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
