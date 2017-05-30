package cn.iseee.service.impl;

import cn.iseee.dao.SeckillDao;
import cn.iseee.dao.SuccessKillDao;
import cn.iseee.dao.cache.RedisDao;
import cn.iseee.entity.Seckill;
import cn.iseee.entity.SuccessKilled;
import cn.iseee.enums.SeckillStatEnum;
import cn.iseee.exception.RepeatKillException;
import cn.iseee.exception.SeckillCloseException;
import cn.iseee.exception.SeckillException;
import cn.iseee.service.SeckillService;
import cn.iseee.dto.Exposer;
import cn.iseee.dto.SeckillExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by popla on 2017/5/27.
 */
//@Component @Service @Dao @Controller

@Service
public class SeckillServiceImpl implements SeckillService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //注入service依赖
    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private SuccessKillDao successKillDao;

    @Autowired
    private RedisDao redisDao;

    private final String slat = "afdadfdasfafasfdsfsdfasdf7(&(&((&F(&*(f89asf";

    public Seckill insertOneSeckill(Seckill seckill) {
        seckillDao.insertSeckill(seckill);
        return seckill;
    }

    public int deleteOneSeckill(long seckillId) {
        int count = seckillDao.deleteSeckill(seckillId);
        return count;
    }

    public Seckill updateOneSeckill(Seckill seckill) {
        seckillDao.updateSeckill(seckill);
        return seckill;
    }

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 10);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        //优化点： redis缓存路由信息
        //访问redis
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                return new Exposer(false, seckillId);
            } else {
                //放入redis
                redisDao.putSeckill(seckill);
            }
        }

        //获取开始时间 结束时间 当前时间
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();

        //判断当前时间和秒杀开始时间
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {

            //不是秒杀期间
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(),
                    endTime.getTime());
        }

        //秒杀时间内执行
        //将IseckillId加密得到md5
        String md5 = getMD5(seckillId);
        logger.info(md5);

        //exposed标记为秒杀中
        boolean exposed = true;
        return new Exposer(exposed, md5, seckillId);
    }

    // md5 加密过程
    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional
    /**
     * 使用注解控制事务方法的有点：
     * 1：开发团队达成一致的约定，明确标注事务方法的编程风格；
     * 2：保证事务方法的执行时间尽可能短，不要穿插其他的网络操作， rpc/http请求/或者剥离到事务方法外；
     * 3：不是所有的方法都需要事务，如只有一条修改操作，只读操作不需要事务控制。
     * */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        //md5不存在或者不匹配 抛出重写异常
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            throw new SeckillException("seckill data rewrite");
        }
        //执行秒杀逻辑：减库存 记录购买行为
        //记录开始执行时间
        Date nowTime = new Date();
        try {

            //记录购买行为
            //成功秒杀列表添加一条秒杀记录
            int insertCount = successKillDao.insertSuccessKilled(seckillId, userPhone);

            //如果秒杀记录存在该条消息（phone 和 seckillId相同）提示秒杀重复
            if (insertCount <= 0) {
                throw new RepeatKillException("seckill repeated");
            } else {
                //执行减库存方法
                int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
                if (updateCount <= 0) {
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }
            }
        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            //所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error" + e.getMessage());
        }
    }
}
