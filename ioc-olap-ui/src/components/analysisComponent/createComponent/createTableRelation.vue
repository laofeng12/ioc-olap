<template>
  <div class="tableRelation">
    <div class="containers" ref="containers" @mousemove="mousemove" @mouseup="dragTable()">
      <fact-table></fact-table>
      <div class="linkSetting" v-if="linkModal" ref="linkSetting">
        <h2 class="title">设置关联关系</h2>
        <el-select name="public-choice" style="margin-top:10px;"  placeholder="请选择关联关系" v-model="linkModal.join.type" @change="getModalRelationSelected">
          <el-option v-for="item in relationData" :key="item.label" :value="item.label" :label="item.value">{{item.value}}</el-option>
        </el-select>
        <div class="item" v-for="(item, index) in linkModalFields" :key="index">
          <h3 class="itemTitle">关联关系{{index+1}}： <a v-if="index > 0" @click="removeField(index)" href="javascript:;">删除</a></h3>
          <h4 class="itemTableTitle"><span>{{linkModal.joinTable}}</span> <span @click="lookDetailData(linkModal.joinId)">查看</span></h4>
          <el-select name="public-choice" value-key="name" v-model="linkModalFields[index].foreign_key" placeholder="请选择关联字段" @visible-change="getModalDataList(linkModal.joinId)" @change="getModalForeignSelected">
            <el-option v-for="coupon in couponList" :key="coupon.name" :label="coupon.name" :value="Object.assign(coupon, { index })" >{{`${coupon.name}（${coupon.dataType}）`}}</el-option>
          </el-select>
          <h4 class="itemTableTitle"><span>{{linkModal.table}}</span><span @click="lookDetailData(linkModal.id)">查看</span></h4>
          <el-select name="public-choice" value-key="name" v-model="linkModalFields[index].primary_key" placeholder="请选择关联字段" @visible-change="getModalDataList(linkModal.id)" @change="getModalPrimarySelected">
            <el-option v-for="coupon in couponList" :key="coupon.name" :label="coupon.name" :value="Object.assign(coupon, { index })" >{{`${coupon.name}（${coupon.dataType}）`}}</el-option>
          </el-select>
        </div>
        <div class="itemAdd"><a href="javascript:;" @click="addFields()" class="itemAddBtn">+添加关联关系</a></div>
      </div>
      <!-- <task-wark></task-wark> -->
      <div class="dragRect" :style="'display:' + (isDragRect && (dragRectPosition.x>=1 || dragRectPosition.y>=1) ? 'block' : 'none') + ';left:' + dragRectPosition.x + 'px;top:' + dragRectPosition.y + 'px;'">{{dragRectPosition.label}}</div>
      <div class="holder" ref="holder">
        <!-- <button style="width:100px;height:30px" @click="click_add">add</button> -->
        <div id="myholder" ref="myHolder"></div>
        <div class="papers" ref="papers" @click="papersClick">
          <div class="halo-cell-layer" :style="cellLayerStyle">
            <!-- 方块部分 -->
            <div v-if="cellLayerData && !cellLayerData.isLink">
              <!-- 删除 -->
              <div class="remove" data-type="remove"></div>
              <!-- 设置关联 -->
              <div class="link" data-type="link"></div>
              <!-- 设置别名 -->
              <div class="clone" data-type="clone"></div>
              <!-- <div class="resize" data-type="resize"></div> -->
            </div>
            <!-- 连线部分 -->
            <div v-else>
              <!-- 删除 -->
              <div class="remove linkRemove" data-type="linkRemove"></div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <create-table-modal ref="dialog"></create-table-modal>
    <steps class="steps" :step="2" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import factTable from '@/components/analysisComponent/modelCommon/factTable'
import steps from '@/components/analysisComponent/modelCommon/steps'
import createTableModal from '@/components/analysisComponent/dialog/createTableModal'
import { mapGetters } from 'vuex'

