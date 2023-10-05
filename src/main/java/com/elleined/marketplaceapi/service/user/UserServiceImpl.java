package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.client.ForumClient;
import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.forum.ForumUserDTO;
import com.elleined.marketplaceapi.exception.field.FieldException;
import com.elleined.marketplaceapi.exception.field.HasDigitException;
import com.elleined.marketplaceapi.exception.field.MalformedEmailException;
import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import com.elleined.marketplaceapi.exception.field.password.PasswordException;
import com.elleined.marketplaceapi.exception.field.password.PasswordNotMatchException;
import com.elleined.marketplaceapi.exception.field.password.WeakPasswordException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.user.NoShopRegistrationException;
import com.elleined.marketplaceapi.exception.user.UserAlreadyVerifiedException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.repository.ShopRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.GetAllUtilityService;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.image.ImageUploader;
import com.elleined.marketplaceapi.service.password.EntityPasswordEncoder;
import com.elleined.marketplaceapi.service.validator.EmailValidator;
import com.elleined.marketplaceapi.service.validator.FullNameValidator;
import com.elleined.marketplaceapi.service.validator.NumberValidator;
import com.elleined.marketplaceapi.service.validator.PasswordValidator;
import com.elleined.marketplaceapi.utils.DirectoryFolders;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService, EntityPasswordEncoder<User>,
        ReferralService, RegistrationPromoService {
    private final PasswordEncoder passwordEncoder;

    private final ImageUploader imageUploader;

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final OrderItemRepository orderItemRepository;

    private final ShopRepository shopRepository;

    private final AddressService addressService;

    private final ForumClient forumClient;

    private final GetAllUtilityService getAllUtilityService;

    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final NumberValidator numberValidator;
    private final FullNameValidator fullNameValidator;

    @Value("cropTrade.img.directory")
    private String cropTradeImgDirectory;

    @Override
    public User getById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User does not exists!"));
    }

    @Override
    public Set<User> getAllById(Set<Integer> userIds) throws ResourceNotFoundException {
        return userRepository.findAllById(userIds).stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<User> getAllSeller() {
        return userRepository.findAll().stream()
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .filter(User::hasNotBeenRejected) // Checking for rejected user
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> searchAllSellerByName(String username) {
        return userRepository.searchByUserName(username).stream()
                .filter(User::isVerified)
                .filter(User::hasShopRegistration)
                .filter(User::hasNotBeenRejected) // Checking for rejected user
                .collect(Collectors.toSet());
    }

    @Override
    public User saveByDTO(UserDTO userDTO)
            throws ResourceNotFoundException,
            HasDigitException,
            PasswordNotMatchException,
            WeakPasswordException,
            MalformedEmailException,
            AlreadyExistException,
            MobileNumberException {

        String email = userDTO.getUserCredentialDTO().getEmail();
        String mobileNumber = userDTO.getUserDetailsDTO().getMobileNumber();
        String password = userDTO.getUserCredentialDTO().getPassword();
        String confirmPassword = userDTO.getUserCredentialDTO().getConfirmPassword();

        numberValidator.validate(mobileNumber);
        fullNameValidator.validate(userDTO.getUserDetailsDTO());
        emailValidator.validate(email);
        passwordValidator.validate(password);
        if (isTwoPasswordNotMatch(password, confirmPassword)) throw new PasswordNotMatchException("Password and confirm password not match!");
        if (getAllUtilityService.getAllEmail().contains(email)) throw new AlreadyExistException("This email " + email + " is already associated with an account!");
        if (getAllUtilityService.getAllMobileNumber().contains(mobileNumber)) throw new AlreadyExistException("Mobile number of " + mobileNumber + " are already associated with another account!");

        User registeringUser = userMapper.toEntity(userDTO);
        this.encodePassword(registeringUser, registeringUser.getUserCredential().getPassword());
        userRepository.save(registeringUser);
        addressService.saveUserAddress(registeringUser, userDTO.getAddressDTO());
        if (!StringUtil.isNotValid(userDTO.getInvitationReferralCode())) addInvitedUser(userDTO.getInvitationReferralCode(), registeringUser);

        log.debug("User with name of {} saved successfully with id of {}", registeringUser.getUserDetails().getFirstName(), registeringUser.getId());
        return registeringUser;
    }

    @Override
    public User saveByDTO(UserDTO dto, MultipartFile profilePicture) throws ResourceNotFoundException, HasDigitException, PasswordNotMatchException, WeakPasswordException, MalformedEmailException, AlreadyExistException, MobileNumberException, IOException {
        User registeringUser = saveByDTO(dto);

        imageUploader.upload(cropTradeImgDirectory + DirectoryFolders.PROFILE_PICTURES_FOLDER, profilePicture);
        registeringUser.getUserDetails().setPicture(profilePicture.getOriginalFilename());
        userRepository.save(registeringUser);

        return registeringUser;
    }

    @Override
    public void saveForumUser(User user) {
        ForumUserDTO forumUserDTO = ForumUserDTO.builder()
                .picture(user.getUserDetails().getPicture())
                .name(user.getFullName())
                .email(user.getUserCredential().getEmail())
                .UUID(user.getReferralCode())
                .build();
        forumClient.save(forumUserDTO);
        log.debug("Saving user with id of {} in forum api success", user.getId());
    }

    @Override
    public void resendValidId(User currentUser, String validId)
            throws UserAlreadyVerifiedException,
            NoShopRegistrationException,
            FieldException {

        if (StringUtil.isNotValid(validId)) throw new FieldException("Please provide your new valid id in valid id input...");
        if (currentUser.isVerified()) throw new UserAlreadyVerifiedException("Cannot resend valid id! you are already been verified");
        if (!currentUser.hasShopRegistration()) throw new NoShopRegistrationException("Cannot resent valid id! you need to submit a shop registration before resending you valid id.");

        currentUser.getUserVerification().setValidId(validId);
        userRepository.save(currentUser);
        log.debug("User with id of {} resended valid id {}", currentUser.getId(), validId);
    }


    @Override
    public User login(CredentialDTO userCredentialDTO)
            throws ResourceNotFoundException,
            InvalidUserCredentialException {

        String email = userCredentialDTO.getEmail();
        if (!userRepository.fetchAllEmail().contains(email)) throw new InvalidUserCredentialException("You have entered an invalid username or password");

        User user = getByEmail(userCredentialDTO.getEmail());
        String encodedPassword = user.getUserCredential().getPassword();
        if (!passwordEncoder.matches(userCredentialDTO.getPassword(), encodedPassword)) throw new InvalidUserCredentialException("You have entered an invalid username or password");
        log.debug("User with email of {} logged in marketplace api", userCredentialDTO.getEmail());
        return user;
    }

    @Override
    public User getByEmail(String email) throws ResourceNotFoundException {
        return userRepository.fetchByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User does not exists!"));
    }

    @Override
    public boolean isLegibleForRegistrationPromo() {
        return userRepository.findAll().size() <= REGISTRATION_LIMIT_PROMO;
    }

    @Override
    public void availRegistrationPromo(User registratingUser) {
        BigDecimal newBalance = registratingUser.getBalance().add(REGISTRATION_REWARD);
        registratingUser.setBalance(newBalance);
        userRepository.save(registratingUser);
        log.debug("Registrating user receives {} as registration reward for the first {} users", REGISTRATION_REWARD, REGISTRATION_LIMIT_PROMO);
    }

    @Override
    public void sendShopRegistration(User owner, ShopDTO shopDTO) throws AlreadyExistException {
        if (owner.isVerified()) throw new AlreadyExistException("Cannot send shop registration! because you are already been verified!");
        if (owner.hasShopRegistration()) throw new AlreadyExistException("Cannot send shop registration! because you already have shop registration! Please wait for email notification. If don't receive an email consider resending your valid id!");
        if (isShopNameAlreadyExists(shopDTO.getShopName())) throw new AlreadyExistException("Cannot send shop registration! because the shop name you provided " + shopDTO.getShopName() + " already been taken by another seller!");

        Shop shop = Shop.builder()
                .picture(shopDTO.getPicture())
                .name(shopDTO.getShopName())
                .description(shopDTO.getDescription())
                .owner(owner)
                .build();
        owner.getUserVerification().setValidId(shopDTO.getValidId());

        userRepository.save(owner);
        shopRepository.save(shop);
        log.debug("Shop registration of owner with id of {} success his verification are now visible in moderator", owner.getId());
    }

    @Override
    public OrderItem getOrderItemById(int orderItemId) throws ResourceNotFoundException {
        return orderItemRepository.findById((long) orderItemId).orElseThrow(() -> new ResourceNotFoundException("Order item does not exists!"));
    }

    @Override
    public User getByReferralCode(String referralCode) throws ResourceNotFoundException {
        return userRepository.fetchByReferralCode(referralCode).orElseThrow(() -> new ResourceNotFoundException("User does not exists!"));
    }

    @Override
    public void addInvitedUser(String invitingUserReferralCode, User invitedUser) {
        User invitingUser = getByReferralCode(invitingUserReferralCode);
        invitingUser.addInvitedUser(invitedUser);
        userRepository.save(invitingUser);
        log.debug("User with id of {} invited user with id of {} successfully", invitingUser.getId(), invitedUser.getId());
    }

    @Override
    public User getInvitingUser(User invitedUser) {
        return userRepository.findAll().stream()
                .map(User::getReferredUsers)
                .flatMap(Collection::stream)
                .filter(invitedUser::equals)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void encodePassword(User user, String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        user.getUserCredential().setPassword(encodedPassword);
    }

    @Override
    public void changePassword(String email, String newPassword, String retypeNewPassword)
            throws PasswordException,
            ResourceNotFoundException {

        User user = getByEmail(email);
        if (isTwoPasswordNotMatch(newPassword, retypeNewPassword)) throw new PasswordNotMatchException("New and re-type password not match!");
        passwordValidator.validate(newPassword);

        this.encodePassword(user, newPassword);
        userRepository.save(user);
        log.debug("User with id of {} successfully changed his/her password", user.getId());
    }

    @Override
    public void changePassword(User user, String oldPassword, String newPassword, String retypeNewPassword) throws PasswordException {
        String encodedPassword = user.getUserCredential().getPassword();
        if (!passwordEncoder.matches(oldPassword, encodedPassword)) throw new PasswordNotMatchException("Old password didn't match to your current password!");
        if (isTwoPasswordNotMatch(newPassword, retypeNewPassword)) throw new PasswordNotMatchException("New and re-type password not match!");
        passwordValidator.validate(newPassword);

        this.encodePassword(user, newPassword);
        userRepository.save(user);
        log.debug("User with id of {} successfully changed his/her password", user.getId());
    }


    private boolean isShopNameAlreadyExists(String shopName) {
        return shopRepository.findAll().stream()
                .map(Shop::getName)
                .anyMatch(shopName::equals);
    }
}
