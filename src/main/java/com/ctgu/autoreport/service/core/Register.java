package com.ctgu.autoreport.service.core;


import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午3:05
 */
public interface Register {
    Result<?> register(UserVO userVO);
}
