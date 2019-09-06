package com.openjava.olap.common.kylin;

import com.openjava.olap.common.KylinConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
@Component
public class KylinHttpClient {
    @Resource
    protected KylinConfig config;
}
