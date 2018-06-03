-- 秒杀执行的存储过程（代替事务)
DELIMITER $$ -- 因为老师直接在console中写，所以需要把";"换行符先换一下
-- 定义存储过程
-- 参数：in 输入参数 out 输出参数,在存储过程中不能使用，但可以赋值
-- row_count():返回上一条修改类型sql(insert/update/delete)的影响行数
-- row_count:0 未修改数据; >0 修改的行数; <0 sql错误/未执行修改sql
CREATE PROCEDURE `seckill`.`execute_seckill`
  (in v_seckill_id BIGINT,in v_phone BIGINT,in v_kill_time TIMESTAMP,
    out r_result INT)
  BEGIN
    DECLARE insert_count INT DEFAULT 0;
    START TRANSACTION ;
    -- 插入购买明细
    INSERT IGNORE INTO success_killed
      (seckill_id,user_phone,create_time)
      VALUES (v_seckill_id,v_phone,v_kill_time);
    SELECT row_count() INTO insert_count;
    IF (insert_count = 0) THEN
      ROLLBACK;
      SET r_result = -1;
    ELSEIF (insert_count < 0) THEN
      ROLLBACK ;
      SET r_result = -2;
    ELSE
      UPDATE seckill
      SET number = number - 1
      WHERE seckill_id = v_seckill_id
        AND end_time > v_kill_time
        AND start_time < v_kill_time
        AND number > 0;
      SELECT row_count() INTO insert_count;
      -- 减库存
      IF (insert_count = 0) THEN
        ROLLBACK ;
        SET r_result = 0;
      ELSEIF (insert_count < 0) THEN
        ROLLBACK ;
        SET r_result = -2;
      ELSE
        COMMIT ;
        SET r_result = 1;
      END IF;
    END IF ;
  END;
$$
-- 存储过程定义结束

-- 调用存储过程
DELIMITER ; -- 改变换行符
SET @r_result=-3;
-- 执行存储过程
CALL execute_seckill(1007,150,now(),@r_result);

-- 获取结果
SELECT @r_result;

##############################################
# 存储过程小结：
# 1：存储过程优化：事务行级锁持有的事件
# 2：不要过度依赖存储过程，存储过程一般只在银行大范围使用，因为有Oracle或DB2
# 3：简单的逻辑可以应用存储过程
# 4：QPS:一个秒杀单6000/qps
##############################################

--