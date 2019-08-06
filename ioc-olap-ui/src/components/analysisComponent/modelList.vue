<template>
  <div class="modelList">
    <header>
      <el-input v-model="like_catalogName" placeholder="请输入服务名称" clearable>
        <template slot="append">
          <el-button type="primary" size="small" icon="el-icon-search" @click.native="searchFetch(like_catalogName)">搜索</el-button>
        </template>
      </el-input>
      <el-button type="success" size="small" icon="el-icon-plus" @click="createolap">新建模型</el-button>
    </header>
    <el-table
        :data="tableData"
        ref="multipleTable"
        :row-key="getRowKeys"
        :expand-row-keys="expands"
        tooltip-effect="dark"
        @row-click="clickTable"
        @selection-change="handleSelectionChange"
        style="width: 100%;margin-top: 10px;">
        <el-table-column align="center" show-overflow-tooltip type="expand">
          <template>
            <el-popover>
              <model-detail @closeExpands="closeExpands"></model-detail>
            </el-popover>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="模型名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="status" label="模型状态" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="size_kb" label="模型大小" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="input_records_count" label="资源记录" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="last_modified" label="上次构建的时间" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="partitionDateStart" label="创建时间" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="partitionDateColumn" label="模型来源" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column
          label="操作"
          align="center">
          <template slot-scope="scope">
            <div class="play">
              <el-dropdown trigger="click" @command="handleCommand">
                <el-button type="text" size="small">操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="{type: 'lookDetail', params: scope.row.apiId}">查看</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row.apiId}">编辑</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'construct', params: scope.row}">构建</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'reloads', params: scope.row}">刷新</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'merge', params: scope.row}">合并</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'disableds', params: scope.row}">禁用</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'enable', params: scope.row}">启用</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'sharedTable', params: scope.row}">共享</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'clones', params: scope.row}">复制</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'dels', params: scope.row}">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <rename ref="rename"></rename>
      <construct ref="construct"></construct>
      <reloads ref="reloads"></reloads>
      <merge ref="merge"></merge>
      <sharedTable ref="sharedTable"></sharedTable>
  </div>
</template>

<script>
import { getModelDataList, buildModeling, cloneModeling, disableModeling, enableModeling } from '@/api/modelList'
import { modelDetail, rename, construct, reloads, merge, sharedTable } from '@/components/analysisComponent/modelListComponent'
export default {
  components: {
    modelDetail, rename, construct, reloads, merge, sharedTable
  },
  data () {
    return {
      like_catalogName: '',
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      getRowKeys (row) {
        return row.apiId
      },
      // 要展开的行，数值的元素是row的key值
      expands: [],
      tableData: []
    }
  },
  mounted () {
    const params = {
      limit: 15,
      offset: 0
    }
    getModelDataList(params).then(res => {
      this.tableData = res
    })
  },
  methods: {
    searchFetch (val) {
      console.log(val)
    },
    createolap () {
      this.$router.push('/analysisModel/createolap/selectStep')
    },
    // 展开详情
    clickTable (val) {
      console.log(val)
    },
    returnSuccess () {
      this.$message({
        type: 'success',
        message: '成功!'
      })
      this.dialogFormVisible = false
    },
    handleCommand (val) {
      this.expands = []
      let texts = ''
      switch (val.type) {
        case 'disableds':
          texts = '确定禁用此模型？禁用后，引用了该模型的功能将无法使用！'
          break
        case 'enable':
          texts = '确定启用该模型？'
          break
        case 'clones':
          texts = '确定复制该模型？'
          break
        case 'dels':
          texts = '确定删除该模型？'
          break
      }
      // let texts = val.type === 'disableds' ? '确定禁用此模型？禁用后，引用了该模型的功能将无法使用！'
      //   : (val.type === 'clones' ? '确定赋值该模型？' : '确定删除改模型？')
      if (val.type === 'lookDetail') {
        this.expands.push(val.params)
        return
      }
      if (val.type === 'disableds' || val.type === 'enable' || val.type === 'clones' || val.type === 'dels') {
        this.$confirm(texts, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          if (val.type === 'disableds') {
            disableModeling({ cubeName: val.params.name }).then(res => {
              console.log(res)
            })
          } else if (val.type === '') {

          }
        })
        return
      }
      this.$refs[val.type].dialog()
    },
    closeExpands () {
      this.expands = []
    },
    handleSelectionChange () {

    }
  }
}
</script>

<style lang="stylus" scoped>
.modelList{
  header{
    height 60px
    background #C9E8FF
    padding 15px
    >>>.el-input{
      width 250px
      float left
    }
    >>>.el-button{
      float right
    }
  }
  >>>.el-table__row{
    .el-table__expand-column{
      opacity 0
    }
  }
  >>>.el-table__expanded-cell{
    height 500px
    overflow auto
    width 100%
    padding 10px
    padding-bottom 50px
    .el-popover{
      display block!important
      position absolute
      top 0
      left 0
      width 100%
    }
  }
}
</style>
