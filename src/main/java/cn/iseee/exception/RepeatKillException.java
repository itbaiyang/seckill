package cn.iseee.exception;

/**
 * 重复秒杀异常
 * Created by popla on 2017/5/27.
 */
public class RepeatKillException extends SeckillException{

    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause){
        super(message, cause);
    }
}
