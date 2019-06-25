package com.openjava.platform.common;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseMapper {
    public int code;
    public String info;
    public Object data;
}
