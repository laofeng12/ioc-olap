<template>
  <div class="rename">
    <el-dialog title="克隆" :visible.sync="dialogFormVisible">
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
export default {
  data () {
    return {
      form: {},
      dialogFormVisible: false
    }
  },
  methods: {
    handlebtn () {
      this.dialogFormVisible = false
    },
    dialog (data) {
      this.dialogFormVisible = true
      let { name } = JSON.parse(JSON.stringify(data))
      this.form.name = `${name}_clone`
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{}
</style>
