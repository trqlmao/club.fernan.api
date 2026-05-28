/**
 * Models for referral codes (creating, validating, applying, stats).
 *
 * <p>{@link club.fernan.api.model.referral.ReferralChoice} is the explicit
 * caller decision passed to {@link club.fernan.api.service.StoreService#purchase}
 * &mdash; the library never silently auto-applies a referral code.
 *
 * @author trq
 * @since 0.1.0
 */
package club.fernan.api.model.referral;