let joint = require('jointjs')
/**
 * routers 设置连线的type
 * 1、manhattan-直角
 * 2、metro-直角
 * 2、normal-直线
 *
*/
export default {
  components: {
    factTable, steps, createTableModal
  },
  data () {
    return {
      defaultId: '',
      arrId: [],
      defaultIdAsiad: '',
      url: require('../../../assets/img/logo.png'),
      arrowheadShape: 'M 10 0 L 0 5 L 10 10 z',
      isDragRect: false,
      filedPosition: {
        x: 0, y: 0
      },
      TableCountNum: 0, // 记录拖入画布的表数量
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
      jointResult: {
        name: '',
        description: '',
        fact_table: '',
        lookups: []
      },
      port: {
        // id: 'abc', // generated if `id` value is not present
        group: 'a',
        args: {}, // extra arguments for the port layout function, see `layout.Port` section
        label: {
          position: {
            name: 'right',
            args: { y: 6 } // extra arguments for the label layout function, see `layout.PortLabel` section
          },
          markup: '<text class="label-text" fill="blue"/>'
        }
      },
      linkModal: null,
      linkModalModel: null,
      linkModalFields: []
      // jointResult: { 'name': 'joint', 'description': '', 'fact_table': 'DEFAULT.KYLIN_CAL_DT', 'lookups': [{ 'joinTable': 'KYLIN_CAL_DT', 'alias': 'KYLIN_CATEGORY_GROUPINGS', 'id': '0ff420eb-79ad-40bd-bca9-12d8cd05c60a', 'table': 'DEFAULT.KYLIN_CATEGORY_GROUPINGS', 'joinAlias': 'KYLIN_CAL_DT', 'joinId': '952d11b5-69d9-45d1-92af-227489485e3f', 'kind': 'LOOKUP', 'join': { 'type': 'left', 'primary_key': ['KYLIN_CATEGORY_GROUPINGS.LEAF_CATEG_ID'], 'foreign_key': ['KYLIN_CAL_DT.CAL_DT'], 'isCompatible': [true], 'pk_type': ['bigint'], 'fk_type': ['date'] } }] }

    }
  },
  mounted () {
    this.init()
  },
  methods: {
    initJointResult (data) {
      if (!data) {
        return this.jointResult
      }
      let lookups = []
      let [database, factTable] = data.fact_table.split('.')
      let containers = this.$refs.containers.getBoundingClientRect()
      let arr = []
      data.lookups.forEach(item => {
        // if (item.id) {
        arr.push(item)
        // }
      })
      arr.forEach(t => {
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
        name: database,
        description: data.description,
        fact_table: factTable,
        lookups
      }
    },
    init () {
      this.TableCountNum = 0
      // 初始化选择的别名组合
      this.arrId = []
      // 获取已经设置的第二步数据
      this.jointResult = this.initJointResult(JSON.parse(JSON.stringify(this.jointResultData)))
      let list = this.jointResult.lookups || []
      // 新建图形
      this.graph = new joint.dia.Graph()
      // 实例化参数
      let paper = new joint.dia.Paper({
        el: document.querySelector('#myholder'),
        width: '100%',
        height: 700,
        backgroundColor: '#ffffff',
        model: this.graph,
        gridSize: 1
      })

      this.clearCells()

      list.forEach(t => {
        this.addLinkCell(t)
      })

      this.bindEvent(paper)
    },

    bindEvent (paper) {
      // 鼠标点击空白处
      paper.on('blank:pointerup', () => {
        this.hideCellLayer()
      })

      // 鼠标点击
      paper.on('cell:pointerclick', (e, d) => {
        if (e.model.attributes.attrs.text) {
          // console.log('点击', e.model.attributes.attrs.text.id, this.arrId)
          if (this.arrId.length > 0 && !this.arrId.includes(e.model.attributes.attrs.text.alias)) {
            this.hideCellLayer()
            return this.$message.warning('当前表不能为主表~')
          }
        }
        // d.stopPropagation()
      })

      // 鼠标拖拽
      paper.on('cell:pointerup', (e, d) => {
        this.linkModalModel = null

        if (this.isClick) {
          // 如果连线
          if (e.model.isLink()) {
            let factTable = this.jointResult.fact_table
            let data = e.model.get('attrs').data
            let linkElements = this.getLinkElements(e.model)
            let linkModal = null
            this.linkModalModel = null
            this.linkModalFields = []

            if (data) {
              let fields = this.getFields(data)

              this.linkModal = data
              this.linkModalModel = e.model
              this.linkModalFields = fields
            } else if (linkElements.source && linkElements.target) {
              let sourceAttrs
              let targetAttrs
              // 判断是否连接事实表， 不管先后顺序 事实表都得是主表
              if (linkElements.target.get('attrs').text.filed === 1) {
                targetAttrs = linkElements.source.get('attrs')
                sourceAttrs = linkElements.target.get('attrs')
              } else {
                sourceAttrs = linkElements.source.get('attrs')
                targetAttrs = linkElements.target.get('attrs')
              }
              // 连线的主表
              let source = {
                filed: sourceAttrs.text.label === factTable ? 1 : 0,
                label: sourceAttrs.text.label,
                alias: sourceAttrs.text.alias || sourceAttrs.text.label,
                id: sourceAttrs.text.id
              }
              // 连线的次表
              let target = {
                filed: sourceAttrs.text.label === factTable ? 1 : 0,
                label: `${targetAttrs.text.label}`,
                alias: targetAttrs.text.alias || targetAttrs.text.label,
                id: targetAttrs.text.id
              }
              // 定义需要传给后台的格式
              linkModal = {
                'joinTable': source.label || '', // 主表名
                'joinAlias': source.alias || '', // 主表别名
                'joinId': source.id || '', // 主表id
                'alias': target.alias || '', // 子表别名
                'id': target.id || '', // 子表id
                'table': target.label || '', // 子表名
                'kind': 'LOOKUP',
                'join': {
                  'type': '', // 连接方式（left||inner）
                  'primary_key': [], // 子表与选择的字段
                  'foreign_key': [], // 主表与选择的字段
                  'isCompatible': [true],
                  'pk_type': [], // 子表字段对应的类型
                  'fk_type': [] // 主表字段对应的类型
                }
              }

              this.addFields() // 调用添加关联字段

              this.linkModal = linkModal
              this.linkModalModel = e.model
            }
            this.showCellLayer(e)
          } else {
            this.showCellLayer(e)
          }
          this.isClick = false
        } else {
          let element = this.getDragElement(e.targetPoint)
          if (element) {
            this.arrId.push(element.attributes.attrs.text.alias)
            e.model.target(element)
            e.model.labels([{ position: 0.5,
              attrs: { '.marker-target': { fill: 'red', stroke: '#ffffff' },
                '.marker-source': {
                  fill: '#0486FE', // 箭头颜色
                  d: 'M 10 0 L 0 5 L 10 10 z'// 箭头样式
                },
                image: { 'xlink:href': this.url },
                text: { text: '未关联', 'color': '#59aff9', 'font-weight': 'bold', 'font-size': '12px', 'z-index': '-1' } } }])
          }
        }
      })

      paper.on('cell:pointerdown', (e, d) => {
        this.isClick = true
        this.filedPosition = e.model.get('position')
      })

      // 鼠标移到连线上
      paper.on('cell:mouseover', (e, d) => {
      })

      // 鼠标离开连线
      paper.on('cell:mouseout', (e, d) => {
      })

      // 鼠标点击连线
      paper.on('link:pointerdown', (e, d) => {
        d.stopPropagation()
      })

      paper.on('cell:pointermove', (e, d) => {
        let attrs = e.model.get('attrs')

        if (attrs.text && attrs.text.filed) {
          e.model.position(this.filedPosition.x, this.filedPosition.y)
        }

        this.isClick = false
        this.cellLayerData = null
        this.cellLayerStyle = ''
      })
    },
    clickTable (e) {
      if (e) {
        this.addRectCell(e)
      }
    },
    papersClick (e) {
      let element = this.cellLayerData || {}
      let model = element.model
      let position = model.get('position')
      switch (e.target.dataset.type) {
        case 'remove': // 删除
          if (model.attributes.attrs.text.label === this.jointResultData.fact_table.split('.')[1]) {
            this.$message.warning('事实表不能删除~')
          } else {
            this.clearElementLink(model)
          }
          break
        case 'linkRemove': // 删除连线
          this.clearElementLink(model)
          break
        case 'clone': // 设置别名
          let attrs = model.get('attrs')
          let label = attrs.text.label
          let defaultVal = label === attrs.text.alias ? '' : attrs.text.alias
          if (model.attributes.attrs.text.label === this.jointResultData.fact_table.split('.')[1]) return this.$message.warning('事实表暂不支持设置别名~')
          this.setAlias(label, defaultVal).then(res => {
            if (res && res.value) {
              attrs.text.alias = res.value
              attrs.text.text = `${label}(${res.value})`

              model.attr(attrs)
              model.resize(attrs.text.text.length * 9, 30)

              this.jointResult = this.updateModel(model.id, res.value)
              let result = this.formatJointList(this.jointResult)
              this.$store.commit('SaveJointResult', result)
              // this.init()
              this.linkModal = null
              this.linkModalModel = null
            }
          })
          break
        case 'link': // 连线
          let link = new joint.shapes.standard.Link({
            source: model,
            target: { x: position.x, y: position.y - 5 },
            attrs: {
              '.marker-target': {
                fill: '#D8D8D8', // 箭头颜色
                d: 'M 10 0 L 0 5 L 10 10 z'// 箭头样式
              },
              '.marker-source': {
                fill: '#D8D8D8', // 箭头颜色
                d: 'M 10 0 L 0 5 L 10 10 z'// 箭头样式
              },
              image: { 'xlink:href': this.url },
              line: {
                stroke: '#D8D8D8', // SVG attribute and value
                'stroke-width': 2// 连线粗细
              }
            },
            connector: { name: 'smooth' },
            router: { name: 'normal' }// 设置连线弯曲样式 normal直角
          })
          this.graph.addCell(link)
          break
        default:
          break
      }

      // 隐藏弹层
      this.hideCellLayer()
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
    // 设置别名
    setAlias (val, defaultVal) {
      return this.$prompt(`（${val}）设置别名：`, {
        inputValue: defaultVal
      }, {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /^.{0,20}$/,
        inputErrorMessage: '不能超过20个字符',
        type: 'warning'
      })
    },

    dragTable (e) {
      if (e && !e.filed) {
        this.isDragRect = true
        this.dragRectPosition.label = e.label
        this.dragRectPosition.id = e.id
        this.dragRectPosition.database = e.database
        this.dragRectPosition.filed = e.filed
        this.dragRectPosition.x = 0
        this.dragRectPosition.y = 0
      } else if (this.isDragRect && this.dragRectPosition) {
        let containers = this.$refs.containers.getBoundingClientRect()
        let holder = this.$refs.holder.getBoundingClientRect()
        let x = this.dragRectPosition.x - holder.left + containers.left
        let y = this.dragRectPosition.y - holder.top + containers.top

        // 如果不在拖动范围内，不做按钮添加
        if (x < 0 || y < 0) {
          this.clearDragRect()
          return false
        }

        let item = {
          filed: this.dragRectPosition.filed,
          id: this.dragRectPosition.id,
          label: this.dragRectPosition.label,
          database: this.dragRectPosition.database,
          alias: this.dragRectPosition.label,
          position: { x, y }
        }

        if (this.checkCellsExist(item)) {
          this.isDragRect = false
          this.setAlias(item.label).then(res => {
            if (res && res.value) {
              item.alias = res.value
              this.addRectCell(item)
            }
          })
        } else {
          this.addRectCell(item)
        }

        this.clearDragRect()
      }
    },

    clearDragRect () {
      this.isDragRect = false
      this.dragRectPosition = {
        label: 'test',
        id: '',
        x: 0,
        y: 0
      }
    },

    mousemove (e) {
      if (this.isDragRect && e) {
        let parentOffset = this.$refs.containers.getBoundingClientRect()
        this.dragRectPosition.x = e.x - parentOffset.left - 100 / 2
        this.dragRectPosition.y = e.y - parentOffset.top
      }
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
          if (attrs && attrs.text && attrs.text.label === item.label && item.alias === attrs.text.alias) {
            itemCell = t
          }
        })
      }

      return itemCell
    },
    // 移出图形
    clearCells () {
      this.linkModal = null
      this.linkModalModel = null
      this.graph.clear()
    },
    // 拖入到画布的表
    addRectCell (item) {
      // console.log(this.arrId, '来的', item)
      if (item.filed === 1) {
        this.defaultId = item.id
        this.defaultIdAsiad = item.id
        this.arrId.push(item.label)
      } else {
        this.defaultId = ''
      }
      this.TableCountNum += 1
      // 判断是否存在此表
      if (!this.graph) this.graph = new joint.dia.Graph()

      let isAdd = true
      let newRect = null

      let cell = this.checkCellsExist(item)
      if (cell) return cell

      if (isAdd) {
        let fillColor = item.filed ? '#0486FE' : '#67C23A'

        if (item.database) {
          this.jointResult.name = item.database
        }

        // 如果是主表， 就清空所有文件
        if (item.filed) {
          this.clearCells()
        }
        // 设置主表
        if (item.filed === 1 && !this.jointResult.fact_table) {
          this.jointResult.fact_table = `${item.label}`
        }

        let randomPosition = this.getCellRamdonPosition(item)
        let text = (!item.alias || item.label === item.alias) ? item.label : `${item.label}(${item.alias})`

        newRect = new joint.shapes.basic.Rect({
          position: {
            x: (item.position && item.position.x) || randomPosition.x,
            y: (item.position && item.position.y) || randomPosition.y
          },
          // "连接点"（port）的风格
          portMarkup: '<rect class="joint-port-body" width="10" height="3" style="fill:black" />',
          // "连接点"（port）标签文字的显示风格
          portLabelMarkup: '<text class="port-label joint-port-label" font-size="10" y="0" fill="#000" /> ',
          ports: { // 定义连接点
          },
          size: { width: text.length * 9, height: randomPosition.height },
          attrs: { image: { 'xlink:href': this.url, opacity: 0.7 }, rect: { fill: fillColor, stroke: '#ffffff' }, text: { text: text, label: item.label, alias: item.alias || item.label, filed: item.filed, id: item.id, database: item.database, fill: 'white', 'font-size': 12 } }
        })
        // newRect.addPort(this.port)
        this.graph.addCell(newRect)
        // this.member()
      }

      return newRect
    },
    // 设置icon
    member () {
      var cell = new joint.shapes.org.Member({
        attrs: {
          image: {
            'xlink:href': this.url,
            opacity: 0.7
          }
        }
      })
      this.graph.addCell(cell)
      return cell
    },
    // test
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
        connector: { name: 'smooth' },
        router: { name: 'normal' }, // 设置连线弯曲样式 normal直角
        labels: [{ position: 0.5, attrs: { text: { text: '已关联', 'font-weight': 'bold', 'font-size': '12px', 'fill': '#0486FE' } } }],
        attrs: {
          'data': item,
          '.marker-target': {
            fill: '#0486FE', // 箭头颜色
            d: 'M 10 0 L 0 5 L 10 10 z'// 箭头样式
          },
          '.marker-source': {
            fill: '#0486FE', // 箭头颜色
            d: 'M 10 0 L 0 5 L 10 10 z'// 箭头样式
          },
          image: { 'xlink:href': 'images/' + this.url },
          line: {
            stroke: '#0486FE', // SVG attribute and value
            'stroke-width': 2// 连线粗细
          }
        }
      })
      this.graph.addCell(newLink)

      return newLink
    },
    // 获取当前线对应的字段关系
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
    // 添加关联关系
    addFields (field) {
      if (!field || {}.toString.call(field) !== '[object Array]') {
        field = [{
          primary_key: '',
          foreign_key: '',
          pk_type: '',
          fk_type: ''
        }]
      }

      this.linkModalFields = [...this.linkModalFields, ...field]
    },

    removeField (index) {
      if (this.linkModalFields.length > 1) {
        this.linkModalFields.splice(index, 1)
        this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
      }
    },

    getModalRelationSelected (e) {

    },

    // 选择子表对应的字段
    getModalPrimarySelected (e) {
      console.log(e)

      let index = e.index
      let primary_key = e.name
      let pk_type = e.dataType
      let foreign_key = this.linkModalFields[index].foreign_key
      let fk_type = this.linkModalFields[index].fk_type

      if (index >= 0 && primary_key && pk_type) {
        if (fk_type && pk_type && pk_type !== fk_type) {
          this.$message.warning('请选择类型一致的字段')
          primary_key = ''
          pk_type = ''
        }

        this.linkModalFields[index].primary_key = primary_key
        this.linkModalFields[index].pk_type = pk_type

        if (foreign_key && fk_type) {
          this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
        }
      }
    },
    // 选择主表对应的字段
    getModalForeignSelected (e) {
      let index = e.index
      let primary_key = this.linkModalFields[index].primary_key
      let pk_type = this.linkModalFields[index].pk_type
      let foreign_key = e.name
      let fk_type = e.dataType

      if (index >= 0 && foreign_key && fk_type) {
        if (fk_type && pk_type && pk_type !== fk_type) {
          this.$message.warning('请选择类型一致的字段')
          foreign_key = ''
          fk_type = ''
        }

        this.linkModalFields[index].foreign_key = foreign_key
        this.linkModalFields[index].fk_type = fk_type

        if (primary_key && pk_type) {
          this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
        }
      }
    },
    // 存储已选择表对应的字段
    updateFields (alias, joinAlias, fields) {
      let primary_key = []; let foreign_key = []; let pk_type = []; let fk_type = []

      fields.forEach((t, i) => {
        if (t.primary_key && t.foreign_key && t.pk_type && t.fk_type) {
          primary_key.push(`${t.primary_key}`)
          foreign_key.push(`${t.foreign_key}`)
          pk_type.push(t.pk_type)
          fk_type.push(t.fk_type)
        }
      })
      this.linkModal.join.primary_key = primary_key
      this.linkModal.join.foreign_key = foreign_key
      this.linkModal.join.pk_type = pk_type
      this.linkModal.join.fk_type = fk_type
      // 判断是否选中了关联对应的字段 (改变对应的文字以及样式)
      if (primary_key.length > 0 && this.linkModalModel.labels) {
        this.linkModalModel.labels([{ position: 0.5, attrs: { image: { 'xlink:href': this.url }, text: { text: '已关联', 'fill': '#0486FE', 'font-weight': 'bold', 'z-index': '-1', 'font-size': '12px' } } }])
        this.linkModalModel.attributes.attrs.line.stroke = '#0486FE'
      }
      this.linkModalModel.attr('data', this.linkModal)
      let result = this.addJointList(this.linkModal)
      // console.log(JSON.stringify(result))
      // this.getIdToList()
      this.$store.commit('SaveJointResult', result)
    },

    getElementPosition () {
      let eles = this.graph.getElements() || []
      let parentOffset = this.$refs.containers.getBoundingClientRect()
      let result = {}

      for (let i = 0; i < eles.length; i++) {
        let ele = eles[i]
        let attrs = ele.get('attrs')
        let pos = ele.get('position')
        let text = attrs.text.label + attrs.text.alias
        if (ele.attributes.type !== 'standard.Link' && !result[text]) {
          result[text] = {
            x: pos.x,
            y: pos.y
          }
        }
      }

      return result
    },
    // 编辑对应的数据
    formatJointList: function (data) {
      // 获取对应的坐标
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
          primary_key_result.push(`${t.alias}.${primary_key[i]}`)
          foreign_key_result.push(`${t.joinAlias}.${foreign_key[i]}`)
        })

        result.lookups.push({
          alias: t.alias,
          id: t.id,
          joinAlias: t.joinAlias,
          joinId: t.joinId,
          joinTable: t.joinTable,
          kind: t.kind,
          table: `${data.name}.${t.table}`,
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

        if (replaceIdx === -1) {
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

    clearElementLink: function (target) {
      let eles = target.collection.models || []
      let elements = []

      this.linkModal = null
      this.linkModalModel = null

      for (let i = 0; i < eles.length; i++) {
        let ele = eles[i]
        // 判断删除的是表还是线
        if (ele.attributes.type === 'standard.Link') {
          if (ele.get('source').id === target.id || ele.get('target').id === target.id || ele.id === target.id) {
            ele.remove()
            // 删除对应存储的数据
            this.jointResultData.lookups = this.jointResultData.lookups.filter((item, index) => {
              return item.id !== ele.attributes.attrs.data.id && item.alias !== ele.attributes.attrs.data.alias
            })
          }
        } else {
          if (ele.id === target.id) {
            ele.remove()
            // console.log(ele.attributes.attrs.text.id, '第三步存储的', this.jointResultData.lookups)
            // 删除对应存储的数据
            this.jointResultData.lookups = this.jointResultData.lookups.filter((item, index) => {
              return item.id !== ele.attributes.attrs.text.id
            })
            // 删除对应选择的维度
            this.saveSelectFiled.map((res, index) => {
              if (res.resid === ele.attributes.attrs.text.id) {
                this.saveSelectFiled.splice(index, 1)
              }
            })
            this.$store.dispatch('SaveNewSortList', this.saveSelectFiled)
            this.TableCountNum -= 1
          }
        }
      }
    },
    // 获取当前线对应的两个表的数据
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
    // 操作按钮的弹框
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
    },

    nextModel (val) {
      // if (this.jointResultData.lookups.length < 1) return this.$message.warning('请建立表关系~')
      if (Object.keys(this.ModelAllList).length === 0) {
        if (!this.isTableAssociate()) return this.$message.warning('请完善表关系~')
      }
      this.$router.push('/analysisModel/createolap/setFiled')
      this.$parent.getStepCountAdd(val)
      this.getIdToList()
    },
    // 判断拖入画布的表是否都关联上
    isTableAssociate () {
      return this.TableCountNum - this.jointResultData.lookups.length === 1
    },
    // 根据当前的id 去获取所有对应的字段
    getIdToList () {
      let arrId = []
      // 存储当前连线的id
      let ids = this.defaultId ? this.defaultId : this.defaultIdAsiad
      if (this.jointResult.lookups.length > 0) {
        this.jointResult.lookups.forEach((item, index) => {
          arrId.push(item.id, item.joinId)
          this.arrId.push(item.id, item.joinId)
        })
      } else {
        arrId.push(ids)
        this.arrId.push(ids)
      }
      this.$store.commit('SaveSelectAllListtwo', [...new Set(arrId)])
    },
    prevModel (val) {
      this.$router.push('/analysisModel/createolap/selectStep')
      this.$parent.getStepCountReduce(val)
    },
    lookDetailData (id) {
      this.$refs.dialog.dialog(id)
    },
    getModalDataList (id) {
      //   this.$store.dispatch('GetColumnList', { dsDataSourceId: 2, tableName: id }).then(res => {
      //     // this.couponList = res.data
      //     this.couponList = [{ 'comment': '所属老板', 'isSupport': 'true', 'columnName': 'SUO_SHU_LAO_BAN', 'dataType': 'string' }, { 'comment': '老板电话', 'isSupport': 'true', 'columnName': 'LAO_BAN_DIAN_HUA', 'dataType': 'string' }, { 'comment': '餐馆名称', 'isSupport': 'true', 'columnName': 'CAN_GUAN_MING_CHENG', 'dataType': 'string' }, { 'comment': '餐馆地址', 'isSupport': 'true', 'columnName': 'CAN_GUAN_DI_ZHI', 'dataType': 'string' }, { 'comment': null, 'isSupport': 'true', 'columnName': 'DS_U_X5OSRKK1C_ID', 'dataType': 'number' }]
      //   })
      // this.$store.dispatch('GetResourceInfo', { resourceId: id }).then(res => {
      //   this.couponList = res.data.columns
      // })
      // 模拟数据
      // this.couponList = [{ 'comment': '所属老板', 'isSupport': 'true', 'name': 'SUO_SHU_LAO_BAN', 'dataType': 'string' }, { 'comment': '老板电话', 'isSupport': 'true', 'name': 'LAO_BAN_DIAN_HUA', 'dataType': 'string' }, { 'comment': '餐馆名称', 'isSupport': 'true', 'name': 'CAN_GUAN_MING_CHENG', 'dataType': 'string' }, { 'comment': '餐馆地址', 'isSupport': 'true', 'name': 'CAN_GUAN_DI_ZHI', 'dataType': 'string' }, { 'comment': null, 'isSupport': 'true', 'name': 'DS_U_X5OSRKK1C_ID', 'dataType': 'number' }]
      // debugger
      // 根据name去获取本地对应的数据
      (this.saveSelectAllList || []).forEach((item, index) => {
        let items = JSON.parse(item)
        if (items.resourceId === id) {
          this.couponList = items.data.columns || []
        }
      })
    }
  },
  computed: {
    ...mapGetters({
      selectTableTotal: 'selectTableTotal',
      saveSelectAllList: 'saveSelectAllList',
      saveSelectFiled: 'saveSelectFiled',
      ModelAllList: 'ModelAllList',
      selectStepList: 'selectStepList',
      jointResultData: 'jointResultData'
    })
  }
}
</script>

