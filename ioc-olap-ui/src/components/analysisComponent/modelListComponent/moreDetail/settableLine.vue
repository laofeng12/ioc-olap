<template>
  <div class="setline">
    <div class="containers">
      <div class="dragRect" :style="'display:' + (isDragRect && (dragRectPosition.x>=1 || dragRectPosition.y>=1) ? 'block' : 'none') + ';left:' + dragRectPosition.x + 'px;top:' + dragRectPosition.y + 'px;'">{{dragRectPosition.label}}</div>
      <div class="holder" ref="holder">
        <div id="myholder" ref="myHolder"></div>
        <div class="papers" ref="papers">
          <div class="halo-cell-layer" :style="cellLayerStyle">
          </div>
        </div>
      </div>
    </div>
    <div class="linkSetting">
      <div class="item" v-if="jointResult.lookups" v-for="(item, index) in jointResult.lookups" :key="index">
        <h3 class="itemTitle">关联关系{{index+1}}</h3>
        <h5>主表：{{item.joinTable}}</h5>
        <h5>子表：{{item.table.substring(item.table.indexOf('.') + 1)}}</h5>
        <h5>连接类型：{{item.join.type}}</h5>
        关联字段：
        <div v-for="(n, index) in item.join.foreign_key" :key="index">
          <span>{{sortSplit(item.join.foreign_key[index])}}</span> => <span>{{sortSplit(item.join.primary_key[index])}}</span>
        </div>
      </div>
    </div>
    <div>
    </div>
  </div>
</template>

