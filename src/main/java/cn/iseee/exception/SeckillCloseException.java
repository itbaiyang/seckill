package cn.iseee.exception;

/**
 * 秒杀关闭异常
 * Created by popla on 2017/5/27.
 */
public class SeckillCloseException extends  SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause){
        super(message, cause);
    }

}
