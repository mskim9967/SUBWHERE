package cau.capstone.ottitor.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SubwayInfo {

    CYCLE("CYCLE"), REVERSE("REVERSE");

    private final String key;
}
