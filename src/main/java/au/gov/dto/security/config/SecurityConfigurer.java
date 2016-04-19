package au.gov.dto.security.config;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration
public class SecurityConfigurer
        extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    @Autowired
    private LoginAuthFailureHandler failureHandler;

    @Autowired
    private LoginAuthSuccessHandler successHandler;

    @Autowired
    private Environment environment;

    @Value("${security.enable-csrf:true}")
    private boolean csrfEnabled;

    @Value("${security.basic.enabled:false}")
    private boolean httpBasic;

    @Override
    public void init(HttpSecurity http) throws Exception {

        // Redirect http to https for production
        if (isProduction()) {
            http.requiresChannel().anyRequest().requiresSecure();
        }

        // Prevent the HTTP response header of "Pragma: no-cache".
        http.headers().cacheControl().disable();

        http.headers().addHeaderWriter(new StaticHeadersWriter("X-UA-Compatible", "IE=Edge"));

        if (httpBasic) {
            http.httpBasic();
        }

        http.authorizeRequests()
            .antMatchers("/")
            .permitAll()
            .antMatchers("/admin/**")
            .hasAuthority("ADMIN")
            .anyRequest()
            .fullyAuthenticated();
        http.formLogin()
            .loginPage("/login")
            .successHandler(successHandler)
            .failureHandler(failureHandler)
            .usernameParameter("email")
            .permitAll();
        http.logout().logoutUrl("/logout").deleteCookies("remember-me").permitAll();

        if (!httpBasic) {
            http.httpBasic();
        }

        if (csrfEnabled) {
            http.csrf().requireCsrfProtectionMatcher(new RequestMatcher() {

                private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");
                private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/api/.*", null);

                @Override
                public boolean matches(HttpServletRequest request) {

                    // No CSRF due to allowedMethod
                    if (allowedMethods.matcher(request.getMethod()).matches())
                        return false;

                    // No CSRF due to api call
                    if (apiMatcher.matches(request))
                        return false;

                    // CSRF for everything else that is not an API call or an
                    // allowedMethod
                    return true;
                }
            });

            http.addFilterAfter(new CsrfTokenResponseHeaderBindingFilter(), CsrfFilter.class);
        } else {
            http.csrf().disable();
        }
    }

    public boolean isProduction() {

        String[] activeProfiles = environment.getActiveProfiles();
        if (activeProfiles != null && activeProfiles.length > 0) {
            if (activeProfiles[0].equals("production")) {
                return true;
            }
        }

        return false;
    }

}
