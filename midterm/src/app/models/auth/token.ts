export interface JwtResponse {
    accessToken: string;
    refreshToken: string;
    tokenType: string;
}

export interface RefreshTokenRequest {
    refreshToken: string;
}