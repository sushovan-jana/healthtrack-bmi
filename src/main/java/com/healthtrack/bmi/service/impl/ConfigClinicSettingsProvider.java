package com.healthtrack.bmi.service.impl;

import com.healthtrack.bmi.dto.ClinicSettings;
import com.healthtrack.bmi.service.ClinicSettingsProvider;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "healthtrack.clinic")
@Data
public class ConfigClinicSettingsProvider implements ClinicSettingsProvider {

    private String name;
    private String doctorName;
    private String address;
    private List<String> phones;
    private String email;

    @Override
    public ClinicSettings getSettings() {
        return ClinicSettings.builder()
                .name(name)
                .doctorName(doctorName)
                .address(address)
                .phones(phones)
                .email(email)
                .build();
    }
}
