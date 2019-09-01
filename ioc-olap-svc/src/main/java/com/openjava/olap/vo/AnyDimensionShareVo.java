package com.openjava.olap.vo;

import com.openjava.olap.dto.ShareUserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnyDimensionShareVo extends AnyDimensionVo {
    private List<ShareUserDto> shareList;
}
