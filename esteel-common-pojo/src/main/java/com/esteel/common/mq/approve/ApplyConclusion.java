package com.esteel.common.mq.approve;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author liujie
 * @version 1.0.0
 * @ClassName ApplyConclusion.java
 * @Description TODO
 * @createTime 2019年12月13日 11:14
 */
@Data
public class ApplyConclusion implements Serializable {

    /**单号*/
    private String applyNum ;

    /**订单状态 0：通过 1：拒绝 2：回退*/
    private Integer type ;

    /**0:初审结论，1：终审结论 */
    private Integer conclusionType ;

    /** 原因吗1*/
    private String code1 ;

    /** 原因吗2*/
    private String code2 ;

    /** 原因吗3*/
    private String code3 ;

    /**备注*/
    private String remark ;

    /**审核时间*/
    private Date conclusionTime ;
}
