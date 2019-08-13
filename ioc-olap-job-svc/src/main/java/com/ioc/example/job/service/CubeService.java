package com.ioc.example.job.service;

import com.openjava.platform.mapper.kylin.CubeMapper;

import java.util.ArrayList;
import java.util.Date;

public interface CubeService {
    void build(String cubeName, Date start, Date end) throws Exception;

    ArrayList<CubeMapper> list(Integer limit, Integer offset) ;
}
