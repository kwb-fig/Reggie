//提交订单
function  addOrderApi(data){
    return $axios({
        'url': '/order/submit',
        'method': 'post',
        data
      })
}

//查询所有订单
function orderListApi() {
  return $axios({
    'url': '/order/list',
    'method': 'get',
  })
}

//分页查询订单
function orderPagingApi(data) {
  return $axios({
      'url': '/order/userPage',
      'method': 'get',
      params:{...data}
  })
}

//再来一单
function orderAgainApi(data) {
  return $axios({
      'url': '/order/userPage',
      'method': 'post',
      data
  })
}

//删除订单
// function deleteOrderApi(params) {
//     return $axios({
//         'url': '/order',
//         'method': 'delete',
//         params
//     })
// }
const deleteOrderApi = (id) => {
    return $axios({
        url: '/order',
        method: 'delete',
        params: { id }
    })
}

// 删除地址
// function deleteOrderApi(id) {
//     return $axios({
//         'url': '/order',
//         'method': 'delete',
//         id
//     })
// }