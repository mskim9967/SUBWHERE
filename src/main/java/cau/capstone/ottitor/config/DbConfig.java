package cau.capstone.ottitor.config;

import cau.capstone.ottitor.service.StationService;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DbConfig {

    private final StationService stationService;

    @PostConstruct
    public void initialize() {
        stationService.initialize();
    }

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
    }
}