package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.client.ForumClient;
import com.elleined.marketplaceapi.mapper.ShopMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserVerification;
import com.elleined.marketplaceapi.repository.ShopRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.password.UserPasswordEncoder;
import com.elleined.marketplaceapi.service.validator.EmailValidator;
import com.elleined.marketplaceapi.service.validator.FullNameValidator;
import com.elleined.marketplaceapi.service.validator.NumberValidator;
import com.elleined.marketplaceapi.service.validator.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private ShopMapper shopMapper;


    @Mock
    private UserPasswordEncoder userPasswordEncoder;


    @Mock
    private ImageUploader imageUploader;


    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;


    @Mock
    private ShopRepository shopRepository;


    @Mock
    private AddressService addressService;


    @Mock
    private ForumClient forumClient;


    @Mock
    private EmailValidator emailValidator;

    @Mock
    private PasswordValidator passwordValidator;

    @Mock
    private NumberValidator numberValidator;

    @Mock
    private FullNameValidator fullNameValidator;

    @InjectMocks
    private UserServiceImpl userService;


    @Test
    void getAllSeller() {
        // Mock data
        User qualifiedSeller = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .validId("valid id.jpg")
                        .build())
                .shop(new Shop())
                .build();

        User unVerifiedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .validId("valid id.jpg")
                        .build())
                .shop(new Shop())
                .build();

        User rejectedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .validId(null)
                        .build())
                .shop(new Shop())
                .build();

        User noShopUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .validId(null)
                        .build())
                .shop(null)
                .build();

        List<User> rawUsers = Arrays.asList(qualifiedSeller, unVerifiedUser, rejectedUser, noShopUser);
        // Stubbing methods
        when(userRepository.findAll()).thenReturn(rawUsers);

        // Calling the method
        Set<User> actual = userService.getAllSeller();

        // Expected/ Actual Data
        Set<User> expected = new HashSet<>(Collections.singletonList(qualifiedSeller));

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(userRepository).findAll();
    }

    @Test
    void searchAllSellerByName() {
        // Mock data
        User qualifiedSeller = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.VERIFIED)
                        .validId("valid id.jpg")
                        .build())
                .shop(new Shop())
                .build();

        User unVerifiedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .validId("valid id.jpg")
                        .build())
                .shop(new Shop())
                .build();

        User rejectedUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .validId(null)
                        .build())
                .shop(new Shop())
                .build();

        User noShopUser = User.builder()
                .userVerification(UserVerification.builder()
                        .status(UserVerification.Status.NOT_VERIFIED)
                        .validId(null)
                        .build())
                .shop(null)
                .build();

        Set<User> rawUsers = new HashSet<>(Arrays.asList(qualifiedSeller, unVerifiedUser, rejectedUser, noShopUser));

        // Stubbing methods
        when(userRepository.searchByUserName(anyString())).thenReturn(rawUsers);

        // Calling the method
        Set<User> actual = userService.searchAllSellerByName(anyString());

        // Expected/ Actual Data
        Set<User> expected = new HashSet<>(Collections.singletonList(qualifiedSeller));

        // Assertions
        assertIterableEquals(expected, actual);

        // Behavior verification
        verify(userRepository).searchByUserName(anyString());
    }

    @Test
    void saveByDTO() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void saveByDTOWithPicture() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void resendValidId() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void login() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getByEmail() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void isLegibleForRegistrationPromo() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void availRegistrationPromo() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void sendShopRegistration() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getByReferralCode() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void addInvitedUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void getInvitingUser() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void changePassword() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }

    @Test
    void testChangePassword() {
        // Mock data

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
    }
}