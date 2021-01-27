//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package io.seata.spring.boot.autoconfigure;

import io.seata.integration.http.HttpHandlerExceptionResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
@ConditionalOnWebApplication
public class HttpAutoConfiguration implements WebMvcConfigurer {
    public HttpAutoConfiguration() {
    }

    /**
     * 注释掉 因为和cloud中的{@link com.alibaba.cloud.seata.web.SeataHandlerInterceptor}重合了，
     * 所以去掉一个，张之所以去掉这个，是因为它实现了postHandle,在异常时不会执行
     * @author lht
     * @since  2021/1/25 14:22
     * @param exceptionResolvers
     */
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new TransactionPropagationInterceptor());
//    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new HttpHandlerExceptionResolver());
    }
}
