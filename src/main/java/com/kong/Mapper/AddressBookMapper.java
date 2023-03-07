package com.kong.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kong.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
