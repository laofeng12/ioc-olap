<template>
  <div class="modelList">
    <header>
      <el-input v-model="like_catalogName" placeholder="请输入服务名称" clearable>
        <template slot="append">
          <el-button type="primary" size="small" icon="el-icon-search" @click.native="searchFetch(like_catalogName)">搜索</el-button>
        </template>
      </el-input>
    </header>
    <el-table
        :data="tableData"
        ref="multipleTable"
        tooltip-effect="dark"
        @selection-change="handleSelectionChange"
        style="width: 100%;margin-top: 10px;">
        <el-table-column prop="apiName" label="工程名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="catalogName" label="模型名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiPaths" label="构建状态" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiProtocols" label="构建时间" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiMethod" label="构建时长" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column
          label="操作"
          align="center">
          <template slot-scope="scope">
            <div class="play">
              <el-dropdown trigger="click" @command="handleCommand">
                <el-button type="text" size="small">操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row.apiId}">查看日志</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row.apiId}">暂停</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">停止</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">诊断</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">运行</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
  </div>
</template>

<script>
export default {
  data () {
    return {
      like_catalogName: '',
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      tableData: []
    }
  },
  mounted () {
    const params = {
      size: this.pageSize,
      sort: 'createtime,desc',
      page: this.currentPage - 1
    }
    this.$api.getApiList(params).then(res => {
      this.tableData = res.rows
    })
  },
  methods: {
    searchFetch (val) {
      console.log(val)
    },
    handleCommand () {

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
}
</style>
