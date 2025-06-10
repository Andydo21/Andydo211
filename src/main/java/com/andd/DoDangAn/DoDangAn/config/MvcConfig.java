package com.andd.DoDangAn.DoDangAn.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ánh xạ /assets/** đến thư mục static/assets/ trong classpath
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/");
        // Giữ nguyên ánh xạ cho uploads và upload_dir
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
        registry.addResourceHandler("/upload_dir/**")
                .addResourceLocations("file:upload_dir/");
    }
}
