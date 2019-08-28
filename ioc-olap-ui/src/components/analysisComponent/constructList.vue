<template>
  <div class="modelList">
    <header>
      <el-input v-model="searchData.cubeName" placeholder="请输入服务名称" clearable>
        <template slot="append">
          <el-button type="primary" size="small" icon="el-icon-search" @click.native="searchFetch(searchData)">搜索</el-button>
        </template>
      </el-input>
    </header>
    <el-table
        v-loading="getLoading"
        :data="tableData"
        ref="multipleTable"
        tooltip-effect="dark"
        style="width: 100%;margin-top: 10px;">
        <el-table-column prop="name" label="工程名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="related_cube" label="模型名称" align="center" show-overflow-tooltip> </el-table-column>
        <el-table-column prop="progress" label="构建状态" align="center" show-overflow-tooltip>
          <template slot-scope="scope">
            <div v-if="scope.row.job_status === 'FINISHED'" style="color:green;">成功</div>
            <div v-if="scope.row.job_status === 'STOPPED'" style="color:block;">已暂停</div>
            <div v-if="scope.row.job_status === 'DISCARDED'" style="color:pink;">已停止</div>
            <div v-if="scope.row.job_status === 'ERROR'" style="color:red;">失败</div>
            <div v-if="['PENDING', 'RUNNING'].includes(scope.row.job_status)">
              <el-progress :percentage="scope.row.progress"></el-progress>
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
                  <el-dropdown-item :command="{type: 'lookUserModal', params: scope.row}">查看日志</el-dropdown-item>
                  <el-dropdown-item v-if="['PENDING', 'RUNNING'].includes(scope.row.job_status)" :command="{type: 'pauseJob', params: scope.row}">暂停</el-dropdown-item>
                  <el-dropdown-item v-if="['PENDING', 'RUNNING', 'ERROR'].includes(scope.row.job_status)" :command="{type: 'cancelJob', params: scope.row}">停止</el-dropdown-item>
                  <el-dropdown-item v-if="['STOPPED', 'DISCARDED', 'ERROR'].includes(scope.row.job_status)" :command="{type: 'resumeJob', params: scope.row}">运行</el-dropdown-item>
                  <el-dropdown-item v-if="['DISCARDED', 'FINISHED'].includes(scope.row.job_status)" :command="{type: 'delete', params: scope.row}">删除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </template>
        </el-table-column>
      </el-table>
    <div class="more" v-if="moreShow && tableData.length > 0" @click="moreData">更多数据</div>
    <el-dialog title="查看日志" :visible.sync="logListVisible">
      <div class="logListBox dis-flex">
        <el-timeline>
          <el-timeline-item icon="el-icon-star-off" type="primary" size="large" timestamp="详细信息" placement="top">
            <table border='1' cellpadding="0" cellspacing="0">
              <tbody>
                <tr>
                  <td class="title">工作名称</td>
                  <td class="text">{{logData.name}}</td>
                </tr>
                <tr>
                  <td class="title">职位ID</td>
                  <td class="text">{{logData.uuid}}</td>
                </tr>
                <tr>
                  <td class="title">状态</td>
                  <td class="text">{{logData.job_status}}</td>
                </tr>
                <tr>
                  <td class="title">持续时间</td>
                  <td class="text">{{Conversion(logData.duration)}}</td>
                </tr>
                <tr>
                  <td class="title">MapReduce等待</td>
                  <td class="text">{{Conversion(logData.exec_interrupt_time)}}</td>
                </tr>
              </tbody>
            </table>
          </el-timeline-item>
          <el-timeline-item v-for="(item, index) in steps" :key="index" :icon="item.icon" placement="top"
            type="primary" :color="item.color" size="large" :timestamp="Translate(item.exec_start_time)">
            <div class="info">
              <div><span class="c-blue">#{{index + 1}}步骤名称：</span>{{item.name}}</div>
              <div><span class="c-blue">数据大小：</span>{{Number(item.info.hdfs_bytes_written) / 1000}}kb</div>
              <div><span class="c-blue">持续时间：</span>{{Conversion(item.exec_end_time - item.exec_start_time)}}</div>
              <div><span class="c-blue">等待：</span>{{item.exec_wait_time || 0}}秒</div>
            </div>
            <el-button type="text" @click="showLogDetails(logData.uuid, item.id)">查看日志</el-button>
          </el-timeline-item>
        </el-timeline>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="logListVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
    <el-dialog title="日志详情" :visible.sync="logDetailsVisible">
      <div class="logDetails">
        <div class="pre" v-html="logDetails"></div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="logDetailsVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { pauseJobListModeling, cancelJobListModeling, resumeJobListModeling, deleteJobListModeling , getLogDetailsApi } from '@/api/modelList'
