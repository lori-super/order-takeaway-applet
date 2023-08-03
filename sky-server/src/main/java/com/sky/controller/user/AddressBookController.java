package com.sky.controller.user;

import com.sky.dto.DefaultAddressDTO;
import com.sky.entity.AddressBook;
import com.sky.result.Result;
import com.sky.service.AddressBookService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "地址簿接口类")
@Slf4j
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    @PostMapping
    @ApiOperation("添加地址薄")
    public Result add(@RequestBody AddressBook addressBook){
        log.info("新增地址簿, {}", addressBook);
        return addressBookService.add(addressBook);
    }

    @GetMapping("/list")
    @ApiOperation("查询用户地址簿")
    public Result<List<AddressBook>> queryList(){
        log.info("新增地址簿");
        return addressBookService.queryById();
    }

    /**
     *
     * @param id 需要修改的地址簿ID
     * @return
     */
    @PutMapping("/default")
    @ApiOperation("修改默认地址")
    public Result updateDefault(@RequestBody DefaultAddressDTO dto){
        log.info("修改地址簿的默认地址：{}", dto);
        return addressBookService.updateDefault(dto.getId());
    }

    @GetMapping("/default")
    @ApiOperation("查询默认地址")
    public Result<AddressBook> queryDefault(){
        log.info("查询地址簿的默认地址");
        return addressBookService.queryDefault();
    }


    @GetMapping("{id}")
    @ApiOperation("根据地址ID查询默认地址")
    public Result<AddressBook> queryByAddressId(@PathVariable Long id){
        log.info("查询地址簿信息");
        return addressBookService.queryByAddressId(id);
    }

    @PutMapping
    @ApiOperation("修改地址")
    public Result<AddressBook> update(@RequestBody AddressBook  addressBook){
        log.info("修改地址");
        return addressBookService.update(addressBook);
    }


}
