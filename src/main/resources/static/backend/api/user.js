const getUserCountApi = () => {
    return $axios({
        url: `/user/getUserCount`,
        method: 'get'
    })
}

function getUserList (params) {
    return $axios({
        url: '/user/page',
        method: 'get',
        params
    })
}

// 修改---启用禁用接口
function enableOrDisableUser (params) {
    return $axios({
        url: '/user/updateStatus',
        method: 'put',
        data: { ...params }
    })
}

// 修改页面反查详情接口
function queryUserById (id) {
    return $axios({
        url: `/user/${id}`,
        method: 'get'
    })
}

// 修改---添加员工
function editUser (params) {
    return $axios({
        url: '/user/updateStatus',
        method: 'put',
        data: { ...params }
    })
}
