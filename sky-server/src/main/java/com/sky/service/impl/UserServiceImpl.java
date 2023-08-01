package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.exception.UpdateFailException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import com.sky.utils.WeChatPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatProperties weChatProperties;

    private static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Override
    public User login(UserLoginDTO userLoginDTO) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weChatProperties.getAppid());
        map.put("secret", weChatProperties.getSecret());
        map.put("js_code", userLoginDTO.getCode());
        map.put("grant_type", "authorization_code");
        String result = HttpClientUtil.doGet(WX_LOGIN, map);

        JSONObject json = JSON.parseObject(result);
        String openid = json.getString("openid");

        if (openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        User user = userMapper.queryByOpenId(openid);
        Boolean success = true;
        if (user == null) {
             user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
             success = userMapper.insert(user);
        }
        if (!success){
            throw new UpdateFailException("创建用户失败！");
        }
        return user;
    }
}
