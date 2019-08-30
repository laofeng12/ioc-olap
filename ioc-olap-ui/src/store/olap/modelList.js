import { getModelDataList, jobsListModeling } from '@/api/modelList'
const modelList = {
  state: {
    /* 模型列表 */
    modelListData: [],
    cubeObjListData: [],
    params: {
      limit: 40,
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
        let list = {
          ...state.params,
          ...data
        }
        const datas = jobsListModeling(list)
        state.cubeObjListData = datas
        resolve(datas)
      })
    }
  }
}

export default modelList
