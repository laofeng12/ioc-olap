<template>
  <div class="mechanism folderAside">
    <el-input suffix-icon="el-icon-search" type="text" placeholder="输入关键词" v-model="serachvalue"  clearable></el-input>
    <div class="trees">
      <el-scrollbar style="height:100%">
        <el-tree 
          ref="tree" 
          lazy 
          :indent="12" 
          :data="treeList" 
          :load="getChildTreeList" 
          v-loading="treeLoading"
          auto-expand-parent node-key="id" 
          @node-click="getCurrents" 
          highlight-current 
          :default-expanded-keys="defaultOpenKeys"
          :filter-node-method="filterNode" 
          :props="defaultProps">
          <span class="custom-tree-node" slot-scope='{ node }'>
            <el-tooltip v-if="node.label.length >= 18" 
              class="node__item-tip" 
              effect="dark" 
              :enterable="false"
              :content="node.label ? node.label : ''" placement="top" popper-class="my-dep-toolTip">
              <span class="show-ellipsis">{{ node.label }}</span>
            </el-tooltip>
              <span v-else  class="show-ellipsis">{{node.label}}</span>
          </span>
        </el-tree>
      </el-scrollbar>

    </div>
  </div>
</template>

<script>
import { setTimeout } from 'timers'
import { mapGetters } from 'vuex'
import { getCatalog, getDtalakeTreeList } from '@/api/newOlapModel'
import { getLocalStorage } from '@/utils/index'

export default {
  name: 'treeList',
  data () {
    return {
      treeLoading: false,
      showTree: true,
      serachvalue: '',
      treeList: [],
      defaultOpenKeys: [], // 默认展开的key
      defaultProps: {
        label: 'orgName',
        children: 'subOrgList',
        isLeaf: 'isLeaf'
      }
    }
  },
  watch: {
    serachvalue (val) {
      this.$refs.tree.filter(val)
    }
  },
  methods: {
    init () {
      this.getTreeList()
      // if (this.$route.query.cubeName) {
      //   setTimeout(() => { this.$root.eventBus.$emit('getserchTableList', { orgId: this.ModelAllList.TableList[0].orgId }, 1) }, 1000)
      // }
    },
    // fetchTreeList (val) {
    //   this.treeLoading = true
    //   // this.fetchDatas(val) // 数据湖
    //   this.fetchKelinData()
    // },
    // 左边树形列表 目录树（数据湖+自定义源+本地空间+数据集）
    async getTreeList () {
      try {
        this.treeLoading = true
        const { data } = await getCatalog()
        if (!data) {
          this.$message.error('获取资源目录失败！')
          return
        }
        this.treeList = []
        if (data.dataLakeTreeName && data.dataLakeTree) {
          this.treeList.push({
            id: 'sjh',
            orgName:'数据湖',
            isLeaf: false,
            subOrgList: data.dataLakeTree
          })
        }
        if (data.dataSetTreeName && data.dataSetTree) {
          this.treeList.push({
            id: 'sjj',
            orgName:'数据集',
            isLeaf: false,
            subOrgList: data.dataSetTree
          })
        }
        if (data.dataSourceTreeName && data.dataSourceTree) {
          this.treeList.push({
            id: 'zdy',
            orgName:'自定义',
            isLeaf: false,
            subOrgList: data.dataSourceTree
          })
        }
        if (data.localSpaceTreeName && data.localSpaceTree) {
          this.treeList.push({
            id: 'sc',
            orgName:'上传',
            isLeaf: false,
            subOrgList: data.localSpaceTree
          })
        }
      } catch (e) {
        console.log(e)
        this.treeLoading = false
      } finally {
        this.treeLoading = false
      }
    },
    // 过滤节点
    filterNode (value, data) {
      if (!value) return true
      return data.orgName.indexOf(value) !== -1
    },
    // 选中对应的表
    getCurrents (data, node, self) {
      if (data.isLeaf) {
        this.getResourceList(data, node.parent.key)
      }
    },
    // 资源列表
    getResourceList (data, nodeId) {
      this.$throttle(_ => {
          this.$root.eventBus.$emit('getserchTableList', data)
        }, 300)
    },
    // 获取数据湖目录树 fetchTree
    async getChildTreeList (node, resolve) {
      if (node.level === 0) return resolve(this.treeList)
      if (node.level === 1) return resolve(node.data.subOrgList)
      if (!node.isLeaf) {
         this.treeLoading = true
        let params = {
          orgId: node.data.orgId,
          databaseType: node.data.databaseType
        }
        try {
          const { data } = await getDtalakeTreeList(params)
          data ? resolve(data) : []
        } catch (e) {
          this.treeLoading = false
        } finally {
          this.treeLoading = false
        }
      }
    }
  },
  computed: {
    ...mapGetters({
      ModelAllList: 'ModelAllList',
      serchTableList: 'serchTableList'
    })
  },
  beforeDestroy () {
    this.$root.eventBus.$off('getserchTableList')
    // this.$root.eventBus.$off('clearSelect')
  },
  created () {
    this.init()
  }
}
</script>

<style lang="stylus" scoped>
.datalake {
  .mechanism {
     width 330px !important;
  }
}
  .mechanism{
    background #ffffff
    height 100%
    .trees{
      width: 298px !important;
      height: calc(100% - 32px);
      margin-bottom: 0;
    }
    >>>.el-tree{
      height 100%;
      width 100%;
      overflow auto
      margin-bottom: 0;
      padding-bottom: 0 !important;
    }
    >>>.el-input__suffix{
      margin-top -3px
    }
    >>>.el-tree{
    .el-tree-node__content{
      font-size: 14px;
      height 25px
      line-height 25px
      color: rgba(0,0,0,0.85);
    }
    .el-tree-node__children{
      .el-tree-node__content{
      font-size: 12px;
      color: rgba(0,0,0,0.65)!important;
      }
    }
  }
  >>>.el-loading-spinner{
    width 100%
    top 30%
    .circular{
      width 32px
      height 32px
    }
  }
    >>>.el-input{
      .el-input__inner{
        height 32px
      }
    }
    >>>.is-leaf{
      background none
      width 0
      height 0
      color transparent
      cursor default
    }
  .trees::-webkit-scrollbar{
    width 8px
    height 8px
    background #fff
  }
  .trees::-webkit-scrollbar-track{
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,0.3);
    border-radius: 10px;
    background-color:#fff;
  }
  .trees::-webkit-scrollbar-thumb{
    border-radius: 10px;
    -webkit-box-shadow: inset 0 0 6px rgba(0,0,0,.3);
    background-color: #B5D2DE;
  }
}
.show-ellipsis {
  display: block;
  width: 100%;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
}
</style>
