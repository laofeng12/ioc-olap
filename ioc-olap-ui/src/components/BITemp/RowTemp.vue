<template>
     <div class="draw-list-li" :data-type="dataType">
<!--       <el-dropdown  v-if="dataType==='date'">
       <span class="el-dropdown-link">
         {{name}}<i class="el-icon-arrow-down el-icon&#45;&#45;right"></i>
        </span>
         <el-dropdown-menu slot="dropdown">
           <el-dropdown-item   :class="activeClass == index ? 'active':''"  v-for="(item,index) in dropItems" :key="index" @click.native="dropItemClick(index,$event)">{{item.name}}</el-dropdown-item>
         </el-dropdown-menu>
       </el-dropdown>-->
       <el-dropdown v-if="dataType==='number'">
       <span class="el-dropdown-link">
         <span>{{name}}</span>
         <span v-if="alias!==''">({{alias}})</span>
         <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
         <el-dropdown-menu slot="dropdown">
           <div class="alias-box">
             <el-input v-model="alias" placeholder="别名" size="small"></el-input>
           </div>
           <el-dropdown-item   :class="activeClass == index ? 'active':''"  v-for="(item,index) in dropNumItems" :key="index" @click.native="dropItemClick(index,$event)">{{item.name}}</el-dropdown-item>
         </el-dropdown-menu>
       </el-dropdown>
       <span v-else>
       <span class="el-dropdown-link">
         {{name}}
        </span>
       </span>
       <span class="filtrate-btn-box">
             <i class="el-icon-d-caret"  v-show="isNoAsc" @click="unSort($event)"></i>
             <i class="el-icon-caret-top"  v-show="!isNoAsc&&isAsc" @click="asc($event)"></i>
             <i class="el-icon-caret-bottom" v-show="!isNoAsc&&!isAsc"  @click="dsc($event)"></i>
             <i class="el-icon-close" @click="filtrate($event)"></i>
       </span>
     </div>
</template>

<script>
export default {
  name: 'RowTemp',
  props: {
    index: {
      type: Number,
      required: true
    },
    name: {
      type: String,
      default: ''
    },
    dataType: {
      type: String,
      default: ''
    },
    items: {
      type: Array,
      default: Array
    }
  },
  data () {
    return {
      alias:'',
      isNoAsc:true,
      isAsc: true,
      dataTypeF: '',
      activeClass: -1, // 0为默认选择第一个，-1为不选择
      dropNumItems: [
        { name: '求和（SUM)', value: 'sum' },
        { name: '平均(AVG)', value: 'avg' },
        { name: '计数(COUNT)', value: 'count' },
        { name: '去重计数（DISTINCT）', value: 'distinct' }
      ],
      dropItems: [
        { name: '按年' },
        { name: '按月' },
        { name: '按日' }],
      vItems: this.items
    }
  },
  watch:{
    alias(val){
      this.vItems[this.index].alias=val
    }

  },
  methods: {
    //修改别名
    editAlias(){

    },
    dropItemClick (index, $event) {
      this.activeClass = index // 把当前点击元素的index，赋值给activeClass
      const name = $event.target.innerText
      this.dropNumItems.forEach((item, index) => {
        if (item.name === name) {
          const aggType = item.value
          this.vItems[this.index].aggType = aggType
          //console.log(this.vItems)
        }
      })
    },
    unSort($event){
      this.isNoAsc=false
      this.isAsc=true
      this.vItems[this.index].sortType= "ASC"
    },
    asc ($event) {
      this.isNoAsc=false
      this.isAsc=false
      this.vItems[this.index].sortType= "DESC"
      //console.log(this.vItems)
    },
    dsc ($event) {
      this.isNoAsc=true
      this.isAsc=false
      this.vItems[this.index].sortType= ""
    },
    // 删除筛选
    filtrate ($event) {
      this.$emit('deleteIndex', this.index)
    }
  }
}
</script>

<style lang="scss" scoped>
  @import '~@/styles/variables.scss';
  .draw-list-li{
    background-color: #409EFF;
    color: #fff;
    border-radius: 5px;
    padding: 5px 10px;
    overflow:hidden;
    margin-right: 10px;
    background-color: #409EFF;
    color: #fff;
    border-radius: 5px;
    padding:0 5px;
/*    width:auto;*/
    white-space:nowrap;
    float: left;
    margin-top: 5px;
    .el-dropdown{
    }
    span{
      color: #fff;
    }
    i{
      color: #fff;
      cursor: pointer;
    }
    .el-icon-notebook-2{display: none}
    .filtrate-btn-box{
      i{
        margin-left: 5px;
        color: #fff;
        cursor: pointer;
      }
    }
  }
  .active{background-color: #ecf5ff;color: #409EFF;}
  .alias-box{padding: 0 20px;margin-bottom: 10px;}
</style>
