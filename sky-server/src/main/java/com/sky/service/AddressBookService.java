package com.sky.service;

import com.sky.entity.AddressBook;
import com.sky.result.Result;

import java.util.List;

public interface AddressBookService {
    Result add(AddressBook addressBook);

    Result<List<AddressBook>> queryById();

    Result updateDefault(Long id);

    Result<AddressBook> queryDefault();

    Result<AddressBook> queryByAddressId(Long id);

    Result<AddressBook> update(AddressBook addressBook);
}
