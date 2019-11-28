const getters = {
  isEdit: state => state.olap.isEdit, // 是否编辑
  tableJoinType: state => state.selectStep.tableJoinType, // 表连接类型
  batchCreateJob: state => state.selectStep.batchCreateJob,
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  token: state => state.user.token,
  userInfo: state => state.user.userInfo,
  menuList: state => state.app.menuList,
  savemousedownData: state => state.olap.savemousedownData,
  HeadNum: state => state.olap.HeadNum,
  ModelAllList: state => state.olap.ModelAllList,
  modelSelectData: state => state.olap.modelSelectData,
  lateData: state => state.olap.lateData,
  totalSaveData: state => state.olap.totalSaveData,
  treeList: state => state.selectStep.treeList,
  serchTableList: state => state.selectStep.serchTableList,
  saveSelectTable: state => state.selectStep.saveSelectTable,
  saveLocalSelectTable: state => state.selectStep.saveLocalSelectTable,
  selectTableTotal: state => state.selectStep.selectTableTotal,
  selectStepList: state => state.selectStep.selectStepList,
  saveSelectAllList: state => state.selectStep.saveSelectAllList,
  saveSelectAllListFiled: state => state.selectStep.saveSelectAllListFiled,
  SaveFactData: state => state.selectStep.SaveFactData,
  /* */
  jointResultData: state => state.createTableRelation.jointResultData,
  foreignKeyData: state => state.createTableRelation.foreignKeyData,
  /* */
  saveSelectFiled: state => state.setFiled.saveSelectFiled,
  saveList: state => state.setFiled.saveList,
  saveNewSortList: state => state.setFiled.saveNewSortList,
  saveNewSortListstructure: state => state.setFiled.saveNewSortListstructure,
  reloadNeedData: state => state.setFiled.reloadNeedData,
  dimensions: state => state.setFiled.dimensions,
  saveNewTitle: state => state.setFiled.saveNewTitle,
  /* */
  measureTableList: state => state.setMeasure.measureTableList,
  relaodFilterList: state => state.reloadSet.relaodFilterList,
  reloadData: state => state.reloadSet.reloadData,
  /* */
  mandatory_dimension_set_list: state => state.advancedSet.mandatory_dimension_set_list,
  saveselectIncludesData: state => state.advancedSet.saveselectIncludesData,
  filterMappingData: state => state.advancedSet.filterMappingData,
  hbase_mapping: state => state.advancedSet.hbase_mapping,
  aggregation_groups: state => state.advancedSet.aggregation_groups,
  selectDataidList: state => state.advancedSet.selectDataidList,
  savedimensionDataId: state => state.advancedSet.savedimensionDataId,
  rowkeyData: state => state.advancedSet.rowkeyData,
  engine_types: state => state.advancedSet.engine_types,
  recordingData: state => state.advancedSet.recordingData,
  savehetComposeDataId: state => state.advancedSet.savehetComposeDataId,
  /* */
  saveFolderList: state => state.analysis.saveFolderList,
  cubeData: state => state.analysis.cubeData,
  newValueList: state => state.analysis.newValueList,
  newFilterList: state => state.analysis.newFilterList,
  newRowList: state => state.analysis.newRowList,
  newColList: state => state.analysis.newColList,
  /** modelList */
  cubeObjListData: state => state.modelList.cubeObjListData
}
export default getters
