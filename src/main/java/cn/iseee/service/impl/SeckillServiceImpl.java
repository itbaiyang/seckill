package cn.iseee.service.impl;

import cn.iseee.dao.SeckillDao;
import cn.iseee.dao.SuccessKillDao;
import cn.iseee.entity.Seckill;
import cn.iseee.entity.SuccessKilled;
import cn.iseee.exception.RepeatKillException;
import cn.iseee.exception.SeckillCloseException;
import cn.iseee.exception.SeckillException;
import cn.iseee.service.SeckillService;
import cn.iseee.dto.Exposer;
import cn.iseee.dto.SeckillExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

/**
 * Created by popla on 2017/5/27.
 */
public class SeckillServiceImpl implements SeckillService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private SeckillDao seckillDao;
    private SuccessKillDao successKillDao;

    private final String slat = "afdadfdasfafas fdsfsdfasdf7(&(&((&F(&*(f89asf";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        if(nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(),
                    endTime.getTime());
        }

        String md5 = getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        try {
            if(md5 == null || md5.equals(getMD5(seckillId))){
                throw new SeckillException("seckill data rewrite");
            }
            //执行秒杀逻辑：减库存 记录购买行为
            Date nowTime = new Date();
            int updateCount = seckillDao.reduceNumber(seckillId, nowTime);
            if(updateCount <=0) {
                throw new SeckillCloseException("seckill is closed");
            }else {
                //记录购买行为

                int insertCount = successKillDao.insertSuccessKilled(seckillId, userPhone);

                if(insertCount <= 0){
                    throw new RepeatKillException("seckill repeated");
                }else {
                    //秒杀成功
                    SuccessKilled successKilled = successKillDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, 1, "秒杀成功", successKilled);
                }
            }
        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            //所有编译期异常转化为运行期异常
            throw new SeckillException("seckill inner error" + e.getMessage());
        }
    }
}
