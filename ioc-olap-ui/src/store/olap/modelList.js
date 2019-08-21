import { getModelDataList, jobsListModeling, buildModeling, cloneModeling, disableModeling, enableModeling, descDataList } from '@/api/modelList'
const modelList = {
  state: {
    /* 模型列表 */
    modelListData: [],
    cubeObjListData: [],
    params: {
      limit: 15,
      offset: 0
    }

  },
  actions: {
    resetList ({ state }) {
      state.modelListData = []
      state.cubeObjListData = []
    },
    SavemodelListData ({ state }, data) {
      return new Promise((resolve, reject) => {
        const data = getModelDataList(state.params)
        state.modelListData = data
      })
    },
    SaveCubeObjListData ({ state }, data) {
      return new Promise((resolve, reject) => {
        const datas = jobsListModeling(state.params)
        state.cubeObjListData = datas
        resolve(datas)
      })
    }
  }
}

export default modelList
