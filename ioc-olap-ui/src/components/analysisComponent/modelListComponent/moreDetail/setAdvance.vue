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
                <el-tag type="" v-for="(n, i) in item.includes" :key="i" closable><h6>{{n}}</h6></el-tag>
              </div>
            </div>
            <div class="item_box">
              <span>必要维度</span>
              <div class="box_r">
                <el-tag type="" v-for="(n, i) in item.select_rule.mandatory_dims" :key="i" closable><h6>{{n}}</h6></el-tag>
              </div>
            </div>
            <div class="item_box noflex">
              <span>层级维度</span>
              <div class="adds" v-for="(itemData, i) in item.select_rule.hierarchy_dims" :key="i">
                <div>
                  <el-tag v-for="(n, q) in itemData" :key="q" closable><h6>{{n}}</h6></el-tag>
                </div>
              </div>
            </div>
            <div class="item_box noflex">
              <span>联合维度</span>
              <div class="adds" v-for="(jsonData, t) in item.select_rule.joint_dims" :key="t">
                <div>
                  <el-tag v-for="(x, y) in jsonData" :key="y" closable><h6>{{x}}</h6></el-tag>
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
                <el-tag v-for="(x, y) in n" :key="y" closable><h6>{{x}}</h6></el-tag>
              </div>
            </div>
          </div>
        </div>
        <el-form-item label="模型构建引擎">
          {{list.engine_type === '1' ? 'MapReduce' : 'Spark' }}
        </el-form-item>
        <div class="listSet hetCompose">
          <span>高级列组合</span>
          <div class="listSet__box hetCompose__box" v-if="list.hbase_mapping">
            <div class="adds" v-for="(n, i) in list.hbase_mapping.column_family" :key="i">
              <div>
                <el-tag v-for="(x, y) in n.columns[0].measure_refs" :key="y" closable><h6>{{x}}</h6></el-tag>
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
      descriptionData: [
        { index: '1', name: 'USER_ID', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '2', name: 'USER_ID1', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '3', name: 'USER_ID2', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '4', name: 'USER_ID3', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '5', name: 'USER_ID4', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '6', name: 'USER_ID5', expression: 'string', value: '用户标识', returntype: '正常模式' },
        { index: '7', name: 'USER_ID6', expression: 'string', value: '用户标识', returntype: '正常模式' }
      ],
      descriptionHead: [
        { prop: 'index', label: '序号 ' },
        { prop: 'name', label: '表名称' },
        { prop: 'expression', label: '字段' },
        { prop: 'value', label: '过滤方式' },
        { prop: 'returntype', label: '过滤值' }
      ]
    }
  },
  mounted () {
    this.init()
  },
  methods: {
    init () {
      if (this.jsonData) {
        let { aggregation_groups, rowkey, hbase_mapping, mandatory_dimension_set_list, engine_type } = this.jsonData.CubeList[0]
        if (mandatory_dimension_set_list.length < 1) mandatory_dimension_set_list = [{}]
        this.list = this.jsonData.CubeList[0]
      }
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
          width 31%
          float left
          margin-left 1%
          margin-bottom 10px
          font-size 8px
          text-align center
          background #FBFBFB
          color #555555
          i{
            float right!important
            margin-top 8px
          }
          h6{
            text-overflow: ellipsis;
            float left
            width: 90%;
            overflow: hidden;
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
          width 32%
          float left
          margin-left 1%
          margin-bottom 10px
          font-size 8px
          text-align center
          background #FBFBFB
          color #555555
          i{
            float right!important
            margin-top 8px
          }
          h6{
            text-overflow: ellipsis;
            float left
            width: 90%;
            overflow: hidden;
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
