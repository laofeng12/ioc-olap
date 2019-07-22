<template>
  <div class="advancedSet">
     <el-form :model='list' ref="formData">
        <el-form-item label="高级设置" class="item_line"></el-form-item>
        <div class="aggregation">
          <div class="aggregation_head">
            <span>维度分组聚合</span>
          </div>
          <el-card class="box-card" v-for="(item, index) in list.totalSaveList" :key="index">
            <div slot="header" class="clearfix">
              <span>聚合小组</span>
            </div>
            <div class="item_box">
              <span>包含维度</span>
              <div class="box_r">
                <el-tag type="" v-for="(n, i) in item.containData" :key="i" closable>{{n.columnName}}</el-tag>
              </div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r">
                <el-tag type="" v-for="(n, i) in item.necessaryData" :key="i" closable>{{n.columnName}}</el-tag>
              </div>
            </div>
            <div class="item_box noflex">
              <span>层级维度</span>
              <div class="adds" v-for="(itemData, i) in item.levelData" :key="i">
                <div>
                  <el-tag v-for="(n, q) in itemData" :key="q" closable>{{n.columnName}}</el-tag>
                </div>
              </div>
            </div>
            <div class="item_box noflex">
              <span>联合维度</span>
              <div class="adds" v-for="(jsonData, t) in item.jointData" :key="t">
                <div>
                  <el-tag v-for="(x, y) in jsonData" :key="y" closable>{{x.columnName}}</el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        <div class="setRowkeys">
          <p style="margin:20px 0">Rowkeys设置</p>
          <el-table
            :data="list.tableData"
            ref="multipleTable"
            tooltip-effect="dark"
            style="margin-top: 10px;">
            <el-table-column prop="columnName" label="字段名称" align="center"> </el-table-column>
            <el-table-column label="编码类型" align="center" prop="type"></el-table-column>
            <el-table-column label="长度" width="100" align="center" prop="length"></el-table-column>
            <el-table-column prop="apiPaths" label="碎片区" align="center"></el-table-column>
          </el-table>
        </div>
        <div class="listSet">
          <span>维度黑白名单设置</span>
          <div class="listSet__box">
            <div class="adds" v-for="(n, i) in list.savedimensionData" :key="i">
              <div>
                <el-tag v-for="(x, y) in n" :key="y" closable>{{x.columnName}}</el-tag>
              </div>
            </div>
          </div>
        </div>
        <el-form-item label="模型构建引擎">
          {{list.val}}
        </el-form-item>
        <div class="listSet hetCompose">
          <span>高级列组合</span>
          <div class="listSet__box hetCompose__box">
            <div class="adds" v-for="(n, i) in list.savehetComposeData" :key="i">
              <div>
                <el-tag v-for="(x, y) in n" :key="y" closable>{{x.columnName}}</el-tag>
              </div>
            </div>
          </div>
        </div>
     </el-form>
  </div>
</template>

<script>
export default {
  data () {
    return {
      list: {
        totalSaveList: [
          {
            containData: [],
            necessaryData: [],
            levelData: [{}],
            jointData: [{}]
          }
        ],
        tableData: [
          { columnName: '啦啦啦啦', type: 'string', length: '10', apiPaths: '是' },
          { columnName: '啦啦啦啦', type: 'string', length: '10', apiPaths: '是' },
          { columnName: '啦啦啦啦', type: 'string', length: '10', apiPaths: '是' }
        ],
        val: 'lalal',
        savedimensionData: [{}],
        savehetComposeData: [{}]
      }
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
    }
  }
}
</script>

<style lang="stylus" scoped>
.advancedSet{
  height 300px
  overflow auto
  padding 30px 100px
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
  }
  >>>.el-form-item__label{
    width 120px
    text-align left
  }
  >>>.el-table__body-wrapper{
    height 200px
    overflow-y auto
  }
  .selects{
    margin-bottom 0
  }
  >>>.el-form-item{
  }
  .aggregation{
    margin-top 20px
    .box-card{
      margin-top 20px
      .item_box{
        display flex
        margin-bottom 30px
        span{
          width 80px
        }
        .box_r{
          border 1px solid #cccccc
          flex 1
          padding 25px
          cursor pointer
        }
        >>>.el-tag{
          width 30%
          float left
          margin-left 1%
          margin-bottom 10px
          font-size 11px
          text-align center
          background #FBFBFB
          color #555555
          i{
            float right!important
            margin-top 8px
          }
        }
        .adds{
          border none!important
          padding 0
          width 100%
          display flex
          div{
            flex 1
            border 1px solid #cccccc
            padding 25px
            margin-left 80px
            margin-bottom 20px
          }
        }
        .adds:first-child{
          margin-top -20px
        }
        p{
          width 80px
          text-align center
          margin-top 10px
          i{
            color red
            font-size 25px
          }
          i:nth-child(2){
            color green
          }
        }
      }
      .noflex{
        display initial!important
      }
    }
  }
  .listSet{
    margin-top 20px
    span{
      width 100px
    }
    .listSet__box{
      margin-top -20px
      .adds{
        display flex
        width 100%
        margin-bottom 10px
        div{
          margin-left 120px
          flex 1
          padding 25px
          border 1px solid #cccccc
        }
      }
      p{
        width 80px
        text-align center
        margin-top 10px
        i{
          color red
          font-size 25px
        }
        i:nth-child(2){
          color green
        }
      }
      >>>.el-tag{
          width 30%
          float left
          margin-left 1%
          margin-bottom 10px
          font-size 11px
          text-align center
          background #FBFBFB
          color #555555
          i{
            float right!important
            margin-top 8px
          }
        }
    }
    .nos{
      margin-left 100px
      margin-top -10px
    }
  }
}
</style>
