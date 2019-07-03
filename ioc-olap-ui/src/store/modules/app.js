import Cookies from 'js-cookie'
import { getMenuList } from '@/api/menu'

const app = {
  state: {
    sidebar: {
      opened: !+Cookies.get('sidebarStatus'),
      withoutAnimation: false
    },
    device: 'desktop',
    menuList: [{
      resId: 'vccvb',
      index: '1',
      resName: '首页',
      resURL: 'nolinkpath11',
      resIcon: '',
      subResList: [{
        resId: 'asdasdasdasd',
        index: '2',
        resName: '首页列表',
        resURL: '/home',
        resIcon: 'fa fa-columns',
        subResList: []
      }]
    }, {
      resId: 'asdasdasdasd1111',
      index: '3',
      resName: 'OLAP',
      resURL: 'nolinkpath222',
      resIcon: '',
      subResList: [{
        resId: 'asdasdasdasdasd',
        index: '4',
        resName: 'OLAP分析',
        resURL: '/olap',
        resIcon: 'fa fa-columns',
        subResList: []
      }]
    }],
    myRoutes: []
  },
  mutations: {
    TOGGLE_SIDEBAR: state => {
      if (state.sidebar.opened) {
        Cookies.set('sidebarStatus', 1)
      } else {
        Cookies.set('sidebarStatus', 0)
      }
      state.sidebar.opened = !state.sidebar.opened
      state.sidebar.withoutAnimation = false
    },
    CLOSE_SIDEBAR: (state, withoutAnimation) => {
      Cookies.set('sidebarStatus', 1)
      state.sidebar.opened = false
      state.sidebar.withoutAnimation = withoutAnimation
    },
    TOGGLE_DEVICE: (state, device) => {
      state.device = device
    },
    SET_MENU_LIST: (state, menuList) => {
      // state.menuList = menuList
    },
    CHANGE_MY_ROUTES: (state, myRoutes) => {
      state.myRoutes = myRoutes
    },
    SHOW_SIDBAR: (state) => {
      state.sidebar.opened = true
    }
  },
  actions: {
    ToggleSideBar: ({ commit }) => {
      commit('TOGGLE_SIDEBAR')
    },
    CloseSideBar ({ commit }, { withoutAnimation }) {
      commit('CLOSE_SIDEBAR', withoutAnimation)
    },
    ToggleDevice ({ commit }, device) {
      commit('TOGGLE_DEVICE', device)
    },
    GetMenuList ({ commit, dispatch }) {
      getMenuList()
        .then(res => {
          let list = res.resources || []
          commit('SET_MENU_LIST', list)
          // dispatch('GetCatalogMenu')
        })
        .catch(error => {
          console.log(error)
        })
    },
    showSidbar ({ commit }) {
      commit('SHOW_SIDBAR')
    },
    ChangeMyRoutes ({ commit }, myRoutes) {
      commit('CHANGE_MY_ROUTES', myRoutes)
    }
  }
}

export default app
