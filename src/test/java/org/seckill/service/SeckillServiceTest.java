package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.dto.Exposer;
import org.seckill.dto.SeckillExecution;
import org.seckill.entity.Seckill;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Create by Wang Mingzhen om 2018/5/4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml","classpath:spring/spring-service.xml"})
public class SeckillServiceTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        //{}是占位符，后面是对应的值
        logger.info("list={}",list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() throws Exception {
        long id = 1007;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);

    }

//    @Test
//    public void executeSeckill() throws Exception{
//        long id = 1007;
//        long phone = 1399;
//        String md5 = "d59e7f31bac4c96823859a4ec3a5408f";
//        try {
//            SeckillExecution execution = seckillService.executeSeckill(id,phone,md5);
//            logger.info("result={}",execution);
//        } catch (RepeatKillException e) {
//            logger.error(e.getMessage());
//        }catch (SeckillCloseException e) {
//            logger.error(e.getMessage());
//        }
//    }

    //测试代码完整逻辑，注意不可重复执行
    @Test
    public void exportSeckillLogic() throws Exception {
        long id = 1007;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if(exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone = 1390;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id,phone,md5);
                logger.info("result={}",execution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            }catch (SeckillCloseException e) {
                logger.error(e.getMessage());
            }
        }else{
            //秒杀未开启
            logger.info("exposer={}",exposer);
        }
    }

    @Test
    public void executeSeckillProcedure(){
        long seckillId = 1007;
        long phone = 1506;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if(exposer.isExposed()){
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillProc(seckillId,phone,md5);
            logger.info(execution.getStateInfo());
            logger.info("result={}",execution);
        }
    }
}

