package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.Category;
import com.andd.DoDangAn.DoDangAn.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(path="categories")
public class CategoryController {
    @Autowired
    private CategoryRepository categoryRepository;
    @RequestMapping(value = "",method = RequestMethod.GET)
    public String getAllCategories(ModelMap modelMap) {
        Iterable<Category> categories = categoryRepository.findAll();
        modelMap.addAttribute("categories", categories);
        System.out.println("getAllCategories");
        return "category";
    }
}
