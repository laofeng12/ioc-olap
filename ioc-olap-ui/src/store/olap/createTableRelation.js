
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
    },
    // 存储连后对应的fk
    foreignKeyData: []
  },
  mutations: {
    SaveJointResult (state, payload) {
      state.jointResultData = payload
      payload.lookups.map(res => {
        if (res.join.foreign_key.length > 1) {
          res.join.foreign_key.forEach(item => {
            state.foreignKeyData = state.foreignKeyData.concat(item)
          })
        } else {
          state.foreignKeyData.push(res.join.foreign_key.join(','))
        }
      })
      state.foreignKeyData = [...new Set(state.foreignKeyData)]
    },
    RESET_CREATE_TABLETIONS (state, payload) {
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
      state.foreignKeyData = []
    },
    resetCreateTabletions ({ state, commit }) {
      state.jointResultData.lookups = []
      state.jointResultData.fact_table = ''
      state.foreignKeyData = []
      console.log(state.jointResultData, '执行之后的')
    }
  }
}

export default creatTableRelation
