<template>
  <div class="limitsModal">
    <el-dialog :title='"设置角色权限：" + roleName' :visible.sync="dialogFormVisible">
      <el-tree
        :data="treeList"
        ref="tree"
        v-loading="treeLoading"
        show-checkbox
        node-key="id"
        :default-expanded-keys="defaultExpanded"
        :default-checked-keys="defaultKey"
        :default-expand-all="isExpand"
        :props="defaultProps">
      </el-tree>
      <div slot="footer" class="dialog-footer">
        <el-button @click="submits">取 消</el-button>
        <el-button type="primary" @click="submits('ok')">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  data () {
    return {
      dialogFormVisible: false,
      treeLoading: false,
      defaultExpanded: [], // 默认展开的
      defaultKey: [], // 已选择的key
      roleid: '',
      roleName: '',
      isExpand: false,
      treeList: [],
      defaultProps: {
        children: 'children',
        label: 'label'
      }
    }
  },
  computed: {
    ...mapGetters({
      userInfos: 'userInfo'
    })
  },
  methods: {
    dialog (val) {
      console.log(val)
      this.dialogFormVisible = true
      this.treeLoading = true
      this.roleid = val.roleid
      this.roleName = val.rolename
      try {
        const pramas = {
          roleid: val.roleid
        }
        this.$api.jurisdictionList(pramas).then(res => {
          if (res.code === 200) {
            this.treeLoading = false
            this.resetTree(res.rows)
            setTimeout(() => {
              this.$refs.tree.store.nodesMap[this.treeList[0].id].expanded = true
            }, 500)
          }
        })
      } catch (error) {
        this.$message.error(error || '服务异常')
      }
    },
    // 循环tree树
    resetTree (val) {
      let item = []
      val.map((list, i) => {
        let newData = {}
        newData.label = list.title
        newData.id = list.key
        newData.checked = list.checked
        newData.children = list.children ? this.resetTree(list.children) : []
        item.push(newData)
        if (list.checked === true && list.children.length === 0) {
          this.defaultKey.push(list.key)
        } else {
          this.resetTree(list.children)
        }
      })
      this.treeList = item
      return item
    },
    submits (type) {
      this.defaultKey = []
      // 折叠面板
      this.isExpand = false
      for (var i = 0; i < this.$refs.tree.store._getAllNodes().length; i++) {
        this.$refs.tree.store._getAllNodes()[i].expanded = this.defaultExpand
      }
      if (type !== 'ok') {
        this.dialogFormVisible = false
        return
      }
      try {
        this.treeLoading = true
        const halfCheckedKeys = this.$refs.tree.getHalfCheckedKeys()
        const checkedKeys = this.$refs.tree.getCheckedKeys()
        const pramas = {
          resIds: halfCheckedKeys.concat(checkedKeys).join(','),
          roleid: this.roleid
        }

        this.$api.saveTreeList(pramas).then(res => {
          if (res.code === 200) {
            this.$message.success('设置成功')
            this.dialogFormVisible = false
            this.treeLoading = false
          }

          this.$store.dispatch('GetMenuList')
        })
      } catch (error) {
        this.$message.error(error || '服务异常')
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
.limitsModal{
  >>>.el-dialog{
    padding-right:50px;
    width:500px;
  }
  >>>.el-select{
    width:100%;
  }
}
</style>
