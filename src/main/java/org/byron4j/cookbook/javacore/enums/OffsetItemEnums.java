package org.byron4j.cookbook.javacore.enums;

public enum OffsetItemEnums {

    /**
     * 手续费
     */
    offsetitem_fee("fee", "手续费"),
    /**
     * 滞纳金
     */
    offsetitem_penalty("penalty", "滞纳金"),
    /**
     * 利息
     */
    offsetitem_int("int", "利息"),
    /**
     * 本金
     */
    offsetitem_principal("principal", "本金"),


    ;

    private String code;
    private String desc;

    private OffsetItemEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static OffsetItemEnums acquireOffsetItemByCode(String code) throws Exception{
        for( OffsetItemEnums ele : OffsetItemEnums.values() ){
            if( ele.code.equalsIgnoreCase(code) ){
                return ele;
            }
        }
        throw new Exception("Error code:" + code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
