package com.openjava.platform.vo;

import com.openjava.platform.dto.ShareUserDto;
import com.openjava.platform.mapper.kylin.QueryResultMapper;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class QueryResultMapperVo extends QueryResultMapper {
    private List<ShareUserDto> shareList;
}

