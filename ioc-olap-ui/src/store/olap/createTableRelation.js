
const creatTableRelation = {
  state: {
    jointResultData: {
      name: 'joint',
      description: '',
      fact_table: '',
      lookups: [
        // {
        //   'table': 'KYLIN.KYLIN_CAL_DT',
        //   'alias': 'KYLIN_CAL_DT',
        //   'joinTable': 'KYLIN.KYLIN_SALES',
        //   'kind': 'LOOKUP',
        //   'join': {
        //     'type': 'inner',
        //     'primary_key': [
        //       'KYLIN_CAL_DT.CAL_DT'
        //     ],
        //     'foreign_key': [
        //       'KYLIN_SALES.PART_DT'
        //     ],
        //     'isCompatible': [
        //       true
        //     ],
        //     'pk_type': [
        //       'date'
        //     ],
        //     'fk_type': [
        //       'date'
        //     ]
        //   }
        // }
      ]
    }
  },
  mutations: {
    SaveJointResult (state, payload) {
      state.jointResultData = payload
    }
  },
  actions: {
    resetList ({ state }) {
      state.jointResultData = {
        name: 'joint',
        description: '',
        fact_table: '',
        lookups: []
      }
    }
    // SaveJointResult ({ commit }, data) {
    //   commit('SAVE_JOINT_RESULT', data)
    //   // state.jointResult = data
    //   // console.log(state.jointResult, '啦啦啦啦啦')
    // }
  }
}

export default creatTableRelation
