package com.zara.Zara.repositories;


import com.zara.Zara.entities.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettingRepository extends JpaRepository<Setting, Long> {

    @Query(value = "select * from settings order by id", nativeQuery = true)
    Setting findSettings();
}
