<template>
  <div class="rename">
    <el-dialog title="选择共享" :visible.sync="shareVisible">
      <div class="shareBox dis-flex" v-loading="shareLoading">
        <div class="box">
          <div class="title">未选择用户</div>
          <div class="dis-flex">
            <el-input placeholder="请输入内容" size="mini" v-model="searchUser"></el-input>
            <el-button size="mini" type="primary" icon="el-icon-search" @click="searchUserFunc('search')"></el-button>
          </div>
          <el-checkbox-group v-model="userCheckList" class="list">
            <div v-for="(item, index) in userList" class="line" :key="index" v-if="!item.disabled">
              <el-checkbox :label="item">{{item.label}}</el-checkbox>
            </div>
          </el-checkbox-group>
          <div class="bottom dis-flex">
            <el-button size="mini" @click="lastPage" :disabled="userPage <= 0">上一页</el-button>
            <el-button size="mini" @click="nextPage" :disabled="userPage >= totalPage">下一页</el-button>
          </div>
        </div>
        <div class="centerButton">
          <el-button icon="el-icon-arrow-left" circle @click="reduceShare"></el-button>
          <el-button icon="el-icon-arrow-right" circle @click="addShare"></el-button>
        </div>
        <div class="box">
          <div class="title">已选择用户</div>
          <div class="dis-flex">
            <el-input placeholder="请输入内容" size="mini" v-model="searchShare"></el-input>
            <el-button size="mini" type="primary" icon="el-icon-search" @click="searchShareFunc"></el-button>
          </div>
          <el-checkbox-group v-model="shareCheckList" class="list no-bottom">
            <div v-for="(item, index) in showShareList" class="line" :key="index">
              <el-checkbox :label="item">{{item.label}}</el-checkbox>
            </div>
          </el-checkbox-group>
          <!--<div class="bottom dis-flex">-->
          <!--<el-button size="mini">上一页</el-button>-->
          <!--<el-button size="mini">下一页</el-button>-->
          <!--</div>-->
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="shareVisible = false">取 消</el-button>
        <el-button type="primary" @click="share">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>

import { getUserListApi } from '../../../api/login'
import { getShareUserApi, saveShareApi } from '../../../api/instantInquiry'

export default {
  data () {
    return {
      shareVisible: false,
      cubeName: null,
      shareLoading: false,
      userRows: [],
      userList: [],
      shareList: [],
      showShareList: [],
      userCheckList: [],
      shareCheckList: [],
      shareData: {},
      searchUser: '',
      searchShare: '',
      userPage: 0,
      totalPage: 0
    }
  },
  methods: {
    dialog (data) {
      this.shareVisible = true
      console.info('data', data)
      this.getShareUserList(data)
    },
    async getShareUserList (data) {
      this.shareLoading = true
      this.shareData = data
      const cubeName = data.name
      if (cubeName) {
        this.cubeName = cubeName
        this.shareVisible = true
        const { rows, totalPage } = await getUserListApi()
        this.totalPage = totalPage
        this.userRows = rows
        const res = await getShareUserApi({
          cubeName: this.cubeName,
          sourceType: 'cube'
        })
        let shareList = []
        res.forEach(v => shareList.push({ key: v.shareUserId, label: v.shareUserName, disabled: false }))
        this.shareList = shareList
        this.showShareList = shareList
        const userList = rows.map(item => {
          let disabled = false
          res.forEach(v => {
            if (v.shareUserId === item.userid) {
              disabled = true
            }
          })
          return Object.assign(item, { key: item.userid, label: item.fullname, disabled })
        })
        this.userList = userList
        this.shareLoading = false
      } else {
        this.$message.error('分享失败')
        this.shareLoading = false
      }
    },
    async share () {
      const data = { // RealQuery（即席查询） Analyze（Olap分析）
        cubeName: this.cubeName,
        sourceType: 'cube'
      }
      let url = '/olap/apis/olapShare/save?'
      this.shareList.forEach((v, i) => {
        if (i === 0) {
          url += `userIds=${v.key}`
        } else {
          url += `&userIds=${v.key}`
        }
      })
      await saveShareApi(url, data)
      this.$message.success('分享成功')
      this.shareVisible = false
      this.$emit('clickItem', this.shareData, 'share')
    },
    addShare () {
      this.searchShare = ''
      this.shareList = [...this.shareList, ...this.userCheckList]
      this.showShareList = this.shareList
      this.userCheckList = []
      this.cleanShare()
    },
    reduceShare () {
      this.searchShare = ''
      let list = []
      this.shareList.forEach((item, index) => {
        this.shareCheckList.forEach(v => {
          if (item.key === v.key) {
            list.push(index)
          }
        })
      })
      list.reverse()
      list.forEach(v => this.shareList.splice(v, 1))
      this.showShareList = this.shareList
      this.cleanShare()
    },
    cleanShare () {
      const userList = this.userRows.map(item => {
        let disabled = false
        this.shareList.forEach(v => {
          if (v.key === item.userid) {
            disabled = true
          }
        })
        return Object.assign(item, { key: item.userid, label: item.fullname, disabled })
      })
      this.userList = userList
    },
    searchShareFunc () {
      const showShareList = this.shareList.filter(v => v.label.includes(this.searchShare))
      this.showShareList = showShareList
    },
    async searchUserFunc (type) {
      this.shareLoading = true
      if (type === 'search') {
        this.userPage = 0
      }
      const data = this.shareData
      const cubeName = data.name
      if (cubeName) {
        this.cubeName = cubeName
        this.shareVisible = true
        const params = { page: this.userPage, like_account: this.searchUser }
        const { rows, totalPage } = await getUserListApi(params)
        this.userRows = rows
        this.totalPage = totalPage
        const userList = rows.map(item => {
          let disabled = false
          this.shareList.forEach(v => {
            if (v.shareUserId === item.userid) {
              disabled = true
            }
          })
          return Object.assign(item, { key: item.userid, label: item.fullname, disabled })
        })
        this.userList = userList
        this.shareLoading = false
      } else {
        this.$message.error('分享失败')
        this.shareLoading = false
      }
    },
    lastPage () {
      if (this.userPage <= 0) return false
      this.userPage = this.userPage - 1
      this.searchUserFunc()
    },
    nextPage () {
      if (this.userPage >= this.totalPage) return false
      this.userPage = this.userPage + 1
      this.searchUserFunc()
    }
  }
}
</script>

<style lang="scss" scoped>
.rename{
  .shareBox {
    justify-content: space-between;
    align-items: center;
    width: 100%;
    .box {
      width: 35%;
      padding: 10px;
      box-sizing: border-box;
      border: 1px #ddd solid;
      border-radius: 5px;
      .title {
        margin-bottom: 10px;
      }
      .list {
        height: 200px;
        margin: 10px 0;
        overflow: auto;
        .line {
          height: 30px;
          line-height: 30px;
        }
      }
      .no-bottom {
        height: 230px;
      }
      .bottom {
        justify-content: space-between;
        height: 30px;
      }
    }
  }
}
</style>
