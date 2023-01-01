package cau.capstone.ottitor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_USER("ROLE_USER"), ROLE_ADMIN("ROLE_ADMIN"), ROLE_WITHDRAWAL("ROLE_WITHDRAWAL"), ROLE_BANNED("ROLE_BANNED");

    private final String key;



}
