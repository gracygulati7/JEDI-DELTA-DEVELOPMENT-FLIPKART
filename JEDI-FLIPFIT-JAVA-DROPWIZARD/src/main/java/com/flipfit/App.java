package com.flipfit;

import com.flipfit.rest.AuthController;
import com.flipfit.rest.GymOwnerController;
import com.flipfit.rest.CustomerController;
import com.flipfit.rest.AdminController;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import io.dropwizard.Configuration;

public class App extends Application<Configuration> {

    public static void main(String[] args) throws Exception {
        new App().run(args);
    }

    @Override
    public void run(Configuration configuration, Environment environment) {
        // Register all REST controllers
        environment.jersey().register(new AuthController());
        environment.jersey().register(new GymOwnerController());
        environment.jersey().register(new CustomerController());
        environment.jersey().register(new AdminController());
        
        System.out.println("FlipFit REST API started successfully!");
        System.out.println("Available endpoints:");
        System.out.println("  - /auth/* (Authentication & Registration)");
        System.out.println("  - /gym-owner/* (Gym Owner Operations)");
        System.out.println("  - /customer/* (Customer Operations)");
        System.out.println("  - /admin/* (Admin Operations)");
    }
}