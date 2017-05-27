package cn.iseee.dao;

import cn.iseee.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by baiyang on 2017/5/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKillDaoTest {

    @Resource
    private SuccessKillDao successKillDao;
    @Test
    public void insertSuccessKilled() throws Exception {
        /**
         * INSERT ignore INTO success_killed(seckill_id, user_phone)
         * VALUES (?, ?)
         Parameters: 1000(Long), 18732189889(Long)
         Updates: 1
         */
        long id = 1001L;
        long phone = 18732189889L;
        int insertCount = successKillDao.insertSuccessKilled(id, phone);
        System.out.println("insertCount=" + insertCount);
    }

    @Test
    public void queryByIdWithSeckill() throws Exception {

        /**
         SuccessKilled{
         seckillId=1000,
         userPhone=18732189889,
         state=-1,
         createTime=Sat May 27 02:04:39 CST 2017}
         Seckill{
         seckillId=1000,
         name='1000元秒杀iphone6',
         number=100,
         startTime=Sun Nov 01 00:00:00 CST 2015,
         endTime=Mon Nov 02 00:00:00 CST 2015,
         cteateTime=null}
         */
        long id = 1001L;
        long phone = 18732189889L;
        SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(id, phone);
        System.out.println(successKilled);
        System.out.println(successKilled.getSeckill());
    }

}