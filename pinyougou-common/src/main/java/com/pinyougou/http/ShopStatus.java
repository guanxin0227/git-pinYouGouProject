package com.pinyougou.http;
/**
 *  @author: guanx
 *  @Date: 2020/2/7 17:02
 *  @Description: 状态
 */
public class ShopStatus {

    /**
     *  @author: guanx
     *  @Date: 2020/2/7 17:02
     *  @Description: 商家申请入驻状态  状态值：  0：未审核   1：已审核   2：审核未通过   3：关闭
     */

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
     *  @Description: 以下是否启动规格配置  状态值：  0：不启用   1：启用
     */
    /** 启用 */
    public static final String ENABLE = "1";

    /** 不启用 */
    public static final String DISENABLE = "0";

    /**
     *  @author: guanx
     *  @Date: 2020/2/10 11:59
     *  @Description: 以下是否默认的商品  状态值：  0：不默认   1：默认
     */
    /** 默认 */
    public static final String DEFAULT = "1";

    /** 不默认 */
    public static final String UNDEFAULT = "0";

    /**
     *  @author: guanx
     *  @Date: 2020/2/10 11:59
     *  @Description: 以下是否被删除的商家  状态值：  0：未删除   1：删除
     */
    /** 默认 */
    public static final String ISNOTDELETE = "0";

    /** 不默认 */
    public static final String ISDELETE = "1";
}
