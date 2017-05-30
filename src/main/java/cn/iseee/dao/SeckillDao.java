package cn.iseee.dao;

import cn.iseee.entity.Seckill;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 秒杀列表
 * Created by baiyang on 2017/5/26.
 */
public interface SeckillDao {

    long insertSeckill(Seckill seckill);

    int deleteSeckill(long seckillId);

    long updateSeckill(Seckill seckill);

    Seckill queryById(long seckillId);

    List<Seckill> queryAll(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 减库存
     *
     * @param seckillId
     * @param killTime
     * @return 如果影响行数>1，表示更新记录的行数
     */
    int reduceNumber(@Param("seckillId") long seckillId, @Param("killTime") Date killTime);
}
