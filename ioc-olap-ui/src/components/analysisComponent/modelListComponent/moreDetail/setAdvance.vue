<template>
  <div class="advancedSet">
     <el-form :model='list' ref="formData">
        <el-form-item label="高级设置" class="item_line"></el-form-item>
        <div class="aggregation">
          <div class="aggregation_head">
            <span>维度分组聚合</span>
          </div>
          <el-card class="box-card" v-for="(item, index) in list.aggregation_groups" :key="index">
            <div slot="header" class="clearfix">
              <span>聚合小组</span>
            </div>
            <div class="item_box">
              <span>包含维度</span>
              <div class="box_r">
                <el-tag type="" v-for="(n, i) in item.includes" :key="i"><h6>{{n}}</h6></el-tag>
              </div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r">
                <el-tag type="" v-for="(n, i) in item.select_rule.mandatory_dims" :key="i"><h6>{{n}}</h6></el-tag>
              </div>
            </div>
            <div class="item_box noflex">
              <span>层级维度</span>
              <div class="adds" v-for="(itemData, i) in item.select_rule.hierarchy_dims" :key="i">
                <div>
                  <el-tag v-for="(n, q) in itemData" :key="q"><h6>{{n}}</h6></el-tag>
                </div>
              </div>
            </div>
            <div class="item_box noflex">
              <span>联合维度</span>
              <div class="adds" v-for="(jsonData, t) in item.select_rule.joint_dims" :key="t">
                <div>
                  <el-tag v-for="(x, y) in jsonData" :key="y"><h6>{{x}}</h6></el-tag>
                </div>
              </div>
            </div>
          </el-card>
        </div>
        <div class="setRowkeys">
          <p style="margin:20px 0">Rowkeys设置</p>
          <element-table :tableData="descriptionData" :colConfigs="descriptionHead"></element-table>
        </div>
        <div class="listSet">
          <span>维度黑白名单设置</span>
          <div class="listSet__box">
            <div class="adds" v-for="(n, i) in list.mandatory_dimension_set_list" :key="i">
              <div>
                <el-tag v-for="(x, y) in n" :key="y"><h6>{{x}}</h6></el-tag>
              </div>
            </div>
          </div>
        </div>
        <el-form-item label="模型构建引擎">
          {{list.engine_type === 2 ? 'MapReduce' : 'Spark' }}
        </el-form-item>
        <div class="listSet hetCompose">
          <span>高级列组合</span>
          <div class="listSet__box hetCompose__box" v-if="list.hbase_mapping">
            <div class="adds" v-for="(n, i) in list.hbase_mapping.column_family" :key="i">
              <div>
                <el-tag v-for="(x, y) in n.columns[0].measure_refs" :key="y"><h6>{{x}}</h6></el-tag>
              </div>
            </div>
          </div>
        </div>
     </el-form>
  </div>
</template>

<script>
import elementTable from '@/components/ElementTable/index'
export default {
  components: {
    elementTable
  },
  props: {
    jsonData: {
      type: [Object, Array]
    }
  },
  data () {
    return {
      list: {},
      descriptionData: [],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'column', label: '字段名称' },
        { prop: 'encodingType', label: '编码类型' },
        { prop: 'encodingLen', label: '长度' },
        { prop: 'isShardBy', label: '碎片区' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        let { mandatory_dimension_set_list, rowkey } = this.jsonData.CubeList
        this.list = this.jsonData.CubeList
        // 重置高级组合
        this.list.hbase_mapping.column_family.forEach((item, index) => {
          if (item.name === 'F1') {
            item.columns[0].measure_refs.forEach((n, i) => {
              if (n === '_COUNT_') {
                item.columns[0].measure_refs.splice(i, 1)
              }
            })
          }
        })
        if (mandatory_dimension_set_list.length < 1) this.list.mandatory_dimension_set_list = [[]]
        this.descriptionData = rowkey.rowkey_columns.map((item, index) => {
          return {
            index: index + 1,
            column: item.column,
            encodingType: item.encoding.indexOf('.') !== -1 ? item.encoding.split('.')[0] : item.encoding,
            encodingLen: item.lengths ? item.lengths : '无',
            isShardBy: item.isShardBy
          }
        })
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
.advancedSet{
  padding 30px
  margin-top 30px
  background #ffffff
  padding-bottom 100px
  .item_line{
    margin-bottom 3px
    border-bottom 1px solid #cccccc
    font-family: PingFangSC-Medium;
    font-size: 16px;
    color: #262626;
    letter-spacing: 0;
  }
  >>>.el-form-item__label{
    width 120px
    text-align left
  }
  >>>.el-table__body-wrapper{
    height 350px
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
          border: 1px solid #D9D9D9;
          flex 1
          min-height 32px
          cursor pointer
        }
        >>>.el-tag{
          width 200px
          margin-bottom 3px
          float left
          margin-left 1%
          font-size 11px
          margin-top:5px;
          height 22px
          line-height 22px
          text-align center
          background #FBFBFB
          color #555555
        }
        .adds{
          border none!important
          padding 0
          width 100%
          display flex
          div{
            flex 1
            margin-left 80px
            height 32px
            margin-bottom 20px
            border: 1px solid #D9D9D9;
            min-height 32px
            cursor pointer
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
  .contain__box::before{
    content: '*'
    color: red
    margin-right 2px
  }
  h6{
    text-overflow: ellipsis;
    float left
    width: 90%;
    overflow: hidden;
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
          border: 1px solid #D9D9D9;
          flex 1
          min-height 32px
          cursor pointer
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
        width 200px
        margin-bottom 3px
        float left
        margin-left 1%
        font-size 11px
        margin-top:5px;
        height 22px
        line-height 22px
        text-align center
        background #FBFBFB
        color #555555
        }
    }
    .nos{
      margin-left 100px
      margin-top -10px
    }
  }
    >>>.el-table__body tr:nth-child(even){
      background #F5F7FA
    }
  >>>.el-table__header th{
      background #444444
      padding 8px 0
      color #ffffff
      font-family: PingFangSC-Regular;
      font-size: 14px;
    }
}
</style>
