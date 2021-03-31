package id.co.learn.ib.util;

import com.alibaba.fastjson.JSON;
import id.co.learn.ib.constants.IBConstants;
import id.co.learn.ib.dto.SessionDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.apache.commons.codec.binary.Base64;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-12-08
*/
@Component
@Slf4j
public class UserUtility {

    @Value("${ib.jwt.expiration}")
    private Integer ibJwtExpiration;
    
    @Value("${ib.jwt.secret}")
    private String ibJwtSecret;

    @Autowired
    private CacheUtility cacheUtility;

    private static final String REGEX_STR = "(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20}$";

    public Boolean isValidPasswordStrength(String password){
        Pattern pattern = Pattern.compile(REGEX_STR);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches()
                && !password.toLowerCase().contains("password")
                && !password.toLowerCase().contains("p@ssw0rd")
                && !password.toLowerCase().contains("admin")
                && !password.toLowerCase().contains("administrator");
    }

    public String getNewJwt(String username) {
        Base64 base64 = new Base64();
        String encodedUsername = new String(base64.encode(username.getBytes()));
        return Jwts.builder()
                .setSubject(encodedUsername)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + ibJwtExpiration * 1000))
                .signWith(SignatureAlgorithm.HS512, ibJwtSecret)
                .compact();
    }

    public Jws<Claims> getJwtClaim(String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        return Jwts.parser().setSigningKey(ibJwtSecret).parseClaimsJws(token);
    }

    public SessionDto getCurrentUserInfo(String token){
        log.info("token = {}", token);
        Jws<Claims> claims = this.getJwtClaim(token.replace("Bearer ", ""));
        if(ObjectUtils.isEmpty(claims)){
            log.error("getCurrentUserInfo.error = JWT claim is null");
            return null;
        }
        String usernameEncode = claims.getBody().getSubject();
        Base64 base64 = new Base64();
		String decodedUsername = new String(base64.decode(usernameEncode.getBytes()));
        String userLoginCache = cacheUtility.get(IBConstants.RDS_USER_LOGIN, decodedUsername);
        if(StringUtils.isEmpty(userLoginCache)){
            log.error("getCurrentUserInfo.error = userLoginCache is empty");
            return null;
        }
        return JSON.parseObject(userLoginCache, SessionDto.class);
    }
    
}