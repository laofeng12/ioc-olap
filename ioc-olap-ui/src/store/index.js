import Vue from 'vue'
import Vuex from 'vuex'
import app from './modules/app'
import user from './modules/user'
import olap from './modules/olap'
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
    // selectStep,
    TaskNodeStore
  },
  getters
})

export default store