import { filterTime } from '@/utils/index'

export default {
  data () {
    return {
      searchData: {
        cubeName: ''
      },
      getLoading: false,
      like_catalogName: '',
      pageSize: 20,
      currentPage: 1,
      totalCount: 1,
      tableData: [],
      logListVisible: false,
      logDetailsVisible: false,
      steps: [],
      logData: {},
      logDetails: '',
      offset: 0,
      moreShow: true
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
    async init (val) {
      console.info(val)
      const params = {
        limit: 15,
        offset: this.offset,
        ...val
      }
      this.getLoading = true
      const res = await this.$store.dispatch('SaveCubeObjListData', params)
      if (res.length > 0) {
        this.tableData = [...this.tableData, ...res]
      } else {
        this.moreShow = false
        this.$message.success('已加载所有数据')
      }
      this.getLoading = false
    },
    searchFetch (val) {
      this.getLoading = true
      this.init(val)
    },
    handleCommand (val) {
      const { type, params } = val
      let texts = ''
      switch (type) {
        case 'lookUserModal':
          this.logData = val.params
          const steps = val.params.steps && val.params.steps.map(v => {
            const data = Object.assign({}, v, {
              color: '#00a65b',
              icon: 'el-icon-check'
            })
            return data
          })
          this.steps = steps
          this.logListVisible = true
          break
        case 'pauseJob':
          texts = '暂停该模型？'
          break
        case 'cancelJob':
          texts = '停止该模型？'
          break
        case 'resumeJob':
          texts = '运行该模型？'
          break
        case 'delete':
          texts = '删除该模型？'
          break
      }
      if (texts) {
        this.$confirm(texts, {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          this.getLoading = true
          let list = {
            jobsId: params.uuid
            // cubeName: params.related_cube
          }
          if (type === 'pauseJob') {
            await pauseJobListModeling(list).then(res => {
              this.$message.success('已暂停')
              this.init()
            }).catch(_ => {
              this.getLoading = false
            })
          }
          if (type === 'cancelJob') {
            await cancelJobListModeling(list).then(res => {
              this.$message.success('已停止')
              this.init()
            }).catch(_ => {
              this.getLoading = false
            })
          }
          if (type === 'resumeJob') {
            await resumeJobListModeling(list).then(res => {
              this.$message.success('已运行')
              this.init()
            }).catch(_ => {
              this.getLoading = false
            })
          }
          if (type === 'delete') {
            await deleteJobListModeling(list).then(res => {
              this.$message.success('已删除')
              this.init()
            }).catch(_ => {
              this.getLoading = false
            })
          }
          this.getLoading = false
        })
      }
    },
    async showLogDetails (jobId, stepId) {
      // this.logListVisible = false
      const params = { jobId, stepId }
      const { cmd_output } = await getLogDetailsApi(params)
      this.logDetails = cmd_output
      this.logDetailsVisible = true
    },
    Conversion (second) {
      var days = Math.floor(second / 86400)
      var hours = Math.floor((second % 86400) / 3600)
      var minutes = Math.floor(((second % 86400) % 3600) / 60)
      var seconds = Math.floor(((second % 86400) % 3600) % 60)
      // var duration = days + '天' + hours + '小时' + minutes + '分' + seconds + '秒'
      var duration = minutes + '分' + seconds + '秒'
      return duration
    },
    Translate (time) {
      return filterTime(time)
    },
    moreData () {
      this.offset += 15
      this.init()
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
  .logListBox {
    max-height 300px
    overflow auto
    padding-left 5px
    box-sizing border-box
    table {
      font-size 14px
      color #606266
      border 1px solid #e0ebf7
      border-collapse collapse
      table-layout fixed
      word-break break-all
      text-align center
      td {
        min-width 150px
        height 40px
      }
    }
    .c-blue {
      color: #1877f1
    }
  }
  .logDetails {
    max-height 300px
    overflow auto
    padding 10px
    box-sizing border-box
    border 1px #ddd solid
    .pre {
      white-space pre-wrap
    }
  }
  .more {
    height 40px
    line-height 40px
    background-color #409EFF
    color #ffffff
    margin-top 30px
    text-align center
    cursor pointer
  }
}
</style>
