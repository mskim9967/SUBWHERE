package cau.capstone.ottitor.repository;

import cau.capstone.ottitor.domain.Station;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, String> {
    List<Station> findByLineNumOrderByFrCodeAsc(String lineNum);
}

