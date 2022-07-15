package com.lee.pay.utils.crud.mapper;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = com.lee.pay.PayApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
class RawSqlMapperTest {

    @Autowired
    private  RawSqlMapper rawSqlMapper;

    @Test
    void rawSelect() {
        List<Map<String, Object>> rawSelect = rawSqlMapper.rawSelect("select * from test_order");

        System.out.println(rawSelect);
    }
}