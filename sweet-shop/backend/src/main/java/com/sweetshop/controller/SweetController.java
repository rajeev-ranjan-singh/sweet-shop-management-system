package com.sweetshop.controller;

import com.sweetshop.model.Sweet;
import com.sweetshop.repository.SweetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sweets")
public class SweetController {
    @Autowired private SweetRepository sweetRepository;

    @GetMapping
    public List<Sweet> getAllSweets() { return sweetRepository.findAll(); }
    
    @GetMapping("/search")
    public List<Sweet> search(@RequestParam String query) { return sweetRepository.findByNameContainingIgnoreCase(query); }

    @PostMapping
    public ResponseEntity<?> addSweet(@RequestBody Sweet sweet) { return ResponseEntity.status(201).body(sweetRepository.save(sweet)); }

    @PostMapping("/{id}/purchase")
    public ResponseEntity<?> purchase(@PathVariable Long id) {
        return sweetRepository.findById(id).map(sweet -> {
            if (sweet.getQuantity() > 0) {
                sweet.setQuantity(sweet.getQuantity() - 1);
                sweetRepository.save(sweet);
                return ResponseEntity.ok("Purchased");
            } else return ResponseEntity.badRequest().body("Out of stock");
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        sweetRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }
}
