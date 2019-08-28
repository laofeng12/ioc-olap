package com.ioc.olap.job.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public interface CubeService {
    void build(String cubeName, Date start, Date end) throws Exception;

    ArrayList<HashMap> list(Integer limit, Integer offset) ;
}
