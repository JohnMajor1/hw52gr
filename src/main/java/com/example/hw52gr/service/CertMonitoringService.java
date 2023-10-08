package com.example.hw52gr.service;

import com.example.hw52gr.dto.CertDTO;
import com.example.hw52gr.service.CertificateService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@Slf4j
public class CertMonitoringService {

  private final MeterRegistry meterRegistry;
  private final CertificateService certificateService;

  public CertMonitoringService(MeterRegistry meterRegistry, CertificateService certificateService) {
    this.meterRegistry = meterRegistry;
    this.certificateService = certificateService;
  }

  @PostConstruct
  public void init() {
    log.debug("Gauge initialization");
    List<CertDTO> certs = certificateService.getCerts();
    for (CertDTO cert : certs) {
      Gauge.builder(cert.getName().substring(0, 15), cert, value -> cert.getExpirationDays())
        .description(cert.getName())
        .register(meterRegistry);
      log.debug("Gauge for cert {} is registered", cert);
    }
  }

}
