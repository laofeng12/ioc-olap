package com.openjava.olap.common;

public enum CubeFlags {
    /**禁用**/
    DISABLED(0,"禁用"),
    /**启用**/
    ENABLED(1,"启用"),
    /**就绪**/
    READY(2,"就绪"),
    /**数据同步中**/
    ON_SYNC(3,"数据同步中"),
    /**同步失败**/
    SYNC_FAILED(4,"同步失败"),
    /**构建中**/
    BUILDING(5,"构建中"),
    /**构建失败**/
    BUILD_FAILED(6,"构建失败")
    ;

    private Integer flags;
    private String value;

    CubeFlags(Integer flags, String value) {
        this.flags = flags;
        this.value = value;
    }
    public static String getByFlags(Integer flags){
        String str = "未知";
        if (flags == null){
            return str;
        }
        switch (flags){
            case 0:
                str = DISABLED.value;
                break;
            case 1:
                str = ENABLED.value;
                break;
            case 2:
                str = READY.value;
                break;
            case 3:
                str = ON_SYNC.value;
                break;
            case 4:
                str = SYNC_FAILED.value;
                break;
            case 5:
                str = BUILDING.value;
                break;
            case 6:
                str = BUILD_FAILED.value;
                break;
        }
        return str;
    }

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
