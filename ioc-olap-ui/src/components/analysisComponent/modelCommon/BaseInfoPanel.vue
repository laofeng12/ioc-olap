<template>
  <div>
    <el-row class='title'>设置关联关系</el-row>
    <div class="base-info-container">
      <div class="linkSetting" v-if="linkModal" ref="linkSetting">
        <el-select name="public-choice" style="margin-top:10px;"  placeholder="请选择关联关系" v-model="linkModal.join.type" @change="getModalRelationSelected" value="">
          <el-option v-for="item in relationData" :key="item.label" :value="item.label" :label="item.value">{{item.value}}</el-option>
        </el-select>
        <div class="item" v-for="(item, index) in linkModalFields" :key="index">
          <h3 class="itemTitle">关联关系{{index+1}}： <a v-if="index > 0" @click="removeField(index)" href="javascript:;">删除</a></h3>
          <h4 class="itemTableTitle"><span>{{linkModal.joinTable}}</span> <span @click="lookDetailData(linkModal.joinId)">查看</span></h4>
          <el-select name="public-choice" value-key="name" v-model="linkModalFields[index].foreign_key" placeholder="请选择关联字段" @visible-change="getModalDataList(linkModal.joinId)" @change="getModalForeignSelected" value="">
            <el-option v-for="coupon in couponList" :key="coupon.name" :label="coupon.name" :value="Object.assign(coupon, { index })" >{{`${coupon.name}（${coupon.dataType}）`}}</el-option>
          </el-select>
          <h4 class="itemTableTitle"><span>{{linkModal.table}}</span><span @click="lookDetailData(linkModal.id)">查看</span></h4>
          <el-select name="public-choice" value-key="name" v-model="linkModalFields[index].primary_key" placeholder="请选择关联字段" @visible-change="getModalDataList(linkModal.id)" @change="getModalPrimarySelected" value="">
            <el-option v-for="coupon in couponList" :key="coupon.name" :label="coupon.name" :value="Object.assign(coupon, { index })" >{{`${coupon.name}（${coupon.dataType}）`}}</el-option>
          </el-select>
        </div>
        <div class="itemAdd"><a href="javascript:;" @click="addFields()" class="itemAddBtn">+添加关联关系</a></div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    linkModal: {
      default: false
    },
    relationData: {
      type: Array,
      default: () => ([])
    },
    linkModalFields: {
      type: Array,
      default: () => ([])
    },
    couponList: {
      type: Array,
      default: () => ([])
    }
  },
  data () {
    return {

    }
  },
  methods: {
    getModalRelationSelected (data) {
      this.$emit('getModalRelationSelected', data)
    },
    lookDetailData (id) {
      this.$emit('lookDetailData', id)
    },
    getModalDataList (id) {
      this.$emit('getModalDataList', id)
    },
    getModalPrimarySelected (e) {
      this.$emit('getModalPrimarySelected', e)
    },
    addFields () {
      this.$emit('addFields')
    },
    getModalForeignSelected (e) {
      this.$emit('getModalForeignSelected', e)
    }
  }
}
</script>

<style lang="scss" scoped>
.title {
  background-color: white;
  height:44px;
  line-height:44px;
  border-bottom:1px solid #e6e9ed;
  color: #1890ff;
  font-weight: 500;
  padding-left:16px;
  box-sizing:border-box;
  // margin-top:0;
}

.input {
  width: 100%;
}
.linkSetting{
  background: #ffffff;
  width: 240px;
  height: 100%;
  overflow: auto;
  text-align: left;
  padding: 0 16px;
  .title{
  font-size: 14px;
  color: #303133;
  height: 42px;
  line-height: 42px;
  border-bottom: 1px solid #E4E7ED;
  letter-spacing: 0;
}
  .itemTitle{
    font-size: 14px;
    color: #B6B6B6;
    letter-spacing: 0;
    line-height: 12px;
    margin-top:10px;
    font-family: PingFangSC-Regular;
  }
  h2,h3,.itemTableTitle{
    margin: 5px 0;
    display: flex;
    span:nth-child(1){
      font-size: 12px;
      color: #5A5A5A;
      text-align: left;
      line-height: 22px;
      text-overflow: ellipsis;
      overflow: hidden;
      width: 80%;
    }
    span:nth-child(2){
      font-size: 14px;
      color: #0486FE;
      cursor: pointer;
      float: right;
    }
  }
  .itemAdd{
    background: #FFFFFF;
    border: 1px solid #0486FE;
    width:100%;
    text-align:center;
    margin-top:15px;
    height: 32px;
    line-height: 32px;
    a{
      font-family: PingFangSC-Regular;
      font-size: 14px;
      color: #0486FE;
      text-align: center;
      line-height: 22px;
    }
  }
  >>>.el-select{
    .el-input__inner{
      font-size: 14px;
    }
    .el-icon-arrow-up{
      margin-top: 5px !important;
    }
    .is-reverse{
      margin-top: -5px !important;
    }
  }
  >>>.el-input__inner{
    height: 32px;
  }
}
</style>
