package com.ctgu.autoreport.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午4:32
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    private String username;

    private String password;

    private String email;
}
