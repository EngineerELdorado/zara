package com.zara.Zara.services;

import com.zara.Zara.dtos.responses.OpenExchangeRateResponse;
import com.zara.Zara.entities.Currency;
import com.zara.Zara.exceptions.exceptions.Zaka500Exception;
import com.zara.Zara.repositories.CurrencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.money.BigMoney;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final OpenExchangeRateService openExchangeRateService;

    public List<Currency> findAll() {
        return currencyRepository.findAllOrderByName();
    }

    @Transactional
    //@Scheduled(fixedRate = 3600000) // Every one hour
    @Scheduled(fixedDelay = 30000) // Every one hour
    public void updateCurrenciesExchangeRates() {

        OpenExchangeRateResponse response = null;
        try {
            response = openExchangeRateService.getExchangeRates();
        } catch (Exception e) {
            String msg = "An error has occurred while retrieving exchange rate from open exchange rate service. ";
            msg += "Possible reason(s) : " + ExceptionUtils.getRootCauseMessage(e);
            log.error(msg);
        }

        if (response != null && response.getRates() != null) {

            Map<String, BigDecimal> exchangeRates = response.getRates();
            currencyRepository.findAll().forEach(currency -> {

                try {
                    currency.setRate(exchangeRates.get(currency.getCode().toUpperCase()));
                    currencyRepository.save(currency);
                } catch (Exception e) {
                    String msg = "An error has occurred while updating exchange rate for currency : "
                            + currency.getCode() + ". ";
                    msg += "Possible reason(s) : " + ExceptionUtils.getRootCauseMessage(e);
                    log.error(msg);
                }
            });
            log.info("Updated rates");
        } else {

            String msg = "Could not retrieve exchangeRates from open exchange rate service";
            log.info(msg);
            try {
                //systemMailService.sendWarningEmail("Error retrieving exchange rates", msg);
            } catch (Exception ex) {
                msg = "Could not send alert email : " + msg + ". Possible reason(s) : "
                        + ExceptionUtils.getRootCauseMessage(ex);
                log.error(msg);
            }
        }

    }

    public BigDecimal convert(String fromCurrencyCode,
                              String toCurrencyCode, BigDecimal amount, Integer scale, RoundingMode roundingMode) {

        return doConvert(fromCurrencyCode, toCurrencyCode, amount,
                scale, roundingMode, null, null
        );
    }

    public BigDecimal convert(String fromCurrencyCode,
                              String toCurrencyCode,
                              BigDecimal amount,
                              Integer scale,
                              RoundingMode roundingMode,
                              BigDecimal fromCurrencyRate,
                              BigDecimal toCurrencyRate) {

        return doConvert(
                fromCurrencyCode, toCurrencyCode, amount, scale, roundingMode, fromCurrencyRate, toCurrencyRate
        );
    }

    private BigDecimal doConvert(String fromCurrencyCode,
                                 String toCurrencyCode,
                                 BigDecimal amount,
                                 Integer scale,
                                 RoundingMode roundingMode,
                                 @Nullable BigDecimal fromCurrencyRate, @Nullable BigDecimal toCurrencyRate) {

        scale = Math.min(scale, 5);
        if (fromCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            return amount;
        }

        if (fromCurrencyRate == null || fromCurrencyRate.compareTo(BigDecimal.ZERO) <= 0) {

            String fromCurrencyErrorMsg = "Currency code '" + fromCurrencyCode
                    + "' is currently not available or not supported";
            Currency fromCurrency = currencyRepository.findByCodeIgnoreCase(fromCurrencyCode)
                    .orElseThrow(() -> new Zaka500Exception(fromCurrencyErrorMsg));

            fromCurrencyRate = fromCurrency.getRate();
        }

        if (toCurrencyRate == null || toCurrencyRate.compareTo(BigDecimal.ZERO) <= 0) {

            String toCurrencyErrorMsg = "Currency code '" + toCurrencyCode
                    + "' is currently not available or not supported";
            Currency toCurrency = currencyRepository.findByCodeIgnoreCase(toCurrencyCode)
                    .orElseThrow(() -> new Zaka500Exception(toCurrencyErrorMsg));

            toCurrencyRate = toCurrency.getRate();
        }

        if (!fromCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {

            if (fromCurrencyCode.equalsIgnoreCase("USD")) { //If equals to system default currency (USD)
                amount = amount.multiply(toCurrencyRate);
                amount = amount.setScale(scale, roundingMode);
            } else {
                amount = amount.divide(fromCurrencyRate, scale, roundingMode);

                if (!toCurrencyCode.equalsIgnoreCase("USD")) {
                    amount = amount.multiply(toCurrencyRate);
                    amount = amount.setScale(scale, roundingMode);
                }
            }
        }
        return amount;
    }

    public BigDecimal convert(String fromCurrencyCode, String toCurrencyCode, BigDecimal amount) {
        return convert(fromCurrencyCode, toCurrencyCode, amount, 5, RoundingMode.HALF_UP);
    }

    public BigDecimal convertFromUSD(String toCurrencyCode, BigDecimal exchangeRate, BigDecimal amount) {
        return doConvertFromUSD(toCurrencyCode, exchangeRate, amount, 5, RoundingMode.HALF_UP);
    }

    BigMoney convertAmount(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode) {

        if (toCurrencyCode.toUpperCase().equals(fromCurrencyCode.toUpperCase()) || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigMoney.parse(fromCurrencyCode.toUpperCase() + " " + amount);
        }

        BigMoney convertedAmount = BigMoney.parse(toCurrencyCode.toUpperCase() + " " + convert(fromCurrencyCode, toCurrencyCode, amount));
        return (convertedAmount.getScale() > 2) ? convertedAmount.rounded(2, RoundingMode.HALF_UP) : convertedAmount;
    }

    BigMoney convertUSDAmount(BigDecimal amount, String toCurrencyCode, BigDecimal exchangeRate) {
        BigMoney convertedAmount = BigMoney.parse(
                toCurrencyCode.toUpperCase() + " " +
                        convertFromUSD(toCurrencyCode, exchangeRate, amount)
        );
        return (convertedAmount.getScale() > 2)
                ? convertedAmount.rounded(2, RoundingMode.HALF_UP) : convertedAmount;
    }

    public BigDecimal convertFromUSD(String toCurrencyCode,
                                     BigDecimal exchangeRate,
                                     BigDecimal amount, int scale, RoundingMode roundingMode) {
        scale = Math.min(scale, 5);
        return doConvertFromUSD(toCurrencyCode, exchangeRate, amount, scale, roundingMode);
    }

    public Currency getCurrencyByCode(String currencyCode) {
        String msg = "Currency code '" + currencyCode + "' is currently not available or not supported";
        return currencyRepository.findByCodeIgnoreCase(currencyCode)
                .orElseThrow(() -> new Zaka500Exception(msg));
    }

    private BigDecimal doConvertFromUSD(String toCurrencyCode,
                                        BigDecimal exchangeRate,
                                        BigDecimal amount, int scale, RoundingMode roundingMode) {

        if (toCurrencyCode.equalsIgnoreCase("USD")) {
            return amount;
        }

        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return amount;
        }

        amount = amount.multiply(exchangeRate);
        return amount.setScale(scale, roundingMode);
    }
}
