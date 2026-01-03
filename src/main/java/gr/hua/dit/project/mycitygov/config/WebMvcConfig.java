package gr.hua.dit.project.mycitygov.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Force legacy Ant-style path matching so Springdoc Swagger UI patterns with ** wildcards remain valid.
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

   @Override
   public void configurePathMatch(final PathMatchConfigurer configurer) {
      // PathPatternParser rejects patterns like "/swagger-ui/**/*swagger-initializer.js"
      configurer.setPatternParser(null);
   }
}
