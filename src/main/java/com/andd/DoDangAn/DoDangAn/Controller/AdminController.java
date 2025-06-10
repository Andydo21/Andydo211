package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.*;
import com.andd.DoDangAn.DoDangAn.repository.*;
import com.andd.DoDangAn.DoDangAn.services.CloudinaryService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Controller
@RequestMapping(path= "/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/home")
    public String home(ModelMap modelMap, @RequestParam("field") Optional<String> field, @RequestParam("field2") Optional<String> field2, @RequestParam("field3") Optional<String> field3) {
        logger.debug("Starting home method in AdminController");
        try {
            // Add logged-in user to the model
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                User loggedInUser = (User) authentication.getPrincipal();
                modelMap.addAttribute("loggedInUser", loggedInUser);
                logger.debug("Added loggedInUser {} to model", loggedInUser.getUsername());
            } else {
                 logger.warn("User not authenticated or not a User instance in security context for /admin/home");
            }

            // Use viewCount (property name) for sorting instead of price (column name)
            Sort sort = Sort.by(Sort.Direction.DESC, field.orElse("viewCount"));
            Sort sort2 = Sort.by(Sort.Direction.DESC, field2.orElse("score"));
            Sort sort3 = Sort.by(Sort.Direction.DESC, field3.orElse("releaseDate"));

            logger.debug("Fetching counts and data for admin dashboard");
            long movieCount = productRepository.count();
            logger.debug("Movie count: {}", movieCount);
            modelMap.addAttribute("movieCount", movieCount);

            long userCount = userRepository.count();
            logger.debug("User count: {}", userCount);
            modelMap.addAttribute("userCount", userCount);

            long categoryCount = categoryRepository.count();
            logger.debug("Category count: {}", categoryCount);
            modelMap.addAttribute("categoryCount", categoryCount);

            var categories = categoryRepository.findAll();
            logger.debug("Found {} categories", ((List<?>) categories).size());
            modelMap.addAttribute("category", categories);

            var products = productRepository.findAll();
            logger.debug("Found {} products", ((List<?>) products).size());
            modelMap.addAttribute("products", products);

            var sortedByPrice = productRepository.findAll(sort);
            logger.debug("Found {} products sorted by price", ((List<?>) sortedByPrice).size());
            modelMap.addAttribute("price", sortedByPrice);

            var sortedByScore = productRepository.findAll(sort2);
            logger.debug("Found {} products sorted by score", ((List<?>) sortedByScore).size());
            modelMap.addAttribute("score", sortedByScore);

            var sortedByRelease = productRepository.findAll(sort3);
            logger.debug("Found {} products sorted by release date", ((List<?>) sortedByRelease).size());
            modelMap.addAttribute("release", sortedByRelease);

            logger.debug("Successfully loaded all data for admin dashboard");
            return "admin/admin-home";
        } catch (Exception e) {
            logger.error("Error in AdminController.home: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Đã xảy ra lỗi khi tải trang admin: " + e.getMessage());
            return "error-page";
        }
    }

    @RequestMapping(value = "/moviev/{productID}", method = RequestMethod.GET)
    public String showMovie(@PathVariable("productID") String productID, ModelMap modelMap) {
        modelMap.addAttribute("productID", productID);
        try {
            if (productRepository.findById(productID) != null) {
                Product foundProduct = productRepository.findById(productID).get();
                foundProduct.setViewCount(foundProduct.getViewCount() + 1);
                productRepository.save(foundProduct);
            }
        } catch (Exception e) {
            return "MovieVip";
        }
        return "MovieVIP";
    }

    @RequestMapping(value = "/Add", method = RequestMethod.GET)
    public String add(ModelMap modelMap) {
        return "Admin-add";
    }

    @RequestMapping(value = "/Add", method = RequestMethod.POST)
    public String add(ModelMap modelMap, @Valid @ModelAttribute("Category") Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Validation Errors: " + bindingResult.getAllErrors());
            return "Admin-add";
        }
        categoryRepository.save(category);
        return "redirect:/admin/home";
    }

    @RequestMapping(value = "/update/{CategoryID}", method = RequestMethod.GET)
    public String update(ModelMap modelMap, @PathVariable String CategoryID, BindingResult bindingResult) {
        return "Admin-update";
    }

    @RequestMapping(value = "/update/{CategoryID}", method = RequestMethod.POST)
    public String update(ModelMap modelMap, @Valid @ModelAttribute("category") Category category, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("Validation Errors: " + bindingResult.getAllErrors());
            return "Admin-update";
        }
        categoryRepository.save(category);
        return "redirect:/admin/home";
    }

    @GetMapping("/insertProduct")
    public String insertProduct(ModelMap modelMap) {
        modelMap.addAttribute("product", new Product());
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("countries", countryRepository.findAll());
        return "admin/admin-movie-add";
    }
    @RequestMapping(value = "/insertProduct", method = RequestMethod.POST)
    public String insertProduct(ModelMap modelMap,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                @RequestParam(value = "videoFiles", required = false) MultipartFile[] videoFiles,
                                @RequestParam(value = "director", required = false) String director,
                                @RequestParam(value = "actors", required = false) String actors,
                                @RequestParam(value = "selectedCountry", required = false) String selectedCountry) {

        logger.info("POST /admin/insertProduct - Processing form submission");
        logger.debug("Product received: {}", product);
        logger.debug("ImageFile: isEmpty={}, size={}, contentType={}", 
            imageFile != null ? imageFile.isEmpty() : "null", 
            imageFile != null ? imageFile.getSize() : "null", 
            imageFile != null ? imageFile.getContentType() : "null");
        logger.debug("ImageUrl from form: {}", product.getImageUrl());

        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("countries", countryRepository.findAll());

        if (bindingResult.hasErrors()) {
            logger.error("Validation Errors: {}", bindingResult.getAllErrors());
            return "admin/admin-movie-add";
        }

        // Kiểm tra và gán thể loại
        if (product.getCategory() == null || product.getCategory().getCategoryID() == null) {
            logger.error("Category or CategoryID is null");
            modelMap.addAttribute("error", "Thể loại là bắt buộc");
            return "admin/admin-movie-add";
        }
        Category category = categoryRepository.findById(product.getCategory().getCategoryID())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + product.getCategory().getCategoryID()));
        product.setCategory(category);
        logger.info("Set category: {}", category.getCategoryName());

        // Xử lý quốc gia từ selectedCountry
        if (selectedCountry == null || selectedCountry.trim().isEmpty()) {
            logger.error("SelectedCountry is null or empty");
            modelMap.addAttribute("error", "Quốc gia là bắt buộc");
            return "admin/admin-movie-add";
        }

        Country country = null;
        Optional<Country> countryById = countryRepository.findById(selectedCountry);
        if (countryById.isPresent()) {
            country = countryById.get();
            logger.info("Selected country from dropdown (by ID): {}", country.getCountryName());
        } else {
            logger.debug("SelectedCountry is not a valid ID, treating as country name: {}", selectedCountry);
            country = countryRepository.findByCountryName(selectedCountry.trim());
            if (country == null) {
                logger.info("Country '{}' not found, creating new country", selectedCountry);
                country = new Country();
                country.setCountryName(selectedCountry.trim());
                country.setCountryId(UUID.randomUUID().toString());
                country = countryRepository.save(country);
                logger.info("Created new country: {}", country.getCountryName());
            } else {
                logger.info("Found existing country by name: {}", country.getCountryName());
            }
        }

        if (country == null) {
            logger.error("Failed to resolve country for selectedCountry: {}", selectedCountry);
            modelMap.addAttribute("error", "Không thể xác định quốc gia");
            return "admin/admin-movie-add";
        }

        product.setCountry(country);

        // Kiểm tra trùng lặp
        if (productRepository.existsByProductNameAndSeason(product.getProductName(), product.getSeason())) {
            logger.error("Duplicate product name and season: {} - {}", product.getProductName(), product.getSeason());
            modelMap.addAttribute("error", "Duplicate product name and season");
            return "admin/admin-movie-add";
        }

        // Xử lý episode
        if (product.getEpisode().equals("series")) {
            product.setEpisode("series");
        } else {
            product.setEpisode("Movie");
        }

        try {
            // Lưu videoPublicIds (Cloudinary)
            StringBuilder videoPublicIds = new StringBuilder();
            if (videoFiles != null && videoFiles.length > 0) {
                for (MultipartFile videoFile : videoFiles) {
                    if (!videoFile.isEmpty()) {
                        if (!videoFile.getContentType().startsWith("video/")) {
                            logger.error("Invalid video file format: {}", videoFile.getContentType());
                            modelMap.addAttribute("error", "Định dạng video không hợp lệ: " + videoFile.getOriginalFilename());
                            return "admin/admin-movie-add";
                        }
                        if (videoFile.getSize() > 10 * 1024 * 1024*100) { // Giới hạn 10MB cho gói miễn phí
                            logger.error("Video file too large: {} bytes", videoFile.getSize());
                            modelMap.addAttribute("error", "Kích thước video vượt quá 10MB: " + videoFile.getOriginalFilename());
                            return "admin/admin-movie-add";
                        }
                        Map uploadResult = cloudinaryService.uploadVideo(videoFile);
                        String publicId = (String) uploadResult.get("public_id");
                        if (videoPublicIds.length() > 0) {
                            videoPublicIds.append(",");
                        }
                        videoPublicIds.append(publicId);
                        logger.info("Uploaded video: public_id={}", publicId);
                    }
                }
                product.setVideoPublicId(videoPublicIds.toString());
            } else {
                logger.warn("No video files uploaded");
                product.setVideoPublicId("");
            }

            // Xử lý ảnh
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xử lý ảnh từ file upload
                logger.info("Processing uploaded image file: size={}, contentType={}", imageFile.getSize(), imageFile.getContentType());
                if (!imageFile.getContentType().startsWith("image/")) {
                    logger.error("Invalid image file format: {}", imageFile.getContentType());
                    modelMap.addAttribute("error", "Định dạng ảnh không hợp lệ (chỉ chấp nhận ảnh)");
                    return "admin/admin-movie-add";
                }
                if (imageFile.getSize() > 10 * 1024 * 1024) {
                    logger.error("Image file too large: {} bytes", imageFile.getSize());
                    modelMap.addAttribute("error", "Kích thước ảnh vượt quá 10MB");
                    return "admin/admin-movie-add";
                }

                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get("uploads", fileName);
                logger.info("Saving uploaded image to: {}", filePath.toAbsolutePath());
                Files.write(filePath, imageFile.getBytes());
                product.setImageUrl("/uploads/" + fileName);
                logger.info("Uploaded image saved successfully at: {}", product.getImageUrl());
            } else if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty() && !product.getImageUrl().equals("/uploads/default.png")) {
                // Xử lý ảnh từ imageUrl (OMDB)
                String imageUrlFromForm = product.getImageUrl();
                logger.debug("ImageUrl from form (OMDB): {}", imageUrlFromForm);

                try {
                    URL url = URI.create(imageUrlFromForm).toURL();
                    String fileName = UUID.randomUUID().toString() + "_poster.jpg";
                    Path filePath = Paths.get("uploads", fileName);

                    // Tải ảnh từ URL và lưu vào uploads/
                    Files.copy(url.openStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                    logger.info("Downloaded and saved image from OMDB to: {}", filePath.toAbsolutePath());

                    product.setImageUrl("/uploads/" + fileName);
                    logger.info("Updated imageUrl after downloading: {}", product.getImageUrl());
                } catch (Exception e) {
                    logger.error("Failed to download image from OMDB URL {}: {}", imageUrlFromForm, e.getMessage(), e);
                    modelMap.addAttribute("error", "Không thể tải ảnh từ OMDB: " + e.getMessage());
                    return "admin/admin-movie-add";
                }
            } else {
                logger.warn("No image file uploaded and no valid OMDB URL, using default image");
                product.setImageUrl("/uploads/default.png");
            }

            product.setDirector(director);
            product.setActor(actors);
            product.setViewCount(0);
            product.setId(UUID.randomUUID().toString());
            productRepository.save(product);

            logger.info("Created product with ID: {}", product.getId());
            return "redirect:/admin/home";
        } catch (IOException e) {
            logger.error("IO error during form processing: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi khi xử lý tệp: " + e.getMessage());
            return "admin/admin-movie-add";
        } catch (Exception e) {
            logger.error("Unexpected error during form processing: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi không xác định: " + e.getMessage());
            return "admin/admin-movie-add";
        }
    }

    @RequestMapping(value = "/updateProduct/{productID}", method = RequestMethod.GET)
    public String updateProduct(ModelMap modelMap, @PathVariable String productID) {
        try {
            // Add logged-in user to the model
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
                User loggedInUser = (User) authentication.getPrincipal();
                modelMap.addAttribute("loggedInUser", loggedInUser);
            }

            // Get product
            Product product = productRepository.findById(productID)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
            modelMap.addAttribute("product", product);

            // Get categories
            var categories = categoryRepository.findAll();
            modelMap.addAttribute("category", categories);

            // Get countries
            var countries = countryRepository.findAll();
            modelMap.addAttribute("countries", countries);

            // Get episodes if it's a series
            if ("series".equals(product.getEpisode())) {
                var episodes = episodeRepository.findByProductId(productID);
                modelMap.addAttribute("episodes", episodes);
            }

            // Load existing images from uploads folder
            Path uploadsPath = Paths.get("uploads");
            List<String> existingImages = new ArrayList<>();
            if (Files.exists(uploadsPath)) {
                Files.list(uploadsPath)
                    .filter(Files::isRegularFile)
                    .map(path -> "/uploads/" + path.getFileName().toString())
                    .forEach(existingImages::add);
            }
            modelMap.addAttribute("existingImages", existingImages);

            return "admin/admin-movie-update";
        } catch (Exception e) {
            logger.error("Error in updateProduct: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Đã xảy ra lỗi khi tải thông tin phim: " + e.getMessage());
            return "error-page";
        }
    }

    @PostMapping("/updateProduct/{productID}")
    @Transactional
    public String updateProduct(ModelMap modelMap, 
                              @PathVariable String productID, 
                              @ModelAttribute Product product,
                              @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                              @RequestParam("existingImage") String existingImage) {
        logger.info("Attempting to update product with ID: {}", productID);

        Optional<Product> productOptional = productRepository.findById(productID);
        if (!productOptional.isPresent()) {
            logger.error("Product with ID {} not found", productID);
            modelMap.addAttribute("error", "Product not found");
            return "admin/admin-movie-update";
        }

        // Cập nhật thông tin sản phẩm
        Product existingProduct = productOptional.get();
        existingProduct.setProductName(product.getProductName());
        existingProduct.setEpisode(product.getEpisode());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setDirector(product.getDirector());
        existingProduct.setActor(product.getActor());
        existingProduct.setDuration(product.getDuration());

        // Xử lý hình ảnh
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                // Xử lý ảnh mới
                String upload = "uploads/";
                File uploadFile = new File(upload);
                if (!uploadFile.exists()) uploadFile.mkdirs();
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(upload, fileName);
                Files.write(filePath, imageFile.getBytes());
                existingProduct.setImageUrl("/" + upload + fileName);
                logger.info("Updated with new image: {}", existingProduct.getImageUrl());
            } else {
                // Giữ nguyên ảnh cũ
                existingProduct.setImageUrl(existingImage);
                logger.info("Kept existing image: {}", existingImage);
            }
        } catch (Exception e) {
            logger.error("Error processing image: {}", e.getMessage());
            modelMap.addAttribute("error", "Lỗi khi xử lý ảnh: " + e.getMessage());
            return "admin/admin-movie-update";
        }

        // Liên kết với danh mục đã chọn
        if (product.getCategory() != null && product.getCategory().getCategoryID() != null) {
            Optional<Category> categoryOptional = categoryRepository.findById(product.getCategory().getCategoryID());
            if (categoryOptional.isPresent()) {
                existingProduct.setCategory(categoryOptional.get());
                logger.info("Linked product ID: {} with category ID: {}", productID, product.getCategory().getCategoryID());
            } else {
                logger.warn("Category ID: {} not found for product ID: {}", product.getCategory().getCategoryID(), productID);
                modelMap.addAttribute("error", "Selected category not found");
                return "admin/admin-movie-update";
            }
        } else {
            logger.warn("No category selected for product ID: {}", productID);
            modelMap.addAttribute("error", "Please select a category");
            return "admin/admin-movie-update";
        }

        productRepository.save(existingProduct);
        logger.info("Product with ID {} updated successfully", productID);
        return "redirect:/admin/movie";
    }

    // Phương thức giả định để lưu hình ảnh - cần triển khai theo logic của bạn
    private String saveImage(MultipartFile imageFile) throws Exception {
        // Triển khai logic lưu file và trả về URL
        return "/uploads/" + imageFile.getOriginalFilename();
    }
    @RequestMapping(value = "/deleteProduct/{productID}", method = RequestMethod.POST)
    @Transactional
    public String deleteProduct(ModelMap modelMap, @PathVariable String productID) {
        logger.info("Attempting to delete product with ID: {}", productID);
        try {
            if (!productRepository.existsById(productID)) {
                logger.warn("Product with ID {} does not exist", productID);
                modelMap.addAttribute("error", "Product not found with ID: " + productID);
                return "redirect:/admin/movie";
            }

            commentRepository.deleteByProductId(productID);
            episodeRepository.deleteByProductId(productID);

            productRepository.deleteById(productID);
            logger.info("Product with ID {} deleted successfully", productID);
            return "redirect:/admin/movie";
        } catch (Exception e) {
            logger.error("Failed to delete product with ID {}: {}", productID, e.getMessage());
            modelMap.addAttribute("error", "Error deleting product: " + e.getMessage());
            return "redirect:/admin/movie";
        }
    }

    @GetMapping("/movie")
    public String movie(Model model, Authentication authentication) {
        logger.debug("Starting movie method in AdminController");
        if (authentication != null && authentication.isAuthenticated()) {
            User loggedInUser = (User) authentication.getPrincipal();
            logger.debug("Added loggedInUser {} to model", loggedInUser.getUsername());
            model.addAttribute("loggedInUser", loggedInUser);
        }
        
        try {
            var products = productRepository.findAll();
            logger.debug("Found {} products", ((List<?>) products).size());
            model.addAttribute("products", products);
            return "admin/admin-movie";
        } catch (Exception e) {
            logger.error("Error in AdminController.movie: {}", e.getMessage(), e);
            model.addAttribute("error", "Đã xảy ra lỗi khi tải danh sách phim: " + e.getMessage());
            return "error-page";
        }
    }

    @GetMapping("/categories")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String categories(Model model, Authentication authentication) {
        logger.debug("Starting categories method in AdminController");
        if (authentication != null && authentication.isAuthenticated()) {
            User loggedInUser = (User) authentication.getPrincipal();
            logger.debug("Added loggedInUser {} to model", loggedInUser.getUsername());
            model.addAttribute("loggedInUser", loggedInUser);
        }
        
        try {
            // Use JOIN FETCH to eagerly load products
            var categories = categoryRepository.findAllWithProducts();
            model.addAttribute("categories", categories);
            return "admin/admin-category";
        } catch (Exception e) {
            logger.error("Error in AdminController.categories: {}", e.getMessage(), e);
            model.addAttribute("error", "Đã xảy ra lỗi khi tải trang thể loại: " + e.getMessage());
            return "error-page";
        }
    }

    @PostMapping("/category/add")
    public String addCategory(@RequestParam("categoryName") String categoryName, Model model) {
        try {
            // Kiểm tra category đã tồn tại chưa
            if (categoryRepository.existsByCategoryName(categoryName)) {
                model.addAttribute("error", "Thể loại này đã tồn tại!");
                return "redirect:/admin/categories";
            }

            // Tạo category mới
            Category category = new Category();
            category.setCategoryName(categoryName);
            
            // Tự động tạo ID theo thứ tự: C1, C2, C3, C4,...
            long count = categoryRepository.count();
            String newId = "C" + (count + 1);
            category.setCategoryID(newId);

            // Lưu vào database
            categoryRepository.save(category);
            model.addAttribute("success", "Thêm thể loại thành công!");
            
            return "redirect:/admin/categories";
        } catch (Exception e) {
            logger.error("Error adding category: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi thêm thể loại: " + e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/category/update")
    public String updateCategory(@RequestParam("categoryID") String categoryID,
                               @RequestParam("categoryName") String categoryName,
                               @RequestParam(value = "newCategoryId", required = false) String newCategoryId,
                               Model model) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryID);
            if (categoryOpt.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy thể loại!");
                return "redirect:/admin/categories";
            }

            Category oldCategory = categoryOpt.get();
            
            // Kiểm tra tên thể loại mới
            if (!oldCategory.getCategoryName().equals(categoryName) && 
                categoryRepository.existsByCategoryName(categoryName)) {
                model.addAttribute("error", "Tên thể loại này đã tồn tại!");
                return "redirect:/admin/categories";
            }

            // Kiểm tra ID mới nếu có
            if (newCategoryId != null && !newCategoryId.trim().isEmpty() && !newCategoryId.equals(categoryID)) {
                if (categoryRepository.existsById(newCategoryId)) {
                    model.addAttribute("error", "ID thể loại này đã tồn tại!");
                    return "redirect:/admin/categories";
                }
                
                // Tạo category mới với ID mới
                Category newCategory = new Category();
                newCategory.setCategoryID(newCategoryId);
                newCategory.setCategoryName(categoryName);
                newCategory.setDescription(oldCategory.getDescription());
                
                // Lưu category mới
                categoryRepository.save(newCategory);
                
                // Cập nhật tất cả sản phẩm để trỏ đến category mới
                Iterable<Product> products = productRepository.findByCategoryID(categoryID);
                for (Product product : products) {
                    product.setCategoryID(newCategoryId);
                    product.setCategory(newCategory);
                    productRepository.save(product);
                }
                
                // Xóa category cũ
                categoryRepository.delete(oldCategory);
            } else {
                // Nếu không có thay đổi ID, chỉ cập nhật tên
                oldCategory.setCategoryName(categoryName);
                categoryRepository.save(oldCategory);
            }

            model.addAttribute("success", "Cập nhật thể loại thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            logger.error("Error updating category: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi cập nhật thể loại: " + e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @GetMapping("/category/delete/{categoryID}")
    public String deleteCategory(@PathVariable String categoryID, Model model) {
        try {
            Optional<Category> categoryOpt = categoryRepository.findById(categoryID);
            if (categoryOpt.isEmpty()) {
                model.addAttribute("error", "Không tìm thấy thể loại!");
                return "redirect:/admin/categories";
            }

            Category category = categoryOpt.get();
            if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                model.addAttribute("error", "Không thể xóa thể loại này vì đang có phim thuộc thể loại này!");
                return "redirect:/admin/categories";
            }

            categoryRepository.delete(category);
            model.addAttribute("success", "Xóa thể loại thành công!");
            return "redirect:/admin/categories";
        } catch (Exception e) {
            logger.error("Error deleting category: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi xóa thể loại: " + e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @GetMapping("/addEpisode/{ProductID}")
    public String addEpisode(@PathVariable String ProductID, ModelMap modelMap) {
        if (productRepository.findById(ProductID).isPresent()) {
            modelMap.addAttribute("episode", new Episode()); // Khởi tạo đối tượng Episode
            modelMap.addAttribute("product", productRepository.findById(ProductID).get());
        }
        return "admin/admin-episode-add";
    }

    @PostMapping("/addEpisode/{productId}")
    public String addEpisode(
            ModelMap modelMap,
            @PathVariable String productId,
            @Valid @ModelAttribute("episode") Episode episode,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("videoFile") MultipartFile videoFile) {
        logger.debug("POST /admin/addEpisode/{}", productId);

        // Validate videoFile presence
        if (videoFile == null || videoFile.isEmpty()) {
            logger.error("Video file is null or empty");
            bindingResult.rejectValue("videoPublicId", "NotNull.episode.videoPublicId", "Vui lòng tải lên file video!");
        }

        // Check other validation errors
        if (bindingResult.hasErrors()) {
            logger.error("Validation errors: {}", bindingResult.getAllErrors());
            bindingResult.getAllErrors().forEach(error ->
                    logger.error("Field: {}, Error: {}", error.getCode(), error.getDefaultMessage()));
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                modelMap.addAttribute("product", productOpt.get());
            }
            modelMap.addAttribute("error", "Dữ liệu không hợp lệ: " + bindingResult.getAllErrors());
            return "admin/admin-episode-add";
        }

        // Check product existence
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            logger.warn("Product not found for ID: {}", productId);
            modelMap.addAttribute("error", "Không tìm thấy phim để thêm tập!");
            return "admin/admin-episode-add";
        }
        Product product = productOpt.get();
        modelMap.addAttribute("product", product);

        List<Episode> existingEpisodes = episodeRepository.findByProductId(productId);
        boolean isDuplicate = existingEpisodes.stream()
                .anyMatch(e -> e.getEpisode() != null && e.getEpisode().equals(episode.getEpisode()));
        if (isDuplicate) {
            logger.warn("Duplicate episode number {} for product ID: {}", episode.getEpisode(), productId);
            modelMap.addAttribute("error", "Tập phim này đã tồn tại cho phim này!");
            return "admin/admin-episode-add";
        }

        try {
            Map uploadResult = cloudinaryService.uploadVideo(videoFile);
            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");
            episode.setVideoPublicId(publicId); // Store public_id
            logger.info("Uploaded video: public_id={}, url={}", publicId, secureUrl);
        } catch (Exception e) {
            logger.error("Error uploading video: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi khi upload video: " + e.getMessage());
            return "admin/admin-episode-add";
        }

        try {
            if (!imageFile.isEmpty()) {
                Map uploadResult = cloudinaryService.uploadImage(imageFile);
                String publicId = (String) uploadResult.get("public_id");
                String secureUrl = (String) uploadResult.get("secure_url");
                episode.setImageUrl(secureUrl);
                logger.info("Uploaded image: public_id={}, url={}", publicId, secureUrl);
            } else {
                episode.setImageUrl("/Uploads/default.png");
                logger.info("Using default image for episode");
            }
        } catch (Exception e) {
            logger.error("Error uploading image: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return "admin/admin-episode-add";
        }

        // Set product and save episode
        episode.setProduct(product);
        episodeRepository.save(episode);
        logger.info("Episode saved successfully for product ID: {}", productId);

        return "redirect:/admin/updateProduct/" + productId;
    }


    @GetMapping("/updateEpisode/{episodeId}")
    public String updateEpisode(@PathVariable Long episodeId, ModelMap modelMap,@Valid @ModelAttribute("episode")Episode episode) {
        if (episodeRepository.findById(episodeId).isPresent()) {
            Episode episode1 = episodeRepository.findById(episodeId).get();
            modelMap.addAttribute("episode", episode1);
            modelMap.addAttribute("Movie", episode1.getProduct());
        } else {
            modelMap.addAttribute("error", "Tập phim không tồn tại");
            return "redirect:/admin/home";
        }
        return "updateEpisode";
    }


    @PostMapping("/updateEpisode/{episodeId}")
    public String updateEpisode(
            @PathVariable Long episodeId,
            @Valid @ModelAttribute("episode") Episode episode,
            BindingResult bindingResult,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam("videoFile") MultipartFile videoFile,
            @RequestParam(value = "videoPublicId", required = false) String videoPublicIds,
            ModelMap modelMap) {
        if (episodeRepository.findById(episodeId).isPresent()) {
            try {
                Episode existingEpisode = episodeRepository.findById(episodeId).get();


                if (videoPublicIds != null && !videoPublicIds.isEmpty()) {
                    existingEpisode.setVideoPublicId(videoPublicIds);
                } else {
                    logger.error("VideoPublicIds is null or empty");
                    modelMap.addAttribute("error", "Video Public IDs là bắt buộc");
                    return "admin/admin-movie-add";
                }


                if (!imageFile.isEmpty()) {
                    if (!imageFile.getContentType().startsWith("image/")) {
                        modelMap.addAttribute("error", "Invalid image file format");
                        return "admin/admin-movie-add";
                    }
                    if (imageFile.getSize() > 10 * 1024 * 1024) {
                        modelMap.addAttribute("error", "Image file too large");
                        return "admin/admin-movie-add";
                    }
                    String upload = "Uploads/";
                    File uploadFile = new File(upload);
                    if (!uploadFile.exists()) uploadFile.mkdirs();
                    String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                    Path filePath = Paths.get(upload, fileName);
                    Files.write(filePath, imageFile.getBytes());
                    existingEpisode.setImageUrl("/" + upload + fileName);
                } else {
                    String imageUrl = episode.getImageUrl();
                    if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("/uploads/default.png")) {
                        try {
                            URL url = URI.create(imageUrl).toURL();
                            String upload = "Uploads/";
                            File uploadFile = new File(upload);
                            if (!uploadFile.exists()) uploadFile.mkdirs();
                            String fileName = UUID.randomUUID().toString() + ".jpg";
                            Path filePath = Paths.get(upload, fileName);
                            Files.copy(url.openStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                            existingEpisode.setImageUrl("/" + upload + fileName);
                        } catch (Exception e) {
                            System.out.println("Error downloading image from URL: " + e.getMessage());
                            existingEpisode.setImageUrl(existingEpisode.getImageUrl() != null ? existingEpisode.getImageUrl() : "/uploads/default.png");
                        }
                    } else {
                        existingEpisode.setImageUrl(existingEpisode.getImageUrl() != null ? existingEpisode.getImageUrl() : "/uploads/default.png");
                    }
                }


                existingEpisode.setTitle(episode.getTitle());
                existingEpisode.setEpisode(episode.getEpisode());
                existingEpisode.setDuration(episode.getDuration());
                existingEpisode.setEpisodeTitle(episode.getEpisodeTitle());
                existingEpisode.setProduct(existingEpisode.getProduct());


                episodeRepository.save(existingEpisode);
                return "redirect:/admin/home";
            } catch (Exception e) {
                modelMap.addAttribute("error", "Error uploading file: " + e.getMessage());
                modelMap.addAttribute("Movie", episodeRepository.findById(episodeId).get().getProduct());
                return "admin/admin-movie-add";
            }
        }
        modelMap.addAttribute("error", "Tập phim không tồn tại");
        return "redirect:/admin/home";
    }
    @GetMapping("/user")
    public String user(ModelMap modelMap) {
        modelMap.addAttribute("users", userRepository.findAll());
        return "admin/admin-user";
    }
    @RequestMapping(value = "/user/update/{id}", method = RequestMethod.GET)
    public String updateUserForm(ModelMap modelMap, @PathVariable String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        modelMap.addAttribute("user", user);
        String currentRole = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("USER");
        modelMap.addAttribute("currentRole", currentRole);
        return "admin/admin-user-update";
    }

    @RequestMapping(value = "/user/update/{id}", method = RequestMethod.POST)
    public String updateUser(ModelMap modelMap,
                             @PathVariable String id,
                             @Validated(User.UpdateUser.class) @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam("role") String role,
                             @RequestParam(value = "password", required = false) String password) {
        logger.info("POST /admin/user/update/{} - Processing form submission", id);

        if (bindingResult.hasErrors()) {
            System.out.println("Validation Errors: " + bindingResult.getAllErrors());
            modelMap.addAttribute("currentRole", role);
            return "admin/admin-user-update";
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
            if (!existingUser.get().getId().equals(id)) {
                modelMap.addAttribute("error", "Tên đăng nhập đã tồn tại");
                modelMap.addAttribute("currentRole", role);
                return "admin/admin-user-update";
            }
        }

        User currentUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        try {
            currentUser.setUsername(user.getUsername());
            currentUser.setFullname(user.getFullname());
            currentUser.setEmail(user.getEmail());
            currentUser.setTelephone(user.getTelephone());
            currentUser.setGender(user.isGender());
            if (user.getBirthday() != null) {
                currentUser.setBirthday(user.getBirthday());
            }
            currentUser.setAddress(user.getAddress());
            currentUser.setEnabled(user.isEnabled());

            if (password != null && !password.trim().isEmpty()) {
                currentUser.setPassword(passwordEncoder.encode(password));
            }

            Role userRole = roleRepository.findByName(role)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
            currentUser.setRoles(new HashSet<>(Collections.singletonList(userRole)));

            // Lưu vào cơ sở dữ liệu
            userRepository.save(currentUser);
            return "redirect:/admin/user";
        } catch (Exception e) {
            modelMap.addAttribute("error", "Lỗi khi cập nhật người dùng: " + e.getMessage());
            modelMap.addAttribute("currentRole", role);
            return "admin/admin-user-update";
        }
    }
    @GetMapping("/episode/{ProductID}")
    public String episode(ModelMap modelMap,@PathVariable String ProductID) {
        List<Episode> episodes = episodeRepository.findByProductId(ProductID);
        modelMap.addAttribute("episodes", episodes);
        return "admin-episode";
    }
}
