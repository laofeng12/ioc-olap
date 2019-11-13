const moduleTypeList = [
  {
    id: 1,
    value: '智能API数据查询'
  },
  {
    id: 6,
    value: 'olap分析'
  },
  {
    id: 9,
    value: '数据湖数据推送'
  },
  {
    id: 10,
    value: '即席查询'
  }
]
// 获取数据服务
export function moduleTypeStr (val) {
  if (!Number.isNaN(Number(val))) {
    return moduleTypeList.find(t => t.id === val)
  }
  return moduleTypeList.find(t => t.value === val)
}