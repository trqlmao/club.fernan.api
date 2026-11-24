package club.fernan.api.locale;

/**
 * Locales for translated product names and descriptions.
 *
 * @author trq
 * @since 0.1.0
 */
public enum FernanLocale {
    EN("en"),
    ES("es"),
    DE("de"),
    FR("fr"),
    JA("ja"),
    PL("pl"),
    RU("ru"),
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
