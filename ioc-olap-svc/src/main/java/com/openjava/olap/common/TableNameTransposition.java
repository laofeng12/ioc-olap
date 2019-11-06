package com.openjava.olap.common;

import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.mapper.kylin.TableNameRelationMapper;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author linchuangang
 * @create 2019-10-31 16:15
 **/
public class TableNameTransposition {

    /**
     * 暂时使用全局替换，如果出现替换了不该替换的数据，再考虑其他方法处理
     * @param json
     * @param reg
     * @param replace
     * @return 替换后的json字符串
     */

    public static String replaceAll(@NonNull String json, @NonNull String reg, @NonNull String replace){
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(json);
        StringBuffer s = new StringBuffer();
        while (matcher.find()){
            matcher.appendReplacement(s,replace);
        }
        matcher.appendTail(s);
        return s.toString();
    }

    /**
     * 提取虚拟、真实表名的关系集合
     * @param table
     * @return
     */
    public static List<TableNameRelationMapper> extract(List<OlapDatalaketable> table){
        List<TableNameRelationMapper> list = new ArrayList<>();
        table.forEach(s->{
            TableNameRelationMapper mapper = new TableNameRelationMapper();
            mapper.setResourceId(s.getResourceId());
            mapper.setTableName(s.getTable_name());
            mapper.setVirtualTableName(s.getVirtualTableName());
            list.add(mapper);
        });
        return list;
    }

    public static void main(String ...args){
        TableNameTransposition tt = new TableNameTransposition();
        String json = "{\"tableName\":\"KYLIN_ACCOUNT\",\"desc\":[{\"table\":\"KYLIN_ACCOUNT\",\"id\":\"default.KYLIN_ACCOUNT\"},{\"table\":\"KYLIN_ACCOUNTS\",\"id\":\"default.KYLIN_ACCOUNTS\"}]}";
        String replace = "123";
        String reg = "KYLIN_ACCOUNT";
        json = tt.replaceAll(json,reg,replace);
        System.out.println(json);
    }

}
