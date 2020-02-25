package battleships.config;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class AppConfig implements WebMvcConfigurer {
    /*
        @Bean
        public InternalResourceViewResolver viewResolver() {
            InternalResourceViewResolver vr = new InternalResourceViewResolver();
            vr.setPrefix("/WEB-INF/jsp/");
            vr.setSuffix(".jsp");
            return vr;
        }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        assert registry != null;
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/register").setViewName("activeGamesAndUsersRegistry");
    }
*/

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        assert registry != null;
        registry.addResourceHandler("/static/**",
                "/css/**",
                "/Images/**",
                "/js/**")
                .addResourceLocations("classpath:/static/",
                        "classpath:/static/css/",
                        "classpath:/static/Images/",
                        "classpath:/static/js/");
    }


    @Bean
    public SessionFactory sessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new AuthProvider();
    }
}
