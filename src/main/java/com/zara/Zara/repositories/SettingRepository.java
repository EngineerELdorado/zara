package com.zara.Zara.repositories;


import com.zara.Zara.entities.PesaPay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingRepository extends JpaRepository<PesaPay, Long> {

    @Query(value = "select * from pesapay order by id limit 1", nativeQuery = true)
    PesaPay findSettings();
}
