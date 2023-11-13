package com.sangeng.ddsys.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.sangeng.ddsys.common.constant.RedisConst;
import com.sangeng.ddsys.common.exception.DdsysException;
import com.sangeng.ddsys.common.result.Result;
import com.sangeng.ddsys.common.result.ResultCodeEnum;
import com.sangeng.ddsys.common.utils.JwtHelper;
import com.sangeng.ddsys.enums.UserType;
import com.sangeng.ddsys.model.user.User;
import com.sangeng.ddsys.user.controller.service.UserService;
import com.sangeng.ddsys.user.utils.ConstantPropertiesUtil;
import com.sangeng.ddsys.user.utils.HttpClientUtils;
import com.sangeng.ddsys.vo.user.LeaderAddressVo;
import com.sangeng.ddsys.vo.user.UserLoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author: calos
 * @create: 2023-11-12 12:36
 */
@RestController
@RequestMapping("/api/user/weixin")
public class WeixinApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation(value = "微信登录获取openid(小程序)")
    @GetMapping("/wxLogin/{code}")
    public Result callback(@PathVariable String code) {
        // 1 得到微信返回code临时票据值，然后使用code+小程序id+小程序秘钥 请求微信接口服务
        // 小程序id
        String wxOpenAppId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        // 小程序秘钥
        String wxOpenAppSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        // get请求拼接请求地址+参数
        StringBuffer url = new StringBuffer().append("https://api.weixin.qq.com/sns/jscode2session").append("?appid=%s")
            .append("&secret=%s").append("&js_code=%s").append("&grant_type=authorization_code");
        String tokenUrl = String.format(url.toString(), wxOpenAppId, wxOpenAppSecret, code);

        // 2 使用HTTPClient 工具发送get请求
        String result = null;
        try {
            result = HttpClientUtils.get(tokenUrl);
        } catch (Exception e) {
            throw new DdsysException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        // 3 请求微信接口服务，返回两个值 session_key 和 openId
        // openId是微信唯一标识
        JSONObject jsonObject = JSONObject.parseObject(result);
        String sessionKey = jsonObject.getString("session_key");
        String openid = jsonObject.getString("openid");

        // 4 添加微信用户信息到数据库中，操作user表
        // 判断是否是第一次使用微信授权的登录
        User user = userService.getUserByOpenid(openid);
        if (user == null) {
            user = new User();
            user.setOpenId(openid);
            user.setNickName(openid);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }
        // 5 根据userId查询提货点和团长信息
        // 提货点 user表 user_delivery表
        // 团长 leader 表
        LeaderAddressVo leaderAddressVo = userService.getLeaderAddressVoByUserId(user.getId());
        // 6 使用JWT工具根据userId和userName生成token字符串
        String token = JwtHelper.createToken(user.getId(), user.getNickName());

        // 7 获取当前登录用户信息，放到Redis中，设置有效时间
        UserLoginVo userLoginVo = userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue()
            .set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(), userLoginVo, RedisConst.USERKEY_TIMEOUT,
                TimeUnit.DAYS);

        // 8 需要数据封装到map返回
        HashMap<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", token);
        map.put("leaderAddressVo", leaderAddressVo);
        return Result.ok(map);
    }
}
