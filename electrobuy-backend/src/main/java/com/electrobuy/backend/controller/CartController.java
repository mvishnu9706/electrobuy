package com.electrobuy.backend.controller;

import com.electrobuy.backend.entity.CartItem;
import com.electrobuy.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * 1. Add item to cart or handle quantity increments safely via service
     * orchestration
     * URL: POST http://localhost:8085/api/cart/add?userId=1&productId=1&quantity=2
     */
    @PostMapping("/add")
    public ResponseEntity<?> addToCart(
            @RequestParam(name = "userId") long userId,
            @RequestParam(name = "productId") long productId,
            @RequestParam(name = "quantity") int quantity) {
        try {
            CartItem cartItem = cartService.addItemToCart(userId, productId, quantity);
            return new ResponseEntity<>(cartItem, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 2. View current user items
     * URL: GET http://localhost:8085/api/cart/1
     */
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItem>> viewCart(@PathVariable long userId) {
        // ✅ FIXED MATCH: Points directly to your getCartItems service layer method
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    /**
     * 3. Update quantity counters directly (For '+' and '-' button support on the
     * UI)
     * URL: PUT http://localhost:8085/api/cart/update/{cartItemId}?quantity=3
     */
    @PutMapping("/update/{cartItemId}")
    public ResponseEntity<?> updateQuantity(
            @PathVariable long cartItemId,
            @RequestParam(name = "quantity") int quantity) {
        try {
            CartItem updatedItem = cartService.updateItemQuantity(cartItemId, quantity);
            return ResponseEntity.ok(updatedItem);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 4. Remove a single targeted row record line out of the basket panel view
     * URL: DELETE http://localhost:8085/api/cart/item/{cartItemId}
     */
    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<String> removeItem(@PathVariable long cartItemId) {
        try {
            cartService.removeItemFromCart(cartItemId);
            return ResponseEntity.ok("Item was successfully removed from your cart.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * 5. Clear entire cart database table array tracking for a specified customer
     * profile index
     * URL: DELETE http://localhost:8085/api/cart/clear/{userId}
     */
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<String> clearCart(@PathVariable long userId) {
        try {
            cartService.clearUserCart(userId);
            return ResponseEntity.ok("Shopping cart was completely flushed clean.");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}