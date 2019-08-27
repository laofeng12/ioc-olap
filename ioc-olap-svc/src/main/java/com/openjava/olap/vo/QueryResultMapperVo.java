package com.openjava.olap.vo;

import com.openjava.olap.dto.ShareUserDto;
import com.openjava.olap.mapper.kylin.QueryResultMapper;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class QueryResultMapperVo extends QueryResultMapper {
    private List<ShareUserDto> shareList;
}

