package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用户是否登录
 *
 * @ClassName: LoginCheckFilter
 * @author: mafangnian
 * @date: 2022/8/8 14:41
 * @Blog: null
 */
@WebFilter(filterName = "LoginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径比较资源类 路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获得本次请求的URI
        String requestURI = request.getRequestURI();

        //设置不需要登录即可访问的地址
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/usersendMsg", //移动端发送验证码
                "/user/login" //移动端登录
        };

        //2.检查本次请求是否需要放行
        boolean check = check(urls, requestURI);

        //3.如果不需要出来直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }

        //4-1.判断登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null){

            log.info("用户已登录,用户名为{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            //将登录用户的id值记录到线程块中
            BaseContext.setCurrentId(empId);

            filterChain.doFilter(request, response);
            return;
        }

        //4-2.判断登录状态，如果已登录，则直接放行 判断登录用户
        if (request.getSession().getAttribute("user")!=null){

            log.info("用户已登录,用户名为{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            //将登录用户的id值记录到线程块中
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request, response);
            return;
        }
        //5.如果是未登录的状态则返回未登录的结果,通过输出流的方式向客户端页面响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;

    }

    /**
     * 路径匹配，是否需要放行
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) {
                return true;
            }
        }
        return false;
    }
}
