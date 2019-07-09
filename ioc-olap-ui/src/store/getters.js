const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  menuList: state => state.app.menuList,
  treeList: state => state.common.treeList,
  serchTableList: state => state.common.menuList,
  saveSelectTable: state => state.common.saveSelectTable,
  saveLocalSelectTable: state => state.common.saveLocalSelectTable,
  selectTableCount: state => state.common.selectTableCount,
  lastClickTab: state => state.common.lastClickTab,
  saveSelctchckoutone: state => state.common.saveSelctchckoutone,
  saveSelctchckouttwo: state => state.common.saveSelctchckouttwo,
  saveSelectFiled: state => state.common.saveSelectFiled
}
export default getters
