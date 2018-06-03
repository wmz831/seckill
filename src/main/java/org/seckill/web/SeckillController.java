package org.seckill.web;

import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.enums.SeckillStatEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.service.SeckillService;
import org.seckill.vo.SeckillResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Create by Wang Mingzhen om 2018/5/8
 */
@Controller//放入spring容器当中
@RequestMapping("/seckill")//url：/模块/资源/{id}/细分
public class SeckillController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    /**
     * 获取秒杀列表
     * Model：用于存放所有的渲染jsp的数据
     * list.jsp：提供页面的模板
     * list.jsp + model = ModelAndView
     * @param model
     * @return 返回可以是ModelAndView，也可以是String
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, name = "秒杀列表")
    public String list(Model model){
        //获取列表页
        List<Seckill> list = seckillService.getSeckillList();
        model.addAttribute("list",list);
        return "list";///WEB-INF/jsp/list.jsp
    }


    @RequestMapping(value = "/{seckillId}/detail", method = RequestMethod.GET, name = "详情页")
    public String detail(@PathVariable("seckillId") Long seckillId, Model model){
        if(seckillId == null){
            return "redirect:/seckill/list";
        }
        Seckill seckill = seckillService.getById(seckillId);
        if(seckill == null){
            return "forward:/seckill/list";
        }
        model.addAttribute("seckill",seckill);
        return "detail";
    }


    /**
     * 返回秒杀地址
     * ajax接口，返回json
     * @param seckillId
     * @return
     */
    @RequestMapping(value = "/{seckillId}/exposer",
            method = RequestMethod.POST,
            produces = "application/json;charset=UTF-8")
    @ResponseBody
    public SeckillResult<Exposer>/*todo*/ exposer(@PathVariable("seckillId") Long seckillId){
        SeckillResult<Exposer> result;
        try {
            Exposer exposer = seckillService.exportSeckillUrl(seckillId);
            result = new SeckillResult<Exposer>(true,exposer);

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            result = new SeckillResult<Exposer>(false,e.getMessage());
        }
        return result;
    }

    /**
     * Service 中的抛出异常是为了让 Spring 能够回滚
     * Controller 中捕获异常是为了将异常转换为对应的 Json 供前台使用
     *
     * @param seckillId
     * @param md5
     * @param phone 使用@CookieValue从用户已登陆的cookie中调取phone数据，如果请求中的request header的cookie中没有killpnone，
     *              springMVC会直接报错，所以设置required = false
     * @return @ResponseBody:将响应数据封装成json
     */
    @RequestMapping(value = "/{seckillId}/{md5}/execution",
            method = RequestMethod.POST,
            produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public SeckillResult<SeckillExecution> execute(@PathVariable("seckillId") Long seckillId,
                                                   @PathVariable("md5") String md5,
                                                   @CookieValue(value = "killPhone", required = false) Long phone/*todo*/) {
        //也可用 SpringMVC valid
        if (seckillId == null|| phone == null) {
            return new SeckillResult<SeckillExecution>(false, "seckillId or phone is Null");
        }

//        SeckillResult<SeckillExecution> result;
        try {
//            SeckillExecution execution = seckillService.executeSeckill(seckillId, phone, md5);
            SeckillExecution execution = seckillService.executeSeckillProc(seckillId,phone,md5);//通过存储过程
            return new SeckillResult<SeckillExecution>(true, execution);
        } catch (RepeatKillException e) {
            //系统允许的异常，可不打印日志
            //这里的几个异常处理也可使用 return new SeckillResult<SeckillExecution>(false,"...");
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

    /**
     * 获取系统时间
     * @return
     */
    @RequestMapping(value = "/time/now", method = RequestMethod.GET)
    @ResponseBody
    public SeckillResult<Long> time(){
        Date now = new Date();
        return new SeckillResult<Long>(true,now.getTime());
        //也可用：return new SeckillResult<Long>(true,System.currentTimeMillis());
    }

}
