package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.Category;
import com.andd.DoDangAn.DoDangAn.models.Product;
import com.andd.DoDangAn.DoDangAn.models.ResponseObject;
import com.andd.DoDangAn.DoDangAn.models.User;
import com.andd.DoDangAn.DoDangAn.repository.CategoryRepository;
import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping(path= "/Products")
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @RequestMapping(value="/",method=RequestMethod.GET)
    public String home(ModelMap modelMap) {
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("products", productRepository.findAll());
        return "products";
    }
    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(ModelMap modelMap) {
        return "register";
    }
    @RequestMapping(value="/register",method = RequestMethod.POST)
    public String register(@Valid User user, BindingResult bindingResult, ModelMap modelMap) {
        if(bindingResult.hasErrors()){
            System.out.println("Validation Errors: " + bindingResult.getAllErrors());
            return "register";
        }
        try {

            userRepository.save(user);
            return "redirect:/categories";
        } catch (Exception e) {
            modelMap.addAttribute("error",e.toString());
            return "register";
        }
    }
    @RequestMapping(value ="/login",method = RequestMethod.GET)
    public String login(ModelMap modelMap) {

        return "login";
    }
    @RequestMapping(value="/login",method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("user") User user, BindingResult result, ModelMap modelMap) {
        if (result.hasErrors()) {
            return "login";
        }
        else {
            Optional<User> foundUser = userRepository.findByUsername(user.getUserName());
            if (foundUser != null) {
                return "redirect:/categories";
            }
            else {
                return "login";
            }
        }
    }
    @RequestMapping(value = "/login/{ProductID}",method = RequestMethod.GET)
    public String login(@PathVariable String ProductID, ModelMap modelMap) {
        return "login";
    }
    @RequestMapping(value ="/login/{ProductID}",method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("user") User user, @PathVariable String ProductID,BindingResult bindingResult, ModelMap modelMap) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        else {
            Optional<User> foundUser = userRepository.findByUsername(user.getUserName());
            if (foundUser != null) {
                return "redirect:/movie/"+ProductID;
            }
            else {
                return "login";
            }
        }
    }

    @RequestMapping(value="/movie/{productID}",method=RequestMethod.GET)
    public String showMovie(@PathVariable("productID") String productID, ModelMap modelMap) {
        modelMap.addAttribute("Product", productRepository.findById(productID));
        return "Movie";
    }

    @RequestMapping(value="/getProductsByCategoryID/{categoryID}",method=RequestMethod.GET)
    public String getProductsByCategoryID(ModelMap modelMap,@PathVariable String categoryID){
        Iterable<Product> products=productRepository.findByCategoryID(categoryID);
        modelMap.addAttribute("products",products);
        return "productList";
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
    public String insertProduct(ModelMap modelMap,@Valid @ModelAttribute("product") Product product,BindingResult bindingResult,@RequestParam("imageFile") MultipartFile imageFile,
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
            System.out.println("Received file: " + imageFile.getOriginalFilename()); // Debug file nhận được
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
            product.setImageUrl("/uploads/default.png"); // Ảnh mặc định nếu không upload
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
    //@Autowired
    //private ProductRepository repository;

    //@GetMapping ("")
    //List<Product> getAllProduct(){
        //return repository.findAll();
    //}
    //@GetMapping("{id}")

    //ResponseEntity<ResponseObject> findById(@PathVariable Long id){
      //  Optional<Product> foundProduct = repository.findById(id);
        //if (foundProduct.isPresent()) {
          //  return ResponseEntity.status(HttpStatus.OK).body(
            //       new ResponseObject("ok","success",foundProduct));
        //}else {
          //  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
            //        new ResponseObject("error","not found"+id,"")
            //);
        //}
    //}
  //  @PostMapping("/insert")
    //ResponseEntity<ResponseObject> insertProduct(@RequestBody Product newProduct){
      //  List<Product> foundProduct = repository.findByProductName(newProduct.getProductName().trim());
        //if(foundProduct.size()>0){
          //  return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body(
            //        new ResponseObject("error","duplicate product","")
            //);
        //}
        //return ResponseEntity.status(HttpStatus.OK).body(
          //      new ResponseObject("ok","success",repository.save(newProduct))
       // );
   // }
    //@PutMapping("/{id}")
    //ResponseEntity<ResponseObject> updateProduct(@PathVariable String id, @RequestBody Product newProduct){
        //Product updatedProduct = repository.findById(id)
          //      .map(product -> {
            //        product.setProductName(newProduct.getProductName().trim());
              //      product.setDescription(newProduct.getDescription().trim());
                //    product.setPrice(newProduct.getPrice());
    //.  return repository.save(product);
      //          }).orElseGet(()->{
        //            newProduct.setId(id);
          //          return repository.save(newProduct);
            //    });
        //return ResponseEntity.status(HttpStatus.OK).body(
      //          new ResponseObject("ok","success",repository.save(updatedProduct))
        //);
    //}
    //@DeleteMapping("/{id}")
    //ResponseEntity<ResponseObject> deleteProduct(@PathVariable Long id){
      //  boolean foundProduct = repository.existsById(id);
        //if(foundProduct){
          //  repository.deleteById(id);
            //return ResponseEntity.status(HttpStatus.OK).body(
              //      new ResponseObject("ok","found"+id,"")
            //);
        //}
        //return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
          //      new ResponseObject("error","not found"+id,"")
        //);
    //}
}