<style lang="stylus" scoped>
.hide{
  display none
}

.tableRelation{
  height calc(100vh)
  position relative
  .containers{
    height 90%
    padding 20px 5px
  }
}

.dragRect{
  position absolute
  width 100px
  height 30px
  background #67C23A
  z-index 10000
  text-align center
  line-height 30px
  color #fff
  font-size 14px
}

.holder{
  position relative
  overflow hidden
  height 100%
  #myholder{
    background #ffffff!important
    margin-left 15px
  }
}
>>>.joint-cell{
  cursor pointer!important
}
.halo-cell-layer{
  display none
  position absolute
  z-index 100
}

.papers .remove {
  position absolute
  left -20px
  top -20px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cg%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M15.386%2C3.365c-3.315-3.314-8.707-3.313-12.021%2C0c-3.314%2C3.315-3.314%2C8.706%2C0%2C12.02%20c3.314%2C3.314%2C8.707%2C3.314%2C12.021%2C0S18.699%2C6.68%2C15.386%2C3.365L15.386%2C3.365z%20M4.152%2C14.598C1.273%2C11.719%2C1.273%2C7.035%2C4.153%2C4.154%20c2.88-2.88%2C7.563-2.88%2C10.443%2C0c2.881%2C2.88%2C2.881%2C7.562%2C0%2C10.443C11.716%2C17.477%2C7.032%2C17.477%2C4.152%2C14.598L4.152%2C14.598z%22%2F%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M12.157%2C11.371L7.38%2C6.593C7.162%2C6.375%2C6.809%2C6.375%2C6.592%2C6.592c-0.218%2C0.219-0.218%2C0.572%2C0%2C0.79%20l4.776%2C4.776c0.218%2C0.219%2C0.571%2C0.219%2C0.79%2C0C12.375%2C11.941%2C12.375%2C11.588%2C12.157%2C11.371L12.157%2C11.371z%22%2F%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M11.369%2C6.593l-4.777%2C4.778c-0.217%2C0.217-0.217%2C0.568%2C0%2C0.787c0.219%2C0.219%2C0.571%2C0.217%2C0.788%2C0l4.777-4.777%20c0.218-0.218%2C0.218-0.571%2C0.001-0.789C11.939%2C6.375%2C11.587%2C6.375%2C11.369%2C6.593L11.369%2C6.593z%22%2F%3E%3C%2Fg%3E%3C%2Fsvg%3E%20')
}

