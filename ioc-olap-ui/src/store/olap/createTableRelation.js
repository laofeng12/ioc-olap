
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
    }
  }
}

export default creatTableRelation
