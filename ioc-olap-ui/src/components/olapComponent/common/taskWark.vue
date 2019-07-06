<template>
  <div>
    <!-- 创建图容器 -->
    <div id="mountNode"></div>
  </div>
</template>

<script>
import G6 from '@antv/g6'
export default {
  data () {
    return {
      datas: {
        nodes: [{ id: '1', x: 50, y: 50, size: 20 }, { id: '2', x: 150, y: 50, size: 20 }],
        edges: [
          {
            source: '1',
            target: '2',
            shape: 'quadratic',
            color: '#722ed1',
            size: 2,
            style: {
              lineDash: [2, 2]
            },
            label: 'quadratic',
            labelCfg: {
              position: 'center', // 其实默认就是 center，这里写出来便于理解
              autoRotate: true,
              style: {
                lineWidth: 5,
                fill: '#722ed1',
                stroke: 'white' // 给文本添加白边和白色背景
              }
            }
          }
        ]
      }
    }
  },
  methods: {
    initData () {
      let graph = new G6.Graph({
        container: 'mountNode',
        width: 500,
        height: 500
      })
      graph.data(this.datas)
      graph.render()
    }
  },
  mounted () {
    this.initData()
  }
}
</script>
