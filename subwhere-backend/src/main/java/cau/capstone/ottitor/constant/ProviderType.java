package cau.capstone.ottitor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType {
    KAKAO("kakao"), TEST("test");

    private final String key;
}