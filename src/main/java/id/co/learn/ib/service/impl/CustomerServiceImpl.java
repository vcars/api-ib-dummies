package id.co.learn.ib.service.impl;

import com.alibaba.fastjson.JSON;
import id.co.learn.ib.dto.*;
import id.co.learn.ib.service.CustomerService;
import id.co.learn.ib.constants.IBConstants;
import id.co.learn.ib.repository.CustomerRepository;
import id.co.learn.ib.request.LoginRequest;
import id.co.learn.ib.util.CacheUtility;
import id.co.learn.ib.util.UserUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserUtility userUtility;

    @Autowired
    private CacheUtility cacheUtility;

    @Value("${ib.user.session.login}")
    private Integer loginSessionTime;

    @Transactional
    @Override
    public SessionDto signIn(LoginRequest request) {
        SessionDto sessionDto = new SessionDto();
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        Boolean isAuthenticated = authentication.isAuthenticated();
        if (!isAuthenticated) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, IBConstants.ERR_UNAUTHORIZED_ACCESS);
        }
        this.customerRepository.getUserByUsername(request.getUsername()).ifPresentOrElse(data->{
            data.setLastLogin(new Date());
            this.customerRepository.save(data);
            sessionDto.setUsername(data.getUsername());
            sessionDto.setFullname(data.getFullname());
            sessionDto.setLastLogin(data.getLastLogin().getTime());
            sessionDto.setToken(this.userUtility.getNewJwt(request.getUsername()));
            this.cacheUtility.set(IBConstants.RDS_USER_LOGIN, sessionDto.getUsername(),
                    JSON.toJSONString(sessionDto), this.loginSessionTime);
        },()->{
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, IBConstants.ERR_UNAUTHORIZED_ACCESS);
        });
        return sessionDto;
    }

    @Override
    public Boolean signOut(String token) {
        SessionDto sessionDto = userUtility.getCurrentUserInfo(token);
        if(ObjectUtils.isEmpty(sessionDto) ||
                StringUtils.isEmpty(this.cacheUtility.get(IBConstants.RDS_USER_LOGIN, sessionDto.getUsername()))){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, IBConstants.ERR_FORBIDDEN_ACCESS);
        }
        this.cacheUtility.delete(IBConstants.RDS_USER_LOGIN, sessionDto.getUsername());
        return true;
    }

    @Override
    public List<MenuDto> getNavigation(String lang) {
        lang = (StringUtils.isEmpty(lang)) ? IBConstants.LANG_ID : lang;
        List<MenuDto> navigation = new ArrayList<>();
        List<MenuDto> transferMenus = new ArrayList<>();
        transferMenus.add(MenuDto.builder().order(1).label((lang.equals(IBConstants.LANG_ID)) ? "Transfer ke Rekening X" : "Transfer to X Account").build());
        transferMenus.add(MenuDto.builder().order(2).label((lang.equals(IBConstants.LANG_ID)) ? "Transfer ke Bank Lain Dalam Negeri" : "Transfer to Another Domestic Bank").build());
        transferMenus.add(MenuDto.builder().order(3).label((lang.equals(IBConstants.LANG_ID)) ? "Transfer Terjadwal" : "Scheduled Transfer").build());
        MenuDto transfer = MenuDto.builder().children(transferMenus).order(1).label("Transfer").build();

        List<MenuDto> paymentMenus = new ArrayList<>();
        paymentMenus.add(MenuDto.builder().order(1).label("Internet").build());
        paymentMenus.add(MenuDto.builder().order(2).label((lang.equals(IBConstants.LANG_ID)) ? "Pendidikan" : "Education").build());
        paymentMenus.add(MenuDto.builder().order(3).label((lang.equals(IBConstants.LANG_ID)) ? "Penerimaan Negara" : "State Revenue").build());
        paymentMenus.add(MenuDto.builder().order(4).label("Multipayment").build());
        paymentMenus.add(MenuDto.builder().order(6).label((lang.equals(IBConstants.LANG_ID)) ? "PLN" : "Electricity").build());
        paymentMenus.add(MenuDto.builder().order(7).label((lang.equals(IBConstants.LANG_ID)) ? "Telekomunikasi" : "Telecommunication").build());
        MenuDto payment = MenuDto.builder().children(paymentMenus).order(2).label((lang.equals(IBConstants.LANG_ID)) ? "Pembayaran" : "Payment").build();

        List<MenuDto> buyMenus = new ArrayList<>();
        buyMenus.add(MenuDto.builder().order(1).label((lang.equals(IBConstants.LANG_ID)) ? "Pulsa" : "Cellular Top Up").build());
        buyMenus.add(MenuDto.builder().order(2).label((lang.equals(IBConstants.LANG_ID)) ? "Paket Data" : "Data Package").build());
        buyMenus.add(MenuDto.builder().order(3).label((lang.equals(IBConstants.LANG_ID)) ? "PLN Prabayar" : "Electricity").build());
        MenuDto buy = MenuDto.builder().children(buyMenus).order(3).label((lang.equals(IBConstants.LANG_ID)) ? "Pembelian" : "Buy").build();
        List<MenuDto> topUpMenus = new ArrayList<>();
        topUpMenus.add(MenuDto.builder().order(1).label("Top-Up LinkAja").build());
        MenuDto topUp = MenuDto.builder().children(buyMenus).order(4).label("Top-Up").build();

        List<MenuDto> eMoneyMenus = new ArrayList<>();
        eMoneyMenus.add(MenuDto.builder().order(1).label("Top-Up E-Money").build());
        eMoneyMenus.add(MenuDto.builder().order(2).label((lang.equals(IBConstants.LANG_ID)) ? "Daftar Kartu E-Money" : "Register E-Money").build());
        MenuDto eMoney = MenuDto.builder().children(eMoneyMenus).order(5).label("Top-Up").build();

        List<MenuDto> otherMenus = new ArrayList<>();
        otherMenus.add(MenuDto.builder().order(1).label((lang.equals(IBConstants.LANG_ID)) ? "Permintaan Blokir Rekening" : "Block My Account").build());
        MenuDto other = MenuDto.builder().children(otherMenus).order(6).label((lang.equals(IBConstants.LANG_ID)) ? "Lainnya" : "Other").build();

        navigation.add(transfer);
        navigation.add(payment);
        navigation.add(buy);
        navigation.add(topUp);
        navigation.add(eMoney);
        navigation.add(other);
        return navigation;
    }

    @Override
    public List<PromoDto> getPromoList() {
        String image1 = "https://hatrabbits.com/wp-content/uploads/2016/12/rare-combinaties.jpg";
        String image2 = "http://www.mandysam.com/img/random.jpg";
        List<PromoDto> promoList = new ArrayList<>();
        String loremIpsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        promoList.add(PromoDto.builder().title("Promo A").content(loremIpsum).featuredImage(image1).build());
        promoList.add(PromoDto.builder().title("Promo B").content(loremIpsum).featuredImage(image2).build());
        return promoList;
    }

    @Override
    public List<CreditCardDto> getCreditCardList(String token) {
        Integer randomCreditCard = new Random().nextInt(4);
        SessionDto sessionDto = userUtility.getCurrentUserInfo(token);
        ArrayList<CreditCardDto> creditCardList = new ArrayList<>();
        for(int i = 0; i < randomCreditCard; i++){
            Random rand = new Random();
            String randomCardNo = String.format((Locale) null,
                    "52%02d-%04d-%04d-%04d",
                    rand.nextInt(100),
                    rand.nextInt(10000),
                    rand.nextInt(10000),
                    rand.nextInt(10000));
            BigDecimal randomUsage = new BigDecimal(Math.random()).multiply(new BigDecimal(150000));
            creditCardList.add(CreditCardDto.builder().cardHolder(sessionDto.getFullname())
                    .expiry("11/25").cardNo(randomCardNo).limit(null)
                    .usage(randomUsage.setScale(2, RoundingMode.CEILING)).cardType("Kartu Tipe - ".concat(String.valueOf(i+1))).build());
        }
        return creditCardList;
    }

    @Override
    public List<DebitCardDto> getDebitCardList(String token) {
        Integer randomDebitCard = new Random().nextInt(2) + 1;
        SessionDto sessionDto = userUtility.getCurrentUserInfo(token);
        ArrayList<DebitCardDto> debitCardList = new ArrayList<>();
        for(int i = 0; i < randomDebitCard; i++){
            Random rand = new Random();
            String randomCardNo = String.format((Locale) null,
                    "11%02d-%04d-%04d-%04d",
                    rand.nextInt(100),
                    rand.nextInt(10000),
                    rand.nextInt(10000),
                    rand.nextInt(10000));
            BigDecimal randomBalance = new BigDecimal(Math.random()).multiply(new BigDecimal(30000000));
            debitCardList.add(DebitCardDto.builder().cardHolder(sessionDto.getFullname())
                    .expiry("11/25").cardNo(randomCardNo).balance(randomBalance.setScale(2, RoundingMode.CEILING))
                    .cardType((i%2==0)? "VISA" : "Master Card").build());
        }
        return debitCardList;
    }

}
