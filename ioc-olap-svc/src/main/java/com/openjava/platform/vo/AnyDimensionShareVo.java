package com.openjava.platform.vo;

import com.openjava.platform.dto.ShareUserDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AnyDimensionShareVo extends AnyDimensionVo {
    private List<ShareUserDto> shareList;
}
