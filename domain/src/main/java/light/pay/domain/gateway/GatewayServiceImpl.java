package light.pay.domain.gateway;

import light.pay.api.accounts.AccountService;
import light.pay.api.accounts.request.CreateAccountRequest;
import light.pay.api.errors.Response;
import light.pay.api.gateway.GatewayService;
import light.pay.api.gateway.request.PayRequest;
import light.pay.api.gateway.request.RegisterCustomerRequest;
import light.pay.api.gateway.request.RegisterMerchantRequest;
import light.pay.api.gateway.request.TopupRequest;
import light.pay.api.gateway.response.PayResponse;
import light.pay.api.gateway.response.RegisterCustomerResponse;
import light.pay.api.gateway.response.RegisterMerchantResponse;
import light.pay.api.gateway.response.TopupResponse;
import light.pay.api.transactions.TransactionService;
import light.pay.api.wallets.WalletService;
import light.pay.api.wallets.request.CreateWalletRequest;
import light.pay.domain.constants.DomainConstants;

import java.util.UUID;

public class GatewayServiceImpl implements GatewayService {

    private AccountService accountService;
    private WalletService walletService;
    private TransactionService transactionService;

    public GatewayServiceImpl(AccountService accountService, WalletService walletService, TransactionService transactionService) {
        this.accountService = accountService;
        this.walletService = walletService;
        this.transactionService = transactionService;
    }

    @Override
    public Response<RegisterCustomerResponse> registerCustomer(RegisterCustomerRequest request) {
        return registerAccount(request.getName(), request.getEmail(), request.getPhoneNumber(), DomainConstants.Customer.CUSTOMER_TYPE)
                .map(userId -> RegisterCustomerResponse.builder()
                        .userID(userId)
                        .name(request.getName())
                        .email(request.getEmail())
                        .phoneNumber(request.getPhoneNumber())
                        .build());
    }

    @Override
    public Response<RegisterMerchantResponse> registerMerchant(RegisterMerchantRequest request) {
        return registerAccount(request.getName(), request.getEmail(), "", DomainConstants.Customer.MERCHANT_TYPE)
                .map(userId -> RegisterMerchantResponse.builder()
                        .merchantID(userId)
                        .name(request.getName())
                        .email(request.getEmail())
                        .build());
    }

    @Override
    public Response<TopupResponse> topup(TopupRequest request) {
        return null;
    }

    @Override
    public Response<PayResponse> pay(PayRequest request) {
        return null;
    }

    private Response<String> registerAccount(String name, String email, String phoneNumber, int userType) {
        String userId = UUID.randomUUID().toString();
        CreateAccountRequest createAccountRequest = CreateAccountRequest.builder()
                .userID(userId)
                .name(name)
                .email(email)
                .phoneNumber(phoneNumber)
                .userType(userType)
                .build();

        Response<String> accountResponse = accountService.createAccount(createAccountRequest);
        if (!accountResponse.isSuccess()) {
            return (Response) accountResponse;
        }

        String walletId = UUID.randomUUID().toString();
        CreateWalletRequest createWalletRequest = CreateWalletRequest.builder()
                .userID(userId)
                .walletID(walletId)
                .build();

        Response<String> walletResponse = walletService.createWallet(createWalletRequest);
        if (!walletResponse.isSuccess()) {
            return (Response) walletResponse;
        }

        return Response.createSuccessResponse(userId);
    }
}