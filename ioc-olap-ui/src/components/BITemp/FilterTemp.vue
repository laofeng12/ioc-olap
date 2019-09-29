<template>
     <li class="data-list dimen-list" :data-type="dataType">
       <el-tooltip class="item" effect="dark" :content="name" placement="top">
         <span class="column-name">{{name}}</span>
       </el-tooltip>
       <span class="filtrate-btn-box"><i v-if="showEdit" class="el-icon-edit"  @click="editClick"></i><i class="el-icon-close" @click="filtrate"></i>
       </span></li>
</template>

<script>
export default {
  name: 'FilterTemp',
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
    },
    showEdit: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      dataTypeF: '',
      dataId: ''
    }
  },
  methods: {
    editClick () {
      const item = this.items[this.index]
      // console.log(this.items[this.index])
      this.$emit('editClick', item, this.index)
    },
    // 删除筛选
    filtrate () {
      this.$emit('deleteIndex', this.index)
    }
  }
}
</script>

<style lang="scss" scoped>
  @import '~@/styles/variables.scss';
  .dimen-list{
    background-color: #409EFF;
    color: #fff;
    border-radius: 5px;
    padding: 5px 10px;
    .column-name{
      display: inline-block;
      max-width: 180px;
      text-overflow: ellipsis;
      white-space: nowrap;
      overflow: hidden;
      height: 22px;
      line-height: 22px;
    }
    .filtrate-btn-box{
      flex-shrink: 0;
      display: flex;
      i{
        margin-left: 10px;
        color: #fff;
        cursor: pointer;
      }
    }
  }
</style>
