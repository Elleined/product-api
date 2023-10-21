package com.elleined.marketplaceapi.model.user;

import com.elleined.marketplaceapi.model.Credential;
import com.elleined.marketplaceapi.model.product.Product;
import com.elleined.marketplaceapi.model.Shop;
import com.elleined.marketplaceapi.model.address.DeliveryAddress;
import com.elleined.marketplaceapi.model.address.UserAddress;
import com.elleined.marketplaceapi.model.atm.transaction.DepositTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.PeerToPeerTransaction;
import com.elleined.marketplaceapi.model.atm.transaction.Transaction;
import com.elleined.marketplaceapi.model.atm.transaction.WithdrawTransaction;
import com.elleined.marketplaceapi.model.item.CartItem;
import com.elleined.marketplaceapi.model.item.OrderItem;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatMessage;
import com.elleined.marketplaceapi.model.message.prv.PrivateChatRoom;
import com.elleined.marketplaceapi.service.address.AddressService;
import com.elleined.marketplaceapi.service.fee.FeeService;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tbl_user", indexes = @Index(name = "referral_code_idx", columnList = "referral_code"))
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(
            name = "user_id",
            nullable = false,
            updatable = false,
            unique = true
    )
    private int id;

    @Column(
            name = "referral_code",
            unique = true,
            nullable = false,
            updatable = false
    )
    private String referralCode;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Embedded
    private UserVerification userVerification;
    @Embedded
    private Credential userCredential;
    @Embedded
    private UserDetails userDetails;


    // user id reference is in user address table
    @OneToOne(mappedBy = "user")
    private UserAddress address;

    @ManyToMany
    @JoinTable(
            name = "tbl_user_referral",
            joinColumns = @JoinColumn(name = "inviting_user_id",
                    referencedColumnName = "user_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "invited_user_id",
                    referencedColumnName = "user_id"
            )
    )
    private Set<User> referredUsers;

    // user id reference is in delivery address table
    @OneToMany(mappedBy = "user")
    @Setter(AccessLevel.NONE)
    private List<DeliveryAddress> deliveryAddresses;

    // user id reference is in order item table
    @OneToMany(mappedBy = "purchaser")
    @Setter(AccessLevel.NONE)
    private List<OrderItem> orderedItems;

    // user id reference is in cart item table
    @OneToMany(mappedBy = "purchaser")
    @Setter(AccessLevel.NONE)
    private List<CartItem> cartItems;

    // user id reference is in shop table
    @OneToOne(mappedBy = "owner")
    @PrimaryKeyJoinColumn
    @Setter(AccessLevel.NONE)
    private Shop shop;

    // user id reference is in premium user table
    @OneToOne(mappedBy = "user")
    @PrimaryKeyJoinColumn
    @Setter(AccessLevel.NONE)
    private Premium premium;

    // user id reference is in products table
    @OneToMany(mappedBy = "seller")
    @Setter(AccessLevel.NONE)
    private List<Product> products;

    // sender id reference is in peer to peer transaction table
    @OneToMany(mappedBy = "sender")
    private List<PeerToPeerTransaction> sentMoneyTransactions;

    // recipient id reference is in peer to peer transaction table
    @OneToMany(mappedBy = "receiver")
    private List<PeerToPeerTransaction> receiveMoneyTransactions;

    // user id reference is in withdraw transaction table
    @OneToMany(mappedBy = "user")
    private List<WithdrawTransaction> withdrawTransactions;

    // user id reference is in withdraw transaction table
    @OneToMany(mappedBy = "user")
    private List<DepositTransaction> depositTransactions;

    // user id reference is in private chat room message table
    @OneToMany(mappedBy = "sender")
    private List<PrivateChatMessage> privateChatMessages;

    // user id reference is in private chat room table
    @OneToMany(mappedBy = "sender")
    private List<PrivateChatRoom> createdChatRooms;

    // user id reference is in private chat room table
    @OneToMany(mappedBy = "receiver")
    private List<PrivateChatRoom> participatingChatRooms;

    public void addInvitedUser(User invitedUser) {
        this.getReferredUsers().add(invitedUser);
    }


    public boolean isPremium() {
        return this.getPremium() != null;
    }

    public boolean isVerified() {
        return this.getUserVerification().getStatus() == UserVerification.Status.VERIFIED;
    }

    public boolean hasShopRegistration() {
        return this.getShop() != null;
    }

    public boolean isProductAlreadyInCart(Product product) {
        return this.getCartItems().stream()
                .map(CartItem::getProduct)
                .anyMatch(product::equals);
    }

    public boolean hasReachedDeliveryAddressLimit() {
        return this.getDeliveryAddresses().size() == AddressService.DELIVERY_ADDRESS_LIMIT;
    }

    public boolean isBalanceNotEnoughForPremium() {
        return this.getBalance().compareTo(new BigDecimal(FeeService.PREMIUM_USER_FEE)) <= 0;
    }

    public boolean hasProduct(Product product) {
        return this.getProducts().stream().anyMatch(product::equals);
    }


    public boolean hasOrder(OrderItem orderItem) {
        return this.getOrderedItems().stream().anyMatch(orderItem::equals);
    }

    public boolean isPremiumSubscriptionExpired() {
        LocalDateTime premiumExpiration = this.getPremium().getRegistrationDate().plusMonths(1);
        return LocalDateTime.now().isAfter(premiumExpiration);
    }

    public boolean isPremiumAndNotExpired() {
        return isPremium() && !isPremiumSubscriptionExpired();
    }

    public boolean isRejected() {
        return this.getUserVerification().getValidId() == null;
    }

    public boolean hasNotBeenRejected() {
        return this.getUserVerification().getValidId() != null;
    }
    public boolean hasWithdrawTransaction(Transaction transaction) {
        return this.getWithdrawTransactions().stream().anyMatch(transaction::equals);
    }

    public String getFullName() {
        return this.getUserDetails().getFirstName() + " " + this.getUserDetails().getMiddleName() + " " + this.getUserDetails().getLastName();
    }
}
