package com.kong.Config;

import com.kong.Interceptor.LoginInterceptor;
import com.kong.common.JacksonObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**")
                .excludePathPatterns("/backend/styles/**","/backend/api/**",
                        "/backend/images/**","/backend/js/**","/backend/plugins/**",
                        "/backend/styles/**")
                .excludePathPatterns("/employee/login","/backend/page/login/login.html",
                        "/employee/logout")
                .excludePathPatterns("/user/sendMsg","/user/login","/front/page/login.html");
    }

    /**
     * 扩展MVC的消息转换器
     * @param converters
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson将Java对象转为json
        messageConverter.setObjectMapper(new JacksonObjectMapper());

        //将上面的消息转换器对象追加到MVC框架的转换器集合中,0表示优先使用自己定义的转换器
        converters.add(0,messageConverter);
    }
}
