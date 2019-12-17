package com.openjava.olap.mapper.kylin;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.ljdp.component.exception.APIException;

import java.util.ArrayList;
import java.util.HashMap;

@Getter
@Setter
@ApiModel("立方体详细消息体")
public class CubeDescDataMapper {

    public String uuid;
    public String last_modified;
    public String version;
    public boolean is_draft;
    @ApiModelProperty("立方体名称")
    public String name;
    @ApiModelProperty("模型名称")
    public String model_name;
    @ApiModelProperty("模型描述")
    public String description;
    @ApiModelProperty("维度列表")
    public ArrayList<DimensionMapper> dimensions;
    @ApiModelProperty("度量列表")
    public ArrayList<MeasureMapper> measures;
    @ApiModelProperty("立方体字典列表")
    public ArrayList<CubeDictionaryMapper> dictionaries;
    @ApiModelProperty("列索引")
    public RowkeyMapper rowkey;
    @ApiModelProperty("聚合消息体")
    public ArrayList<AggregationGroupMapper> aggregation_groups;
    public ArrayList mandatory_dimension_set_list;
    @ApiModelProperty("增量构建的开始时间")
    public Long partition_date_start;
    public ArrayList<String> notify_list;
    public HbaseMappingMapper hbase_mapping;
    public String volatile_range;
    public String retention_range;
    public ArrayList<String> status_need_notify;
    public ArrayList<Long> auto_merge_time_ranges;
    public Integer engine_type;
    public String storage_type;
    public String columns_Type;
    public HashMap override_kylin_properties;

    public CubeDescDataMapper(){
        override_kylin_properties=new HashMap();
        override_kylin_properties.put("kylin.cube.aggrgroup.is-mandatory-only-valid","true");
        override_kylin_properties.put("kylin.cube.aggrgroup.max-combination",Integer.MAX_VALUE);
    }

    /**
     * 麒麟返回encoding时，拆分里面的类型和长度
     */
    public void separateEncodingAndLength(){
        if (this.rowkey != null && this.rowkey.getRowkey_columns() != null) {
            for (RowkeyColumnMapper s : this.rowkey.getRowkey_columns()) {
                String[]sr = s.getEncoding().split(":");
                if (sr.length == 2){
                    s.setEncoding(sr[0]);
                    s.setLengths(sr[1]);
                }
            }
        }
    }

    /**
     * 创建模型时，校验encoding和length，并重新设置encoding的格式为type:length
     * @throws APIException
     */
    public void validateRowKeyEncoding()throws APIException{
        if (this.rowkey != null && this.rowkey.getRowkey_columns() != null){
            for (RowkeyColumnMapper s : this.rowkey.getRowkey_columns()) {
                String type = s.getEncoding();
                if (EncodingType.INTEGER.getType().equalsIgnoreCase(type)) {
                    if (s.getLengths() == null || "".equals(s.getLengths())){
                        throw new APIException(400,"integer长度不能为空");
                    }
                    Integer length = Integer.parseInt(s.getLengths());
                    if (EncodingType.INTEGER.min > length || EncodingType.INTEGER.max < length) {
                        throw new APIException(400,String.format("integer长度必须在%s~%s",EncodingType.INTEGER.min,EncodingType.INTEGER.max));
                    }
                }else if (EncodingType.BOOLEAN.getType().equalsIgnoreCase(type)){
                    if (s.getLengths() == null || "".equals(s.getLengths())){
                        throw new APIException(400,"boolean长度不能为空");
                    }
                    Integer length = Integer.parseInt(s.getLengths());
                    if (EncodingType.BOOLEAN.min > length || EncodingType.BOOLEAN.max < length) {
                        throw new APIException(400,String.format("boolean长度必须在%s~%s",EncodingType.BOOLEAN.min,EncodingType.BOOLEAN.max));
                    }
                }else if (EncodingType.DATE.getType().equalsIgnoreCase(type)){
                    if (s.getLengths() == null || "".equals(s.getLengths())){
                        throw new APIException(400,"date长度不能为空");
                    }
                    Integer length = Integer.parseInt(s.getLengths());
                    if (EncodingType.DATE.min > length || EncodingType.DATE.max < length) {
                        throw new APIException(400,String.format("date长度必须在%s~%s",EncodingType.DATE.min,EncodingType.DATE.max));
                    }
                }
                else if (EncodingType.TIME.getType().equalsIgnoreCase(type)){
                    if (s.getLengths() == null || "".equals(s.getLengths())){
                        throw new APIException(400,"time长度不能为空");
                    }
                    Integer length = Integer.parseInt(s.getLengths());
                    if (EncodingType.TIME.min > length || EncodingType.TIME.max < length) {
                        throw new APIException(400,String.format("time长度必须在%s~%s",EncodingType.TIME.min,EncodingType.TIME.max));
                    }
                }
                s.setEncoding(s.getEncoding().concat(":").concat(s.getLengths()));
            }
        }
    }

    public enum EncodingType{

        INTEGER(1,"INTEGER",8),BOOLEAN(0,"BOOLEAN",0),DATE(0,"DATE",0),TIME(0,"TIME",0);

        private Integer min;
        private String type;
        private Integer max;

        EncodingType(Integer min, String type, Integer max) {
            this.min = min;
            this.type = type;
            this.max = max;
        }

        public Integer getMin() {
            return min;
        }

        public void setMin(Integer min) {
            this.min = min;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getMax() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
        }
    }
}
