package id.co.learn.ib.config;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import id.co.learn.ib.constants.IBConstants;
import id.co.learn.ib.service.CustomerDetailService;
import id.co.learn.ib.util.CacheUtility;
import id.co.learn.ib.util.UserUtility;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author  Adinandra Dharmasurya
 * @version 1.0
 * @since   2020-12-08
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CustomerDetailService customerDetailService;

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private UserUtility userUtility;

	@Autowired
	private CacheUtility cacheUtility;

	private static final String [] ignoredApis = {
			"/api/customer/v1/sign-in",
			"/api/customer/v1/sign-out",
			"/swagger",
			"/webjars",
			"/v2/api-docs"
	};

	@Override
	public void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.customerDetailService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().antMatchers("/v**").authenticated();
		http.addFilterBefore(serviceFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	private Filter serviceFilter() {
		AtomicReference<Boolean> isIgnored = new AtomicReference<>();
		return new OncePerRequestFilter(){
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
				response.addHeader("Access-Control-Allow-Headers",
						"Access-Control-Allow-Origin, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization");
				response.addHeader("Access-Control-Allow-Origin", "http://localhost:4200");
				AtomicBoolean isPermitted = new AtomicBoolean(false);
				Arrays.stream(ignoredApis).filter(url -> request.getRequestURI().contains(url))
					.findAny()
					.ifPresentOrElse(
						data -> isPermitted.set(true),
						() -> isPermitted.set(false)
					);
				log.info("isPermitted = {} on url = {}", isPermitted.get(), request.getRequestURI());
				if(isPermitted.get()){
					SecurityContextHolder.getContext().setAuthentication(null);
				}
				else {
					isPermitted.set(isValidUserAuthorization(request));
				}
				if(!isPermitted.get()){
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, IBConstants.ERR_UNAUTHORIZED_ACCESS);
					return ;
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private Boolean isValidUserAuthorization(HttpServletRequest request) {
		String token = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(ObjectUtils.isEmpty(userUtility.getCurrentUserInfo(token)) ||
				StringUtils.isEmpty(cacheUtility.get(IBConstants.RDS_USER_LOGIN, userUtility.getCurrentUserInfo(token).getUsername()))){
			log.error("Invalid session occured...");
			return false;
		}
		else{
			UserDetails userDetails = userDetailsService.loadUserByUsername(userUtility.getCurrentUserInfo(token).getUsername());
			UsernamePasswordAuthenticationToken authentication
					= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			log.info("Valid session detected...");
			return true;
		}

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
