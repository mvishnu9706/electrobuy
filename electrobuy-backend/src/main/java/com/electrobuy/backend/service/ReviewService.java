package com.electrobuy.backend.service;

import com.electrobuy.backend.entity.*;
import com.electrobuy.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor // Automatically handles high-performance immutable constructor injection
public class ReviewService {

    // Fields marked final to eliminate IDE linting warnings and achieve absolute
    // null type-safety
    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Binds a customer's comment, rating score, and feedback metrics to a product
     * listing.
     */
    @Transactional

    public Review addReview(long userId, long productId, Review review) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        review.setUser(user);
        review.setProduct(product);
        return reviewRepository.save(review);
    }

    /**
     * Fetches the complete collection of public customer evaluations matching a
     * target product ID.
     */
    public List<Review> getProductReviews(long productId) {
        return reviewRepository.findByProductId(productId);
    }
}