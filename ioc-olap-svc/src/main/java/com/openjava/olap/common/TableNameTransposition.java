package com.openjava.olap.common;

import com.openjava.olap.domain.OlapDatalaketable;
import com.openjava.olap.domain.OlapFilterCondidion;
import com.openjava.olap.mapper.kylin.*;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    public static void replaceAll(ModelingMapper body,String tableName,String virtualTableName){
        body.getCubeDatalaketableNew().forEach(s->{
            s.getTableList().stream().filter(y->y.getTable_name().equalsIgnoreCase(virtualTableName))
                .forEach(x->x.setTable_name(tableName));
        });
        String factTable = body.getModels().getModelDescData().getFact_table();
        String[] str = factTable.split("\\.");
        if (str[1].equalsIgnoreCase(virtualTableName)){
            body.getModels().getModelDescData().setFact_table(str[0]+"."+tableName);
        }
        body.getModels().getModelDescData().getLookups().forEach(s->{
            if (s.getAlias().equalsIgnoreCase(virtualTableName)){
                s.setAlias(tableName);
            }
            if (s.getJoinAlias().equalsIgnoreCase(virtualTableName)){
                s.setJoinAlias(tableName);
            }
            if (s.getJoinTable().equalsIgnoreCase(virtualTableName)){
                s.setJoinTable(tableName);
            }
            String[]srs = s.getTable().split("\\.");
            if (srs[1].equalsIgnoreCase(virtualTableName)){
                s.setTable(srs[0]+"."+tableName);
            }
            List<String> pks = Arrays.asList(s.getJoin().getPrimary_key());
            for (int x=0;x<pks.size();x++) {
                String[]sr = pks.get(x).split("\\.");
                if (sr[0].equalsIgnoreCase(virtualTableName)) {
                    pks.set(x,tableName+"."+sr[1]);
                }
            }
            List<String> fks = Arrays.asList(s.getJoin().getForeign_key());
            for (int x=0;x<fks.size();x++) {
                String[]sr = fks.get(x).split("\\.");
                if (sr[0].equalsIgnoreCase(virtualTableName)) {
                    fks.set(x,tableName+"."+sr[1]);
                }
            }

        });

        body.getCube().getCubeDescData().getAggregation_groups().forEach(s->{
            for (int x =0;x<s.getIncludes().size();x++) {
                String[] sr = s.getIncludes().get(x).split("\\.");
                if (sr[0].equalsIgnoreCase(virtualTableName)) {
                    s.getIncludes().set(x,tableName+"."+sr[1]);
                }
            }
        });
        body.getCube().getCubeDescData().getDimensions().forEach(s->{
            if (s.getTable().equalsIgnoreCase(virtualTableName)){
                s.setTable(tableName);
            }
            String[]sr = s.getId().split("\\.");
            if (sr[0].equalsIgnoreCase(virtualTableName)){
                s.setId(tableName+"."+sr[1]);
            }
        });

        body.getCube().getCubeDescData().getMeasures().forEach(s->{
            String[]sr = s.getFunction().getParameter().getValue().split("\\.");
            if(sr[0].equalsIgnoreCase(virtualTableName)){
                s.getFunction().getParameter().setValue(tableName+"."+sr[1]);
            }
        });
        body.getCube().getCubeDescData().getRowkey().getRowkey_columns().forEach(s->{
            String[]sr = s.getColumn().split("\\.");
            if(sr[0].equalsIgnoreCase(virtualTableName)){
                s.setColumn(tableName+"."+sr[1]);
            }
        });
    }


    /**
     * 查看模型详情时虚拟真实替换回去
     * @param map
     * @param tableName
     * @param virtualTableName
     */
    public static void replaceAll(Map<String,Object> map, String tableName, String virtualTableName){
        for (Map.Entry<String,Object> entry : map.entrySet()){
            if (entry.getKey().equalsIgnoreCase("ModesList")){
                ModelsDescDataMapper m = (ModelsDescDataMapper) entry.getValue();
            }else if (entry.getKey().equalsIgnoreCase("CubeList")){
                CubeDescDataMapper c = (CubeDescDataMapper) entry.getValue();
            }else if (entry.getKey().equalsIgnoreCase("TableList")){
                List<CubeDatalaketableNewMapper> list = (List<CubeDatalaketableNewMapper>) entry.getValue();
                list.forEach(s->{
                    s.getTableList().forEach(x->{
                        if (x.getTable_name().equalsIgnoreCase(tableName)){
                            x.setTable_name(virtualTableName);
                        }
                    });
                });
            }else if (entry.getKey().equalsIgnoreCase("filterCondidion")){
                List<OlapFilterCondidion> list = (List<OlapFilterCondidion>) entry.getValue();
            }else if (entry.getKey().equalsIgnoreCase("graphData")){
                String graphData = (String) entry.getValue();
            }
        }
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
