package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.entity.AddressBook;
import com.sky.mapper.AddressBookMapper;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Override
    @Transactional
    public Result add(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBook.setIsDefault(0);
        Boolean success = addressBookMapper.insert(addressBook);
        return success ? Result.success() : Result.error("新增地址失败！");
    }

    @Override
    public Result<List<AddressBook>> queryById() {
        List<AddressBook> addressBooks = addressBookMapper.queryById(BaseContext.getCurrentId());
        return Result.success(addressBooks);
    }

    @Override
    @Transactional
    public Result updateDefault(Long id) {
        List<AddressBook> addressBooks = addressBookMapper.queryById(BaseContext.getCurrentId());
        for (AddressBook addressBook : addressBooks) {
            if (addressBook.getIsDefault() > 0){
                if (id.equals(addressBook.getId())){
                    return Result.success();
                }
                addressBook.setIsDefault(0);
                Boolean update = addressBookMapper.update(addressBook);
            }
        }
        Boolean success = addressBookMapper.update(AddressBook.builder().id(id).isDefault(1).build());
        return success ? Result.success() : Result.error("修改默认地址失败！");
    }

    @Override
    public Result<AddressBook> queryDefault() {
        Long currentId = BaseContext.getCurrentId();
        List<AddressBook> addressBooks = addressBookMapper.queryById(currentId);
        AddressBook addressBook = null;
        for (AddressBook address : addressBooks) {
            if (address.getIsDefault() > 0)
                addressBook = address;
        }
        return Result.success(addressBook);
    }

    @Override
    public Result<AddressBook> queryByAddressId(Long id) {
        AddressBook addressBook = addressBookMapper.queryByAddressId(id);
        return Result.success(addressBook);
    }

    @Override
    public Result<AddressBook> update(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        Boolean success = addressBookMapper.update(addressBook);
        return success ? Result.success() : Result.error("修改地址失败！");
    }


}
