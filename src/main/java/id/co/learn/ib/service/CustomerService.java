package id.co.learn.ib.service;

import id.co.learn.ib.dto.*;
import id.co.learn.ib.request.LoginRequest;

import java.util.List;

public interface CustomerService {

    SessionDto signIn(LoginRequest request);
    Boolean signOut(String token);
    List<MenuDto> getNavigation(String lang);
    List<PromoDto> getPromoList();
    List<CreditCardDto> getCreditCardList(String token);
    List<DebitCardDto> getDebitCardList(String token);

}
