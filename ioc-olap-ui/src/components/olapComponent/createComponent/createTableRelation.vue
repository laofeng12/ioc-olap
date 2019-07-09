<template>
  <div class="tableRelation">
    <div class="containers">
      <fact-table></fact-table>
      <!-- <task-wark></task-wark> -->
      <div class="holder">
        <button style="width:100px;height:30px" @click="click_add">add</button>
        <div id="myholder" @click="click_joint"></div>
        <div class="papers">
          <div class="halo-cell-layer">
            <div class="remove" data-type="remove"></div>
            <div class="link" data-type="link"></div>
            <div class="clone" data-type="clone"></div>
            <!-- <div class="resize" data-type="resize"></div> -->
          </div>
        </div>
      </div>
    </div>
    <steps class="steps" :step="2" @nextModel="nextModel" @prevModel="prevModel"></steps>
  </div>
</template>

<script>
import factTable from '@/components/olapComponent/common/factTable'
import steps from '@/components/olapComponent/common/steps'
import taskWark from '@/components/olapComponent/common/taskWark'
import { mapGetters } from 'vuex'
let $ = require('jquery')
let joint = require('jointjs')

export default {
  components: {
    factTable, steps, taskWark
  },
  data () {
    return {
    }
  },
  mounted: function () {
    this.$root.eventBus.$emit('createTable', this.selectTableCount)
    this.init()
  },
  watch: {
  },
  methods: {
    init () {
      this.graph = new joint.dia.Graph()

      let paper = new joint.dia.Paper({
        el: $('#myholder'),
        width: 600,
        height: 600,
        model: this.graph,
        gridSize: 1
      })

      let rect = new joint.shapes.basic.Rect({
        position: { x: 100, y: 30 },
        size: { width: 100, height: 30 },
        attrs: { rect: { fill: 'blue' }, text: { text: 'my box', fill: 'white' } }
      })

      let rect2 = rect.clone()
      rect2.translate(300)

      let link = new joint.shapes.standard.Link({
        source: rect,
        target: rect2,
        router: { name: 'manhattan' }, // 设置连线弯曲样式 manhattan直角
        labels: [{ position: 0.5, attrs: { text: { text: '未关联', 'font-weight': 'bold', 'font-size': '12px' } } }]
      })


      this.graph.addCells([rect, rect2, link])

      // 有鼠标点击，鼠标拖拽等等事件,cell:在源码里面找--利用自带的事件，可以获取到点击元素的信息，便于之后的增删改等操作
      paper.on('blank:pointerup', (e, d) => {
        // e.remove();
        this.hideCellLayer()
      })

      // 有鼠标点击，鼠标拖拽等等事件,cell:在源码里面找--利用自带的事件，可以获取到点击元素的信息，便于之后的增删改等操作
      paper.on('cell:pointerup', (e, d) => {
        // console.log(e);
        if (this.isClick) {
          this.showCellLayer(e)
          this.isClick = false
        } else {
          let element = this.getLinkElement(e.targetPoint)
          if (element) {
            e.model.target(element)
            e.model.labels([{ position: 0.5, attrs: { text: { text: '未关联', 'font-weight': 'bold', 'font-size': '12px' } } }])
          }
        }
      })

      paper.on('cell:pointerdown', (e, d) => {
        this.isClick = true
      })

      paper.on('cell:pointermove', (e, d) => {
        // console.log(e)
        this.isClick = false
      })

      $('.papers').on('click', e => {
        let element = $('.halo-cell-layer').data('element')
        let model = element.model
        // let id = element.id

        switch (e.target.dataset.type) {
          case 'remove':
            this.clearElementLink(model)
            element.remove()
            break
          case 'link':
            let link = new joint.shapes.standard.Link({
              source: model,
              target: { x: model.attributes.position.x, y: model.attributes.position.y - 5 },
              router: { name: 'manhattan' }// 设置连线弯曲样式 manhattan直角
            })
            this.graph.addCell(link)
            break
          case 'clone':
            let cell = model.clone()
            cell.translate(50, 50)
            this.graph.addCell(cell)
            break
        }

        $('.halo-cell-layer').hide()
      })

      $('.holder').on('mouseup', e => {
        let x = e.offsetX
        let y = e.offsetY
        // console.log(e)
      })
    },

    clearElementLink: function (target) {
      console.log(target)

      let eles = target.collection.models || []
      let elements = []

      for (let i = 0; i < eles.length; i++) {
        let ele = eles[i]
        if (ele.attributes.type == 'standard.Link') {
          if (ele.get('source').id == target.id || ele.get('target'.id == target.id)) {
            ele.remove()
          }
        }
      }
    },

    getLinkElement: function (point) {
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
      $('.halo-cell-layer').css({
        display: 'none'
      })
    },
    showCellLayer (element) {
      let haloTypes = ['remove', 'link', 'clone', 'resize']
      let parentOffset = $('.holder').offset()
      let rect = element.$el[0].getBoundingClientRect()
      let offset = element.$el.offset()

      let $rect = $('.papers')
      let $layer = $('.halo-cell-layer')

      $layer.css({
        display: 'block',
        position: 'absolute',
        width: rect.width,
        height: rect.height,
        left: offset.left - parentOffset.left,
        top: offset.top - parentOffset.top,
        zIndex: '1000'
      })

      $layer.data('element', element)

      // haloTypes.forEach(haloType => {
      //   let $item = $('<div class="halo-cell-item"></div>')
      //   $item.addClass(haloType)
      //   $layer.append($item)

      // })

      // $rect.append($layer)
    },
    click_add () {
      let rect3 = new joint.shapes.basic.Rect({
        position: { x: 100, y: 130 },
        size: { width: 100, height: 30 },
        attrs: { rect: { fill: 'blue' }, text: { text: 'my box', fill: 'white' } }
      })
      this.graph.addCells([rect3])
    },

    click_joint () {
      // let graph = new joint.dia.Graph;

      // let paper = new joint.dia.Paper({
      //   el: $('#myholder'),
      //   width: 600,
      //   height: 600,
      //   model: graph,
      //   gridSize: 1
      // });

      // let rect = new joint.shapes.basic.Rect({
      //   position: { x: 100, y: 30 },
      //   size: { width: 100, height: 30 },
      //   attrs: { rect: { fill: 'blue' }, text: { text: 'my box', fill: 'white' } }
      // });

      // let rect2 = rect.clone();
      // rect2.translate(300);

      // let link = new joint.dia.Link({
      //   source: { id: rect.id },
      //   target: { id: rect2.id }
      // });

      // graph.addCells([rect, rect2, link]);
    },
    nextModel (val) {
      this.$router.push('/olap/createolap/setFiled')
      this.$parent.getStepCountAdd(val)
    },
    prevModel (val) {
      this.$router.push('/olap/createolap/selectStep')
      this.$parent.getStepCountReduce(val)
      this.$root.eventBus.$emit('openDefaultTree')
    }
  },
  computed: {
    ...mapGetters({
      selectTableCount: 'selectTableCount'
    })
  }
}
</script>

