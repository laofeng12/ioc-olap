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
        <el-table-column prop="apiName" label="模型名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="catalogName" label="模型状态" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiPaths" label="模型大小" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiProtocols" label="资源记录" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiMethod" label="上次构建的时间" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiMethod" label="创建时间" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="apiMethod" label="模型来源" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column
          label="操作"
          align="center">
          <template slot-scope="scope">
            <div class="play">
              <el-dropdown trigger="click" @command="handleCommand">
                <el-button type="text" size="small">操作<i class="el-icon-arrow-down el-icon--right"></i></el-button>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item :command="{type: 'rename', params: scope.row.apiId}">重命名</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'lookDetail', params: scope.row.apiId}">查看</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row.apiId}">编辑</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">构建</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">刷新</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">合并</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">禁用</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">共享</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">复制</el-dropdown-item>
                  <el-dropdown-item :command="{type: 'flowControlModal', params: scope.row}">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <rename ref="rename"></rename>
  </div>
</template>

<script>
import { getApiList } from '@/api/common'
import {  modelDetail, rename } from '@/components/olapComponent/modelListComponent'
export default {
  components: {
    modelDetail, rename
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
      size: this.pageSize,
      sort: 'createtime,desc',
      page: this.currentPage - 1
    }
    getApiList(params).then(res => {
      this.tableData = res.rows
    })
  },
  methods: {
    searchFetch (val) {
      console.log(val)
    },
    createolap () {
      this.$router.push('/olap/createolap/selectStep')
    },
    // 展开详情
    clickTable (val) {
      console.log(val)
    },
    handleCommand (val) {
      this.expands = []
      if (val.type === 'lookDetail') {
        this.expands.push(val.params)
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
