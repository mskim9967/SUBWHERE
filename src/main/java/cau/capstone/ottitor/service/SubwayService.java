package cau.capstone.ottitor.service;


import cau.capstone.ottitor.constant.Code;
import cau.capstone.ottitor.domain.User;
import cau.capstone.ottitor.dto.RealtimePositionDto;
import cau.capstone.ottitor.repository.JwtRepository;
import cau.capstone.ottitor.repository.UserRepository;
import cau.capstone.ottitor.util.GeneralException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SubwayService {

    @Value("${api.key}")
    private String apiKey;

    public Object test(String subwayNm) {

        return new RestTemplate().exchange(
            "http://swopenapi.seoul.go.kr/api/subway/%s/json/realtimePosition/0/5/" + subwayNm,
            HttpMethod.GET,
            null,
            RealtimePositionDto.class
        ).getBody();
    }

}