<style lang="stylus" scoped>
.tableRelation{
  height calc(100vh - 150px)
  position relattive
  .containers{
    padding 20px 5px
  }
}

.holder{
  position relative
  overflow hidden
}

.halo-cell-layer{
  display none
}

.papers .remove {
  position absolute
  left -20px
  top -20px
  width 18px
  height 18px
  background-image: url('data:image/svg+xml;charset=utf8,%3C%3Fxml%20version%3D%221.0%22%20encoding%3D%22utf-8%22%3F%3E%3C!DOCTYPE%20svg%20PUBLIC%20%22-%2F%2FW3C%2F%2FDTD%20SVG%201.1%2F%2FEN%22%20%22http%3A%2F%2Fwww.w3.org%2FGraphics%2FSVG%2F1.1%2FDTD%2Fsvg11.dtd%22%3E%3Csvg%20version%3D%221.1%22%20id%3D%22Layer_1%22%20xmlns%3D%22http%3A%2F%2Fwww.w3.org%2F2000%2Fsvg%22%20xmlns%3Axlink%3D%22http%3A%2F%2Fwww.w3.org%2F1999%2Fxlink%22%20x%3D%220px%22%20y%3D%220px%22%20width%3D%2218.75px%22%20height%3D%2218.75px%22%20viewBox%3D%220%200%2018.75%2018.75%22%20enable-background%3D%22new%200%200%2018.75%2018.75%22%20xml%3Aspace%3D%22preserve%22%3E%3Cg%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M15.386%2C3.365c-3.315-3.314-8.707-3.313-12.021%2C0c-3.314%2C3.315-3.314%2C8.706%2C0%2C12.02%20c3.314%2C3.314%2C8.707%2C3.314%2C12.021%2C0S18.699%2C6.68%2C15.386%2C3.365L15.386%2C3.365z%20M4.152%2C14.598C1.273%2C11.719%2C1.273%2C7.035%2C4.153%2C4.154%20c2.88-2.88%2C7.563-2.88%2C10.443%2C0c2.881%2C2.88%2C2.881%2C7.562%2C0%2C10.443C11.716%2C17.477%2C7.032%2C17.477%2C4.152%2C14.598L4.152%2C14.598z%22%2F%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M12.157%2C11.371L7.38%2C6.593C7.162%2C6.375%2C6.809%2C6.375%2C6.592%2C6.592c-0.218%2C0.219-0.218%2C0.572%2C0%2C0.79%20l4.776%2C4.776c0.218%2C0.219%2C0.571%2C0.219%2C0.79%2C0C12.375%2C11.941%2C12.375%2C11.588%2C12.157%2C11.371L12.157%2C11.371z%22%2F%3E%3Cpath%20fill%3D%22%236A6C8A%22%20d%3D%22M11.369%2C6.593l-4.777%2C4.778c-0.217%2C0.217-0.217%2C0.568%2C0%2C0.787c0.219%2C0.219%2C0.571%2C0.217%2C0.788%2C0l4.777-4.777%20c0.218-0.218%2C0.218-0.571%2C0.001-0.789C11.939%2C6.375%2C11.587%2C6.375%2C11.369%2C6.593L11.369%2C6.593z%22%2F%3E%3C%2Fg%3E%3C%2Fsvg%3E%20')
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
</style>
