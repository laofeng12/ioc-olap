package com.openjava.olap.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * 点击构建时返回的结果
 * @author linchuangang
 * @create 2019-11-07 15:07
 * @see com.openjava.olap.service.OlapCubeBuildServiceImpl
 **/
@Getter
@Setter
public class OlapCubeBuildVo {
    private Integer status;
    private String msg;
}
