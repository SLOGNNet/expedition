package com.bridge18.expedition.api;

import akka.Done;
import akka.NotUsed;
import com.bridge18.expedition.dto.v1.DriverDTO;
import com.bridge18.expedition.dto.v1.DriverSummaryDTO;
import com.bridge18.expedition.dto.v1.PaginatedSequence;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceAcl;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.*;
public interface LagomDriverService extends Service {
    ServiceCall<DriverDTO, DriverDTO> createNewDriver();
    ServiceCall<NotUsed, PaginatedSequence<DriverSummaryDTO>> getDriverSummaries(Optional<String> pagingState, Optional<Integer> pageSize);
    ServiceCall<DriverDTO, DriverDTO> updateDriverInformation(String id);
    ServiceCall<NotUsed, Done> deleteDriver(String id);
    ServiceCall<NotUsed, DriverDTO> getDriverInformation(String id);

    @Override
    default Descriptor descriptor(){
        return named("expeditionDriver").withCalls(
                restCall(Method.POST, "/v1/api/expedition/drivers", this::createNewDriver),
                restCall(Method.GET, "/v1/api/expedition/drivers?pagingState&pageSize", this::getDriverSummaries),
                restCall(Method.PUT, "/v1/api/expedition/drivers/:id", this::updateDriverInformation),
                restCall(Method.DELETE, "/v1/api/expedition/drivers/:id", this::deleteDriver),
                restCall(Method.GET, "/v1/api/expedition/drivers/:id", this::getDriverInformation)
        ).withAutoAcl(true)
                .withServiceAcls(
                        ServiceAcl.methodAndPath(Method.OPTIONS, "\\*")
                );
    }
}
