<template>
  <div class="con" :style="`width: ${tableBoxWidth}px; height: ${tableBoxHeight}px`">
    <div class="showCon">
      <table border='1' cellpadding="0" cellspacing="0">
        <tbody>
          <tr v-for="(item, index) in tableData" :key="index">
            <td v-for="(tdItem, tdIndex) in item" :colspan="tdItem.colspan"
                :class="`${ (tdItem.type === 'th' || tdItem.type !== 4) && 'table-header' } ${(tdItem.type === 4 && canClick) && 'cur-pointer'}`"
                :rowspan="tdItem.rowspan" :key="`${index}-${tdIndex}`" @click="`${(tdItem.type === 4 && canClick) && tdClick(tdItem, canClick)}`">
              <div class="cell">
                {{tdItem.value}}
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    tableData: {
      type: Array,
      required: true
    },
    canClick: {
      default: false
    },
    diffWidth: {
      type: Number,
      default: 536
    }
    // sumsData: {
    //   type: Object
    // }
  },
  data () {
    return {
      search: '',
      tableBoxWidth: 800,
      tableBoxHeight: 700
    }
  },
  watch: {},
  created () {
    this.tableBoxWidth = document.body.offsetWidth - this.diffWidth
    this.tableBoxHeight = document.body.offsetHeight - 141
  },
  methods: {
    tdClick (item, type) {
      this.$emit('tdClick', item, type)
    }
  }
}
</script>

<style lang="scss" scoped>
  .con {
    width: 100%;
    overflow: auto;
    background-color: #ffffff;
    .showCon {
      table {
        font-size: 14px;
        color: #606266;
        border: 1px solid #e0ebf7;
        border-collapse: collapse;
        table-layout: fixed;
        word-break: break-all;
        min-width: 100%;
        text-align: center;
        td {
          min-width: 150px;
          height: 50px;
        }
        .table-header {
          background-color: #f4f9fb;
          font-weight: bold;
        }
        .cur-pointer {
          cursor: pointer;
        }
      }
    }
  }
  .mar-center {
    margin: 0 auto;
  }
</style>
