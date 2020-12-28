package com.climb.seata.handler;

import com.climb.common.bean.Result;
import com.climb.common.exception.GlobalException;
import com.climb.common.util.ResultUtil;
import io.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * seata全局异常处理
 * 保证rpc请求后能够将异常正确传递
 * @author lht
 * @since 2020/9/25 11:54
 */
@Slf4j
@ControllerAdvice
public class GlobalSeataExceptionHandler {

    public GlobalSeataExceptionHandler() {
       log.info("seata GlobalSeataExceptionHandler init");
    }

    @ExceptionHandler(value = GlobalException.class)
    @ResponseBody
    public Result<String> globalException(HttpServletRequest req, HttpServletResponse response, GlobalException e){
        setResponseStatus(response,req);
        log.error("业务异常：",e);
        return ResultUtil.error(e.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result<String> error(HttpServletRequest req, HttpServletResponse response, Exception e){
        setResponseStatus(response,req);
        log.error("未知异常：",e);
        return ResultUtil.error("服务器异常");
    }

    /**
     * set ResponseStatus，seata process
     * @author lht
     * @since  2020/12/28 12:04
     * @param response
     * @param request
     */
    private void setResponseStatus(HttpServletResponse response,HttpServletRequest request){
        String rpcXid = request.getHeader(RootContext.KEY_XID);
        //如果rpcxid不为空，代表不是发起端，需要抛出异常实现异常传递
        if(rpcXid != null){
            response.setStatus(500);
        }else{
            response.setStatus(200);
        }
    }

}
