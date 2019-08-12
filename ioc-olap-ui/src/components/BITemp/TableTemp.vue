<template>
  <div class="preview">
    <table   border='1' cellpadding="0" cellspacing="0" :width="width">
      <thead>
      <tr v-for="(item, index) in data.headerData" :key="index">
        <th  v-for="(tdItem, tdIndex) in item" :colspan="tdItem.colspan"
             :rowspan="tdItem.rowspan" :key="tdIndex">
          {{tdItem.data}}
        </th>
        <th v-show="sumsData.rowSum===true &&index===0" :rowspan="data.headerData.length">汇总</th>
      </tr>

      </thead>
      <tbody>
      <tr v-for="(item, index) in data.bodyData" :key="index">
        <td  v-for="(tdItem, tdIndex) in item" :colspan="tdItem.colspan" :class="{'table-body-header': tdItem.property==='column_key'}"
             :rowspan="tdItem.rowspan" :key="tdIndex">
          <a class="link" v-if="type===1&&tdItem.islink===true&&tdIndex===0" @click="linkChildTable(tdItem)">
            {{tdItem.data}}
          </a>
          <div class="cell" v-else>
            {{tdItem.data}}
          </div>
        </td>
        <td v-show="sumsData.rowSum===true">
          <div class="cell">
            {{sumRowList[index]}}
          </div>
        </td>
      </tr>
      <!--列汇总-->
     <tr v-show="sumsData.colSum===true">
        <td :class="{'table-body-header': index===0}" v-for="(item,index) in  sumColList" :key="index">{{item}}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
export default {
  name: 'TableTemp',
  props: {
    hasColSum:{
      type:Boolean,
      default:false
    },
    type: {
      type: Number,
      default: 0
    },
    width: {
      type: String,
      default: ''
    },
    tableData: {
      type: Object,
      default: Object
    },
    sumsData: {
      type: Object,
      default: Object
    }
  },
  data () {
    return {
     data:this.tableData,
      sumColList:['汇总'],
      sumRowList:[],
      rowSum:false,
      colSum:false
    }
  },
  watch:{
    tableData: {
      handler: function (newVal, oldVal) {
        this.data=newVal

      },
      deep: true
    },
    sumsData: {
      handler: function (newVal, oldVal) {
        console.log('配置汇总新',newVal)
        this.rowSum=newVal.rowSum
        this.colSum=newVal.colSum
        //列汇总
        if(this.colSum===true){
          this.getColSum()

        }
        if(this.rowSum===true){
          this.getRowSum()
        }

      },
      deep: true
    }
  },
  created(){
    if(Object.keys(this.sumsData).length==0&&Object.keys(this.data).length==0){
    }else{
      this.getColSum()
      this.getRowSum()
    }
  },
  mounted(){
  },
  methods: {
    linkChildTable (item) {
      this.$emit('linkChildTable', item)
    },
    getRowSum(){
        const bodyData=this.data.bodyData
        const  cofigsData=JSON.parse(this.data.jsonData)
        const rowsCount=cofigsData && cofigsData.rows.length
        // console.log('rowsCount:',rowsCount)
        if(bodyData.length!==0){
          this.sumRowList=[]
          for (let i = 0; i <  bodyData.length; i++) {
            let sum=0
            for (let j = rowsCount;j <  bodyData[0].length; j++) {
              sum += Number(bodyData[i][j].data)
            }
            this.sumRowList.push(sum)
          }
        }
    },
    //列汇总
    getColSum(){
        const bodyData=this.data.bodyData
        if(bodyData.length!==0){
          this.sumColList=['汇总']
          for (let i = 0; i < bodyData[0].length; i++) {
            if (i !== 0) {
              let sum = 0
              for (let j = 0;j < bodyData.length; j++) {
                sum += Number(bodyData[j][i].data)
              }
              this.sumColList.push(sum)
            }
          }
        }
      }

  }
}
</script>

<style lang="scss" scoped>
  @import '~@/styles/variables.scss';
  .preview {
    margin: 10px auto;
    table {
      font-size: 14px;
      color: #606266;
      border: 1px solid #e0ebf7;
      border-collapse: collapse;
      table-layout: fixed;
      word-break:break-all;
      tr {
        text-align: left;
        &.sum{
          color: #409EFF;
        }
        th{
          word-break:break-all;
          min-width: 50px;
        }
     td {
       .cell{
       }

        }
      }
      thead {
        background-color: #f4f9fb;
        th {
          padding: 10px 0;
          padding-left: 10px;
        }
      }
      tbody {
        tr {
          .table-body-header {
            background-color: #f4f9fb;
            font-weight: bold;
            .cell{
              word-break:break-all;
            }
          }
        }
        tr:hover > td {
          background-color: #f5f7fa;
        }
        td {
          padding: 10px;
          transition: background-color .25s ease;
          .link {
            color: #409EFF;
          }
        }
      }
    }
  }

</style>