.papers .linkRemove{
  left 50%
  top 54%
  height 18px
  width 18px
  background-size 100% 100%
  tranform translate(-50%, -50%)
}

.papers .rotate {
  position absolute
  left -20px
  top -20px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M9.374%2C17.592c-4.176%2C0-7.57-3.401-7.57-7.575c0-4.175%2C3.395-7.574%2C7.57-7.574c0.28%2C0%2C0.56%2C0.018%2C0.837%2C0.05%20V1.268c0-0.158%2C0.099-0.3%2C0.239-0.36c0.151-0.058%2C0.315-0.026%2C0.428%2C0.086l2.683%2C2.688c0.152%2C0.154%2C0.152%2C0.399%2C0%2C0.553l-2.68%2C2.693%20c-0.115%2C0.112-0.279%2C0.147-0.431%2C0.087c-0.141-0.063-0.239-0.205-0.239-0.361V5.296C9.934%2C5.243%2C9.654%2C5.22%2C9.374%2C5.22%20c-2.646%2C0-4.796%2C2.152-4.796%2C4.797s2.154%2C4.798%2C4.796%2C4.798c2.645%2C0%2C4.798-2.153%2C4.798-4.798c0-0.214%2C0.174-0.391%2C0.391-0.391h1.991%20c0.217%2C0%2C0.394%2C0.177%2C0.394%2C0.391C16.947%2C14.19%2C13.549%2C17.592%2C9.374%2C17.592L9.374%2C17.592z%20M9.374%2C17.592%22%2F%3E%3C%2Fsvg%3E%20')
}

