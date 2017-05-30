package cn.iseee.web;

import cn.iseee.dto.Exposer;
import cn.iseee.dto.SeckillExecution;
import cn.iseee.dto.SeckillResult;
import cn.iseee.entity.Seckill;
import cn.iseee.enums.SeckillStatEnum;
import cn.iseee.exception.RepeatKillException;
import cn.iseee.exception.SeckillCloseException;
import cn.iseee.exception.SeckillException;
import cn.iseee.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Created by baiyang on 2017/5/28.
 */
@Controller
@RequestMapping("/seckill") //url模块
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    @ResponseBody
    public SeckillResult<Seckill> insert(@RequestParam("name") String name,
                                         @RequestParam("number") int number,
                                         @RequestParam("startTime") String startTime,
                                         @RequestParam("endTime") String endTime) {
        SeckillResult<Seckill> result;
        System.out.println(name);
        try {
            Seckill seckill = new Seckill();
            seckill.setName(name);
            seckill.setNumber(number);
            Date start = new Date(Long.parseLong(startTime));
            seckill.setStartTime(start);
            Date end = new Date(Long.parseLong(endTime));
            seckill.setEndTime(end);
            seckill.setCreateTime(new Date());
            seckillService.insertOneSeckill(seckill);
            result = new SeckillResult<Seckill>(true, seckill);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Seckill>(false, e.getMessage());
        }

        return result;
    }

    @RequestMapping(value = "/{seckillId}/delete", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Boolean> delete(@PathVariable("seckillId") Long seckillId, Model model) {
        SeckillResult<Boolean> result;
        if (seckillId == null) {
            result = new SeckillResult<Boolean>(false, "seckillId为null");
        }
        try {
            int conut = seckillService.deleteOneSeckill(seckillId);
            if(conut > 0) {
                result = new SeckillResult<Boolean>(true, true);
            }else {
                result = new SeckillResult<Boolean>(false, "删除失败");
            }
        }  catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Boolean>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/{seckillId}/update", method = RequestMethod.POST)
    @ResponseBody
    public SeckillResult<Seckill> update(@PathVariable("seckillId") long seckillId,
                                         @RequestParam("name") String name,
                                         @RequestParam("number") int number,
                                         @RequestParam("startTime") String startTime,
                                         @RequestParam("endTime") String endTime) {
        SeckillResult<Seckill> result;
        System.out.println(name);
        try {
            Seckill seckill = new Seckill();
            seckill.setSeckillId(seckillId);
            seckill.setName(name);
            seckill.setNumber(number);
            Date start = new Date(Long.parseLong(startTime));
            seckill.setStartTime(start);
            Date end = new Date(Long.parseLong(endTime));
            seckill.setEndTime(end);
            seckill.setCreateTime(new Date());
            seckillService.updateOneSeckill(seckill);
            result = new SeckillResult<Seckill>(true, seckill);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Seckill>(false, e.getMessage());
        }
        return result;
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list(Model model) {
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list", list);
        return "list";
    }

    //TODO: Model
    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET)
    public String detail(@PathVariable("seckillId") Long seckillId, Model model) {
        if (seckillId == null) {
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if (seckill == null) {
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill", seckill);
        return "detail";
    }

    //暴露秒下系统的url
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<Exposer> exposer(@PathVariable("seckillId") Long seckillId) {
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true, exposer);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            result = new SeckillResult<Exposer>(false, e.getMessage());
        }
        return result;
    }

    //处理秒杀操作
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @CookieValue(value = "killPhone", required = false) Long phone,
                                                   @PathVariable("md5") String md5) {

        //springMVC valid
        if (phone == null) {
            return new SeckillResult<SeckillExecution>(false, "未注册");
        }
        SeckillResult<SeckillExecution> result;

        try {
            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.REPEAT_KILL);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (SeckillCloseException e) {
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.END);
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            SeckillExecution execution = new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);
            return new SeckillResult<SeckillExecution>(true, execution);
        }
    }

    /*获取服务器当前时间*/
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time() {
        Date now = new Date();
        //返回SeckillREsult类型的值
        return new SeckillResult(true, now.getTime());
    }

}
