package com.andd.DoDangAn.DoDangAn.Controller;

import com.andd.DoDangAn.DoDangAn.models.Comment;
import com.andd.DoDangAn.DoDangAn.models.Episode;
import com.andd.DoDangAn.DoDangAn.models.Product;
import com.andd.DoDangAn.DoDangAn.models.User;
import com.andd.DoDangAn.DoDangAn.repository.CommentRepository;
import com.andd.DoDangAn.DoDangAn.repository.EpisodeRepository;
import com.andd.DoDangAn.DoDangAn.repository.ProductRepository;
import com.andd.DoDangAn.DoDangAn.repository.UserRepository;
import com.andd.DoDangAn.DoDangAn.services.CloudinaryService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.andd.DoDangAn.DoDangAn.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    private static final Logger logger = LoggerFactory.getLogger(MovieController.class);

    @RequestMapping(value = "/preshow/{productID}", method = RequestMethod.GET)
    public String showMovie(@PathVariable("productID") String productID, ModelMap modelMap, HttpSession session) {
        Product product = productRepository.findById(productID).get();
        List<Comment> comments = commentRepository.findByProductId(product.getId());
        modelMap.addAttribute("product", product);
        modelMap.addAttribute("comments", comments);
        return "preshow";
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


            List<String> videoUrls = new ArrayList<>();


            if (product.getVideoPublicId() != null && !product.getVideoPublicId().trim().isEmpty()) {
                String[] publicIds = product.getVideoPublicId().split(",");
                for (String publicId : publicIds) {
                    publicId = publicId.trim();
                    if (!publicId.isEmpty()) {
                        try {
                            String videoUrl = cloudinaryService.getPublicUrl(publicId);
                            if (videoUrl != null) {
                                videoUrls.add(videoUrl);
                            }
                        } catch (Exception e) {
                            logger.error("Error generating URL for publicId: " + publicId, e);
                        }
                    }
                }
            }
            modelMap.addAttribute("videoUrls",videoUrls);
            return "movie-detail";
        } else {
            modelMap.addAttribute("error", "Phim không tồn tại");
            return "preshow";
        }
    }
}



