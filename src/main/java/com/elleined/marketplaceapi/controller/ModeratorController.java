package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators")
@CrossOrigin(origins = "*")
public class ModeratorController {
    private final ModeratorService moderatorService;
    private final UserService userService;
    private final ProductService productService;

    @PostMapping("/login")
    public Moderator login(@Valid @RequestBody CredentialDTO moderatorCredentialDTO,
                           HttpSession session) {

        Moderator loggedInModerator = moderatorService.login(moderatorCredentialDTO);
        session.setAttribute("loggedInModerator", loggedInModerator);
        return null;
    }

    @GetMapping("/getAllUnverifiedUser")
    public List<UserDTO> getAllUnverifiedUser() {
        return moderatorService.getAllUnverifiedUser();
    }

    @GetMapping("/getAllPendingProduct")
    public List<ProductDTO> getAllPendingProduct() {
        return moderatorService.getAllPendingProduct();
    }


    @PatchMapping("/{moderatorId}/verifyUser/{userToBeVerifiedId}")
    public void verifyUser(@PathVariable("moderatorId") int moderatorId,
                           @PathVariable("userToBeVerifiedId") int userToBeVerifiedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        User userToBeVerified = userService.getById(userToBeVerifiedId);
        moderatorService.verifyUser(moderator, userToBeVerified);
    }

    @PatchMapping("/{moderatorId}/verifyAllUser")
    public void verifyAllUser(@PathVariable("moderatorId") int moderatorId,
                              @RequestBody List<Integer> userToBeVerifiedIds) {

        Moderator moderator = moderatorService.getById(moderatorId);
        List<User> usersToBeVerified = userService.getAllById(userToBeVerifiedIds);
        moderatorService.verifyAllUser(moderator, usersToBeVerified);
    }


    @PatchMapping("/{moderatorId}/listProduct/{productToBeListedId}")
    public void listProduct(@PathVariable("moderatorId") int moderatorId,
                            @PathVariable("productToBeListedId") int productToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Product product = productService.getById(productToBeListedId);
        moderatorService.listProduct(moderator, product);
    }

    @PatchMapping("/{moderatorId}/listAllProduct")
    public void listAllProduct(@PathVariable("moderatorId") int moderatorId,
                               @RequestParam List<Integer> productsToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        List<Product> productsToBeListed = productService.getAllById(productsToBeListedId);
        moderatorService.listAllProduct(moderator, productsToBeListed);
    }
}
