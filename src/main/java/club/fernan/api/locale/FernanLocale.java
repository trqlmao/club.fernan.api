package club.fernan.api.locale;

/**
 * Supported response locales for endpoints that accept a {@code locale} query parameter
 * (currently {@code /store/stock} and {@code /store/cooldown}).
 *
 * @author trq
 * @since 0.1.0
 */
public enum FernanLocale {
    EN("en"),
    ES("es"),
    DE("de"),
    JA("ja"),
    ZH("zh"),
    TW("tw");

    private final String code;

    FernanLocale(String code) {
        this.code = code;
    }

    /**
     * The two-letter code accepted by the {@code locale} query parameter.
     *
     * @return The locale code.
     */
    public String code() {
        return code;
    }
}
