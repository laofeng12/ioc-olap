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
        v-loading="getLoading"
        :data="tableData"
        ref="multipleTable"
        tooltip-effect="dark"
        @selection-change="handleSelectionChange"
        style="width: 100%;margin-top: 10px;">
        <el-table-column prop="name" label="工程名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="related_cube" label="模型名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="progress" label="构建状态" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
              <el-progress :percentage="70"></el-progress>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="exec_end_time" label="构建时间" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
              {{scope.row.exec_end_time | formatDate}}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="构建时长" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div>
              {{Conversion(scope.row.duration)}}
            </div>
          </template>
        </el-table-column>
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
import { mapGetters } from 'vuex'
import { filterTime } from '@/utils/index'
export default {
  data () {
    return {
      getLoading: false,
      like_catalogName: '',
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      tableData: []
    }
  },
  filters: {
    formatDate (time) {
      return filterTime(time)
    }
  },
  mounted () {
    this.init()
  },
  computed: {
    ...mapGetters({
      cubeObjListData: 'cubeObjListData'
    })
  },
  methods: {
    init () {
      this.$store.dispatch('SaveCubeObjListData').then(res => {
        this.getLoading = true
        if (res) {
          this.getLoading = false
          this.tableData = res
        }
      })
    },
    searchFetch (val) {
      console.log(val)
    },
    handleCommand () {

    },
    handleSelectionChange () {

    },
    Conversion (second) {
      var days = Math.floor(second / 86400)
      var hours = Math.floor((second % 86400) / 3600)
      var minutes = Math.floor(((second % 86400) % 3600) / 60)
      var seconds = Math.floor(((second % 86400) % 3600) % 60)
      // var duration = days + '天' + hours + '小时' + minutes + '分' + seconds + '秒'
      var duration = minutes + '分' + seconds + '秒'
      return duration
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
  >>>.el-progress-bar{
    height 20px!important
    line-height 20px
    .el-progress-bar__outer{
      height 20px!important
      line-height 20px
      border-radius 0
      .el-progress-bar__inner{
        border-radius 0
      }
    }
    .el-progress__text{
      font-size 13px
    }
  }
}
</style>
