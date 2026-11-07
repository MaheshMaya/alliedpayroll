package com.alliedpayroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.modulith.Modulithic;

/** The one backend. Deployed twice from the same image: {@code api} and {@code batch}. */
@SpringBootApplication
@Modulithic(systemName = "Allied Payroll", sharedModules = "platform")
public class BackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(BackendApplication.class, args);
  }
}
