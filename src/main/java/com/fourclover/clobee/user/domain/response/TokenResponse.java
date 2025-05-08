package com.fourclover.clobee.user.domain.response;

public record TokenResponse(
        String access,
        String refresh
) {
    public TokenResponse(String access, String refresh) {
        this.access = access;
        this.refresh = refresh;
    }
}
