package com.ctgu.autoreport.service.core;


import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;

/**
 * 注册相关服务
 *
 * @author Elm Forest
 * @date 14/10/2022 下午3:05
 */
public interface RegisterService {
    /**
     * 注册服务
     *
     * @param userVO 用户视图对象
     * @return Result视图
     */
    Result<?> register(UserVO userVO);

    /**
     * 删除服务
     *
     * @param userVO 用户视图对象
     * @return Result视图
     */
    Result<?> delete(UserVO userVO);
}
