package com.openjava.platform.api;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "共享接口")
@RestController
@RequestMapping("/olap/apis/olapShare")
public class OlapShareAction {

}
