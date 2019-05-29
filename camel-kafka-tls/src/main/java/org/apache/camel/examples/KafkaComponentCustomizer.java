/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package org.apache.camel.examples;

import com.heroku.sdk.BasicKeyStore;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.springboot.KafkaComponentConfiguration;
import org.apache.camel.spi.ComponentCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaComponentCustomizer implements ComponentCustomizer<KafkaComponent> {

  private static final Logger log = LoggerFactory.getLogger(KafkaComponentCustomizer.class);

  @Autowired
  private KafkaComponentConfiguration kafkaConfiguration;

  @Value("#{systemEnvironment['KAFKA_CLUSTER_CRT']}")
  private String kafkaClusterCrt;

  @Value("#{systemEnvironment['KAFKA_USER_KEY']}")
  private String kafkaUserKey;

  @Value("#{systemEnvironment['KAFKA_USER_CRT']}")
  private String kafkaUserCrt;

  @Override
  public void customize(KafkaComponent component) {
    log.debug("Customizing KafkaComponent...");
    try {
      if (kafkaClusterCrt != null && !kafkaClusterCrt.isEmpty()) {
        log.debug("Generating a truststore...");
        BasicKeyStore truststore = new BasicKeyStore(kafkaClusterCrt, kafkaConfiguration.getConfiguration().getSslTruststorePassword());
        Path truststoreFile = Paths.get(kafkaConfiguration.getConfiguration().getSslTruststoreLocation());
        Files.deleteIfExists(truststoreFile);
        truststore.store(truststoreFile);
      }
    } catch (IOException | GeneralSecurityException e) {
      log.debug(String.format("Unable to generate truststore. Exception: %s", e.getMessage()));
      throw new RuntimeException(e);
    }

    try {
      if (kafkaUserKey != null && !kafkaUserKey.isEmpty() && kafkaUserCrt != null && !kafkaUserCrt.isEmpty()) {
        log.debug("Generating a keystore...");
        BasicKeyStore keystore = new BasicKeyStore(kafkaUserKey, kafkaUserCrt, kafkaConfiguration.getConfiguration().getSslKeystorePassword());
        Path keystoreFile = Paths.get(kafkaConfiguration.getConfiguration().getSslKeystoreLocation());
        Files.deleteIfExists(keystoreFile);
        keystore.store(keystoreFile);
      }
    } catch (IOException | GeneralSecurityException e) {
      log.debug(String.format("Unable to generate keystore. Exception: %s", e.getMessage()));
      throw new RuntimeException(e);
    }
  }
}
