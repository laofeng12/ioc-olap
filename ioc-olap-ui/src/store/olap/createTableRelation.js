
const creatTableRelation = {
  state: {
    jointResult: {
      name: 'joint',
      description: '',
      fact_table: '',
      lookups: []
    }
  },
  actions: {
    SaveJointResult ({ state }, data) {
      state.jointResult = data
      console.log('来了', state.jointResult)
    }
  }
}

export default creatTableRelation
