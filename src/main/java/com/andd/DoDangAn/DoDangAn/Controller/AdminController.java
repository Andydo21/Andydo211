package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.*;
import com.andd.DoDangAn.DoDangAn.repository.*;
import com.andd.DoDangAn.DoDangAn.services.CloudinaryService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
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

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap, @RequestParam("field") Optional<String> field, @RequestParam("field2") Optional<String> field2, @RequestParam("field3") Optional<String> field3) {
        Sort sort = Sort.by(Sort.Direction.DESC, field.orElse("viewCount"));
        Sort sort2 = Sort.by(Sort.Direction.DESC, field2.orElse("score"));
        Sort sort3 = Sort.by(Sort.Direction.DESC, field3.orElse("releaseDate"));
        modelMap.addAttribute("movieCount",userRepository.count());
        modelMap.addAttribute("userCount",productRepository.count());
        modelMap.addAttribute("categoryCount",categoryRepository.count());
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("products", productRepository.findAll());
        modelMap.addAttribute("price", productRepository.findAll(sort));
        modelMap.addAttribute("score", productRepository.findAll(sort2));
        modelMap.addAttribute("release", productRepository.findAll(sort3));
        modelMap.addAttribute("movieCount", productRepository.count());
        return "admin-home";
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

    @RequestMapping(value = "/insertProduct", method = RequestMethod.GET)
    public String insertProduct(ModelMap modelMap) {
        modelMap.addAttribute("product", new Product());
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("countries", countryRepository.findAll());
        return "admin-movie-add";
    }
    @RequestMapping(value = "/insertProduct", method = RequestMethod.POST)
    public String insertProduct(ModelMap modelMap,
                                @Valid @ModelAttribute("product") Product product,
                                BindingResult bindingResult,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                @RequestParam(value = "videoPublicId", required = false) String videoPublicIds,
                                @RequestParam(value = "director", required = false) String director,
                                @RequestParam(value = "actors", required = false) String actors,
                                @RequestParam(value = "selectedCountry", required = false) String selectedCountry) {

        logger.info("POST /admin/insertProduct - Processing form submission");
        logger.debug("Product received: {}", product);
        logger.debug("ImageFile: isEmpty={}, size={}, contentType={}", imageFile.isEmpty(), imageFile.getSize(), imageFile.getContentType());
        logger.debug("ImageUrl from form: {}", product.getImageUrl());
        logger.debug("VideoPublicIds: {}", videoPublicIds);

        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("countries", countryRepository.findAll());

        if (bindingResult.hasErrors()) {
            logger.error("Validation Errors: {}", bindingResult.getAllErrors());
            return "admin-movie-add";
        }

        // Kiểm tra và gán thể loại
        if (product.getCategory() == null || product.getCategory().getCategoryID() == null) {
            logger.error("Category or CategoryID is null");
            modelMap.addAttribute("error", "Thể loại là bắt buộc");
            return "admin-movie-add";
        }
        Category category = categoryRepository.findById(product.getCategory().getCategoryID())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + product.getCategory().getCategoryID()));
        product.setCategory(category);
        logger.info("Set category: {}", category.getCategoryName());

        // Xử lý quốc gia từ selectedCountry
        if (selectedCountry == null || selectedCountry.trim().isEmpty()) {
            logger.error("SelectedCountry is null or empty");
            modelMap.addAttribute("error", "Quốc gia là bắt buộc");
            return "admin-movie-add";
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
            return "admin-movie-add";
        }

        product.setCountry(country);

        // Kiểm tra trùng lặp
        if (productRepository.existsByProductNameAndSeason(product.getProductName(), product.getSeason())) {
            logger.error("Duplicate product name and season: {} - {}", product.getProductName(), product.getSeason());
            modelMap.addAttribute("error", "Duplicate product name and season");
            return "admin-movie-add";
        }

        // Xử lý episode
        if (product.getEpisode().equals("series")) {
            product.setEpisode("series");
        } else {
            product.setEpisode("Movie");
        }

        try {
            // Lưu videoPublicIds (Cloudinary)
            if (videoPublicIds != null && !videoPublicIds.isEmpty()) {
                product.setVideoPublicId(videoPublicIds);
            } else {
                logger.error("VideoPublicIds is null or empty");
                modelMap.addAttribute("error", "Video Public IDs là bắt buộc");
                return "admin-movie-add";
            }

            // Xử lý ảnh
            String uploadDir = "uploads/";
            File uploadDirFile = new File(uploadDir);
            if (!uploadDirFile.exists()) {
                boolean created = uploadDirFile.mkdirs();
                logger.info("Created upload directory: {} (success={})", uploadDir, created);
                if (!created) {
                    logger.error("Failed to create upload directory: {}", uploadDir);
                    modelMap.addAttribute("error", "Không thể tạo thư mục lưu ảnh");
                    return "admin-movie-add";
                }
            }
            if (!uploadDirFile.canWrite()) {
                logger.error("Upload directory is not writable: {}", uploadDir);
                modelMap.addAttribute("error", "Thư mục uploads không có quyền ghi");
                return "admin-movie-add";
            }

            if (!imageFile.isEmpty()) {
                // Xử lý ảnh từ file upload
                logger.info("Processing uploaded image file: size={}, contentType={}", imageFile.getSize(), imageFile.getContentType());
                if (!imageFile.getContentType().startsWith("image/")) {
                    logger.error("Invalid image file format: {}", imageFile.getContentType());
                    modelMap.addAttribute("error", "Định dạng ảnh không hợp lệ (chỉ chấp nhận ảnh)");
                    return "admin-movie-add";
                }
                if (imageFile.getSize() > 10 * 1024 * 1024) {
                    logger.error("Image file too large: {} bytes", imageFile.getSize());
                    modelMap.addAttribute("error", "Kích thước ảnh vượt quá 10MB");
                    return "admin-movie-add";
                }

                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(uploadDir, fileName);
                logger.info("Saving uploaded image to: {}", filePath.toAbsolutePath());
                Files.write(filePath, imageFile.getBytes());
                product.setImageUrl("/" + uploadDir + fileName);
                logger.info("Uploaded image saved successfully at: {}", product.getImageUrl());
            } else {
                // Xử lý ảnh từ imageUrl (OMDB)
                String imageUrlFromForm = product.getImageUrl();
                logger.debug("ImageUrl from form (OMDB): {}", imageUrlFromForm);

                if (imageUrlFromForm != null && !imageUrlFromForm.trim().isEmpty() && !imageUrlFromForm.equals("/uploads/default.png")) {
                    logger.info("Attempting to download image from OMDB URL: {}", imageUrlFromForm);
                    try {
                        URL url = new URL(imageUrlFromForm);
                        String fileName = UUID.randomUUID().toString() + "_poster.jpg"; // Tên mặc định
                        Path filePath = Paths.get(uploadDir, fileName);

                        // Tải ảnh từ URL và lưu vào uploads/
                        Files.copy(url.openStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("Downloaded and saved image from OMDB to: {}", filePath.toAbsolutePath());

                        product.setImageUrl("/" + uploadDir + fileName);
                        logger.info("Updated imageUrl after downloading: {}", product.getImageUrl());
                    } catch (Exception e) {
                        logger.error("Failed to download image from OMDB URL {}: {}", imageUrlFromForm, e.getMessage(), e);
                        modelMap.addAttribute("error", "Không thể tải ảnh từ OMDB: " + e.getMessage());
                        return "admin-movie-add";
                    }
                } else {
                    logger.warn("No image file uploaded and no valid OMDB URL, using default image");
                    product.setImageUrl("/uploads/default.png");
                }
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
            return "admin-movie-add";
        } catch (Exception e) {
            logger.error("Unexpected error during form processing: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi không xác định: " + e.getMessage());
            return "admin-movie-add";
        }
    }

    @RequestMapping(value = "/updateProduct/{productID}", method = RequestMethod.GET)
    public String updateProduct(ModelMap modelMap, @PathVariable String productID) {
        Product product = productRepository.findById(productID)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product ID"));
        modelMap.addAttribute("product", product);
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("countries", countryRepository.findAll());
        return "admin-movie-update";
    }

    @PostMapping("/update/{productID}")
    @Transactional
    public String updateProduct(ModelMap modelMap, @PathVariable String productID, @ModelAttribute Product product, @RequestParam("imageFile") MultipartFile imageFile) {
        logger.info("Attempting to update product with ID: {}", productID);

        Optional<Product> productOptional = productRepository.findById(productID);
        if (!productOptional.isPresent()) {
            logger.error("Product with ID {} not found", productID);
            modelMap.addAttribute("error", "Product not found");
            return "admin/updateProduct";
        }

        // Cập nhật thông tin sản phẩm
        Product existingProduct = productOptional.get();
        existingProduct.setProductName(product.getProductName());
        existingProduct.setEpisode(product.getEpisode());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setDirector(product.getDirector());
        existingProduct.setActor(product.getActor());
        existingProduct.setDuration(product.getDuration());

        // Xử lý hình ảnh nếu có
        if (!imageFile.isEmpty()) {
            try {
                // Logic lưu hình ảnh (giả sử bạn đã có phương thức lưu file)
                String imageUrl = saveImage(imageFile); // Cần triển khai phương thức này
                existingProduct.setImageUrl(imageUrl);
            } catch (Exception e) {
                logger.error("Failed to upload image for product ID: {}", productID, e);
                modelMap.addAttribute("error", "Failed to upload image");
                return "admin/updateProduct";
            }
        }

        // Liên kết với danh mục đã chọn
        String categoryID = product.getCategoryID();
        if (categoryID != null && !categoryID.isEmpty()) {
            Optional<Category> categoryOptional = categoryRepository.findById(categoryID);
            if (categoryOptional.isPresent()) {
                existingProduct.setCategory(categoryOptional.get());
                logger.info("Linked product ID: {} with category ID: {}", productID, categoryID);
            } else {
                logger.warn("Category ID: {} not found for product ID: {}", categoryID, productID);
                modelMap.addAttribute("error", "Selected category not found");
                return "admin/updateProduct";
            }
        } else {
            logger.warn("No category selected for product ID: {}", productID);
            modelMap.addAttribute("error", "Please select a category");
            return "admin/updateProduct";
        }

        productRepository.save(existingProduct);
        logger.info("Product with ID {} updated successfully", productID);
        return "redirect:/admin/home";
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
                return "redirect:/admin/home";
            }

            commentRepository.deleteByProductId(productID);
            episodeRepository.deleteByProductId(productID); // Giả sử có phương thức này

            productRepository.deleteById(productID);
            logger.info("Product with ID {} deleted successfully", productID);
            return "redirect:/admin/home";
        } catch (DataIntegrityViolationException e) {
            logger.error("Failed to delete product with ID {} due to foreign key constraint: {}", productID, e.getMessage());
            modelMap.addAttribute("error", "Cannot delete product because it is referenced by other records (e.g., episodes). Please delete related episodes first.");
            return "redirect:/admin/home";
        } catch (Exception e) {
            logger.error("Failed to delete product with ID {}: {}", productID, e.getMessage());
            modelMap.addAttribute("error", "Error deleting product: " + e.getMessage());
            return "redirect:/admin/home";
        }
    }

    @RequestMapping(value = "/movie", method = RequestMethod.GET)
    public String MovieList(ModelMap modelMap) {
        modelMap.addAttribute("movies", productRepository.findAll());
        return "admin-movie";
    }

    @GetMapping("/addEpisode/{ProductID}")
    public String addEpisode(@PathVariable String ProductID, ModelMap modelMap) {
        if (productRepository.findById(ProductID).isPresent()) {
            modelMap.addAttribute("episode", new Episode()); // Khởi tạo đối tượng Episode
            modelMap.addAttribute("product", productRepository.findById(ProductID).get());
        }
        return "admin-episode-add";
    }

    @PostMapping("/addEpisode/{ProductID}")
    public String addEpisode(ModelMap modelMap,@PathVariable String ProductID, @Valid @ModelAttribute("episode") Episode episode, BindingResult bindingResult,@RequestParam("imageFile") MultipartFile imageFile,@RequestParam(value = "videoPublicId", required = false) String videoPublicIds,
                             @RequestParam("videoFile") MultipartFile videoFile) {
        if (episodeRepository.findByProductId(ProductID) != null) {
            Optional<Episode> productOptional = episodeRepository.findById(episode.getId());
            if (!productOptional.isPresent()) {
                logger.error("Product with ID {} not found",episode.getId() );
                modelMap.addAttribute("error", "Product not found");
                return "admin/updateProduct";
            }

            Episode existingProduct = productOptional.get();
            existingProduct.setEpisodeTitle(episode.getEpisodeTitle());
            existingProduct.setEpisode(episode.getEpisode());
            existingProduct.setDuration(episode.getDuration());
            try {
                if (videoPublicIds != null && !videoPublicIds.isEmpty()) {
                    episode.setVideoPublicId(videoPublicIds);
                } else {
                    logger.error("VideoPublicIds is null or empty");
                    modelMap.addAttribute("error", "Video Public IDs là bắt buộc");
                    return "admin-episode-add";
                }
            } catch (Exception e) {
                modelMap.addAttribute("error", "Error uploading file: " + e.getMessage());
                return "admin-episode-add";
            }

        }
        episode.setTitle(episode.getTitle());
        episode.setEpisode(episode.getEpisode());
        episode.setDuration(episode.getDuration());
        episode.setEpisodeTitle(episode.getEpisodeTitle());
        episode.setProduct(productRepository.findById(ProductID).get());
        episodeRepository.save(episode);
        return "redirect:/admin/home";
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
                        return "admin-movie-add";
                    }


                if (!imageFile.isEmpty()) {
                    if (!imageFile.getContentType().startsWith("image/")) {
                        modelMap.addAttribute("error", "Invalid image file format");
                        return "admin-movie-add";
                    }
                    if (imageFile.getSize() > 10 * 1024 * 1024) {
                        modelMap.addAttribute("error", "Image file too large");
                        return "admin-movie-add";
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
                            URL url = new URL(imageUrl);
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
                return "admin-movie-add";
            }
        }
        modelMap.addAttribute("error", "Tập phim không tồn tại");
        return "redirect:/admin/home";
    }
    @GetMapping("/user")
    public String user(ModelMap modelMap) {
        modelMap.addAttribute("user",userRepository.findAll());
        return "admin-user";
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
        return "admin-user-update";
    }

    @RequestMapping(value = "/user/update/{id}", method = RequestMethod.POST)
    public String updateUser(ModelMap modelMap,
                             @PathVariable String id,
                             @Valid @ModelAttribute("user") User user,
                             BindingResult bindingResult,
                             @RequestParam("role") String role,
                             @RequestParam(value = "password", required = false) String password) {
        logger.info("POST /admin/user/update/{} - Processing form submission", id);


        if (bindingResult.hasErrors()) {
            System.out.println("Validation Errors: " + bindingResult.getAllErrors());
            modelMap.addAttribute("currentRole", role);
            return "admin-user-update";
        }


        if (userRepository.existsByUsername(user.getUsername())) {
            Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
            if (!existingUser.get().getId().equals(id)) {
                modelMap.addAttribute("error", "Tên đăng nhập đã tồn tại");
                modelMap.addAttribute("currentRole", role);
                return "admin-user-update";
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
            currentUser.setBirthday(user.getBirthday());
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
            return "admin-user-update";
        }
    }
    @GetMapping("/episode/{ProductID}")
    public String episode(ModelMap modelMap,@PathVariable String ProductID) {
        List<Episode> episodes = episodeRepository.findByProductId(ProductID);
        modelMap.addAttribute("episodes", episodes);
        return "admin-episode";
    }
    @RequestMapping(value = "/user/save", method = RequestMethod.POST)
    public String saveUser(ModelMap modelMap,
                           @Valid @ModelAttribute("user") User user,
                           BindingResult bindingResult,
                           @RequestParam("password") String password,
                           @RequestParam("role") String role) {

        logger.info("POST /admin/user/save - Processing form submission");


        if (bindingResult.hasErrors()) {
            logger.error("Validation errors occurred: {}", bindingResult.getAllErrors());
            return "admin-user-add";
        }


        if (userRepository.existsByUsername(user.getUsername())) {
            logger.error("Username already exists: {}", user.getUsername());
            modelMap.addAttribute("error", "Tên đăng nhập đã tồn tại");
            return "admin-user-add";
        }


        if (userRepository.existsByEmail(user.getEmail())) {
            logger.error("Email already exists: {}", user.getEmail());
            modelMap.addAttribute("error", "Email đã tồn tại");
            return "admin-user-add";
        }

        try {

            if (password == null || password.trim().isEmpty()) {
                logger.error("Password is required");
                modelMap.addAttribute("error", "Mật khẩu là bắt buộc");
                return "admin-user-add";
            }
            user.setPassword(passwordEncoder.encode(password));

            Role userRole = roleRepository.findByName(role)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid role: " + role));
            user.setRoles(new HashSet<>(Collections.singletonList(userRole)));


            user.setId(UUID.randomUUID().toString());


            userRepository.save(user);
            logger.info("User created successfully with ID: {}", user.getId());
            return "redirect:/admin/user";
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi khi tạo người dùng: " + e.getMessage());
            return "admin-user-add";
        }
    }
}
//package com.andd.DoDangAn.DoDangAn.Controller;
//
//import com.andd.DoDangAn.DoDangAn.models.Category;
//import com.andd.DoDangAn.DoDangAn.models.Product;
//import com.andd.DoDangAn.DoDangAn.repository.CategoryRepository;
//import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
//import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
//import jakarta.validation.Valid;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.*;
//
//@RestController
//@RequestMapping("/api/admin")
//public class AdminController {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @GetMapping("/home")
//    public Map<String, Object> home(@RequestParam Optional<String> field,
//                                    @RequestParam Optional<String> field2,
//                                    @RequestParam Optional<String> field3) {
//        Map<String, Object> data = new HashMap<>();
//        Sort sort = Sort.by(Sort.Direction.DESC, field.orElse("price"));
//        Sort sort2 = Sort.by(Sort.Direction.DESC, field2.orElse("score"));
//        Sort sort3 = Sort.by(Sort.Direction.DESC, field3.orElse("releaseDate"));
//
//        data.put("category", categoryRepository.findAll());
//        data.put("products", productRepository.findAll());
//        data.put("price", productRepository.findAll(sort));
//        data.put("score", productRepository.findAll(sort2));
//        data.put("release", productRepository.findAll(sort3));
//        return data;
//    }
//
//    @GetMapping("/moviev/{productID}")
//    public Product showMovie(@PathVariable String productID) {
//        return productRepository.findById(productID).map(product -> {
//            product.setPrice(product.getPrice() + 1);
//            return productRepository.save(product);
//        }).orElse(null);
//    }
//
//    @GetMapping("/changeCategory/{productID}")
//    public Map<String, Object> updateProduct(@PathVariable String productID) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("categories", categoryRepository.findAll());
//        data.put("product", productRepository.findById(productID).orElse(null));
//        return data;
//    }
//
//    @PostMapping("/insertProduct")
//    public Map<String, Object> insertProduct(@Valid @ModelAttribute Product product,
//                                             @RequestParam("imageFile") MultipartFile imageFile,
//                                             @RequestParam("videoFile") MultipartFile videoFile) {
//        Map<String, Object> response = new HashMap<>();
//
//        try {
//            if (!videoFile.isEmpty()) {
//                String uploadDir = "upload_dir/";
//                File dir = new File(uploadDir);
//                if (!dir.exists()) dir.mkdirs();
//
//                String fileName = UUID.randomUUID() + "_" + videoFile.getOriginalFilename();
//                Path path = Paths.get(uploadDir + fileName);
//                Files.write(path, videoFile.getBytes());
//                product.setVideoUrl("/" + uploadDir + fileName);
//            }
//
//            if (!imageFile.isEmpty()) {
//                String upload = "uploads/";
//                File dir = new File(upload);
//                if (!dir.exists()) dir.mkdirs();
//
//                String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
//                Path path = Paths.get(upload + fileName);
//                Files.write(path, imageFile.getBytes());
//                product.setImageUrl("/" + upload + fileName);
//            } else {
//                product.setImageUrl("/uploads/default.png");
//            }
//
//            if (product.getEpisode() != null && !product.getEpisode().equals("")) {
//                try {
//                    double episodeNumber = Double.parseDouble(product.getEpisode());
//                    if (episodeNumber < 0) {
//                        response.put("error", "Episode must be a positive number");
//                        return response;
//                    }
//                    if (productRepository.findByProductName(product.getProductName()) != null &&
//                        productRepository.existsByEpisode(product.getEpisode())) {
//                        response.put("error", "Episode already exists");
//                        return response;
//                    }
//                } catch (NumberFormatException e) {
//                    response.put("error", "Invalid episode format");
//                    return response;
//                }
//            } else {
//                product.setEpisode("Movie");
//            }
//
//            product.setId(UUID.randomUUID().toString());
//            product.setPrice(0);
//            productRepository.save(product);
//            response.put("message", "Insert success");
//            return response;
//
//        } catch (Exception e) {
//            response.put("error", e.toString());
//            return response;
//        }
//    }
//
//    @PostMapping("/updateProduct/{productID}")
//    public Product updateProduct(@Valid @RequestBody Product product, @PathVariable String productID) {
//        return productRepository.findById(productID).map(foundProduct -> {
//            if (!product.getProductName().trim().isEmpty()) foundProduct.setProductName(product.getProductName());
//            if (!product.getCategoryID().isEmpty()) foundProduct.setCategoryID(product.getCategoryID());
//            if (!product.getDescription().trim().isEmpty()) foundProduct.setDescription(product.getDescription());
//            if (product.getPrice() >= 0) foundProduct.setPrice(product.getPrice());
//            if (!product.getImageUrl().isEmpty()) foundProduct.setImageUrl(product.getImageUrl());
//            if (!product.getVideoUrl().isEmpty()) foundProduct.setVideoUrl(product.getVideoUrl());
//            if (!product.getEpisode().isEmpty()) foundProduct.setEpisode(product.getEpisode());
//            return productRepository.save(foundProduct);
//        }).orElse(null);
//    }
//
//    @DeleteMapping("/deleteProduct/{productID}")
//    public Map<String, String> deleteProduct(@PathVariable String productID) {
//        productRepository.deleteById(productID);
//        return Collections.singletonMap("message", "Deleted successfully");
//    }
//}
