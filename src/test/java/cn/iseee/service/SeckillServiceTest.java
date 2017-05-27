package cn.iseee.service;

import cn.iseee.dto.Exposer;
import cn.iseee.dto.SeckillExecution;
import cn.iseee.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by baiyang on 2017/5/27.
 */

@RunWith(SpringJUnit4ClassRunner.class)
//告诉junit spring的配置文件
@ContextConfiguration({"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;
    @Test
    public void getSeckillList() throws Exception {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
    }

    @Test
    public void getById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id= 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("export={}",exposer);
    }

    @Test
    public void executeSeckill() throws Exception {
        long id = 1002;
        long phone = 13599000990L;
        String md5 = "7571d7f9dcd3ad0bbfa9905092753198";

        SeckillExecution execution= seckillService.executeSeckill(id, phone, md5);
        logger.info("result={}",execution);
    }

}