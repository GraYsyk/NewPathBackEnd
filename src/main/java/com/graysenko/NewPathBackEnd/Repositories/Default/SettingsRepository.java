package com.graysenko.NewPathBackEnd.Repositories.Default;

import com.graysenko.NewPathBackEnd.Entities.Default.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends JpaRepository<Settings, Long> {
}
