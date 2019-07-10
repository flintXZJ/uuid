package com.xzj.stu.uuid.dao;

import com.xzj.stu.uuid.pojo.UuidPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author zhijunxie
 * @since 2019-07-08
 */
@Mapper
public interface UuidMapper {
    int insert(UuidPO uuid);
}
