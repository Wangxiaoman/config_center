package cc.constants;


/**
 * 
* @ClassName: CommonStatus 
* @Description: 接口状态描述
* @author xiaoman 
* @date 2015年5月28日 下午11:17:39 
*
 */
public enum CommonStatus{

    UNKNOWN(-1, ""),

    SUCCESS(200,"成功"),
    NO_AUTH(300,"权限不足，请检查签名"),
    PARAM_ERROR(400,"参数异常,请检查"),
    SERVER_ERROR(500,"服务器内部错误"),
    
    TOCKEN_NOT_EXIST(40010,"用户未登录"),
    USER_TOKEN_EXPIRED(40011,"用户token过期"),
    ;
    private int    value;
    private String text; 

    private static final KV<Integer, CommonStatus> lookUp = new KV<Integer, CommonStatus>();

    static {
        for (CommonStatus status : CommonStatus.values()) {
            lookUp.put(status.getValue(), status);
        }
        lookUp.putDefault(UNKNOWN);
    }

    private CommonStatus(int value, String text) {
        this.value = value;
        this.text = text;
    }

    public int getValue() {
        return this.value;
    }

    public String getText() {
        return this.text;
    }
    
    public static CommonStatus of(Integer value) {
        return lookUp.get(value);
    }

}
