package com.bridge18.expedition.services.objects.impl;

import akka.Done;
import com.bridge18.expedition.entities.driver.*;
import com.bridge18.expedition.services.objects.DriverService;
import com.google.inject.Singleton;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PVector;

import javax.inject.Inject;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Singleton
public class DriverServiceImpl implements DriverService {
    private final PersistentEntityRegistry persistentEntityRegistry;

    @Inject
    public DriverServiceImpl(PersistentEntityRegistry persistentEntityRegistry) {
        this.persistentEntityRegistry = persistentEntityRegistry;
        persistentEntityRegistry.register(DriverEntity.class);
    }

    @Override
    public CompletionStage<DriverState> createDriver(Optional<String> position, Optional<String> firstName,
                                                     Optional<String> middleName, Optional<String> lastName,
                                                     Optional<Date> birthDate, Optional<String> ssn,
                                                     Optional<PaymentOptions> paymentOption, Optional<Double> rate,
                                                     Optional<DriverTypes> driverType,
                                                     Optional<PVector<ContactInfo>> contactInfo,
                                                     Optional<Address> address, Optional<License> license) {
        CreateDriver cmd = CreateDriver.builder()
                .position(position)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .birthDate(birthDate)
                .ssn(ssn)
                .paymentOption(paymentOption)
                .rate(rate)
                .driverType(driverType)
                .contactInfo(contactInfo)
                .address(address)
                .license(license)
                .build();

        PersistentEntityRef<DriverCommand> ref = persistentEntityRegistry.refFor(DriverEntity.class, UUID.randomUUID().toString());

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<DriverState> updateDriver(String id, Optional<String> position, Optional<String>
            firstName, Optional<String> middleName, Optional<String> lastName, Optional<Date> birthDate,
                                                     Optional<String> ssn, Optional<PaymentOptions> paymentOption,
                                                     Optional<Double> rate, Optional<DriverTypes> driverType,
                                                     Optional<PVector<ContactInfo>> contactInfo,
                                                     Optional<Address> address, Optional<License> license) {
        UpdateDriver cmd = UpdateDriver.builder()
                .position(position)
                .firstName(firstName)
                .middleName(middleName)
                .lastName(lastName)
                .birthDate(birthDate)
                .ssn(ssn)
                .paymentOption(paymentOption)
                .rate(rate)
                .driverType(driverType)
                .contactInfo(contactInfo)
                .address(address)
                .license(license)
                .build();

        PersistentEntityRef<DriverCommand> ref = persistentEntityRegistry.refFor(DriverEntity.class, id);

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<Done> deleteDriver(String id) {
        DeleteDriver cmd = DeleteDriver.builder().build();

        PersistentEntityRef<DriverCommand> ref = persistentEntityRegistry.refFor(DriverEntity.class, id);

        return ref.ask(cmd);
    }

    @Override
    public CompletionStage<DriverState> getDriverInformation(String id) {
        GetDriverInformation cmd = GetDriverInformation.builder().build();

        PersistentEntityRef<DriverCommand> ref = persistentEntityRegistry.refFor(DriverEntity.class, id);

        return ref.ask(cmd);
    }
}
