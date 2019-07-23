<template>
  <!--通用报表生成表格-->
  <div v-if="type===1">
    <table-temp :tableData="pTableData[0]"  width="100%" :sumsData="pSumsData[0]"></table-temp>
  </div>
  <!--主从报表生成表格-->
  <div v-else-if="type===2">
    <table-temp :tableData="pTableData[0]" :sumsData="pSumsData[0]" width="100%"  @linkChildTable="linkChildTable" :type="1"></table-temp>
    <!--查看从报表-->
    <el-dialog title="查看从报表" :visible.sync="formReport" :modal="false" :modal-append-to-body="false" width="80%">
      <table-temp :tableData="pTableData[1]"  :sumsData="pSumsData[1]" width="100%"></table-temp>
    </el-dialog>
  </div>
  <!--嵌套报表生成表格-->
  <div  v-else class="insert-table-box">
    <table-temp v-for="(item,index) in pTableData" :key="index" :tableData="item"  :sumsData="pSumsData[index]"></table-temp>
  </div>
</template>

<script>
import TableTemp from './TableTemp'
export default {
  name: 'StatementTable',
  props: {
    sumsData:{
      type: Array,
      default:()=>[]
    },
    type: {
      type: Number,
      default: 0
    },
    tableData: {
      type: Array,
      default:()=>[]
    }
  },
  components: { TableTemp },
  data () {
    return {
      formReport: false,
      pTableData:[],
      pSumsData:[]
    }
  },
  created(){

  },
  watch:{
    tableData: {
      handler: function (newVal, oldVal) {
          this.pTableData=newVal
      },
      deep: true
    },
    sumsData: {
      handler: function (newVal, oldVal) {
        this.pSumsData=newVal
        console.log("this.pSumsData",this.pSumsData)
      },
      deep: true
    }
  },
  methods: {
    linkChildTable (item) {
      console.log(item)
      this.formReport = true
    }
  }
}
</script>

<style lang="scss" scoped>
  .insert-table-box{
    border: 1px solid #e0ebf7;
    min-height: 400px;
    display: flex;
    flex-wrap: wrap;
    align-items:flex-start;
    justify-content:space-around ;
  .preview{
    padding: 10px;
 /*   flex: 50%;*/
    overflow: hidden;
    overflow-x: auto;
  }
  }
</style>
