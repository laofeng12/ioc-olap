package com.openjava.olap.query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

/**
 * @author linchuangang
 * @create 2019-10-28 17:34
 **/
@Data
@Builder
public class DataLakeJobQueryParam {

    private String databaseId;
    private String resourceId;
    private String type;
    private String syncSource;
    @JsonIgnore
    private String cubeName;
    @JsonIgnore
    private Integer syncStatus;
}
