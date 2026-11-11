package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.Review;
import com.electrobuy.backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor // Automatically generates high-performance immutable constructor injection
public class ReviewController {

    // Field marked final to eliminate IDE linting warnings and achieve absolute
    // null safety
    private final ReviewService reviewService;

    /**
     * Endpoint to bind a customer's comment, rating score, and feedback metrics to
     * a product listing.
     * URL: POST http://localhost:8085/api/reviews/add?userId=1&productId=2
     */
    @PostMapping("/add")
    public ResponseEntity<Review> addReview(
            @RequestParam long userId,
            @RequestParam long productId,
            @RequestBody Review review) {
        Review savedReview = reviewService.addReview(userId, productId, review);
        return ResponseEntity.ok(savedReview);
    }

    /**
     * Endpoint to fetch the complete collection of public customer evaluations
     * matching a target product ID.
     * URL: GET http://localhost:8085/api/reviews/product/2
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable long productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }
}