.papers .link {
  position absolute
  right -20px
  top -20px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M9.884%2C9.838c0.54-0.551%2C1.005-0.955%2C1.384-1.201c0.463-0.308%2C0.749-0.352%2C0.887-0.352h1.34v1.367%20c0%2C0.104%2C0.061%2C0.2%2C0.154%2C0.242s0.204%2C0.027%2C0.284-0.038l3.168-2.669c0.06-0.051%2C0.096-0.125%2C0.096-0.203S17.16%2C6.83%2C17.101%2C6.781%20l-3.168-2.677c-0.08-0.067-0.19-0.081-0.284-0.038c-0.094%2C0.045-0.154%2C0.139-0.154%2C0.242v1.414h-1.343%20c-1.24%2C0.014-2.215%2C0.67-2.927%2C1.242c-0.797%2C0.65-1.533%2C1.447-2.245%2C2.217c-0.361%2C0.391-0.7%2C0.759-1.044%2C1.1%20c-0.541%2C0.549-1.011%2C0.951-1.395%2C1.199c-0.354%2C0.231-0.678%2C0.357-0.921%2C0.357h-1.8c-0.146%2C0-0.266%2C0.12-0.266%2C0.265v2.029%20c0%2C0.148%2C0.12%2C0.268%2C0.266%2C0.268h1.8l0%2C0c1.255-0.014%2C2.239-0.667%2C2.958-1.24c0.82-0.661%2C1.572-1.475%2C2.297-2.256%20C9.225%2C10.524%2C9.555%2C10.169%2C9.884%2C9.838z%22%2F%3E%3C%2Fsvg%3E%20')
}

