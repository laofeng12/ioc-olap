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
            if (s.getTableList() != null) {
                s.getTableList().stream().filter(y -> y.getTable_name().equalsIgnoreCase(virtualTableName))
                    .forEach(x -> x.setTable_name(tableName));
            }
        });
        String factTable = body.getModels().getModelDescData().getFact_table();
        if (factTable != null) {
            String[] str = factTable.split("\\.");
            if (str[1].equalsIgnoreCase(virtualTableName)) {
                body.getModels().getModelDescData().setFact_table(str[0] + "." + tableName);
            }
        }
        body.getModels().getModelDescData().getLookups().forEach(s->{
            if (s.getAlias() != null && s.getAlias().equalsIgnoreCase(virtualTableName)){
                s.setAlias(tableName);
            }
            if (s.getJoinAlias()!=null &&s.getJoinAlias().equalsIgnoreCase(virtualTableName)){
                s.setJoinAlias(tableName);
            }
            if (s.getJoinTable()!=null && s.getJoinTable().equalsIgnoreCase(virtualTableName)){
                s.setJoinTable(tableName);
            }
            if (s.getTable() != null) {
                String[] srs = s.getTable().split("\\.");
                if (srs[1].equalsIgnoreCase(virtualTableName)) {
                    s.setTable(srs[0] + "." + tableName);
                }
            }
            if (s.getJoin().getPrimary_key() != null) {
                List<String> pks = Arrays.asList(s.getJoin().getPrimary_key());
                for (int x = 0; x < pks.size(); x++) {
                    String[] sr = pks.get(x).split("\\.");
                    if (sr[0].equalsIgnoreCase(virtualTableName)) {
                        pks.set(x, tableName + "." + sr[1]);
                    }
                }
            }
            if (s.getJoin().getForeign_key() != null) {
                List<String> fks = Arrays.asList(s.getJoin().getForeign_key());
                for (int x = 0; x < fks.size(); x++) {
                    String[] sr = fks.get(x).split("\\.");
                    if (sr[0].equalsIgnoreCase(virtualTableName)) {
                        fks.set(x, tableName + "." + sr[1]);
                    }
                }
            }

        });

        if (body.getModels().getModelDescData().getPartition_desc() != null) {
            String s = body.getModels().getModelDescData().getPartition_desc().getPartition_date_column();
            if (s != null){
                String[]sr = s.split("\\.");
                if (sr.length == 2){
                    if (sr[0].equalsIgnoreCase(virtualTableName)) {
                        body.getModels().getModelDescData().getPartition_desc().setPartition_date_column( tableName + "." + sr[1]);
                    }
                }
            }
        }
        body.getCube().getCubeDescData().getAggregation_groups().forEach(s->{
            if (s.getIncludes() != null) {
                for (int x = 0; x < s.getIncludes().size(); x++) {
                    String[] sr = s.getIncludes().get(x).split("\\.");
                    if (sr[0].equalsIgnoreCase(virtualTableName)) {
                        s.getIncludes().set(x, tableName + "." + sr[1]);
                    }
                }
            }
        });
        body.getCube().getCubeDescData().getDimensions().forEach(s->{
            if (s.getTable() != null && s.getTable().equalsIgnoreCase(virtualTableName)){
                s.setTable(tableName);
            }
            if (s.getId() != null) {
                String[] sr = s.getId().split("\\.");
                if (sr[0].equalsIgnoreCase(virtualTableName)) {
                    s.setId(tableName + "." + sr[1]);
                }
            }
        });
        body.getCube().getCubeDescData().getAggregation_groups().forEach(s->{
            if (s.getSelect_rule().getMandatory_dims() != null){
                List<String> mandatory = s.getSelect_rule().getMandatory_dims();
                for (int i = 0;i<mandatory.size();i++){
                    String[] sr = mandatory.get(i).split("\\.");
                    if (sr[0].equalsIgnoreCase(virtualTableName)) {
                        mandatory.set(i,tableName+"."+sr[1]);
                    }
                }
            }

            if (s.getSelect_rule().getJoint_dims() != null){
                s.getSelect_rule().getJoint_dims().forEach(x->{
                    if (x != null) {
                        for (int i = 0;i<x.size();i++){
                            String[] sr = x.get(i).split("\\.");
                            if (sr[0].equalsIgnoreCase(virtualTableName)) {
                                x.set(i,tableName+"."+sr[1]);
                            }
                        }
                    }
                });
            }

            if (s.getSelect_rule().getHierarchy_dims() != null){
                s.getSelect_rule().getHierarchy_dims().forEach(x->{
                    if (x != null) {
                        for (int i = 0;i<x.size();i++){
                            String[] sr = x.get(i).split("\\.");
                            if (sr[0].equalsIgnoreCase(virtualTableName)) {
                                x.set(i,tableName+"."+sr[1]);
                            }
                        }
                    }
                });
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
                String[]sr = m.getFact_table().split("\\.");
                if (sr.length==2 && sr[1].equalsIgnoreCase(tableName)){
                    m.setFact_table(sr[0]+"."+virtualTableName);
                }
                m.getLookups().forEach(s->{
                    String[]rs = s.getTable().split("\\.");
                    if (rs.length==2 && rs[1].equalsIgnoreCase(tableName)){
                        s.setTable(rs[0]+"."+virtualTableName);
                    }
                    if (s.getAlias().equalsIgnoreCase(tableName)){
                        s.setAlias(virtualTableName);
                    }
                    if (s.getJoinTable().equalsIgnoreCase(tableName)){
                        s.setJoinTable(virtualTableName);
                    }
                    if (s.getJoinAlias().equalsIgnoreCase(tableName)){
                        s.setJoinAlias(virtualTableName);
                    }
                    List<String> pks = Arrays.asList(s.getJoin().getPrimary_key());
                    for (int x=0;x<pks.size();x++) {
                        String[]sr4 = pks.get(x).split("\\.");
                        if (sr4[0].equalsIgnoreCase(tableName)) {
                            pks.set(x,virtualTableName+"."+sr4[1]);
                        }
                    }
                    List<String> fks = Arrays.asList(s.getJoin().getForeign_key());
                    for (int x=0;x<fks.size();x++) {
                        String[]sr5 = fks.get(x).split("\\.");
                        if (sr5[0].equalsIgnoreCase(tableName)) {
                            fks.set(x,virtualTableName+"."+sr5[1]);
                        }
                    }
                });
                m.getDimensions().forEach(s->{
                    if (s.getTable().equalsIgnoreCase(tableName)){
                        s.setTable(virtualTableName);
                    }
                });
            }else if (entry.getKey().equalsIgnoreCase("CubeList")){
                CubeDescDataMapper c = (CubeDescDataMapper) entry.getValue();
                c.getDimensions().forEach(s->{
                    if (s.getTable().equalsIgnoreCase(tableName)){
                        s.setTable(virtualTableName);
                    }
                    String[]sr = s.getId().split("\\.");
                    if (sr.length>0&&sr[0].equalsIgnoreCase(tableName)){
                        s.setId(virtualTableName+"."+sr[1]);
                    }
                });
                c.getMeasures().forEach(s->{
                    String[]sr = s.getFunction().getParameter().getValue().split("\\.");
                    if (sr.length>0 && sr[0].equalsIgnoreCase(tableName)){
                        s.getFunction().getParameter().setValue(virtualTableName+"."+sr[1]);
                    }
                });
                c.getRowkey().getRowkey_columns().forEach(s->{
                    String[]sr = s.getColumn().split("\\.");
                    if (sr.length>0 && sr[0].equalsIgnoreCase(tableName)){
                        s.setColumn(virtualTableName+"."+sr[1]);
                    }
                });
                c.getAggregation_groups().forEach(s->{
                    for (int x = 0;x<s.getIncludes().size();x++) {
                        String[] sr =  s.getIncludes().get(x).split("\\.");
                        if (sr.length > 0 && sr[0].equalsIgnoreCase(tableName)) {
                            s.getIncludes().set(x,virtualTableName + "." + sr[1]);
                        }
                    }
                });
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