<script>
let joint = require('jointjs')
export default {
  data () {
    return {
      isDragRect: false,
      filedPosition: {
        x: 0, y: 0
      },
      couponList: [],
      prevId: '', // 记录上一个id
      relationData: [{ label: 'left', value: '左连接' }, { label: 'inner', value: '内连接' }],
      dragRectPosition: {
        filed: 0,
        id: 0,
        label: '',
        x: -1,
        y: -1
      },
      cellLayerStyle: '',
      cellLayerData: null,
      // jointResult: {
      //   name: '',
      //   description: '',
      //   fact_table: '',
      //   lookups: []
      // },
      linkModal: null,
      linkModalModel: null,
      linkModalFields: [],
      jointResult: { 'name': 'DEFAULT', 'description': '', 'SAxis': 402, 'YAxis': 235, 'fact_table': 'DEFAULT.KYLIN_SALES', 'lookups': [{ 'alias': 'KYLIN_CATEGORY_GROUPINGS', 'id': 'c65b5c62-6d14-4456-bc37-2f7bff28fcca', 'joinAlias': 'KYLIN_SALES', 'joinId': '3fa28668-b02c-49cf-aa53-008e06c49cb7', 'joinTable': 'KYLIN_SALES', 'kind': 'LOOKUP', 'table': 'DEFAULT.KYLIN_CATEGORY_GROUPINGS', 'SAxis': 273, 'YAxis': 95.80000305175781, 'joinSAxis': 502, 'joinYAxis': 235, 'join': { 'primary_key': ['KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID', 'aaaa'], 'foreign_key': ['KYLIN_SALES.PART_DT', 'bbbbbb'], 'pk_type': ['bigint'], 'fk_type': ['date'], 'isCompatible': [true], 'type': 'inner' } }, { 'alias': 'KYLIN_CAL_DT', 'id': 'a2a023c7-8e9d-4d5d-bc5e-9a3b09ce8972', 'joinAlias': 'KYLIN_SALES', 'joinId': '3fa28668-b02c-49cf-aa53-008e06c49cb7', 'joinTable': 'KYLIN_SALES', 'kind': 'LOOKUP', 'table': 'DEFAULT.KYLIN_CAL_DT', 'SAxis': 100, 'YAxis': 227.8000030517578, 'joinSAxis': 502, 'joinYAxis': 235, 'join': { 'primary_key': ['KYLIN_CAL_DT.YEAR_BEG_DT'], 'foreign_key': ['KYLIN_SALES.TRANS_ID'], 'pk_type': ['date'], 'fk_type': ['bigint'], 'isCompatible': [true], 'type': 'left' } }] }

    }
  },
  props: {
    jsonData: {
      type: [Object, Array]
    }
  },
  watch: {
    jsonData () {
      this.init()
    }
  },
  mounted: function () {
    this.init()
  },
  methods: {
    sortSplit (data) {
      let val = data.substring(data.indexOf('.') + 1)
      return val
    },
    initJointResult (data) {
      if (!data) {
        return this.jointResult
      }
      let lookups = []
      let factTable = this.jsonData.ModesList.fact_table
      data.forEach(t => {
        let { primary_key, foreign_key, pk_type, fk_type, isCompatible, type } = t.join
        let primary_key_result = []; let foreign_key_result = []
        let table = t.table.split('.')[1];

        (primary_key || []).forEach((m, i) => {
          primary_key_result.push(primary_key[i].split('.')[1])
          foreign_key_result.push(foreign_key[i].split('.')[1])
        })
        lookups.push({
          alias: t.alias,
          id: t.id,
          SAxis: t.SAxis,
          YAxis: t.YAxis,
          joinAlias: t.joinAlias,
          joinId: t.joinId,
          joinTable: t.joinTable,
          joinSAxis: t.joinSAxis,
          joinYAxis: t.joinYAxis,
          kind: t.kind,
          table: table,
          join: {
            primary_key: primary_key_result,
            foreign_key: foreign_key_result,
            pk_type: pk_type,
            fk_type: fk_type,
            isCompatible: isCompatible,
            type: type
          }
        })
      })

      return {
        description: data.description,
        fact_table: factTable,
        lookups
      }
    },
    init () {
      // this.jointResult = this.initJointResult(JSON.parse(JSON.stringify(this.jointResultData)))
      this.jointResult = this.initJointResult(JSON.parse(JSON.stringify(this.jsonData.ModesList.lookups)))
      // debugger
      let list = this.jointResult.lookups || []
      // let list = this.jsonData.ModesList.lookups || []
      this.graph = new joint.dia.Graph()
      let paper = new joint.dia.Paper({
        el: document.querySelector('#myholder'),
        width: '100%',
        height: 700,
        model: this.graph,
        gridSize: 1
      })

      list.forEach(t => {
        this.addLinkCell(t)
      })
    },

    // 更新模块
    updateModel (id, value) {
      let data = this.jointResult
      let linkIndex = -1
      let updateList = []
      let cells = this.graph.getCells()
      cells.forEach((t, i) => {
        if (t.isLink()) {
          let item = t.get('attrs').data
          linkIndex++

          if (t.get('target').id === id) {
            updateList.push({
              idx: linkIndex,
              field: 'alias'
            })
            item.alias = value
            t.attr('data', item)
          }
          if (t.get('source').id === id) {
            updateList.push({
              idx: linkIndex,
              field: 'joinAlias'
            })
            item.joinAlias = value
            t.attr('data', item)
          }
        }
      });

      (updateList || []).forEach(t => {
        if (data.lookups[t.idx]) {
          data.lookups[t.idx][t.field] = value
        }
      })
      return data
    },

    getCellRamdonPosition (item) {
      let rectWidth = item.label.length * 10
      let rectHeight = 30
      let height = this.$refs.myHolder.offsetHeight
      let width = this.$refs.myHolder.offsetWidth

      let position = {
        x: 200 + Math.ceil(100 * Math.random()),
        y: Math.ceil(100 * Math.random()),
        width: rectWidth,
        height: rectHeight
      }

      if (item.filed) {
        position.x = (width - rectWidth) / 2
        position.y = (height - rectHeight) / 2
      }

      return position
    },

    checkCellsExist (item) {
      if (!this.graph) this.graph = new joint.dia.Graph()

      let itemCell = null
      let cells = this.graph.getCells()

      if (cells.length > 0) {
        cells.forEach(t => {
          let attrs = t.get('attrs')
          if (attrs && attrs.text && attrs.text.label === item.label && item.alias == attrs.text.alias) {
            itemCell = t
          }
        })
      }

      return itemCell
    },
    addLinkCell (item) {
      let factTable = this.jointResult.fact_table
      let source = {
        filed: item.joinTable === factTable ? 1 : 0,
        id: item.joinId,
        label: item.joinTable,
        alias: item.joinAlias,
        position: {
          x: item.joinSAxis,
          y: item.joinYAxis
        }
      }
      let target = {
        filed: item.table === factTable ? 1 : 0,
        id: item.id,
        label: item.table,
        alias: item.alias,
        position: {
          x: item.SAxis,
          y: item.YAxis
        }
      }

      if (!this.graph) {
        this.graph = new joint.dia.Graph()
      }

      let sourceItem = this.addRectCell(source)

      let targetItem = this.addRectCell(target)

      let newLink = new joint.shapes.standard.Link({
        source: sourceItem || { x: 50, y: 50 },
        target: targetItem || { x: 50, y: 50 },
        router: { name: 'manhattan' }, // 设置连线弯曲样式 manhattan直角
        labels: [{ position: 0.5, attrs: { text: { text: '已关联', 'font-weight': 'bold', 'font-size': '12px', 'color': '#ffffff' } } }],
        attrs: {
          'data': item,
          '.marker-target': {
            fill: '##59aff9', // 箭头颜色
            d: 'M 10 0 L 0 5 L 10 10 z'// 箭头样式
          },
          line: {
            stroke: '#59aff9', // SVG attribute and value
            'stroke-width': 0.5// 连线粗细
          }
        }
      })

      this.graph.addCell(newLink)

      return newLink
    },
    getFields (data) {
      let join = data.join
      let list = []
      let primary_key = join.primary_key || []
      let foreign_key = join.foreign_key || []
      let pk_type = join.pk_type || []
      let fk_type = join.fk_type || []

      primary_key.forEach((t, i) => {
        list.push({
          primary_key: `${primary_key[i]}`,
          foreign_key: `${foreign_key[i]}`,
          pk_type: pk_type[i],
          fk_type: fk_type[i]
        })
      })
      return list
    },

    getElementPosition () {
      let eles = this.graph.getElements() || []
      let parentOffset = this.$refs.containers.getBoundingClientRect()
      let result = {}

      for (let i = 0; i < eles.length; i++) {
        let ele = eles[i]
        let attrs = ele.get('attrs')
        let pos = ele.get('position')
        // let text = attrs.text.label + attrs.text.alias
        let text = attrs.text.label
        if (ele.attributes.type !== 'standard.Link' && !result[text]) {
          result[text] = {
            x: pos.x,
            y: pos.y
          }
        }
      }

      return result
    },
    addRectCell (item) {
      if (!this.graph) this.graph = new joint.dia.Graph()

      let isAdd = true
      let newRect = null

      let cell = this.checkCellsExist(item)
      if (cell) return cell

      if (isAdd) {
        let fillColor = item.filed ? '#59AFF9' : '#009688'

        if (item.database) {
          this.jointResult.name = item.database
        }

        // 如果是主表， 就清空所有文件
        if (item.filed) {
          this.clearCells()
        }
        // 设置主表
        if (item.filed == 1 && !this.jointResult.fact_table) {
          this.jointResult.fact_table = `${item.label}`
        }

        let randomPosition = this.getCellRamdonPosition(item)
        // let text = (!item.alias || item.label === item.alias) ? item.label : `${item.label}(${item.alias})`
        let text = (!item.alias || item.label === item.alias) ? item.label : `${item.label}`

        newRect = new joint.shapes.basic.Rect({
          position: {
            x: (item.position && item.position.x) || randomPosition.x,
            y: (item.position && item.position.y) || randomPosition.y
          },
          size: { width: text.length * 9, height: randomPosition.height },
          attrs: { rect: { fill: fillColor, stroke: '#ffffff' }, text: { text: text, label: item.label, alias: item.alias || item.label, filed: item.filed, id: item.id, database: item.database, fill: 'white', 'font-size': 12 } }
        })

        this.graph.addCell(newRect)
      }

      return newRect
    },
    formatJointList: function (data) {
      let posList = this.getElementPosition() || {}
      let factText = data.fact_table + data.fact_table
      let result = {
        name: data.name || '',
        description: data.description || '',
        SAxis: (posList[factText] && posList[factText].x) || 0,
        YAxis: (posList[factText] && posList[factText].y) || 0,
        fact_table: `${data.name}.${data.fact_table}`,
        lookups: []
      };
      (data.lookups || []).forEach(t => {
        let { primary_key, foreign_key, pk_type, fk_type, isCompatible, type } = t.join
        let primary_key_result = []; let foreign_key_result = []
        let pos = posList[t.table + t.alias] || {}
        let joinPos = posList[t.joinTable + t.joinAlias] || {};

        (primary_key || []).forEach((m, i) => {
          primary_key_result.push(`${primary_key[i]}`)
          foreign_key_result.push(`${foreign_key[i]}`)
          // primary_key_result.push(`${t.alias}.${primary_key[i]}`)
          // foreign_key_result.push(`${t.joinAlias}.${foreign_key[i]}`)
        })

        result.lookups.push({
          alias: t.alias,
          id: t.id,
          joinAlias: t.joinAlias,
          joinId: t.joinId,
          joinTable: t.joinTable,
          kind: t.kind,
          // table: `${data.name}.${t.table}`,
          table: `${t.table}`,
          SAxis: pos.x || 0,
          YAxis: pos.y || 0,
          joinSAxis: joinPos.x || 0,
          joinYAxis: joinPos.y || 0,
          join: {
            primary_key: primary_key_result,
            foreign_key: foreign_key_result,
            pk_type: pk_type,
            fk_type: fk_type,
            isCompatible: isCompatible,
            type: type
          }
        })
      })

      return result
    },

    addJointList: function (item) {
      let list = this.jointResult.lookups || []

      if (list.length >= 1) {
        let replaceIdx = -1
        list.forEach((t, i) => {
          if (t.table === item.table && t.joinTable === item.joinTable && t.alias === item.alias && t.joinAlias === item.joinAlias) {
            replaceIdx = i
          }
        })

        if (replaceIdx == -1) {
          this.jointResult.lookups.push(item)
        } else {
          this.jointResult.lookups.splice(replaceIdx, 1, item)
        }
      } else {
        this.jointResult.lookups.push(item)
      }
      let result = this.formatJointList(this.jointResult)

      return result
    },

    getLinkElements: function (ele) {
      let source = ele.getSourceElement() || null
      let target = ele.getTargetElement() || null

      if (target && !target.id) {
        target = null
      }
      return {
        source,
        target
      }
    },

    getDragElement: function (point) {
      let eles = this.graph.getElements() || []
      let element = null

      if (!point || !point.x || !point.y) {
        return false
      }

      for (let i = 0; i < eles.length; i++) {
        let ele = eles[i].attributes
        let x1 = ele.position.x
        let x2 = x1 + ele.size.width
        let y1 = ele.position.y
        let y2 = y1 + ele.size.height

        if (point.x >= x1 && point.x <= x2 && point.y >= y1 && point.y <= y2) {
          element = eles[i]
          break
        }
      }

      return element
    },

    hideCellLayer () {
      this.cellLayerStyle = ''
      this.cellLayerData = null
    },

    getAbsoluteOffset (el) {
      let target = el
      let pos = {
        left: 0,
        top: 0
      }

      while (target) {
        pos.left += target.offsetLeft
        pos.top += target.offsetTop
        target = target.offsetParent
      }

      return pos
    },

    showCellLayer (element) {
      let parentOffset = this.getAbsoluteOffset(this.$refs.holder)
      let rect = element.$el[0].getBoundingClientRect()
      let offset = element.$el.offset()

      this.cellLayerData = element
      this.cellLayerData.isLink = element.model.isLink()
      this.cellLayerStyle = `display:block;width:${rect.width}px;height:${rect.height}px;left:${offset.left - parentOffset.left}px;top:${offset.top - parentOffset.top}px`
    }
  }
}
</script>

<style lang="stylus" scoped>
.hide{
  display none
}
.setline{
  height calc(100vh - 150px)
  position relative
  // overflow auto
  padding-bottom 100px
  .containers{
    position absolute
    width 100%
    padding 20px 5px
  }
}

.dragRect{
  position absolute
  width 100px
  height 30px
  background #009688
  z-index 10000
  text-align center
  line-height 30px
  color #fff
  font-size 14px
}

.holder{
  position relative
  overflow hidden
}

.halo-cell-layer{
  display none
  position absolute
  z-index 100
}

.linkSetting{
  position absolute
  right 0
  width 250px
  padding-top 10px
  height 100%
  // padding-bottom 100px
  overflow auto
  text-align left
  padding-left 20px
  border-left 1px solid #cccccc
  h2,h3,.itemTableTitle{
    margin 5px 0
    span{
      margin-left 10px
      color #009688
      cursor pointer
    }
  }
  >>>.el-select{
    .el-input__inner{
      font-size 10px
    }
  }
  >>>.el-input__inner{
    height 30px
  }
}
</style>