.papers .clone {
  position absolute
  left -20px
  bottom -20px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cg%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M12.852%2C0.875h-9.27c-0.853%2C0-1.547%2C0.694-1.547%2C1.547v10.816h1.547V2.422h9.27V0.875z%20M15.172%2C3.965h-8.5%20c-0.849%2C0-1.547%2C0.698-1.547%2C1.547v10.816c0%2C0.849%2C0.698%2C1.547%2C1.547%2C1.547h8.5c0.85%2C0%2C1.543-0.698%2C1.543-1.547V5.512%20C16.715%2C4.663%2C16.021%2C3.965%2C15.172%2C3.965L15.172%2C3.965z%20M15.172%2C16.328h-8.5V5.512h8.5V16.328z%22%2F%3E%3C%2Fg%3E%3C%2Fsvg%3E%20')
}

.papers .resize {
  position absolute
  right -20px
  bottom -20px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20%3F%3E%3Csvg%20height%3D%2224px%22%20version%3D%221.1%22%20viewBox%3D%220%200%2024%2024%22%20width%3D%2224px%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Asketch%3D%22http%3A%2F%2Fwww.bohemiancoding.com%2Fsketch%2Fns%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%3E%3Ctitle%2F%3E%3Cdesc%2F%3E%3Cdefs%2F%3E%3Cg%20fill%3D%22none%22%20fill-rule%3D%22evenodd%22%20id%3D%22miu%22%20stroke%3D%22none%22%20stroke-width%3D%221%22%3E%3Cg%20id%3D%22Artboard-1%22%20transform%3D%22translate(-251.000000%2C%20-443.000000')
}

.linkSetting{
  float right
  background #ffffff
  width 200px
  height 100%
  overflow auto
  text-align left
  padding 0 20px
  border-left 1px solid #cccccc
  .title{
    font-size: 14px;
    color: #303133;
    height 35px
    line-height 35px
    border-bottom 1px solid #E4E7ED
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
    margin 5px 0
    display flex
    span:nth-child(1){
      font-size: 12px;
      color: #5A5A5A;
      text-align: left;
      line-height: 22px;
      text-overflow: ellipsis;
      overflow: hidden;
      width:80%;
    }
    span:nth-child(2){
      font-size: 14px;
      color: #0486FE;
      cursor pointer
      float right
    }
  }
  .itemAdd{
    background: #FFFFFF;
    border: 1px solid #0486FE;
    width:100%;
    text-align:center;
    margin-top:15px;
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
      font-size 10px
    }
    .el-icon-arrow-up{
      margin-top 5px
    }
    .is-reverse{
      margin-top -5px
    }
  }
  >>>.el-input__inner{
    height 30px
  }
}
</style>
