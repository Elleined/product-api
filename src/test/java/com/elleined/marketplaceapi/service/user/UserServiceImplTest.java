package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.client.ForumClient;
import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.address.AddressDTO;
import com.elleined.marketplaceapi.exception.field.FullNameException;
import com.elleined.marketplaceapi.exception.field.MalformedEmailException;
import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import com.elleined.marketplaceapi.exception.field.password.PasswordNotMatchException;
import com.elleined.marketplaceapi.exception.field.password.WeakPasswordException;
import com.elleined.marketplaceapi.exception.resource.ResourceException;
import com.elleined.marketplaceapi.exception.resource.exists.EmailAlreadyExistsException;
import com.elleined.marketplaceapi.exception.resource.exists.MobileNumberExistsException;
import com.elleined.marketplaceapi.mapper.ShopMapper;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.mock.MultiPartFileDataFactory;
import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.model.user.UserDetails;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
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
        String notExistingEmail = "notExistingEmail@gmail.com";
        String notExistingMobileNumber = "09111111111";

        String firstName = "firstName";
        String password = "password";
        String confirmPassword = "confirmPassword";
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName(firstName)
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber(notExistingMobileNumber)
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email(notExistingEmail)
                        .password(password)
                        .confirmPassword(confirmPassword)
                        .build())
                .invitationReferralCode(null)
                .build();

        List<String> emails = Arrays.asList("email@gmail.com");
        List<String> mobileNumbers = Arrays.asList("09999999999");

        UserAddress userAddress = new UserAddress();
        User invitedUser = User.builder()
                .userCredential(Credential.builder()
                        .email(notExistingEmail)
                        .password(password)
                        .build())
                .userDetails(UserDetails.builder()
                        .firstName(firstName)
                        .build())
                .build();

        // Stubbing methods
        when(userRepository.fetchAllEmail()).thenReturn(emails);
        when(userRepository.fetchAllMobileNumber()).thenReturn(mobileNumbers);

        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        when(passwordValidator.isPasswordNotMatch(anyString(), anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(invitedUser);
        doAnswer(i -> {
            invitedUser.getUserCredential().setPassword("hashedPassword");
            return invitedUser;
        }).when(userPasswordEncoder).encodePassword(any(User.class), anyString());
        when(userRepository.save(any(User.class))).thenReturn(invitedUser);
        doAnswer(i -> {
            invitedUser.setAddress(userAddress);
            return invitedUser;
        }).when(addressService).saveUserAddress(any(User.class), any(AddressDTO.class));

        // Expected/ Actual values

        // Calling the method
        userService.saveByDTO(userDTO);

        // Assertions
        assertNotNull(invitedUser.getAddress());
        assertNotEquals(userDTO.getUserCredentialDTO().getPassword(), invitedUser.getUserCredential().getPassword());

        // Behavior verification
        verify(numberValidator).validate(anyString());
        verify(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        verify(emailValidator).validate(anyString());
        verify(passwordValidator).validate(anyString());
        verify(passwordValidator).isPasswordNotMatch(anyString(), anyString());
        verify(userRepository).fetchAllEmail();
        verify(userRepository).fetchAllMobileNumber();
        verify(userMapper).toEntity(any(UserDTO.class));
        verify(userPasswordEncoder).encodePassword(any(User.class), anyString());
        verify(userRepository).save(any(User.class));
        verify(addressService).saveUserAddress(any(User.class), any(AddressDTO.class));
        assertDoesNotThrow(() ->  userService.saveByDTO(userDTO));
    }

    @Test
    @DisplayName("saving user validation 1: invalid mobile number validations")
    void invalidMobileNumberValidations() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09111111111")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("notExistingEmail@gmail.com")
                        .password("password")
                        .build())
                .invitationReferralCode("")
                .build();

        // Stubbing methods
        doThrow(MobileNumberException.class)
                .when(numberValidator)
                .validate(userDTO.getUserDetailsDTO().getMobileNumber());

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(MobileNumberException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(fullNameValidator,
                emailValidator,
                passwordValidator,
                userRepository,
                userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user validation 1: invalid full name validations")
    void invalidFullNameValidations() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09111111111")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("notExistingEmail@gmail.com")
                        .password("password")
                        .build())
                .invitationReferralCode("")
                .build();

        // Stubbing methods
        doNothing().when(numberValidator).validate(userDTO.getUserDetailsDTO().getMobileNumber());
        doThrow(FullNameException.class)
                .when(fullNameValidator)
                .validate(userDTO.getUserDetailsDTO());
        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(FullNameException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(emailValidator,
                passwordValidator,
                userRepository,
                userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user validation 3: invalid mobile number validations")
    void invalidEmailValidations() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09111111111")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("notExistingEmail@gmail.com")
                        .password("password")
                        .build())
                .invitationReferralCode("")
                .build();

        // Stubbing methods
        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doThrow(MalformedEmailException.class)
                .when(emailValidator)
                .validate(userDTO.getUserCredentialDTO().getEmail());
        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(MalformedEmailException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(passwordValidator,
                userRepository,
                userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user validation 4: invalid password validations")
    void invalidPasswordValidations() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09111111111")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("notExistingEmail@gmail.com")
                        .password("password")
                        .build())
                .invitationReferralCode("")
                .build();

        // Stubbing methods
        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doThrow(WeakPasswordException.class)
                .when(passwordValidator)
                .validate(userDTO.getUserCredentialDTO().getPassword());

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(WeakPasswordException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(userRepository,
                userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user validation 4: password not match")
    void savingUserPasswordNotMatch() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09111111111")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("notExistingEmail@gmail.com")
                        .password("password")
                        .confirmPassword("confirmPassword")
                        .build())
                .invitationReferralCode("")
                .build();

        // Stubbing methods
        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        when(passwordValidator.isPasswordNotMatch(anyString(), anyString())).thenReturn(true);

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(PasswordNotMatchException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(userRepository,
                userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user validation 5: user email should be unique")
    void savingUserEmailShouldBeUnique() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09999999999")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("existingEmail@gmail.com")
                        .password("password")
                        .confirmPassword("confirmPassword")
                        .build())
                .invitationReferralCode("")
                .build();

        List<String> existingEmails = List.of("existingEmail@gmail.com");

        // Stubbing methods
        when(userRepository.fetchAllEmail()).thenReturn(existingEmails);

        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        when(passwordValidator.isPasswordNotMatch(anyString(), anyString())).thenReturn(false);

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(EmailAlreadyExistsException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user validation 5: user mobile number should be unique")
    void savingUserMobileNumberShouldBeUnique() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09999999999")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("uniqueEmail@gmail.com")
                        .password("password")
                        .confirmPassword("confirmPassword")
                        .build())
                .invitationReferralCode("")
                .build();

        List<String> existingEmails = List.of("existingEmail@gmail.com");
        List<String> existingMobileNumbers = List.of("09999999999");

        // Stubbing methods
        when(userRepository.fetchAllEmail()).thenReturn(existingEmails);
        when(userRepository.fetchAllMobileNumber()).thenReturn(existingMobileNumbers);

        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        when(passwordValidator.isPasswordNotMatch(anyString(), anyString())).thenReturn(false);

        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertThrowsExactly(MobileNumberExistsException.class, () -> userService.saveByDTO(userDTO));

        // Behavior verification
        verifyNoInteractions(userMapper,
                userPasswordEncoder,
                addressService);
    }

    @Test
    @DisplayName("saving user workflow 1: adding registating user to inviting user invited users")
    void whenRegistratingUserHaveReferralCodeHeShouldBeAddedToInvitingUserInvitedUsers() {
        // Mock data
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName("First name")
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber("09999999991")
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email("uniqueEmail@gmail.com")
                        .password("password")
                        .confirmPassword("confirmPassword")
                        .build())
                .invitationReferralCode("invitingUserReferralCode")
                .build();

        List<String> existingEmails = List.of("existingEmail@gmail.com");
        List<String> existingMobileNumbers = List.of("09999999999");

        UserAddress userAddress = new UserAddress();
        User invitedUser = User.builder()
                .userCredential(Credential.builder()
                        .email("uniqueEmail@gmail.com")
                        .password("password")
                        .build())
                .userDetails(UserDetails.builder()
                        .firstName("firstName")
                        .build())
                .build();

        User invitingUser = User.builder()
                .referralCode("invitingUserReferralCode")
                .referredUsers(new HashSet<>())
                .build();

        // Stubbing methods
        when(userRepository.fetchAllEmail()).thenReturn(existingEmails);
        when(userRepository.fetchAllMobileNumber()).thenReturn(existingMobileNumbers);
        when(userRepository.fetchByReferralCode(anyString())).thenReturn(Optional.of(invitingUser));

        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        when(passwordValidator.isPasswordNotMatch(anyString(), anyString())).thenReturn(false);
        when(userMapper.toEntity(userDTO)).thenReturn(invitedUser);
        when(userRepository.save(any(User.class))).thenReturn(invitedUser);
        doAnswer(i -> {
            invitingUser.setAddress(userAddress);
            return invitingUser;
        }).when(addressService).saveUserAddress(any(User.class), any(AddressDTO.class));
        // Expected/ Actual values

        // Calling the method
        // Assertions
        assertDoesNotThrow(() -> userService.saveByDTO(userDTO));
        assertNotNull(invitingUser);
        assertNotNull(invitingUser.getAddress());

        // Behavior verification
        verify(numberValidator).validate(anyString());
        verify(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        verify(emailValidator).validate(anyString());
        verify(passwordValidator).validate(anyString());
        verify(passwordValidator).isPasswordNotMatch(anyString(), anyString());
        verify(userRepository).fetchAllEmail();
        verify(userRepository).fetchAllMobileNumber();
        verify(userMapper).toEntity(any(UserDTO.class));
        verify(userPasswordEncoder).encodePassword(any(User.class), anyString());

        verify(userRepository).fetchByReferralCode(anyString());
        verify(userRepository, times(2)).save(any(User.class));
        verify(addressService).saveUserAddress(any(User.class), any(AddressDTO.class));
        assertDoesNotThrow(() ->  userService.saveByDTO(userDTO));
    }

    @Test
    void saveByDTOWithPicture() throws IOException {
        // Mock data
        String notExistingEmail = "notExistingEmail@gmail.com";
        String notExistingMobileNumber = "09111111111";

        String firstName = "firstName";
        String password = "password";
        String confirmPassword = "confirmPassword";
        UserDTO userDTO = UserDTO.builder()
                .userDetailsDTO(UserDTO.UserDetailsDTO.builder()
                        .firstName(firstName)
                        .middleName("MIddle name")
                        .lastName("Last name")
                        .gender("MALE")
                        .birthDate(LocalDate.now())
                        .mobileNumber(notExistingMobileNumber)
                        .picture("picutre")
                        .suffix("NONE")
                        .build())
                .addressDTO(AddressDTO.builder()
                        .details("detauk")
                        .regionName("region name")
                        .provinceName("province name")
                        .cityName("city name")
                        .baranggayName("barnaggay name")
                        .build())
                .userCredentialDTO(CredentialDTO.builder()
                        .email(notExistingEmail)
                        .password(password)
                        .confirmPassword(confirmPassword)
                        .build())
                .invitationReferralCode(null)
                .build();

        List<String> emails = Arrays.asList("email@gmail.com");
        List<String> mobileNumbers = Arrays.asList("09999999999");

        UserAddress userAddress = new UserAddress();
        User invitedUser = User.builder()
                .userCredential(Credential.builder()
                        .email(notExistingEmail)
                        .password(password)
                        .build())
                .userDetails(UserDetails.builder()
                        .firstName(firstName)
                        .build())
                .build();

        // Stubbing methods
        when(userRepository.fetchAllEmail()).thenReturn(emails);
        when(userRepository.fetchAllMobileNumber()).thenReturn(mobileNumbers);

        doNothing().when(numberValidator).validate(anyString());
        doNothing().when(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        doNothing().when(emailValidator).validate(anyString());
        doNothing().when(passwordValidator).validate(anyString());
        when(passwordValidator.isPasswordNotMatch(anyString(), anyString())).thenReturn(false);
        when(userMapper.toEntity(any(UserDTO.class))).thenReturn(invitedUser);
        doAnswer(i -> {
            invitedUser.getUserCredential().setPassword("hashedPassword");
            return invitedUser;
        }).when(userPasswordEncoder).encodePassword(any(User.class), anyString());
        when(userRepository.save(any(User.class))).thenReturn(invitedUser);
        doAnswer(i -> {
            invitedUser.setAddress(userAddress);
            return invitedUser;
        }).when(addressService).saveUserAddress(any(User.class), any(AddressDTO.class));

        // Expected/ Actual values

        // Calling the method
        userService.saveByDTO(userDTO, MultiPartFileDataFactory.notEmpty());

        // Assertions
        assertNotNull(invitedUser.getAddress());
        assertNotNull(invitedUser.getUserDetails().getPicture());
        assertNotEquals(userDTO.getUserCredentialDTO().getPassword(), invitedUser.getUserCredential().getPassword());

        // Behavior verification
        verify(numberValidator).validate(anyString());
        verify(fullNameValidator).validate(any(UserDTO.UserDetailsDTO.class));
        verify(emailValidator).validate(anyString());
        verify(passwordValidator).validate(anyString());
        verify(passwordValidator).isPasswordNotMatch(anyString(), anyString());
        verify(userRepository).fetchAllEmail();
        verify(userRepository).fetchAllMobileNumber();
        verify(userMapper).toEntity(any(UserDTO.class));
        verify(userPasswordEncoder).encodePassword(any(User.class), anyString());
        verify(userRepository, times(2)).save(any(User.class));
        verify(addressService).saveUserAddress(any(User.class), any(AddressDTO.class));
        verify(imageUploader).upload(anyString(), any(MultipartFile.class));
        assertDoesNotThrow(() ->  userService.saveByDTO(userDTO));
    }

    @Test
    void saveByDTOWithPictureShouldNotBeEmpty() {
        // Mock data
        assertThrowsExactly(ResourceException.class, () -> userService.saveByDTO(new UserDTO(), MultiPartFileDataFactory.empty()));

        // Stubbing methods

        // Expected/ Actual values

        // Calling the method

        // Assertions

        // Behavior verification
        verifyNoInteractions(numberValidator,
                fullNameValidator,
                emailValidator,
                passwordValidator,
                userRepository,
                userMapper,
                userPasswordEncoder,
                addressService,
                imageUploader
                );
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