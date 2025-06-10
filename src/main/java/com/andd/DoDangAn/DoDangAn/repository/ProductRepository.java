package com.andd.DoDangAn.DoDangAn.repository;

import com.andd.DoDangAn.DoDangAn.models.Category;
import com.andd.DoDangAn.DoDangAn.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends PagingAndSortingRepository<Product, String> {
    Optional<Product> findById(String id);
    List<Product> findAll();
    void deleteById(String id);
    @Query("SELECT p FROM Product p WHERE p.category.categoryID = :categoryID")
    Page<Product> findByCategoryID(@Param("categoryID") String categoryID, Pageable pageable);
    <S extends Product> S save(S entity);
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);


    @Modifying
    @Query("UPDATE Product m SET m.viewCount = m.viewCount + 1 WHERE m.id = :id")
    void incrementViewCount(@Param("id") String id);
    Long count();
    Iterable<Product> findByEpisode(String episode);
    @Query("SELECT p FROM Product p WHERE p.episode != 'Movie'")
    Iterable<Product> findByEpisodeNotMovie();
    boolean existsByProductNameAndEpisode(String productName, String episode);
    boolean existsByProductNameAndSeason(String productName,int season);
    List<Product> findTop5ByCategoryIDOrderByViewCountDesc(String category);

    boolean existsById(String id);
    Product findByProductNameAndSeason(String productName, int season);
    //List<Product> findByCategoryIDAndCountry(String categoryID, String country);
    @Query("SELECT p FROM Product p WHERE " +
            "(:episode IS NULL OR p.episode = :episode) AND " +
            "(:country IS NULL OR p.country.countryId = :country) AND " +
            "(:year IS NULL OR FUNCTION('YEAR', p.releaseDate) = :year)")
    Page<Product> findByFilters(@Param("episode") String episode,
                                @Param("country") String country,
                                @Param("year") String year,
                                Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.categoryID = :categoryID")
    Iterable<Product> findByCategoryID(@Param("categoryID") String categoryID);

    @Query("SELECT p FROM Product p WHERE p.releaseDate >= :startDate")
    Page<Product> findByReleaseDateAfter(@Param("startDate") LocalDateTime startDate, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.releaseDate <= :endDate")
    Page<Product> findByReleaseDateBefore(@Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.releaseDate BETWEEN :startDate AND :endDate")
    Page<Product> findByReleaseDateBetween(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate,
                                           Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.categoryID = :categoryID AND p.releaseDate >= :startDate")
    Page<Product> findByCategoryIDAndReleaseDateAfter(@Param("categoryID") String categoryID,
                                                      @Param("startDate") LocalDateTime startDate,
                                                      Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.categoryID = :categoryID AND p.releaseDate <= :endDate")
    Page<Product> findByCategoryIDAndReleaseDateBefore(@Param("categoryID") String categoryID,
                                                       @Param("endDate") LocalDateTime endDate,
                                                       Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.category.categoryID = :categoryID AND p.releaseDate BETWEEN :startDate AND :endDate")
    Page<Product> findByCategoryIDAndReleaseDateBetween(@Param("categoryID") String categoryID,
                                                        @Param("startDate") LocalDateTime startDate,
                                                        @Param("endDate") LocalDateTime endDate,
                                                        Pageable pageable);

}
