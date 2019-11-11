package com.openjava.olap.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="gateway")
@Getter
@Setter
public class GateWayConfig {
    public String address;
}

