package com.openjava.platform.common;


import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;

@Component
@ConfigurationProperties(prefix="kylin")
@Getter
@Setter
public class KylinConfig {
    public String address;
    public String account;
    public String pwd;
}
