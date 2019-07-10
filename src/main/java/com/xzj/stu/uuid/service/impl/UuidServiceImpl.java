package com.xzj.stu.uuid.service.impl;

import com.xzj.stu.uuid.dao.UuidMapper;
import com.xzj.stu.uuid.pojo.UuidPO;
import com.xzj.stu.uuid.service.UuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author zhijunxie
 * @since 2019-07-08
 */
@Service
public class UuidServiceImpl implements UuidService {

    @Resource
    private UuidMapper uuidMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long insert(UuidPO uuidPO) {
        uuidMapper.insert(uuidPO);
        return uuidPO.getId();
    }
}
