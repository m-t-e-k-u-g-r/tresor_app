package ch.bbw.pr.tresorbackend;

import ch.bbw.pr.tresorbackend.config.EncryptionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(EncryptionProperties.class)
public class TresorbackendApplication {
   public static void main(String[] args) {
      SpringApplication.run(TresorbackendApplication.class, args);
   }
}
