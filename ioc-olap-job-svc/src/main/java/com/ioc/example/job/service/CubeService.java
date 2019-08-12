package com.ioc.example.job.service;

import java.util.Date;

public interface CubeService {
    void build(String cubeName, Date start, Date end) throws Exception;
}
