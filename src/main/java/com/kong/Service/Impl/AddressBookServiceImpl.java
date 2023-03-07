package com.kong.Service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kong.Mapper.AddressBookMapper;
import com.kong.Service.AddressBookService;
import com.kong.entity.AddressBook;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
