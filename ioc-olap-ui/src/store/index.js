import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './modules/user'
import olap from './modules/olap'
import selectStep from './olap/selectStep'
import createTableRelation from './olap/createTableRelation'
import setFiled from './olap/setFiled'
import setMeasure from './olap/setMeasure'
import reloadSet from './olap/reloadSet'
import advancedSet from './olap/advancedSet'
import modelList from './olap/modelList'
import analysis from './modules/analysis'
// import selectStep from './olap/selectStep'
import getters from './getters'
import { TaskNode, TaskNodeStore } from 'vue-task-node'

Vue.use(TaskNode)
Vue.use(Vuex)

const store = new Vuex.Store({
  modules: {
    app,
    user,
    olap,
    selectStep,
    setFiled,
    createTableRelation,
    setMeasure,
    reloadSet,
    advancedSet,
    analysis,
    modelList,
    TaskNodeStore
  },
  getters
})

export default store
