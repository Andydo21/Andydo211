package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.Comment;
import com.andd.DoDangAn.DoDangAn.models.Episode;
import com.andd.DoDangAn.DoDangAn.models.Product;
import com.andd.DoDangAn.DoDangAn.models.User;
import com.andd.DoDangAn.DoDangAn.repository.CommentRepository;
import com.andd.DoDangAn.DoDangAn.repository.EpisodeRepository;
import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import com.andd.DoDangAn.DoDangAn.repository.CategoryRepository;
import com.andd.DoDangAn.DoDangAn.services.CloudinaryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.andd.DoDangAn.DoDangAn.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

import com.andd.DoDangAn.DoDangAn.models.Category;

@Controller
@RequestMapping(path = "movie")
public class MovieController {

    @Autowired
    private ProductService productService;
    @Autowired
    private CloudinaryService cloudinaryService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EpisodeRepository episodeRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
            "user/register", "user/login", "user/home", "categories", "/", "movie/preshow", "assets",
            "uploads", "upload_dir", "views", "error", "movie/movies"
    );

    @RequestMapping(value = "/preshow/{productID}", method = RequestMethod.GET)
    public String showMovie(@PathVariable("productID") String productID, ModelMap modelMap, HttpSession session) {
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
                logger.error("Failed to cast principal to User in showMovie: {}", e.getMessage(), e);
            }
        }

        Optional<Product> productOptional = productRepository.findById(productID);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            logger.info("Found product: {} with current rating: {}", product.getProductName(), product.getScore());

            // Lấy rating từ OMDB nếu chưa có
                logger.info("Fetching OMDB rating for: {}", product.getProductName());
                try {
                    productService.updateOMDBRating(product);
                    logger.info("Updated rating for movie: {}", product.getProductName());
                } catch (Exception e) {
                    logger.error("Error fetching OMDB rating: {}", e.getMessage(), e);
                }

            modelMap.addAttribute("product", product);
            List<Comment> comments = commentRepository.findByProductId(productID);
            modelMap.addAttribute("comments", comments);
            return "preshow";
        }
        logger.warn("Product not found for ID: {}", productID);
        return "redirect:/movie/preshow/" + productID;
    }



    @RequestMapping(value = "/show/{productID}", method = RequestMethod.GET)
    public String showShow(@PathVariable("productID") String productID, ModelMap modelMap, HttpServletResponse response) {
        response.setHeader("X-Robots-Tag", "noindex, nofollow");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated) {
            try {
                loggedInUser = (User) authentication.getPrincipal();
                modelMap.addAttribute("loggedInUser", loggedInUser);
                modelMap.addAttribute("isAuthenticated", true);
            } catch (ClassCastException e) {
                logger.error("Failed to cast principal to User: {}", e.getMessage(), e);
                modelMap.addAttribute("isAuthenticated", false);
            }
        } else {
            modelMap.addAttribute("isAuthenticated", false);
        }

        productService.incrementViewCount(productID);

        Optional<Product> productOptional = productRepository.findById(productID);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            List<Episode> episodes = episodeRepository.findByProductId(product.getId());
            List<Comment> comments = commentRepository.findByProductId(product.getId());
            List<Product> suggestedMovies = productRepository.findTop5ByCategoryIDOrderByViewCountDesc(product.getCategoryID());

            modelMap.addAttribute("episodes", episodes);
            modelMap.addAttribute("product", product);
            modelMap.addAttribute("comments", comments);
            modelMap.addAttribute("suggestedMovies", suggestedMovies);

            List<String> videoPublicIds = new ArrayList<>();
            String videoPublicId = product.getVideoPublicId();
            if (videoPublicId != null && !videoPublicId.trim().isEmpty()) {
                videoPublicIds.addAll(Arrays.asList(videoPublicId.split(",")));
            }
            modelMap.addAttribute("videoPublicIds", videoPublicIds);

            return "movie-detail";
        } else {
            modelMap.addAttribute("error", "Phim không tồn tại");
            return "preshow";
        }
    }
    @RequestMapping(value = "/showep/{episodeID}", method = RequestMethod.GET)
    public String showep(@PathVariable("episodeID") Long episodeID, ModelMap modelMap, HttpServletResponse response) {
        response.setHeader("X-Robots-Tag", "noindex, nofollow");


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated) {
            try {
                loggedInUser = (User) authentication.getPrincipal();
                modelMap.addAttribute("loggedInUser", loggedInUser);
                modelMap.addAttribute("isAuthenticated", true);
            } catch (ClassCastException e) {
                logger.error("Failed to cast principal to User: {}", e.getMessage(), e);
                modelMap.addAttribute("isAuthenticated", false);
            }
        } else {
            modelMap.addAttribute("isAuthenticated", false);
        }
        Optional<Episode> episodeOpt = episodeRepository.findById(episodeID);
        String productID=null;
        if (episodeOpt.isPresent()) {
            Episode episode = episodeOpt.get();
            productID = episode.getProduct().getId();
            if (productID != null) {
                productService.incrementViewCount(productID);
            } else {
                logger.warn("Product is null for episode ID: {}", episodeID);
            }
        } else {
            logger.warn("Episode not found with ID: {}", episodeID);
        }




        Optional<Product> productOptional = productRepository.findById(productID);
        Optional<Episode> episodeOptional=episodeRepository.findById(episodeID);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            List<Episode> episodes = episodeRepository.findByProductId(product.getId());
            List<Comment> comments = commentRepository.findByProductId(product.getId());
            List<Product> suggestedMovies = productRepository.findTop5ByCategoryIDOrderByViewCountDesc(product.getCategoryID());

            modelMap.addAttribute("episodes", episodes);
            modelMap.addAttribute("product", product);
            modelMap.addAttribute("comments", comments);
            modelMap.addAttribute("suggestedMovies", suggestedMovies);


            List<String> videoPublicIds = new ArrayList<>();
            String videoPublicId = product.getVideoPublicId();
            if (videoPublicId != null && !videoPublicId.trim().isEmpty()) {
                videoPublicIds.addAll(Arrays.asList(videoPublicId.split(",")));
            }



            modelMap.addAttribute("videoPublicIds", videoPublicIds);
            return "movie-detail";
        } else {
            modelMap.addAttribute("error", "Phim không tồn tại");
            return "preshow";
        }
    }

    @GetMapping("/movies")
    public String listMovies(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "releaseDate") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortOrder,
            ModelMap modelMap,
            HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User loggedInUser = null;
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal());
        if (isAuthenticated) {
            try {
                loggedInUser = (User) authentication.getPrincipal();
                modelMap.addAttribute("loggedInUser", loggedInUser);
                session.setAttribute("loggedInUser", loggedInUser);
            } catch (ClassCastException e) {
                logger.error("Failed to cast principal to User in listMovies: {}", e.getMessage(), e);
            }
        }
        modelMap.addAttribute("category", categoryRepository.findAll());
        int pageSize = 12;
        Sort sort = Sort.by(sortBy);
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = sort.descending();
        } else {
            sort = sort.ascending();
        }
        Pageable pageable = PageRequest.of(page - 1, pageSize, sort);


        LocalDateTime start = null;
        LocalDateTime end = null;
        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate + "T00:00:00");
            }
            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate + "T23:59:59");
            }
        } catch (Exception e) {
            logger.error("Error parsing date: {}", e.getMessage(), e);
            modelMap.addAttribute("dateError", "Định dạng ngày không hợp lệ");
        }

        Page<Product> moviePage;
        String title = "Tất cả phim";

        if (category != null && !category.isEmpty()) {
            if (start != null && end != null) {
                moviePage = productRepository.findByCategoryIDAndReleaseDateBetween(category, start, end, pageable);
            } else if (start != null) {
                moviePage = productRepository.findByCategoryIDAndReleaseDateAfter(category, start, pageable);
            } else if (end != null) {
                moviePage = productRepository.findByCategoryIDAndReleaseDateBefore(category, end, pageable);
            } else {
                moviePage = productRepository.findByCategoryID(category, pageable);
            }
            // Lấy tên thể loại
            Optional<Category> categoryOpt = categoryRepository.findById(category);
            if (categoryOpt.isPresent()) {
                title = "Phim " + categoryOpt.get().getCategoryName();
            }
        } else {
            if (start != null && end != null) {
                moviePage = productRepository.findByReleaseDateBetween(start, end, pageable);
            } else if (start != null) {
                moviePage = productRepository.findByReleaseDateAfter(start, pageable);
            } else if (end != null) {
                moviePage = productRepository.findByReleaseDateBefore(end, pageable);
            } else {
                moviePage = productRepository.findAll(pageable);
            }
        }

        // Thêm dữ liệu vào model
        modelMap.addAttribute("movies", moviePage.getContent());
        modelMap.addAttribute("currentPage", page);
        modelMap.addAttribute("totalPages", moviePage.getTotalPages());
        modelMap.addAttribute("title", title);
        modelMap.addAttribute("selectedCategory", category);
        modelMap.addAttribute("startDate", startDate);
        modelMap.addAttribute("endDate", endDate);
        modelMap.addAttribute("sortBy", sortBy);
        modelMap.addAttribute("sortOrder", sortOrder);

        return "movie";
    }
}



