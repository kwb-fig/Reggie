// function userInfoApi (phone) {
//     return $axios({
//         url: `/user/${phone}`,
//         method: 'get',
//
//     })
// }

const userInfoApi = (phone) => {
    return $axios({
        url: `/user/getUserInfo/${phone}`,
        method: 'get'
    })
}

function  updateUserApi(data){
    return $axios({
        'url': '/user',
        'method': 'put',
        data
    })
}

