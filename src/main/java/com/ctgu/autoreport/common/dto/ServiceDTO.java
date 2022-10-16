package com.ctgu.autoreport.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午8:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceDTO {

    private Boolean flag;

    private Integer code;

    private String data;

    private String message;
}
