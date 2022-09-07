package cau.capstone.ottitor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserConstraint {
    NICKNAME_MIN_LENGTH(1),
    NICKNAME_MAX_LENGTH(14);

    private final Integer key;
}