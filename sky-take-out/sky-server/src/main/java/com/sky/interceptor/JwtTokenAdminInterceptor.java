package com.sky.interceptor;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.properties.JwtProperties;
import com.sky.utils.JwtUtil;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenAdminInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 校验jwt
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getAdminTokenName());

        //2、校验令牌
        try {
            log.info("jwt校验");
            Claims claims = JwtUtil.parseJWT(jwtProperties.getAdminSecretKey(), token);
            Long empId = Long.valueOf(claims.get(JwtClaimsConstant.EMP_ID).toString());
            BaseContext.setCurrentId(empId); //将当前员工id设置到ThreadLocal中
            log.info("当前操作者id：{}", empId);
            //3、通过，放行
            return true;
        } catch (ExpiredJwtException e) {
            log.info("JWT令牌已过期: {}", e.getMessage());
            response.setStatus(401);
            return false;
        } catch (MalformedJwtException e) {
            log.info("JWT令牌格式错误: {}", e.getMessage());
            response.setStatus(401);
            return false;
        } catch (SignatureException e) {
            log.info("JWT签名验证失败: {}", e.getMessage());
            response.setStatus(401);
            return false;
        } catch (UnsupportedJwtException e) {
            log.info("JWT令牌不支持: {}", e.getMessage());
            response.setStatus(401);
            return false;
        } catch (IllegalArgumentException e) {
            log.info("JWT参数异常: {}", e.getMessage());
            response.setStatus(401);
            return false;
        } catch (Exception e) {
            log.info("JWT令牌校验失败: {}", e.getMessage());
            response.setStatus(401);
            return false;
        }
    }
}
