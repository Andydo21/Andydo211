package com.andd.DoDangAn.DoDangAn.config;

import com.andd.DoDangAn.DoDangAn.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private CategoryRepository categoryRepository;

    @ModelAttribute
    public void addCategoriesToModel(org.springframework.ui.Model model) {
        model.addAttribute("category", categoryRepository.findAll());
    }
} 