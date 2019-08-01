<template>
  <div class="aside">
    <el-input class="search" placeholder="请输入关键字" v-model="search">
      <i slot="suffix" class="el-input__icon el-icon-search cur-poi"></i>
    </el-input>
    <el-menu default-active="2" class="menu" @open="handleOpen" @close="handleClose" @select="handleSelect">
      <el-submenu v-for="(item, index) in menuList" :index="item.id" :key="index">
        <template slot="title">
          <i class="el-icon-menu"></i>
          <span>{{item.title}}</span>
        </template>
        <el-submenu v-for="(value, i) in item.row" :key="i" :index="value.id" v-if="(value.row && value.row.length > 0)">
          <template slot="title">{{value.name}}</template>
          <el-menu-item v-for="(v, j) in value.row" :index="v.id" :key="j">{{v.name}}</el-menu-item>
        </el-submenu>
        <el-menu-item-group v-else>
          <el-menu-item :index="value.id">{{value.name}}</el-menu-item>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
export default {
  props: {
    menuList: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      search: ''
    }
  },
  watch: {

  },
  methods: {
    handleOpen (key, keyPath) {
      console.log('open', key, keyPath)
    },
    handleClose (key, keyPath) {
      console.log('close', key, keyPath)
    },
    handleSelect (key, keyPath) {
      console.log('select', key, keyPath)
    }
  }
}
</script>

<style lang="scss" scoped>
  .aside {
    width: 200px;
    flex-shrink: 0;
    background-color: white;
    margin-right: 20px;
    .search {
      margin-bottom: 10px;
    }
    .menu {
      border: none;
    }
  }
</style>
