package com.ioc.example.job.service;

import com.openjava.platform.domain.OlapTimingrefresh;

import java.util.Date;
import java.util.List;

public interface CubeService {
    void build(String cubeName, Date start, Date end) throws Exception;

    List<OlapTimingrefresh> findTableInfo(int frequencyType) throws Exception;

    void doSave(OlapTimingrefresh ot) throws Exception;
}
