package id.co.learn.ib.controller;

import id.co.learn.ib.service.CustomerService;
import id.co.learn.ib.constants.IBConstants;
import id.co.learn.ib.dto.CreditCardDto;
import id.co.learn.ib.dto.DebitCardDto;
import id.co.learn.ib.dto.SessionDto;
import id.co.learn.ib.request.LoginRequest;
import id.co.learn.ib.util.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping("/v1/sign-in")
    public ResponseEntity<Response> signIn(@RequestBody LoginRequest loginRequest){
        SessionDto sessionDto = customerService.signIn(loginRequest);
        Response response = new Response(sessionDto, "Hi Welcome to Dummy X IB", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/v1/sign-out")
    public ResponseEntity<Response> signOut(@RequestHeader(name= HttpHeaders.AUTHORIZATION) String token){
        Response response = new Response(null, "Thanks for Using Dummy X IB", customerService.signOut(token));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/v1/navigation/{lang}")
    public ResponseEntity<Response> navigation(@PathVariable("lang") String lang){
        if(lang.equals(IBConstants.LANG_ID) || lang.equals(IBConstants.LANG_EN)){
            Response response = new Response(this.customerService.getNavigation(lang), "Here is your navigation", true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Oops, which language that we should use?");
        }
    }

    @GetMapping("/v1/fiesta-poin")
    public ResponseEntity<Response> fiestaPoin(){
        Response response = new Response(new Random().nextInt(9999), "Here is your fiesta poin", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/v1/promo-banner")
    public ResponseEntity<Response> promoBanner(){
        Response response = new Response(this.customerService.getPromoList(), "Here is your promo list", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/v1/my-credit-cards")
    public ResponseEntity<Response> ownedCreditCards(@RequestHeader(name= HttpHeaders.AUTHORIZATION) String token){
        List<CreditCardDto> creditCardDtoList = this.customerService.getCreditCardList(token);
        if(creditCardDtoList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You do not have any credit card(s)");
        }
        Response response = new Response(creditCardDtoList, "Here is your credit card list", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/v1/my-debit-cards")
    public ResponseEntity<Response> ownedDebitCards(@RequestHeader(name= HttpHeaders.AUTHORIZATION) String token){
        List<DebitCardDto> debitCardList = this.customerService.getDebitCardList(token);
        if(debitCardList.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "You do not have any debit card(s)");
        }
        Response response = new Response(debitCardList, "Here is your debit card list", true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
