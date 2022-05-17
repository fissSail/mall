package com.yff.mall.product.exception;

import com.yff.common.utils.ExceptionCodeEnum;
import com.yff.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yanfeifan
 * @Package com.yff.mall.product.exception
 * @Description 全局异常处理
 * @date 2021/12/17 15:27
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.yff.mall.product.controller")
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R exceptionValidHandler(MethodArgumentNotValidException e) {
        log.error("数据校验出现问题：{}，类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,Object> errResult = new HashMap<>();
        bindingResult.getFieldErrors().forEach(data->{
            errResult.put(data.getField(),data.getDefaultMessage());
        });
        //获取校验的错误结果
        return R.error(ExceptionCodeEnum.VAILD_EXCEPTION.getCode(),ExceptionCodeEnum.VAILD_EXCEPTION.getMsg()).put("data",errResult);
    }

    @ExceptionHandler(Throwable.class)
    public R exceptionHandler(Throwable t) {
        log.error("error：{}", t);
        return R.error(ExceptionCodeEnum.UNKNOW_EXCEPTION.getCode(), ExceptionCodeEnum.UNKNOW_EXCEPTION.getMsg()).put("data",t.getMessage());
    }

}
