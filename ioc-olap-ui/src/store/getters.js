const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  menuList: state => state.app.menuList,
  treeList: state => state.common.treeList,
  serchTableList: state => state.common.serchTableList,
  saveSelectTable: state => state.common.saveSelectTable,
  saveLocalSelectTable: state => state.common.saveLocalSelectTable,
  selectTableTotal: state => state.common.selectTableTotal,
  lastClickTab: state => state.common.lastClickTab,
  saveSelctchckoutone: state => state.common.saveSelctchckoutone,
  saveSelctchckouttwo: state => state.common.saveSelctchckouttwo,
  saveSelectFiled: state => state.common.saveSelectFiled,
  saveSelectFiledTree: state => state.common.saveSelectFiledTree,
  saveNewSortList: state => state.common.saveNewSortList,
  saveList: state => state.common.saveList,
  saveRightTableList: state => state.common.saveRightTableList,
  measureTableList: state => state.common.measureTableList,
  relaodFilterList: state => state.common.relaodFilterList
}
export default getters
