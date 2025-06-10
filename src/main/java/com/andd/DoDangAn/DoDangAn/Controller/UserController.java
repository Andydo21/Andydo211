package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.*;
import com.andd.DoDangAn.DoDangAn.repository.*;
import com.andd.DoDangAn.DoDangAn.services.CloudinaryService;
import com.andd.DoDangAn.DoDangAn.services.CustomUserDetailsService;
import com.andd.DoDangAn.DoDangAn.Util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping(path = "/user")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");

    @Autowired
    CloudinaryService cloudinaryService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    CustomUserDetailsService userDetailsService;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MovieListRepository movieListRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    UserMovieListRepository userMovieListRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired
    private JwtUtil jwtUtil;

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public String home(ModelMap modelMap, @RequestParam("field") Optional<String> field,
            @RequestParam("field2") Optional<String> field2, @RequestParam("field3") Optional<String> field3, HttpSession session) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            modelMap.addAttribute("loggedInUser", loggedInUser);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            String username = authentication.getName();
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent()) {
                modelMap.addAttribute("loggedInUser", user.get());
            }
        }
        Sort sort = Sort.by(Sort.Direction.DESC, field.orElse("viewCount"));
        Sort sort2 = Sort.by(Sort.Direction.DESC, field2.orElse("score"));
        Sort sort3 = Sort.by(Sort.Direction.DESC, field3.orElse("releaseDate"));
        List<Product> list = productRepository.findAll();
        modelMap.addAttribute("category", categoryRepository.findAll());
        modelMap.addAttribute("products", list);
        modelMap.addAttribute("viewCount", productRepository.findAll(sort));
        modelMap.addAttribute("score", productRepository.findAll(sort2));
        Sort sortByReleaseDate = Sort.by(Sort.Direction.DESC, field3.orElse("releaseDate"));
        Sort sortByView = Sort.by(Sort.Direction.DESC, field.orElse("viewCount"));

        List<Product> bannerMovies = StreamSupport.stream(productRepository.findAll(sortByReleaseDate).spliterator(), false)
                .collect(Collectors.toList());
        if (bannerMovies.size() > 5) {
            bannerMovies = bannerMovies.subList(0, 5);
        }

        modelMap.addAttribute("bannerMovies", bannerMovies);

        List<Product> trendingMovies = StreamSupport.stream(productRepository.findAll(sort2).spliterator(), false)
                .collect(Collectors.toList());
        if (trendingMovies.size() > 5) {
            trendingMovies = trendingMovies.subList(0, 5);
        }
        modelMap.addAttribute("trendingMovies", trendingMovies);

        List<Product> Action = StreamSupport.stream(productRepository.findByCategoryID("C1").spliterator(), false)
                .collect(Collectors.toList());
        if (Action.size() > 5) {
            Action = Action.subList(0, 5);
        }

        List<Product> Comedy = StreamSupport.stream(productRepository.findByCategoryID("C2").spliterator(), false)
                .collect(Collectors.toList());
        if (Comedy.size() > 5) {
            Comedy = Comedy.subList(0, 5);
        }

        List<Product> Cartoon = StreamSupport.stream(productRepository.findByCategoryID("C3").spliterator(), false)
                .collect(Collectors.toList());
        if (Cartoon.size() > 5) {
            Cartoon = Cartoon.subList(0, 5);
        }

        List<Product> Horror = StreamSupport.stream(productRepository.findByCategoryID("C4").spliterator(), false)
                .collect(Collectors.toList());
        if (Horror.size() > 5) {
            Horror = Horror.subList(0, 5);
        }

        modelMap.addAttribute("Cartoon", Cartoon);
        modelMap.addAttribute("Comedy", Comedy);
        modelMap.addAttribute("Action", Action);
        modelMap.addAttribute("Horror", Horror);

        return "home";
    }

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String showRegisterForm(ModelMap modelMap) {
        if (!modelMap.containsAttribute("user")) {
            modelMap.addAttribute("user", new User());
        }
        return "register";
    }

    @PostMapping(value = "/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
            ModelMap modelMap, RedirectAttributes redirectAttributes) {
        // Custom validation
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "username", "Tên đăng nhập không được để trống!"));
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "password", "Mật khẩu không được để trống!"));
        }
        if (user.getFullname() == null || user.getFullname().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "fullname", "Họ tên không được để trống!"));
        }
        // Gender không cần kiểm tra null vì nó là boolean và đã được đảm bảo bởi form (required)
        if (user.getBirthday() == null) {
            bindingResult.addError(new FieldError("user", "birthday", "Ngày sinh không được để trống!"));
        } else {
            LocalDate birthday = user.getBirthday();
            LocalDate today = LocalDate.now();
            if (birthday.isAfter(today)) {
                bindingResult.addError(new FieldError("user", "birthday", "Ngày sinh không được là ngày trong tương lai!"));
            }
        }
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "address", "Địa chỉ không được để trống!"));
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "email", "Email không được để trống!"));
        } else if (!EMAIL_PATTERN.matcher(user.getEmail()).matches()) {
            bindingResult.addError(new FieldError("user", "email", "Email không đúng định dạng!"));
        }
        if (user.getTelephone() == null || user.getTelephone().trim().isEmpty()) {
            bindingResult.addError(new FieldError("user", "telephone", "Số điện thoại không được để trống!"));
        } else if (!PHONE_PATTERN.matcher(user.getTelephone()).matches()) {
            bindingResult.addError(new FieldError("user", "telephone", "Số điện thoại phải có 10 chữ số!"));
        }

        if (bindingResult.hasErrors()) {
            logger.warn("Registration failed for user {}: Validation errors", user.getUsername());
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.user", bindingResult);
            redirectAttributes.addFlashAttribute("user", user);
            return "redirect:/user/register";
        }

        try {
            userDetailsService.loadUserByUsername(user.getUsername());
            logger.warn("Registration failed: Username {} already exists", user.getUsername());
            modelMap.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "register";
        } catch (UsernameNotFoundException e) {
            user.setId(UUID.randomUUID().toString());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(true);

            Optional<Role> userRole = roleRepository.findByName("USER");
            Role role;
            if (userRole.isEmpty()) {
                role = new Role();
                role.setId("r2");
                role.setName("USER");
                roleRepository.save(role);
            } else {
                role = userRole.get();
            }

            user.setRoles(new HashSet<>());
            user.getRoles().add(role);

            userDetailsService.saveUser(user);
            logger.info("User {} registered successfully", user.getUsername());
            return "redirect:/user/login";
        }
    }

    //@RequestMapping(value="/movie",method = RequestMethod.GET)
    //        public String movie(
    //            ModelMap modelMap,
    //                    @RequestParam(value = "sort", required = false, defaultValue = "releaseDate:DESC") String sort,
    //                   @RequestParam(value = "category", required = false) String category,
    //                   @RequestParam(value = "country", required = false) String country,
    //                    HttpSession session) {
    //                User loggedInUser = (User) session.getAttribute("loggedInUser");
    //                if (loggedInUser != null) {
    //                    modelMap.addAttribute("loggedInUser", loggedInUser);
    //                }
    //
    //                // Xử lý sắp xếp
    //                Sort sortOrder = Sort.unsorted();
    //                if (sort != null && !sort.isEmpty()) {
    //                    String[] sortParts = sort.split(":");
    //                    String field = sortParts[0].trim();
    //                    Sort.Direction direction = sortParts.length > 1 && sortParts[1].trim().equalsIgnoreCase("ASC")
    //                            ? Sort.Direction.ASC
    //                            : Sort.Direction.DESC;
    //                    sortOrder = Sort.by(direction, field);
    //                }
    //
    //                // Xử lý lọc
    //                Iterable<Product> products;
    //                if (category != null || country != null) {
    //                    Specification<Product> spec = ProductSpecifications.buildSpecification(category, country);
    //                    products = productRepository.findAll(spec, sortOrder);
    //                } else {
    //                    products = productRepository.findAll(sortOrder);
    //                }
    //        }
    @RequestMapping(value = "/getProductsByCategoryID/{categoryID}", method = RequestMethod.GET)
    public String getProductsByCategoryID(ModelMap modelMap, @PathVariable String categoryID) {
        Iterable<Product> products = productRepository.findByCategoryID(categoryID);
        modelMap.addAttribute("products", products);
        return "productList";
    }

    @RequestMapping(value = "/movielist", method = RequestMethod.GET)
    public String getMovieList(ModelMap modelMap, HttpSession session) {
        logger.info("Processing request for /user/movielist");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.debug("Authentication object: {}", authentication);

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Invalid authentication for /user/movielist: null={}, authenticated={}, principal={}",
                    authentication == null, authentication != null && authentication.isAuthenticated(),
                    authentication != null ? authentication.getPrincipal() : "null");
            return "redirect:/user/login?error=PleaseLogin";
        }

        String username = authentication.getName();
        logger.info("Authenticated user: {}", username);
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            logger.info("User found in database: {}", username);
            User loggedInUser = userOptional.get();
            modelMap.addAttribute("loggedInUser", loggedInUser);
            
            // Explicitly add loggedInUser to session for navbar access
            session.setAttribute("loggedInUser", loggedInUser);
            
            // Get all UserMovieList entries for the user
            List<UserMovieList> userMovieLists = userMovieListRepository.findByUserId(loggedInUser.getId());
            
            // Extract the Products from the UserMovieList entries
            List<Product> movies = userMovieLists.stream()
                                    .map(UserMovieList::getProduct)
                                    .collect(Collectors.toList());
                                    
            modelMap.addAttribute("movieList", movies);
            
            return "mylist";
        } else {
            logger.warn("User not found in database: {}", username);
            return "redirect:/user/login?error=PleaseLogin";
        }
    }

    @PostMapping("/add-to-list")
    @Transactional
    public String addMovieToList(@RequestParam String movieId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để thêm phim vào danh sách.");
            return "redirect:/user/login";
        }

        try {
            // Kiểm tra phim tồn tại
            String checkMovieQuery = "SELECT COUNT(*) FROM Products WHERE productID = ? COLLATE utf8mb4_unicode_ci";
            Long movieCount = ((Number) entityManager.createNativeQuery(checkMovieQuery)
                    .setParameter(1, movieId)
                    .getSingleResult()).longValue();

            if (movieCount == 0) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy phim");
                return "redirect:/movie/preshow/" + movieId;
            }

            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

            // Kiểm tra phim đã có trong danh sách chưa
            String checkListQuery = "SELECT COUNT(*) FROM usermovielist WHERE userId = ? AND productId = ? COLLATE utf8mb4_unicode_ci";
            Long count = ((Number) entityManager.createNativeQuery(checkListQuery)
                    .setParameter(1, user.getId())
                    .setParameter(2, movieId)
                    .getSingleResult()).longValue();

            if (count > 0) {
                redirectAttributes.addFlashAttribute("error", "Phim đã có trong danh sách của bạn.");
                return "redirect:/movie/preshow/" + movieId;
            }

            // Fetch the Product entity
            Product product = productRepository.findById(movieId)
                              .orElseThrow(() -> new IllegalArgumentException("Product not found: " + movieId));

            // Create and save the UserMovieList entry
            UserMovieList userMovieList = new UserMovieList(user, product);
            userMovieListRepository.save(userMovieList);

            redirectAttributes.addFlashAttribute("message", "Đã thêm phim vào danh sách thành công!");
            return "redirect:/movie/preshow/" + movieId;
        } catch (Exception e) {
            logger.error("Error adding movie to list: ", e);
            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra: " + e.getMessage());
            return "redirect:/movie/preshow/" + movieId;
        }
    }

    @PostMapping("/movielist/remove/{productId}")
    @Transactional
    public String removeFromMovieList(@PathVariable String productId, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để xóa phim.");
            return "redirect:/user/login";
        }
        String username = authentication.getName();
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            
            // Delete the UserMovieList entry for the user and product
            userMovieListRepository.deleteByUserIdAndProductId(user.getId(), productId);
            
            redirectAttributes.addFlashAttribute("message", "Đã xóa phim khỏi danh sách.");
            return "redirect:/user/movielist";
        }
        redirectAttributes.addFlashAttribute("error", "Người dùng không tồn tại.");
        return "redirect:/user/login";
    }

    @RequestMapping(value = "movie", method = RequestMethod.GET)
    public String showMovieForm(ModelMap modelMap) {
        modelMap.addAttribute("Movie", new User());
        return "movie";
    }

    @RequestMapping(value = "movie/{CategoryID}", method = RequestMethod.GET)
    public String showMoviesort(@PathVariable String CategoryID, ModelMap modelMap, HttpSession session) {
        // Add logged-in user to the model
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated) {
            try {
                loggedInUser = (User) authentication.getPrincipal();
                modelMap.addAttribute("loggedInUser", loggedInUser);
                session.setAttribute("loggedInUser", loggedInUser);
            } catch (ClassCastException e) {
                logger.error("Failed to cast principal to User in showMoviesort: {}", e.getMessage(), e);
            }
        }

        modelMap.addAttribute("Product", productRepository.findByCategoryID(CategoryID));
        String episode;
        if (CategoryID == "Movie") {
            episode = "Movie";
            Iterable<Product> products = productRepository.findByEpisode(episode);
            modelMap.addAttribute("products", products);
            return "movie";
        }
        Iterable<Product> products = productRepository.findByEpisodeNotMovie();
        modelMap.addAttribute("products", products);

        return "movie";
    }

    @GetMapping(value = "/search")
    public String search(
            @RequestParam(value = "query", defaultValue = "") String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            ModelMap modelMap,
            HttpSession session) {
        logger.info("GET /user/search - Received query param: {}", keyword);

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User loggedInUser = null;
            boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
            if (isAuthenticated) {
                try {
                    loggedInUser = (User) authentication.getPrincipal();
                    modelMap.addAttribute("loggedInUser", loggedInUser);
                    session.setAttribute("loggedInUser", loggedInUser);
                } catch (ClassCastException e) {
                    logger.error("Failed to cast principal to User in search: {}", e.getMessage(), e);
                }
            }

            int pageSize = 12;
            if (page < 1) {
                page = 1;
            }
            Pageable pageable = PageRequest.of(page - 1, pageSize);

            String trimmedKeyword = keyword.trim();
            logger.info("Trimmed keyword: {}", trimmedKeyword);

            Page<Product> moviePage;
            if (!trimmedKeyword.isEmpty()) {
                moviePage = productRepository.findByNameContaining(trimmedKeyword, pageable);
                logger.info("Found {} results for keyword '{}', total pages: {}", moviePage.getTotalElements(), trimmedKeyword, moviePage.getTotalPages());
            } else {
                moviePage = Page.empty(pageable);
            }

            if (page > moviePage.getTotalPages() && moviePage.getTotalPages() > 0) {
                logger.warn("Requested page {} exceeds total pages {}, redirecting to last page", page, moviePage.getTotalPages());
                return "redirect:/user/search?query=" + trimmedKeyword + "&page=" + moviePage.getTotalPages();
            }

            modelMap.addAttribute("movies", moviePage.getContent());
            modelMap.addAttribute("currentPage", page);
            modelMap.addAttribute("totalPages", moviePage.getTotalPages());
            modelMap.addAttribute("keyword", trimmedKeyword);

            if (moviePage.getContent().isEmpty() && !trimmedKeyword.isEmpty()) {
                modelMap.addAttribute("message", "Không tìm thấy phim phù hợp với từ khóa '" + trimmedKeyword + "'.");
            }

            return "search";
        } catch (Exception e) {
            logger.error("Error processing search: {}", e.getMessage(), e);
            modelMap.addAttribute("error", "Lỗi khi tìm kiếm: " + e.getMessage());
            modelMap.addAttribute("keyword", keyword);
            modelMap.addAttribute("currentPage", 1);
            modelMap.addAttribute("totalPages", 0);
            return "search";
        }
    }

    @PostMapping("/comment")
    @Transactional
    public ResponseEntity<Map<String, Object>> addComment(@Valid @RequestBody Comment newComment,
            BindingResult result, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        logger.info("Nhận yêu cầu thêm bình luận: content={}, productId={}",
                newComment.getContent(), newComment.getProduct() != null ? newComment.getProduct().getId() : "null");

        // Kiểm tra xác thực
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            logger.warn("Yêu cầu không được xác thực");
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để bình luận.");
            return ResponseEntity.status(401).body(response);
        }

        // Kiểm tra lỗi validation
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            logger.error("Validation errors: {}", errorMessage);
            response.put("success", false);
            response.put("message", "Lỗi: " + errorMessage);
            return ResponseEntity.badRequest().body(response);
        }

        if (newComment.getContent() == null || newComment.getContent().trim().isEmpty()) {
            logger.error("Nội dung bình luận không được để trống");
            response.put("success", false);
            response.put("message", "Nội dung bình luận không được để trống.");
            return ResponseEntity.badRequest().body(response);
        }

        Product product = newComment.getProduct();
        if (product == null || !productRepository.existsById(product.getId())) {
            logger.error("Phim không tồn tại: {}", product != null ? product.getId() : "null");
            response.put("success", false);
            response.put("message", "Phim không tồn tại.");
            return ResponseEntity.badRequest().body(response);
        }

        // Lưu bình luận
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));
        logger.info("Tìm thấy người dùng: {}", user.getUsername());
        newComment.setUser(user);
        newComment.setCreatedAt(LocalDateTime.now());
        newComment.setUpdatedAt(LocalDateTime.now());
        try {
            Comment savedComment = commentRepository.save(newComment);
            logger.info("Đã lưu bình luận: id={}", savedComment.getId());

            // Định dạng createdAt thành chuỗi ISO 8601
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            String formattedCreatedAt = savedComment.getCreatedAt().format(formatter);

            response.put("success", true);
            response.put("message", "Bình luận đã được thêm thành công!");
            response.put("comment", Map.of(
                    "id", savedComment.getId(),
                    "content", savedComment.getContent(),
                    "username", user.getUsername(),
                    "createdAt", formattedCreatedAt
            ));
        } catch (Exception e) {
            logger.error("Lỗi khi lưu bình luận: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "Lỗi khi lưu bình luận: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/updateComment/{commentId}")
    public String updateComment(@PathVariable Long commentId,
            @Valid @ModelAttribute("comment") Comment updatedComment,
            BindingResult result, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để cập nhật bình luận.");
            return "redirect:/user/login";
        }

        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors().stream()
                    .map(error -> error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + errorMessage);
            return "redirect:/movie/show/" + updatedComment.getProduct().getId();
        }

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        Comment existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Bình luận không tồn tại"));

        if (!existingComment.getUser().getId().equals(user.getId())) {
            redirectAttributes.addFlashAttribute("error", "Bạn không có quyền cập nhật bình luận này.");
            return "redirect:/movie/show/" + existingComment.getProduct().getId();
        }
        existingComment.setContent(updatedComment.getContent());
        existingComment.setUpdatedAt(LocalDateTime.now());
        commentRepository.save(existingComment);

        redirectAttributes.addFlashAttribute("message", "Bình luận đã được cập nhật thành công!");
        return "redirect:/movie/show/" + existingComment.getProduct().getId();
    }

    @GetMapping
    public String userProfile(Model model, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            User loggedInUser = (User) authentication.getPrincipal();
            model.addAttribute("loggedInUser", loggedInUser);
            return "user";
        }
        return "redirect:/user/login?error=PleaseLogin";
    }


    @PostMapping("/deleteComment/{commentId}")
    public ResponseEntity<Map<String, Object>> deleteComment(@PathVariable Long commentId, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        // Kiểm tra xác thực
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            response.put("success", false);
            response.put("message", "Vui lòng đăng nhập để xóa bình luận.");
            return ResponseEntity.status(401).body(response);
        }

        // Kiểm tra người dùng
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

        // Kiểm tra bình luận
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Bình luận không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!comment.getUser().getId().equals(user.getId())) {
            response.put("success", false);
            response.put("message", "Bạn không có quyền xóa bình luận này.");
            return ResponseEntity.status(403).body(response);
        }

        // Xóa bình luận
        commentRepository.delete(comment);
        response.put("success", true);
        response.put("message", "Bình luận đã được xóa thành công!");
        response.put("commentId", commentId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/video")
    public ResponseEntity<Map<String, String>> getVideoUrl(@RequestParam String publicId) {
        Map<String, String> response = new HashMap<>();
        try {
            logger.info("Requesting video URL for publicId: {}", publicId);
            String videoUrl = cloudinaryService.getPublicUrl(publicId);
            response.put("url", videoUrl);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for publicId {}: {}", publicId, e.getMessage());
            response.put("url", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (RuntimeException e) {
            logger.error("Error processing video URL for publicId {}: {}", publicId, e.getMessage());
            response.put("url", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update-info")
    public String updateUserInfo(@RequestParam String fullname, @RequestParam String telephone, 
                               Authentication authentication, 
                               RedirectAttributes redirectAttributes,
                               HttpServletResponse response,
                               HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để cập nhật thông tin.");
            return "redirect:/user/login";
        }

        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new IllegalArgumentException("Người dùng không tồn tại"));

            // Validate fullname
            if (fullname == null || fullname.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Họ tên không được để trống!");
                return "redirect:/user";
            }

            // Validate telephone
            if (telephone == null || telephone.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại không được để trống!");
                return "redirect:/user";
            } else if (!PHONE_PATTERN.matcher(telephone).matches()) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại phải có 10 chữ số!");
                return "redirect:/user";
            }

            // Update user info
            user.setFullname(fullname);
            user.setTelephone(telephone);
            userRepository.save(user);

            // Update session with new user info
            session.setAttribute("loggedInUser", user);

            // Generate new JWT token with updated information
            String token = jwtUtil.generateToken(user.getUsername(), user.getFullname());
            
            // Create cookie with new token
            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60); // 24 hours
            response.addCookie(jwtCookie);

            // Create new authentication with updated user info
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(
                user,
                authentication.getCredentials(),
                authentication.getAuthorities()
            );
            newAuth.setDetails(authentication.getDetails());

            // Update SecurityContext with new authentication
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            redirectAttributes.addFlashAttribute("message", "Thông tin đã được cập nhật thành công!");
            return "redirect:/user";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật thông tin: " + e.getMessage());
            return "redirect:/user";
        }
    }
}
