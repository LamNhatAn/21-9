package com.example.shop.controller;

import com.example.shop.model.Product;
import com.example.shop.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    private final ProductRepository products;
    public ProductController(ProductRepository products) { this.products = products; }
    @GetMapping public String list(Model model) { model.addAttribute("products", products.findAll()); return "products"; }
    @PostMapping public String save(@Valid @ModelAttribute Product product) { products.save(product); return "redirect:/admin/products"; }
    @PostMapping("/{id}/delete") public String delete(@PathVariable Long id) { products.deleteById(id); return "redirect:/admin/products"; }
}
