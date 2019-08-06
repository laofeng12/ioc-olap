const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  menuList: state => state.app.menuList,
  savemousedownData: state => state.olap.savemousedownData,
  lateData: state => state.olap.lateData,
  totalSaveData: state => state.olap.totalSaveData,
  treeList: state => state.selectStep.treeList,
  serchTableList: state => state.selectStep.serchTableList,
  saveSelectTable: state => state.selectStep.saveSelectTable,
  saveLocalSelectTable: state => state.selectStep.saveLocalSelectTable,
  selectTableTotal: state => state.selectStep.selectTableTotal,
  lastClickTab: state => state.selectStep.lastClickTab,
  saveSelctchckoutone: state => state.selectStep.saveSelctchckoutone,
  saveSelctchckouttwo: state => state.selectStep.saveSelctchckouttwo,
  /* */
  jointResult: state => state.createTableRelation.jointResult,
  /* */
  saveSelectFiled: state => state.setFiled.saveSelectFiled,
  saveList: state => state.setFiled.saveList,
  saveNewSortList: state => state.setFiled.saveNewSortList,
  saveNewSortListstructure: state => state.setFiled.saveNewSortListstructure,
  reloadNeedData: state => state.setFiled.reloadNeedData,
  saveLeftFiled: state => state.setFiled.saveLeftFiled,
  dimensions: state => state.setFiled.dimensions,
  /* */
  measureTableList: state => state.setMeasure.measureTableList,
  relaodFilterList: state => state.reloadSet.relaodFilterList,
  reloadData: state => state.reloadSet.reloadData,
  /* */
  mandatory_dimension_set_list: state => state.advancedSet.mandatory_dimension_set_list,
  hbase_mapping: state => state.advancedSet.hbase_mapping,
  aggregation_groups: state => state.advancedSet.aggregation_groups,
  selectDataidList: state => state.advancedSet.selectDataidList,
  savedimensionDataId: state => state.advancedSet.savedimensionDataId,
  rowkeyData: state => state.advancedSet.rowkeyData,
  engine_types: state => state.advancedSet.engine_types,
  savehetComposeDataId: state => state.advancedSet.savehetComposeDataId,
  /* */
  saveFolderList: state => state.analysis.saveFolderList,
  cubeId: state => state.analysis.cubeId,
  newValueList: state => state.analysis.newValueList,
  newFilterList: state => state.analysis.newFilterList,
  newRowList: state => state.analysis.newRowList,
  newColList: state => state.analysis.newColList
}
export default getters
