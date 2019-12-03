package com.openjava.olap.vo;

import com.openjava.olap.service.OlapCubeServiceImpl;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>第一步查询当前用户所关联的共享的模型列表</p>
 * <p>第二步，返回的列表，循环查询麒麟接口，返回具体的模型信息</p>
 * @see OlapCubeServiceImpl getOlapShareByShareUserId方法
 * @see com.openjava.olap.api.OlapModelingAction cubeList方法，dataType=0的时候是查询共享的模型
 * @author linchuangang
 * @create 2019-12-03 11:44
 **/
@Getter
@Setter
public class ShareCubeVo {

    private Long shareId;
    private String name;
    private Long createId;
}
