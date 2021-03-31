package id.co.learn.ib.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Component
public class WebUtility {

    public void doWriteCookie( HttpServletResponse response,  String cookieName,  String cookieValue, Integer expiry, Boolean secure, Boolean httpOnly){
         Cookie cookie = new Cookie(cookieName, cookieValue);
        if(expiry != null){
            cookie.setMaxAge(expiry);
        }
        cookie.setPath("/");
        cookie.setSecure(secure);
        cookie.setHttpOnly(httpOnly);
        response.addCookie(cookie);
    }

    public void doDeleteCookie(HttpServletResponse response, String cookieName){
         Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }

    public String getCookie( HttpServletRequest request,  String cookieName){
         Cookie cookie = WebUtils.getCookie(request, cookieName);
        if(cookie != null){
            return cookie.getValue();
        }
        return null;
    }

}
