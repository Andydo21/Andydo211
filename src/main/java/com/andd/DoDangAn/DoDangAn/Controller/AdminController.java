package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.Category;
import com.andd.DoDangAn.DoDangAn.models.Product;
import com.andd.DoDangAn.DoDangAn.repository.CategoryRepository;
import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping(path= "/admin")
public class AdminController {
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping
    @RequestMapping(value="/movie/{productID}",method= RequestMethod.GET)
    public String showMovie(@PathVariable("productID") String productID, ModelMap modelMap) {
        modelMap.addAttribute("productID", productID);
        return "MovieVIP";
    }
    @RequestMapping(value = "/changeCategory/{productID}",method=RequestMethod.GET)
    public String updateProduct(ModelMap modelMap,@PathVariable String productID){
        Iterable<Category> categories = categoryRepository.findAll();
        modelMap.addAttribute("categories", categories );
        modelMap.addAttribute("product", productRepository.findById(productID).get());
        return "assign";
    }
    @RequestMapping(value = "/insertProduct",method=RequestMethod.GET)
    public String insertProduct(ModelMap modelMap){
        modelMap.addAttribute("product", new Product());
        modelMap.addAttribute("category",categoryRepository.findAll());
        return "insertProduct";
    }
    @RequestMapping(value = "/insertProduct",method=RequestMethod.POST)
    public String insertProduct(ModelMap modelMap, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @RequestParam("imageFile") MultipartFile imageFile,
                                @RequestParam("videoFile") MultipartFile videoFile){
        if(bindingResult.hasErrors()){
            System.out.println("Validation Errors: " + bindingResult.getAllErrors());
            modelMap.addAttribute("product", new Product());
            modelMap.addAttribute("category",categoryRepository.findAll());
            return "insertProduct";
        }
        try{
            if (!videoFile.isEmpty()) {
                String UPLOAD_DIR="upload_dir";
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = UUID.randomUUID().toString() + "_" + videoFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.write(filePath, videoFile.getBytes());

                product.setVideoUrl("/" + UPLOAD_DIR + fileName);
            }
            else{
                return "insertProduct";
            }
            System.out.println("Received file: " + imageFile.getOriginalFilename());
            System.out.println("File size: " + imageFile.getSize());
            if(!imageFile.isEmpty()){
                String upload="uploads/";
                File uploadFile=new File(upload);
                if(!uploadFile.exists()){uploadFile.mkdirs();}
                String fileName = UUID.randomUUID().toString()+" "+imageFile.getOriginalFilename();
                Path filePath = Paths.get(upload + fileName);
                Files.write(filePath, imageFile.getBytes());
                product.setImageUrl("/"+upload+fileName);
            }
            else {
                product.setImageUrl("/uploads/default.png");
            }
            product.setId(UUID.randomUUID().toString());
            productRepository.save(product);
            return "redirect:/categories";
        } catch (Exception e) {
            modelMap.addAttribute("error",e.toString());
            return "insertProduct";
        }
    }
    @RequestMapping(value = "/updateProduct/{productID}",method=RequestMethod.POST)
    public String updateProduct(ModelMap modelMap, @Valid @ModelAttribute("product") Product product, BindingResult bindingResult, @PathVariable String productID ){
        Iterable<Category> categories = categoryRepository.findAll();
        if(bindingResult.hasErrors()){
            System.out.println("hell no");
            modelMap.addAttribute("categories", categories );
            return "assign";

        }
        try {
            if (productRepository.findById(productID).isPresent()){
                Product foundProduct = productRepository.findById(productID).get();
                if (!product.getProductName().trim().isEmpty()){
                    foundProduct.setProductName(product.getProductName());
                }
                if (!product.getCategoryID().isEmpty()){
                    foundProduct.setCategoryID(product.getCategoryID());
                }
                if (!product.getDescription().trim().isEmpty()){
                    foundProduct.setDescription(product.getDescription());
                }
                if (product.getPrice()>=0){
                    foundProduct.setPrice(product.getPrice());

                }
                if(!product.getImageUrl().isEmpty()){
                    foundProduct.setImageUrl(product.getImageUrl());
                }
                if (!product.getVideoUrl().isEmpty()){
                    foundProduct.setVideoUrl(product.getVideoUrl());
                }
                productRepository.save(foundProduct);
            }
        } catch (Exception e) {
            return "updateProduct";
        }
        return "redirect:/Products/getProductsByCategoryID/"+product.getCategoryID();
    }
    @RequestMapping(value = "/deleteProduct/{productID}",method = RequestMethod.POST)
    public String deleteProduct(ModelMap modelMap, @PathVariable String productID){
        productRepository.deleteById(productID);
        return "redirect:/categories";
    }
}
