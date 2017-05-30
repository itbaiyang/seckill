package cn.iseee.dao;

import cn.iseee.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 *
 * Created by baiyang on 2017/5/27.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Resource
    private SeckillDao seckillDao;

    @Test
    public void testInsertSeckill() throws Exception {
        Seckill seckill = new Seckill();
        seckill.setName("白杨");
        seckill.setNumber(100);
        seckill.setStartTime(new Date());
        seckill.setEndTime(new Date());
        seckill.setCreateTime(new Date());
        System.out.println(seckill);
        seckillDao.insertSeckill(seckill);
        System.out.println(seckill.getSeckillId());
    }

    @Test
    public void testDeleteSeckill() throws Exception {
        long id = 1007;
        int deleteCount = seckillDao.deleteSeckill(id);
        System.out.println(deleteCount);
    }

    @Test
    public void testUpdateSeckill() throws Exception {
        Seckill seckill = new Seckill();
        seckill.setSeckillId(1010);
        seckill.setName("修改");
        seckill.setNumber(1000);
        seckill.setStartTime(new Date());
        seckill.setEndTime(new Date());
        seckill.setCreateTime(new Date());
        System.out.println(seckill);
        seckillDao.updateSeckill(seckill);
        System.out.println(seckill.getSeckillId());
    }

    @Test
    public void testQueryById() throws Exception {
        long id = 1000;
        Seckill seckill = seckillDao.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void testQueryAll() throws Exception {

        List<Seckill> seckills = seckillDao.queryAll(0,100);
        for (Seckill seckill : seckills) {
            System.out.println(seckill);
        }
    }

    @Test
    public void testReduceNumber() throws Exception {
        /**
         UPDATE seckill SET number = number -1
         WHERE seckill_id = ?
         and start_time <= ?
         and end_time >= ?
         and number > 0;
         Parameters: 1000(Long), 2017-05-27 01:57:01.938(Timestamp),
         Updates: 0
         */
        Date killTime = new Date();
        int updateCount = seckillDao.reduceNumber(1000L, killTime);
        System.out.println(updateCount);
    }

}