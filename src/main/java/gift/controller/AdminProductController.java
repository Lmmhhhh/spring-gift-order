package gift.controller;

import gift.dto.request.OptionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    @GetMapping
    public String redirectToLogin() {
        return "redirect:/admin/login.html";
    }

    @GetMapping("/list")
    public String showList() {
        return "redirect:/admin/product-list.html";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "redirect:/admin/product-register.html";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id) {
        return "redirect:/admin/product-edit.html?productId=" + id;
    }
}