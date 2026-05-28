package club.fernan.api.locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class FernanLocaleTest {

    @Test
    void codes_match_api_contract() {
        assertEquals("en", FernanLocale.EN.code());
        assertEquals("es", FernanLocale.ES.code());
        assertEquals("de", FernanLocale.DE.code());
        assertEquals("ja", FernanLocale.JA.code());
        assertEquals("zh", FernanLocale.ZH.code());
        assertEquals("tw", FernanLocale.TW.code());
    }
}
