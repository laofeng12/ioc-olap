<template>
  <div class="rename">
    <el-dialog title="构建模型" :visible.sync="dialogFormVisible">
      <h5>选择仪表板：</h5>
      <div class="level">
        <el-tree
          :data="data"
          ref="trees1"
          show-checkbox
          node-key="id"
          :default-expanded-keys="defaultExpand"
          :default-checked-keys="defaultCheckd"
          @check-change="handleCheckChange"
          :props="defaultProps">
        </el-tree>
        <div class="interaction">
          <i class="el-icon-sort"></i>
        </div>
        <div class="openList">
          <p v-for="(item, index) in dataList1" :key="index">{{item}}</p>
        </div>
      </div>
      <h5>选择共享人：</h5>
      <div class="level">
        <el-tree
          :data="data"
          show-checkbox
          node-key="id"
          :default-expanded-keys="[2, 3]"
          :default-checked-keys="[5]"
          :props="defaultProps">
        </el-tree>
        <div class="interaction">
          <i class="el-icon-sort"></i>
        </div>
        <div class="openList">
          <p v-for="(item, index) in dataList" :key="index">{{item}}</p>
        </div>
      </div>
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
      defaultProps: [],
      defaultExpand: [],
      defaultCheckd: [],
      dataList: ['2019年1月南城交通数据', '2019年2月东城交通数据'],
      dataList1: [],
      data: [{
        id: 1,
        label: '一级 1',
        children: [{
          id: 4,
          label: '二级 1-1'
        }]
      }, {
        id: 2,
        label: '一级 2',
        children: [{
          id: 5,
          label: '二级 2-1'
        }, {
          id: 6,
          label: '二级 2-2'
        }]
      }, {
        id: 3,
        label: '一级 3',
        children: [{
          id: 7,
          label: '二级 3-1'
        }, {
          id: 8,
          label: '二级 3-2'
        }]
      }],
      dialogFormVisible: false
    }
  },
  methods: {
    handlebtn () {
    },
    handleCheckChange (data, type, node) {
      // console.log(data, '1111111111', this.$refs.trees1.getCheckedNodes())
      let datas = this.$refs.trees1.getCheckedNodes()
      this.reduceList(datas)
    },
    reduceList (item) {
      console.log(item)
      item && item.map(item => {
        if (item.children) {
          this.reduceList(item)
        } else {
          console.log('获取的', item)
        }
      })
    },
    dialog () {
      this.dialogFormVisible = true
    }
  }
}
</script>

<style lang="stylus" scoped>
.rename{
  .level{
    margin-top 10px
    height 250px
    h5{
      clear both
    }
    .el-tree{
      height 250px
      width 40%
      padding 20px
      border 1px solid #cccccc
      float left
      overflow auto
    }
    .openList{
      width 40%
      height 250px
      border 1px solid #cccccc
      padding 20px
      float right
      overflow auto
    }
    .interaction{
      transform rotate(90deg)
      font-size 30px
      height 250px
      line-height 250px
      float left
      width 20%
    }
  }
}
</style>
