package io.ricall.rocksdb.rocksdbdemo;

import io.ricall.rocksdb.rocksdbdemo.repository.KVRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.util.Assert;

import java.util.Optional;

@Slf4j
@SpringBootApplication
@ConfigurationPropertiesScan
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner applicationStartup(KVRepository<String, Object> repository) {
        return args -> {
            log.info("saving key/value");
            Assert.isTrue(repository.save("1234", "testing save"), "save key/value");

            Optional<Object> value = repository.find("1234");
            log.info("read value {} from key 1234", value);
            Assert.hasText((String) value.get(), "testing save");


            Optional<Object> missingValue = repository.find("5678");
            log.info("read missing value {} from key 5678", missingValue);
            Assert.isTrue(missingValue.isEmpty(), "cannot find missing value");

            log.info("deleting key");
            Assert.isTrue(repository.delete("1234"), "deleting key");
            Assert.isTrue(repository.delete("5678"), "deleting missing key");
        };
    }

}
