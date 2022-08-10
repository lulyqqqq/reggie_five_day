package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.AddressBook;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.mapper.AddressBookMapper;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.AddressBookService;
import com.itheima.reggie.service.CategoryService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: AddressBookServiceImpl
 * @author: mafangnian
 * @date: 2022/8/10 20:33
 * @Blog: null
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
