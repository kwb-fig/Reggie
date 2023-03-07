// 查询列表页接口
const getOrderDetailPage = (params) => {
  return $axios({
    url: '/order/page',
    method: 'get',
    params
  })
}

// 查询今日订单量
const getToDayOrderApi = () => {
  return $axios({
    url: `/order/getToDayOrder`,
    method: 'get'
  })
}

// 查询昨日订单量
const getYesDayOrderApi = () => {
  return $axios({
    url: `/order/getYesDayOrder`,
    method: 'get'
  })
}
//查询近一周的订单流水
const getOneWeekLiuShuiApi = () => {
  return $axios({
    url: `/order/getOneWeekLiuShui`,
    method: 'get'
  })
}

//查询近一周的订单数量
const getOneWeekOrderApi = () => {
  return $axios({
    url: `/order/getOneWeekOrder`,
    method: 'get'
  })
}

//查询热门套餐
const getHotSealApi = () => {
  return $axios({
    url: `/order/getHotSeal`,
    method: 'get'
  })
}


// 查看接口
const queryOrderDetailById = (id) => {
  return $axios({
    url: `/orderDetail/${id}`,
    method: 'get'
  })
}

// 取消，派送，完成接口
const editOrderDetail = (params) => {
  return $axios({
    url: '/order',
    method: 'put',
    data: { ...params }
  })
}
