package club.fernan.api.model.user;

import com.google.gson.annotations.SerializedName;

/**
 * Authenticated fernan.club user account.
 *
 * @author trq
 * @since 0.1.0
 */
public record User(
        @SerializedName("discord_id") String discordId,
        String username,
        @SerializedName("avatar_url") String avatarUrl,
        long balance,
        @SerializedName("role_id") int roleId) {

    /** True if {@code role_id >= 3}. */
    public boolean hasMediaPlus() {
        return roleId >= 3;
    }

    /** True if {@code role_id >= 5}. */
    public boolean hasPartner() {
        return roleId >= 5;
    }

    /** True if {@code role_id == 7}. */
    public boolean isAdmin() {
        return roleId == 7;
    }
}
