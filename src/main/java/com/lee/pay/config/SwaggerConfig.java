package com.lee.pay.config;

import com.github.xiaoymin.knife4j.spring.extension.OpenApiExtensionResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.function.Predicate;


@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Value("${pay.swagger.enable:false}")
    private boolean enable  ;

    private final OpenApiExtensionResolver openApiExtensionResolver;

    public SwaggerConfig(OpenApiExtensionResolver openApiExtensionResolver) {
        this.openApiExtensionResolver = openApiExtensionResolver;
    }


    @Bean
    public Docket customDocket() {

        Predicate<RequestHandler> p1 = RequestHandlerSelectors.basePackage("com.lee");
        Contact contact = new Contact("liyi", "", "");
        return new Docket(DocumentationType.SWAGGER_2)
                .enable(enable)
                .apiInfo(
                        new ApiInfoBuilder()
                                .title("支付模块")
                                .description("API文档")
                                .version("2022-6-18-1-SNAPSHOT")
                                .contact(contact)
                                .build()
                )
                .select()
                .apis(p1)
                .build()
                .extensions(openApiExtensionResolver.buildExtensions("primary"));
    }

}
