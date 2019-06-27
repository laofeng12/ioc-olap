package com.openjava.platform.mapper.kylin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RowkeyColumnMapper {
    public String column;
    public String encoding;
    public String isShardBy;
    public String encoding_version;
}
