package com.bjtl;


import com.bjtl.service.LogService;
import com.bjtl.service.impl.LogServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class LogManagementApplicationTests {

    @Autowired
    private LogService logService;

    @Test
    public void contextLoads() {
        //logService.getDataByCeShi("log_log");
    }

}
