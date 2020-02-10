package com.pinyougou.http;
/**
 *  @author: guanx
 *  @Date: 2020/2/7 17:02
 *  @Description: 商家申请入驻状态  状态值：  0：未审核   1：已审核   2：审核未通过   3：关闭
 */
public class ShopStatus {

    /** 未审核 */
    public static final String NO_EXAMINE = "0";

    /** 已审核 */
    public static final String YES_EXAMINE = "1";

    /** 审核未通过 */
    public static final String NO_PASS_EXAMINE = "2";

    /** 关闭 */
    public static final String CLOSE_EXAMINE = "3";

    /**
     *  @author: guanx
     *  @Date: 2020/2/10 11:59
     *  @Description: 以下是否启动规格配置
     */
    /** 启用 */
    public static final String ENABLE = "1";

    /** 不启用 */
    public static final String DISENABLE = "0";

    /**
     *  @author: guanx
     *  @Date: 2020/2/10 11:59
     *  @Description: 以下是否默认的商品
     */
    /** 默认 */
    public static final String DEFAULT = "1";

    /** 不默认 */
    public static final String UNDEFAULT = "0";
}
