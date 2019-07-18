<template>
   <div class="shink-pannel">
     <el-card class="box-card" shadow="never" :style="nameCardStyleObj" :body-style="{ padding: '7px 10px' }">
       <div slot="header" class="clearfix" style="line-height: 32px">
         <div class="box-card-title">
           <span  class="box-card-name" style="">{{name}}</span>
           <el-input v-model="searchText" @change="handleSearch" placeholder="请输入关键词" size="small" v-show="isHasInput"></el-input>
         </div>
         <div class="icon-button-box">
           <a @click="addClick()" v-show="isHasAdd">
             <i class="el-icon-plus"></i>
           </a>
           <a @click="searchClick()" v-show="isHasSearch">
             <i class="el-icon-search"></i>
           </a>
           <a  @click="fold()"  v-show="showFold">
             <i class="el-icon-arrow-down"></i>
           </a>
           <a  @click="spread()"  v-show="!showFold">
             <i class="el-icon-arrow-up"></i>
           </a>
         </div>
       </div>
       <div class="vbox_content">
         <slot name="content"></slot>
       </div>
     </el-card>
   </div>
</template>

<script>
// import _ from 'lodash'

export default {
  name: 'ShrinkPannel',
  props: {
    name: {
      type: String,
      default: ''
    },
    isHasAdd: {
      type: Boolean,
      default: false
    },
    isHasSearch: {
      type: Boolean,
      default: false
    },
    isHasInput: {
      type: Boolean,
      default: false
    }

  },
  data () {
    return {
      nameCardStyleObj: {
        height: 'auto'
      },
      showFold: true,
      searchText: ''
    }
  },
  methods: {
    addClick () {
      this.$emit('addClick')
    },
    searchClick () {
      this.isHasInput = true
      this.isHasSearch = false
      this.$emit('searchClick')
    },
    fold () {
      this.nameCardStyleObj.height = '50px'
      this.showFold = false
      // this.$emit('onSpread', false)
    },
    spread () {
      this.nameCardStyleObj.height = 'auto'
      this.showFold = true
      // this.$emit('onSpread', true)
    },
    handleSearch () {
      // if (!this.searchText) return
      console.log(this.searchText)
      // const func = this.$emit('on-shrink-search', this.searchText)
      // _.debounce(func, 400)
    }
  }
}
</script>

<style lang="scss">
  @import '~@/styles/variables.scss';
  .box-card{
    margin-bottom: 10px;
    border-radius: 5px;
    /deep/ .el-card__header{
      /*padding: 10px 20px;*/
      padding: 8px 10px;
    }
  &.is-always-shadow{
     box-shadow: 0 2px 12px 0 rgba(0,0,0,0);
   }
  .clearfix:before,
  .clearfix:after {
    display: table;
    content: "";
  }
  .clearfix:after {
    clear: both
  }
    .box-card-title{
      float: left;
      display: flex;
      .box-card-name{
        display: inline-block;min-width:40px;
        font-size: 15px;
      }
      /deep/ input{
        width: 130px;
      }
    }
    .icon-button-box{
      float: right;
      color: #cccccc;
  /*    display: flex;
      justify-content:flex-end;
      align-items: center; */
      a{
        margin-left: 5px;
      }
      a:hover{
        color: #409EFF;
      }
    }
  }
</style>
