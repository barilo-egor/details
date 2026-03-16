package tgb.cryptoexchange.details.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import tgb.cryptoexchange.details.exception.EnumTypeNotFoundException;
import tgb.cryptoexchange.details.interfaces.ObjectNodeConvertable;
import tgb.cryptoexchange.details.util.JacksonUtil;

import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;

@AllArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum FiatCurrency implements ObjectNodeConvertable<FiatCurrency> {
    /**
     * Бел.рубль
     */
    BYN("byn", "Бел.рубли", "бел.рублей", "\uD83C\uDDE7\uD83C\uDDFE", 2000, 30),
    /**
     * Рос.рубль
     */
    RUB("rub", "Рос.рубли", "₽", "\uD83C\uDDF7\uD83C\uDDFA", 50000, 1000),
    UAH("uah", "Гривны", "гривен", "\uD83C\uDDFA\uD83C\uDDE6", 10000, 10000);

    final String code;

    final String displayName;

    final String genitive;

    final String flag;

    final Integer defaultMaxSum;

    final Integer defaultMinSumForReferralDiscount;

    public String getName() {
        return this.name();
    }

    public static FiatCurrency getByCode(String code) {
        for (FiatCurrency fiatCurrency : FiatCurrency.values()) {
            if (fiatCurrency.getCode().equals(code)) return fiatCurrency;
        }
        throw new EnumTypeNotFoundException("Фиатная валюта не найдена.");
    }

    @Override
    public Function<FiatCurrency, ObjectNode> mapFunction() {
        return fiatCurrency -> JacksonUtil.getEmpty()
                .put("name", fiatCurrency.name())
                .put("code", fiatCurrency.getCode())
                .put("displayName", fiatCurrency.getDisplayName())
                .put("genitive", fiatCurrency.getGenitive())
                .put("flag", fiatCurrency.getFlag());
    }

    public static FiatCurrency valueOfNullable(String name) {
        if (Objects.isNull(name)) return null;
        FiatCurrency fiatCurrency;
        try {
            fiatCurrency = FiatCurrency.valueOf(name);
        } catch (IllegalArgumentException e) {
            fiatCurrency = null;
        }
        return fiatCurrency;
    }

    public static class NameSerializer extends JsonSerializer<FiatCurrency> {

        @Override
        public void serialize(FiatCurrency value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.getName());
        }
    }
}
