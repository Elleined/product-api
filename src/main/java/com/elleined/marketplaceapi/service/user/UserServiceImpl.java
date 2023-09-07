package com.elleined.marketplaceapi.service.user;

import com.elleined.marketplaceapi.client.ForumClient;
import com.elleined.marketplaceapi.dto.CredentialDTO;
import com.elleined.marketplaceapi.dto.ShopDTO;
import com.elleined.marketplaceapi.dto.UserDTO;
import com.elleined.marketplaceapi.dto.forum.ForumUserDTO;
import com.elleined.marketplaceapi.exception.field.HasDigitException;
import com.elleined.marketplaceapi.exception.field.MalformedEmailException;
import com.elleined.marketplaceapi.exception.field.MobileNumberException;
import com.elleined.marketplaceapi.exception.field.password.PasswordException;
import com.elleined.marketplaceapi.exception.field.password.PasswordNotMatchException;
import com.elleined.marketplaceapi.exception.field.password.WeakPasswordException;
import com.elleined.marketplaceapi.exception.resource.AlreadyExistException;
import com.elleined.marketplaceapi.exception.resource.ResourceNotFoundException;
import com.elleined.marketplaceapi.exception.user.InvalidUserCredentialException;
import com.elleined.marketplaceapi.exception.user.UserAlreadyVerifiedException;
import com.elleined.marketplaceapi.mapper.UserMapper;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.user.User;
import com.elleined.marketplaceapi.repository.OrderItemRepository;
import com.elleined.marketplaceapi.repository.ShopRepository;
import com.elleined.marketplaceapi.repository.UserRepository;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.utils.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;

    private final UserRepository userRepository;
    private final UserCredentialValidator userCredentialValidator;
    private final UserDetailsValidator userDetailsValidator;
    private final UserMapper userMapper;

    private final OrderItemRepository orderItemRepository;

    private final ShopRepository shopRepository;

    private final AddressService addressService;

    private final ForumClient forumClient;

    @Override
    public User getById(int id) throws ResourceNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User with id of " + id + " does not exists!"));
    }

    @Override
    public Set<User> getAllById(Set<Integer> userIds) throws ResourceNotFoundException {
        return userRepository.findAllById(userIds).stream().collect(Collectors.toUnmodifiableSet());
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

        userDetailsValidator.validatePhoneNumber(userDTO.getUserDetailsDTO());
        userDetailsValidator.validateFullName(userDTO.getUserDetailsDTO());
        userCredentialValidator.validateEmail(userDTO.getUserCredentialDTO());
        String password = userDTO.getUserCredentialDTO().getPassword();
        String confirmPassword = userDTO.getUserCredentialDTO().getConfirmPassword();
        if (isTwoPasswordNotMatch(password, confirmPassword)) throw new PasswordNotMatchException("Password and confirm password not match!");
        userCredentialValidator.validatePassword(password);

        User registeringUser = userMapper.toEntity(userDTO);
        this.encodePassword(registeringUser, registeringUser.getUserCredential().getPassword());
        userRepository.save(registeringUser);
        addressService.saveUserAddress(registeringUser, userDTO.getAddressDTO());
        if (!StringUtil.isNotValid(userDTO.getInvitationReferralCode())) addInvitedUser(userDTO.getInvitationReferralCode(), registeringUser);

        saveForumUser(registeringUser);
        log.debug("User with name of {} saved successfully with id of {}", registeringUser.getUserDetails().getFirstName(), registeringUser.getId());
        return registeringUser;
    }

    private void saveForumUser(User user) {
        ForumUserDTO forumUserDTO = ForumUserDTO.builder()
                .id(user.getId())
                .picture(user.getUserDetails().getPicture())
                .name(user.getUserDetails().getFirstName())
                .email(user.getUserCredential().getEmail())
                .UUID(user.getReferralCode())
                .build();
        forumClient.save(forumUserDTO);
        log.debug("Saving user with id of {} in forum api success", user.getId());
    }

    @Override
    public void resendValidId(User currentUser, String validId) throws UserAlreadyVerifiedException {
        if (currentUser.isVerified()) throw new UserAlreadyVerifiedException("Cannot resend valid id! User with id of " + currentUser.getId() + " are already been verified");
        currentUser.getUserVerification().setValidId(validId);
        userRepository.save(currentUser);
        log.debug("User with id of {} resended  valid id {}", currentUser.getId(), validId);
    }

    @Override
    public User login(CredentialDTO userCredentialDTO) throws ResourceNotFoundException, InvalidUserCredentialException {
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
        return userRepository.fetchByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User with email of " + email + " does not exists!"));
    }

    @Override
    public boolean existsById(int userId) {
        return userRepository.existsById(userId);
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
        if (owner.isVerified()) throw new AlreadyExistException("Cannot resend shop registration! because user with id of " + owner.getId() + " are already been verified!");
        if (owner.hasShopRegistration()) throw new AlreadyExistException("User with id of " + owner.getId() + " already have shop registration! Please wait for email notification. If don't receive an email consider resending your valid id!");

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
        return orderItemRepository.findById((long) orderItemId).orElseThrow(() -> new ResourceNotFoundException("Order item with id of " + orderItemId + " does not exists!"));
    }

    @Override
    public User getByReferralCode(String referralCode) throws ResourceNotFoundException {
        return userRepository.fetchByReferralCode(referralCode).orElseThrow(() -> new ResourceNotFoundException("User with referral code of " + referralCode +  " does not exists!"));
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
    public void changePassword(User user, String newPassword, String retypeNewPassword) throws PasswordException {
        if (isTwoPasswordNotMatch(newPassword, retypeNewPassword)) throw new PasswordNotMatchException("New and re-type password not match!");
        userCredentialValidator.validatePassword(newPassword);

        this.encodePassword(user, newPassword);
        userRepository.save(user);
        log.debug("User with id of {} successfully changed his/her password", user.getId());
    }
}
