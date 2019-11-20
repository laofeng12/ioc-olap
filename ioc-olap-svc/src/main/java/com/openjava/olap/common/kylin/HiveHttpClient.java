package com.openjava.olap.common.kylin;

import com.alibaba.fastjson.JSON;
import com.openjava.olap.common.HttpClient;
import com.openjava.olap.mapper.kylin.TableStructureMapper;
import org.apache.commons.lang3.StringUtils;
import org.ljdp.component.exception.APIException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
@Component
public class HiveHttpClient extends KylinHttpClient {
    public String[] list(String LibraryName) throws APIException {
        String url = config.address + "/kylin/api/tables/hive/" + LibraryName;
        String[] result = HttpClient.get2(url, config.authorization, String[].class);
        return result;
    }

    public void create(List<String> tableNameList, String cubeName) throws APIException {
        String TableName = StringUtils.join(tableNameList, ",") + ",";
        String url = config.address + "/kylin/api/tables/" + TableName + "/" + cubeName;
        HashMap hash = new HashMap();
        hash.put("calculate", true);
        HttpClient.post2(url, JSON.toJSONString(hash), config.authorization, String.class);
    }

    public TableStructureMapper getTableMeta(String project,String database,String tableName)throws APIException{
        String url = config.address + "/kylin/api/tables/" + project + "/" + database+"."+tableName;
        return HttpClient.get(url, config.authorization, TableStructureMapper.class);
    }

}
