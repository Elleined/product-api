package com.elleined.marketplaceapi.controller.moderator;

import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.exception.field.NotValidBodyException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Moderator;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.service.email.EmailService;
import com.elleined.marketplaceapi.service.moderator.ModeratorService;
import com.elleined.marketplaceapi.service.user.UserService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/moderators/{moderatorId}/unverified-users")
public class ModeratorUserVerificationRequestController {

    private final ModeratorService moderatorService;

    private final UserService userService;
    private final UserMapper userMapper;

    private final EmailService emailService;

    @GetMapping
    public List<UserDTO> getAllUnverifiedUser() {
        return moderatorService.getAllUnverifiedUser().stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @PatchMapping("/{userToBeVerifiedId}/verify")
    public void verifyUser(@PathVariable("moderatorId") int moderatorId,
                           @PathVariable("userToBeVerifiedId") int userToBeVerifiedId) {

        Moderator moderator = moderatorService.getById(moderatorId);
        User userToBeVerified = userService.getById(userToBeVerifiedId);
        moderatorService.verifyUser(moderator, userToBeVerified);
        emailService.sendAcceptedVerificationEmail(userToBeVerified);
    }

    @PatchMapping("/verify")
    public void verifyAllUser(@PathVariable("moderatorId") int moderatorId,
                              @RequestBody Set<Integer> userToBeVerifiedIds) {

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<User> usersToBeVerified = userService.getAllById(userToBeVerifiedIds);
        moderatorService.verifyAllUser(moderator, usersToBeVerified);
    }

    @PatchMapping("/{userToBeRejectedId}/reject")
    public void rejectUser(@PathVariable("moderatorId") int moderatorId,
                           @PathVariable("userToBeRejectedId") int userToBeRejectedId,
                           @RequestParam("reason") String reason) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting this user...");

        Moderator moderator = moderatorService.getById(moderatorId);
        User userToBeRejected = userService.getById(userToBeRejectedId);
        moderatorService.rejectUser(moderator, userToBeRejected);
        emailService.sendRejectedVerificationEmail(userToBeRejected, reason);
    }

    @PatchMapping("/reject")
    public void rejectAllUser(@PathVariable("moderatorId") int moderatorId,
                              @RequestParam("reason") String reason,
                              @RequestBody Set<Integer> userToBeRejectedIds) {

        if (StringUtil.isNotValid(reason)) throw new NotValidBodyException("Please provide the reason why you are rejecting all this user...");

        Moderator moderator = moderatorService.getById(moderatorId);
        Set<User> usersToBeRejected = userService.getAllById(userToBeRejectedIds);
        moderatorService.rejectAllUser(moderator, usersToBeRejected);

        usersToBeRejected.forEach(rejectedUser -> emailService.sendRejectedVerificationEmail(rejectedUser, reason));
    }
}
