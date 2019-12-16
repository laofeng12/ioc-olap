<template>
  <div class="tableRelation">
    <div class='editor-box'>
      <div id='editorContainer' ref="containers"></div>
      <div class='option' v-if="linkModal">
        <base-info-panel class='base-info' :linkModal='linkModal' :relationData="relationData"
                         :linkModalFields="linkModalFields" :couponList="couponList" @lookDetailData="lookDetailData"
                         @getModalDataList="getModalDataList" @getModalPrimarySelected="getModalPrimarySelected"
                         @addFields="addFields" @getModalForeignSelected="getModalForeignSelected" @removeField="removeField"/>
      </div>
    </div>
    <create-table-modal ref="dialog"></create-table-modal>
    <steps class="steps" :step="2" @nextModel="nextModel" @prevModel="prevModel"></steps>
    <setfact-table ref="setfact"></setfact-table>
  </div>
</template>

<script>
import IOCEditor from 'flow-edit'
import factTable from '@/components/analysisComponent/modelCommon/factTable'
import setfactTable from '@/components/analysisComponent/dialog/setfactTable'
import steps from '@/components/analysisComponent/modelCommon/steps'
import BaseInfoPanel from '@/components/analysisComponent/modelCommon/BaseInfoPanel'
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
    factTable, steps, createTableModal, setfactTable, BaseInfoPanel
  },
  data () {
    return {
      defaultId: '',
      arrId: [],
      isShowLink: true,
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
      linkModalFields: [],
      factTable: [
        {
          key: 'data',
          label: '数据目录',
          list: []
        }
      ],
      edgeList: [],
      nodeList: []
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
  },
  watch: {
    selectTableTotal: {
      handler: function (val) {
        const factTable = val.map((v, i) => {
          const obj = Object.assign({}, v, {
            databaseType: `${i}`,
            title: v.label,
            icon: `${process.env.BASE_URL}dataBase.svg`,
            isLeaf: true
          })
          return obj
        })
        this.factTable[0].list = factTable
        this.editor.updatePannelList(this.factTable)
      },
      deep: true
    }
  },
  mounted () {
    const factTable = this.selectTableTotal.map((v, i) => {
      const obj = Object.assign({}, v, {
        databaseType: `${i}`,
        title: v.label,
        icon: `${process.env.BASE_URL}dataBase.svg`,
        isLeaf: true
      })
      return obj
    })
    this.factTable[0].list = factTable
    this.init()
    this.initEditor()
  },
  methods: {

    init () {
      this.TableCountNum = 0
      // 初始化选择的别名组合
      this.arrId = []
      this.isEditLooks()
      // 获取已经设置的第二步数据
      this.jointResult = this.initJointResult(JSON.parse(JSON.stringify(this.jointResultData)))
      if (this.jointResult.lookups.length > 0) {
        this.linkModal = this.jointResult.lookups[0]
        this.linkModalFields = this.getFields(this.linkModal)
      }
      // 新建图形
      this.graph = new joint.dia.Graph()
    },
    initEditor () {
      // 编辑的时候 ModelAllList是一个object,新增的时候是一个array
       let graphData = ''
       if (!Array.isArray(this.ModelAllList)) {
         graphData = Object.keys(JSON.parse(this.ModelAllList.graphData)).length ? JSON.parse(this.ModelAllList.graphData) : {}
       } else {
         graphData = this.ModelAllList.length > 0 ? JSON.parse(this.ModelAllList.graphData) : {}
       }
      this.editor = new IOCEditor({
        el: 'editorContainer', // 容器id
        baseInfo: '', // 基础信息，标题，描述，状态等
        pannelList: this.factTable, // 左边数据源和功能组件
        graphData, // 初始化数据
        toolbarList: ['zoomIn', 'zoomOut', 'autoZoom', 'resetZoom']
      })
      const listeners = [
        { key: 'addNode', fn: this.addNode },
        // { key: 'nodeClick', fn: this.onNodeClick },
        { key: 'addEdge', fn: this.addEdge },
        { key: 'edgeClick', fn: this.edgeClick },
        // { key: 'flowClick', fn: this.flowClick },
        // { key: 'contextMenuClick', fn: this.contextMenuClick },
        { key: 'change', fn: this.flowChange }
      ]
      listeners.forEach(({ key, fn }) => this.editor.addListener(key, fn))
    },
    flowChange (obj) {
      const graph = this.editor.getGraph()
      if (obj.name === 'delete') {
        const nodeIndex = this.nodeList.findIndex(v => v.id === obj.itemIds[0])
        if (nodeIndex >= 0) {
          this.nodeList.splice(nodeIndex, 1)
          this.linkModalFields = []
          this.linkModal = null
          this.linkModalModel = null
          this.removeNode(obj.itemIds[0])
          const jointResultData = this.initJointResult(JSON.parse(JSON.stringify(this.jointResultData)))
          const { source, target } = obj.snapShot.edges.filter(v => v.id === obj.itemIds[0])[0]
          const sourceAttrs = graph.find(source).model.item
          const targetAttrs = graph.find(target).model.item
          const index = jointResultData.lookups.findIndex(v => v.id === targetAttrs.id && v.joinId === sourceAttrs.id)
          jointResultData.lookups.splice(index, 1)
          this.$store.commit('SaveJointResult', jointResultData)
        }
        const edgeIndex = this.edgeList.findIndex(v => v.id === obj.itemIds[0])
        if (edgeIndex >= 0) {
          this.edgeList.splice(edgeIndex, 1)
          this.linkModalFields = []
          this.linkModal = null
          this.linkModalModel = null
          this.removeEdge(obj.itemIds[0])
          const jointResultData = this.initJointResult(JSON.parse(JSON.stringify(this.jointResultData)))
          const { source, target } = obj.snapShot.edges.filter(v => v.id === obj.itemIds[0])[0]
          const sourceAttrs = graph.find(source).model.item
          const targetAttrs = graph.find(target).model.item
          const index = jointResultData.lookups.findIndex(v => v.id === targetAttrs.id && v.joinId === sourceAttrs.id)
          jointResultData.lookups.splice(index, 1)
          this.$store.commit('SaveJointResult', jointResultData)
        }
      }
    },

    addNode (node) {
      const { graphData } = this.editor.getResult()
      if (graphData.nodes.length === 1) {
        this.$refs.setfact.dialog(graphData.nodes[0])
      }
      if (graphData.nodes.length > 1) {
        if (graphData.nodes[0].label === node.addModel.label) {
          this.removeNode()
          this.$message.warning('事实表是唯一存在的，请选择其他表')
        } else {
          graphData.nodes.forEach(async (v, i) => {
            if (v.label === node.addModel.label && (i + 1) < graphData.nodes.length) {
              const res = await this.setAlias(node.addModel.label)
              node.addModel.label = `${node.addModel.label}(${res.value})`
              node.addModel.item.alias = `${res.value}`
              this.editor.updateNode(node.addModel.id, node.addModel)
            }
          })
        }
      }
      this.nodeList = graphData.nodes
    },
    removeNode (id) {
      const { graphData } = this.editor.getResult()
      const graph = this.editor.getGraph()
      if (id) {
        graph.remove(id)
      } else {
        graph.remove(graphData.nodes[graphData.nodes.length-1].id)
      }
      this.nodeList = graphData.nodes
    },
    removeEdge (id) {
      const { graphData } = this.editor.getResult()
      const graph = this.editor.getGraph()
      graph.remove(id)
      this.edgeList = graphData.edges || []
    },
    async addEdge (a, id, obj) {
      const graph = this.editor.getGraph()
      const { graphData } = this.editor.getResult()
      const being = graphData.edges.filter(v => obj.source === v.source && obj.target === v.target) || []
      if (being.length > 1) {
        this.removeEdge(id)
        return this.$message.warning('请勿重复连线')
      }
      const factTable = this.jointResult.fact_table
      this.linkModalFields = []
      const sourceAttrs = graph.find(obj.source).model.item
      const targetAttrs = graph.find(obj.target).model.item
      if (sourceAttrs.label === factTable || sourceAttrs.filed === 1) {
        const updateModelItem = Object.assign({}, targetAttrs, { filed: 1 })
        const updateModel = Object.assign({}, graph.find(obj.target).model, { item: updateModelItem })
        await this.editor.updateNode(obj.target, updateModel)
        this.edgeList.push(obj)
        this.initEdge(id, obj.source, obj.target)
      } else {
        this.removeEdge(id)
        this.$message.warning('只能事实表为源头')
      }
    },
    edgeClick (model) {
      const graph = this.editor.getGraph()
      const sourceAttrs = graph.find(model.item.model.source).model.item
      const targetAttrs = graph.find(model.item.model.target).model.item
      const data = this.jointResultData.lookups.filter(v => v.id === targetAttrs.id && v.joinId === sourceAttrs.id)
      if (data && data.length > 0) {
        this.linkModal = data[0]
        this.linkModalModel = graph.find(model.item.model.source).model
        this.linkModalFields = this.getFields(data[0])
      } else {
        this.initEdge(model.shape.id, model.item.model.source, model.item.model.target)
      }
    },
    initEdge (id, sourceData, targetData) {
      const graph = this.editor.getGraph()
      const sourceAttrs = graph.find(sourceData).model.item
      const targetAttrs = graph.find(targetData).model.item
      const source = {
        filed: 1,
        label: sourceAttrs.label,
        alias: sourceAttrs.alias || sourceAttrs.label,
        id: sourceAttrs.id
      }
      const target = {
        filed: 1,
        label: `${targetAttrs.label}`,
        alias: targetAttrs.alias || targetAttrs.label,
        id: targetAttrs.id
      }
      const linkModal = {
        'joinTable': source.label || '', // 主表名
        'joinAlias': source.alias || '', // 主表别名
        'joinId': source.id || '', // 主表id
        'alias': target.alias || '', // 子表别名
        'id': target.id || '', // 子表id
        'edgeId': id || '', // 线id
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
      this.linkModalFields = []
      this.addFields()
      this.linkModal = linkModal
      this.linkModalModel = graph.find(sourceData).model
    },
    initJointResult (data) {
      if (!data) {
        return this.jointResult
      }
      let lookups = []
      let [database, factTable] = data.fact_table.split('.')
      // let containers = this.$refs.containers.getBoundingClientRect()
      let arr = []
      data.lookups.forEach(item => {
        arr.push(item)
      })
      arr.forEach(t => {
        let { primary_key, foreign_key, pk_type, fk_type, isCompatible, type } = t.join
        let primary_key_result = []
        let foreign_key_result = []
        let table = t.table.split('.')[1];

        (primary_key || []).forEach((m, i) => {
          primary_key_result.push(primary_key[i].split('.')[1])
          foreign_key_result.push(foreign_key[i].split('.')[1])
        })

        lookups.push({
          // alias: t.alias.toUpperCase(),
          alias: t.alias,
          id: t.id,
          SAxis: t.SAxis,
          YAxis: t.YAxis,
          // joinAlias: t.joinAlias.toUpperCase(),
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
    // 判断是否是编辑进来的，需要将lookups里的表名筛选出来
    isEditLooks () {
      this.jointResult.lookups.forEach((item, index) => {
        this.arrId.push(item.alias, item.joinAlias)
      })
    },
    clickTable (e) {
      if (e) {
        this.addRectCell(e)
      }
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
            // item.alias = value.toUpperCase()
            item.alias = value
            t.attr('data', item)
          }
          if (t.get('source').id === id) {
            updateList.push({
              idx: linkIndex,
              field: 'joinAlias'
            })
            // item.joinAlias = value.toUpperCase()
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
    getFields (data = {}) {
      let join = data.join
      let list = []
      if (!join) {
        list.push({
          primary_key: '',
          foreign_key: '',
          pk_type: '',
          fk_type: '',
          type: ''
        })
        this.$message.error('请设置字段关系')
      } else {
        let primary_key = join.primary_key || []
        let foreign_key = join.foreign_key || []
        let pk_type = join.pk_type || []
        let fk_type = join.fk_type || []
        let type = join.type
        primary_key.forEach((t, i) => {
          list.push({
            primary_key: `${primary_key[i].includes('.') ? primary_key[i].split('.')[1] : primary_key[i]}`,
            foreign_key: `${foreign_key[i].includes('.') ? foreign_key[i].split('.')[1] : foreign_key[i]}`,
            pk_type: pk_type[i],
            fk_type: fk_type[i],
            type
          })
        })
      }
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

    // 选择子表对应的字段
    getModalPrimarySelected (e, index) {
      let primary_key = e.definition
      let pk_type = e.type
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
        // this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
      }
    },
    // 选择主表对应的字段
    getModalForeignSelected (e, index) {
      let primary_key = this.linkModalFields[index].primary_key
      let pk_type = this.linkModalFields[index].pk_type
      // let foreign_key = e.name
      let foreign_key = e.definition
      let fk_type = e.type

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
      let primary_key = []
      let foreign_key = []
      let pk_type = []
      let fk_type = []

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
      Object.assign(this.linkModalModel, { data: this.linkModal })
      // this.linkModalModel.attr('data', this.linkModal)
      let result = this.addJointList(this.linkModal)
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
        // let text = attrs.text.label + attrs.text.alias.toUpperCase()
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
      const graph = this.editor.getGraph()
      let result = {
        name: data.name || '',
        description: data.description || '',
        SAxis: this.nodeList.length ? this.nodeList[0].x : 0,
        YAxis: this.nodeList.length ? this.nodeList[0].y : 0,
        fact_table: `${data.name}.${data.fact_table}`,
        lookups: []
      };
      (data.lookups || []).forEach(t => {
        let { primary_key, foreign_key, pk_type, fk_type, isCompatible, type } = t.join
        let primary_key_result = []; let foreign_key_result = []
        let pos = posList[t.table + t.alias] || {}
        let joinPos = posList[t.joinTable + t.joinAlias] || {};
        const item = this.edgeList.find(v => {
          return (v.id === t.edgeId)
        })

        const source = graph.find(item.source);
        const target = graph.find(item.target);

        (primary_key || []).forEach((m, i) => {
          primary_key_result.push(`${t.alias}.${primary_key[i]}`)
          foreign_key_result.push(`${t.joinAlias}.${foreign_key[i]}`)
        })

        result.lookups.push({
          // alias: t.alias.toUpperCase(),
          alias: t.alias,
          id: t.id,
          // joinAlias: t.joinAlias.toUpperCase(),
          joinAlias: t.joinAlias,
          joinId: t.joinId,
          joinTable: t.joinTable,
          kind: t.kind,
          table: `${data.name}.${t.table}`,
          SAxis: target.model.x || 0,
          YAxis: target.model.y || 0,
          joinSAxis: source.model.x || 0,
          joinYAxis: source.model.y || 0,
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
    // 下一步
    async nextModel (val) {
      if (Object.keys(this.ModelAllList).length === 0) {
        if (!this.isTableAssociate()) return this.$message.warning('请完善表关系~')
      }
      if (!this.linkModal || !this.linkModal.join.type) {
          this.$message.warning('请选择表的关联关系~')
          return
      }
      // this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
      const { graphData } = this.editor.getResult()
      // 关联
      this.$store.commit('SET_TABLE_JOINTYPE',this.linkModal.join.type)
      await this.$store.dispatch('getGraphData', JSON.stringify(graphData))
      // 下一步
      this.$parent.getStepCountAdd(val)
      // 获取表的列
      this.getIdToList()
      // 跳转到第三步
      this.$router.push('/analysisModel/createolap/setFiled')
      // this.$parent.getStepCountAdd(val)
      // this.getIdToList()
      // this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
      // this.$router.push('/analysisModel/createolap/setFiled')

      // this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
      // this.$router.push('/analysisModel/createolap/setFiled')
    },
    // 判断拖入画布的表是否都关联上
    isTableAssociate () {
      return (this.jointResultData.lookups.length > 0) &&
        (this.nodeList.length - this.jointResultData.lookups.length <= 1)
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
      // 存储已经建表对应的所有字段
      this.$store.commit('SaveSelectAllListtwo', [...new Set(arrId)])
    },
    prevModel (val) {
      this.$router.push('/analysisModel/createolap/selectStep')
      this.$parent.getStepCountReduce(val)
    },
    // 查看表的数据
    lookDetailData (id) {
      this.$refs.dialog.dialog(id)
    },
    getModalDataList (id) {
      (this.saveSelectAllList || []).forEach((item, index) => {
        if (item.resourceId === id) {
          this.couponList = item.column || []
        }
      })
    },
    removeField (index) {
      if (this.linkModalFields.length > 1) {
        this.linkModalFields.splice(index, 1)
        this.updateFields(this.linkModal.alias, this.linkModal.joinAlias, this.linkModalFields)
      }
    }
  }
}
</script>

<style lang="stylus" scoped>
  .editor-box {
    display: flex;
    width: 100%;
    height:100%;
    #editorContainer {
      flex: 1;
      display: block;
      width: 100%;
    }
    .option,
    .base-info {
      height:100%;
      flex: initial;
      max-width: 260px;
      border: 1px solid #e6e9ed;
      z-index: 9;
      background-color #ffffff
    }
  }
.hide{
  display none
}

.tableRelation{
  margin-top 16px
  height calc(100vh)
  position relative
  .containers{
    height 91.5%
    padding 16px 0
  }
}

.dragRect{
  position absolute
  width 100px
  height 30px
  background #67C23A
  z-index 10
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
  z-index 1
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

.papers .remove {
  position absolute
  right -15px
  bottom -15px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cg%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M15.386%2C3.365c-3.315-3.314-8.707-3.313-12.021%2C0c-3.314%2C3.315-3.314%2C8.706%2C0%2C12.02%20c3.314%2C3.314%2C8.707%2C3.314%2C12.021%2C0S18.699%2C6.68%2C15.386%2C3.365L15.386%2C3.365z%20M4.152%2C14.598C1.273%2C11.719%2C1.273%2C7.035%2C4.153%2C4.154%20c2.88-2.88%2C7.563-2.88%2C10.443%2C0c2.881%2C2.88%2C2.881%2C7.562%2C0%2C10.443C11.716%2C17.477%2C7.032%2C17.477%2C4.152%2C14.598L4.152%2C14.598z%22%2F%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M12.157%2C11.371L7.38%2C6.593C7.162%2C6.375%2C6.809%2C6.375%2C6.592%2C6.592c-0.218%2C0.219-0.218%2C0.572%2C0%2C0.79%20l4.776%2C4.776c0.218%2C0.219%2C0.571%2C0.219%2C0.79%2C0C12.375%2C11.941%2C12.375%2C11.588%2C12.157%2C11.371L12.157%2C11.371z%22%2F%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M11.369%2C6.593l-4.777%2C4.778c-0.217%2C0.217-0.217%2C0.568%2C0%2C0.787c0.219%2C0.219%2C0.571%2C0.217%2C0.788%2C0l4.777-4.777%20c0.218-0.218%2C0.218-0.571%2C0.001-0.789C11.939%2C6.375%2C11.587%2C6.375%2C11.369%2C6.593L11.369%2C6.593z%22%2F%3E%3C%2Fg%3E%3C%2Fsvg%3E%20')
}

.papers .link {
  position absolute
  right -15px
  top -15px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M9.884%2C9.838c0.54-0.551%2C1.005-0.955%2C1.384-1.201c0.463-0.308%2C0.749-0.352%2C0.887-0.352h1.34v1.367%20c0%2C0.104%2C0.061%2C0.2%2C0.154%2C0.242s0.204%2C0.027%2C0.284-0.038l3.168-2.669c0.06-0.051%2C0.096-0.125%2C0.096-0.203S17.16%2C6.83%2C17.101%2C6.781%20l-3.168-2.677c-0.08-0.067-0.19-0.081-0.284-0.038c-0.094%2C0.045-0.154%2C0.139-0.154%2C0.242v1.414h-1.343%20c-1.24%2C0.014-2.215%2C0.67-2.927%2C1.242c-0.797%2C0.65-1.533%2C1.447-2.245%2C2.217c-0.361%2C0.391-0.7%2C0.759-1.044%2C1.1%20c-0.541%2C0.549-1.011%2C0.951-1.395%2C1.199c-0.354%2C0.231-0.678%2C0.357-0.921%2C0.357h-1.8c-0.146%2C0-0.266%2C0.12-0.266%2C0.265v2.029%20c0%2C0.148%2C0.12%2C0.268%2C0.266%2C0.268h1.8l0%2C0c1.255-0.014%2C2.239-0.667%2C2.958-1.24c0.82-0.661%2C1.572-1.475%2C2.297-2.256%20C9.225%2C10.524%2C9.555%2C10.169%2C9.884%2C9.838z%22%2F%3E%3C%2Fsvg%3E%20')
}

.papers .clone {
  position absolute
  left -15px
  bottom -15px
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

</style>
