package com.kong.Service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.kong.common.R;
import com.kong.entity.OrderDetail;

import java.util.List;

public interface OrderDetailService extends IService<OrderDetail> {
    public R<List<OrderDetail>> getByOrderId(Long id);
}
