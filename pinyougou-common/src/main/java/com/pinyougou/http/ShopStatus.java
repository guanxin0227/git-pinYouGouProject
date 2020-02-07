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

}
