package cn.iseee.service;

import cn.iseee.dto.Exposer;
import cn.iseee.dto.SeckillExecution;
import cn.iseee.entity.Seckill;
import cn.iseee.exception.RepeatKillException;
import cn.iseee.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
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
    public void insertOneSeckill() throws Exception {
        Seckill seckill = new Seckill();
        seckill.setName("白杨");
        seckill.setNumber(100);
        seckill.setStartTime(new Date());
        seckill.setEndTime(new Date());
        seckill.setCreateTime(new Date());
        seckillService.insertOneSeckill(seckill);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void deleteOneSeckill() throws Exception {
        long id = 1006;
        int count = seckillService.deleteOneSeckill(id);
        logger.info("count={}", count);
    }

    @Test
    public void updateOneSeckill() throws Exception {
        Seckill seckill = new Seckill();
        long id = 1006;
        seckill.setSeckillId(id);
        seckill.setName("二姣");
        seckill.setNumber(500);
        seckill.setStartTime(new Date());
        seckill.setEndTime(new Date());
        seckill.setCreateTime(new Date());
        seckillService.updateOneSeckill(seckill);
        logger.info("seckill={}", seckill);
    }

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
    public void testSeckillLogic() throws Exception {
        long id= 1001;
        Exposer exposer = seckillService.exportSeckillUrl(id);

        if(exposer.isExposed()) {
            logger.info("export={}",exposer);
            long phone = 13599000999L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution= seckillService.executeSeckill(id, phone, md5);
                logger.info("result={}",execution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }

        }else {
            logger.warn("exposer={}", exposer);
        }
    }

    @Test
    public void executeSeckill() throws Exception {




        /*
        result=SeckillExecution{
        seckillId=1000,
         state=1,
          stateInfo='秒杀成功',
           successKilled=SuccessKilled{
           seckillId=1000,
            userPhone=13599000999,
             state=0,
              createTime=Sun May 28 11:10:29 CST 2017}
        }
        */
    }

}