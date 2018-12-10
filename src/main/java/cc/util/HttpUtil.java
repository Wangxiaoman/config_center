package cc.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
     * 发送get请求
     * @param params
     * @param url
     * @param charSet
     * @return
     * @throws Exception
     */
// 补上换行符 
// 添加url参数  
public class HttpUtil {
    
    protected static Logger logger = LoggerFactory.getLogger(HttpUtil.class);
    
    public static String getTokenFromCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("token")){
                    return cookie.getValue();
                }
            }
        }
        return "";
    }
}
