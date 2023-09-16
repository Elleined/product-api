package com.elleined.marketplaceapi.controller;


import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ModeratorDTO;
import com.elleined.marketplaceapi.dto.ProductDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.mapper.ProductMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.Product;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.product.ProductService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.utils.StringUtil;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators")
public class ModeratorController {
    private final ModeratorService moderatorService;

    private final UserService userService;

    private final ProductService productService;

    private final EmailService emailService;

    private final UserMapper userMapper;
    private final ProductMapper productMapper;

    @PostMapping("/login")
    public ModeratorDTO login(@Valid @RequestBody CredentialDTO moderatorCredentialDTO,
                              HttpSession session) {
        ModeratorDTO moderatorDTO = moderatorService.login(moderatorCredentialDTO);
        session.setAttribute("moderatorId", moderatorDTO.id());
        return moderatorDTO;
    }

    @GetMapping("/getAllUnverifiedUser")
    public List<UserDTO> getAllUnverifiedUser() {
        return moderatorService.getAllUnverifiedUser().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @GetMapping("/getAllPendingProduct")
    public List<ProductDTO> getAllPendingProduct() {
        return moderatorService.getAllPendingProduct().stream()
                .map(productMapper::toDTO)
                .toList();
    }


    @PatchMapping("/rejectUser/{userToBeRejectedId}")
    public void rejectUser(@PathVariable("userToBeRejectedId") int userToBeRejectedId,
                           @RequestParam("reason") String reason) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting this user...");

        Moderator moderator = moderatorService.getById(1);
        User userToBeRejected = userService.getById(userToBeRejectedId);
        moderatorService.rejectUser(moderator, userToBeRejected);
        emailService.sendRejectedVerificationEmail(userToBeRejected, reason);
    }

    @PatchMapping("/{moderatorId}/verifyUser/{userToBeVerifiedId}")
    public void verifyUser(@PathVariable("moderatorId") int moderatorId,
                           @PathVariable("userToBeVerifiedId") int userToBeVerifiedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        User userToBeVerified = userService.getById(userToBeVerifiedId);
        moderatorService.verifyUser(moderator, userToBeVerified);
        emailService.sendAcceptedVerificationEmail(userToBeVerified);
    }

    @PatchMapping("/{moderatorId}/verifyAllUser")
    public void verifyAllUser(@PathVariable("moderatorId") int moderatorId,
                              @RequestBody Set<Integer> userToBeVerifiedIds) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<User> usersToBeVerified = userService.getAllById(userToBeVerifiedIds);
        moderatorService.verifyAllUser(moderator, usersToBeVerified);
    }

    @PatchMapping("/{moderatorId}/rejectProduct/{productToBeRejectedId}")
    public void rejectProduct(@PathVariable("moderatorId") int moderatorId,
                              @PathVariable("productToBeRejectedId") int productToBeRejectedId,
                              @RequestParam("reason") String reason) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting this product...");

        Moderator moderator = moderatorService.getById(moderatorId);
        Product productToBeRejected = productService.getById(productToBeRejectedId);
        moderatorService.rejectProduct(moderator, productToBeRejected);
        emailService.sendRejectedProductEmail(productToBeRejected, reason);
    }


    @PatchMapping("/{moderatorId}/listProduct/{productToBeListedId}")
    public void listProduct(@PathVariable("moderatorId") int moderatorId,
                            @PathVariable("productToBeListedId") int productToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Product product = productService.getById(productToBeListedId);
        moderatorService.listProduct(moderator, product);
        emailService.sendProductListedEmail(product.getSeller(), product);
    }

    @PatchMapping("/{moderatorId}/listAllProduct")
    public void listAllProduct(@PathVariable("moderatorId") int moderatorId,
                               @RequestBody Set<Integer> productsToBeListedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<Product> productsToBeListed = productService.getAllById(productsToBeListedId);
        moderatorService.listAllProduct(moderator, productsToBeListed);
    }
}
