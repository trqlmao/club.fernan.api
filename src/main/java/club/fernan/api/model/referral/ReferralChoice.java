package club.fernan.api.model.referral;

/**
 * Explicit caller choice for whether to apply a referral code to a purchase.
 *
 * <p>This type exists to make the call site unambiguous: rather than passing a
 * nullable {@code String} (where omission can look like a bug), callers pick
 * one of:
 *
 * <ul>
 *   <li>{@link #of(String) ReferralChoice.of("creator123")} — apply the given code</li>
 *   <li>{@link #none()} — deliberately apply no referral code</li>
 * </ul>
 *
 * <p>The library never silently chooses a referral on the caller's behalf.
 * Host applications are expected to prompt the end user (or use their stored
 * preference) and pass the resulting choice through.
 *
 * @author trq
 * @since 0.1.0
 */
public record ReferralChoice(String code) {

    private static final ReferralChoice NONE = new ReferralChoice(null);

    /** Apply the given referral code (must be non-blank). */
    public static ReferralChoice of(String code) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("referral code must be non-blank; use ReferralChoice.none()");
        }
        return new ReferralChoice(code);
    }

    /** Apply no referral code. */
    public static ReferralChoice none() {
        return NONE;
    }

    /** True if a code is present. */
    public boolean isPresent() {
        return code != null;
    }
}
