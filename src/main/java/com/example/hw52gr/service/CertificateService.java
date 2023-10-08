package com.example.hw52gr.service;

import com.example.hw52gr.dto.CertDTO;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@Data
@NoArgsConstructor
public class CertificateService {
  private List<CertDTO> certs;

  @PostConstruct
  public void init() {

    try {

      TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      trustManagerFactory.init((KeyStore) null);

      List<TrustManager> trustManagers = Arrays.asList(trustManagerFactory.getTrustManagers());
      certs = trustManagers.stream()
        .filter(X509TrustManager.class::isInstance)
        .map(X509TrustManager.class::cast)
        .map(trustManager -> Arrays.asList(trustManager.getAcceptedIssuers()))
        .flatMap(Collection::stream)
        .map(cert -> new CertDTO(cert.getSubjectDN().getName(),
          TimeUnit.DAYS.convert(cert.getNotAfter().getTime() - cert.getNotBefore().getTime(), TimeUnit.MILLISECONDS)))
        .sorted(Comparator.comparing(CertDTO::getName))
        .collect(Collectors.toList());
      log.debug("initialized certificates: {}", certs);
    } catch (Exception e) {
      log.warn("Cannot load certificates by cause: ", e);
    }
  }

}
