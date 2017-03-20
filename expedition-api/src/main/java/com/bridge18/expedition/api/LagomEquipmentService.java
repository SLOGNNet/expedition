package com.bridge18.expedition.api;


import akka.Done;
import akka.NotUsed;
import com.bridge18.expedition.dto.v1.EquipmentDTO;
import com.bridge18.expedition.dto.v1.EquipmentSummary;
import com.bridge18.expedition.dto.v1.PaginatedSequence;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceAcl;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface LagomEquipmentService extends Service {
    ServiceCall<EquipmentDTO, EquipmentDTO> createNewEquipment();
    ServiceCall<EquipmentDTO, EquipmentDTO> changeEquipmentInformation(String id);
    ServiceCall<NotUsed, Done> deleteEquipment(String id);
    ServiceCall<NotUsed, EquipmentDTO> getEquipment(String id);
    ServiceCall<NotUsed, PaginatedSequence<EquipmentSummary>> getEquipmentSummaries(Optional<Integer> pageNo, Optional<Integer> pageSize);

    @Override
    default Descriptor descriptor(){
        return named("expeditionEquipment").withCalls(
                restCall(Method.POST, "/v1/api/expedition/equipment", this::createNewEquipment),
                restCall(Method.PUT, "/v1/api/expedition/equipment/:id", this::changeEquipmentInformation),
                restCall(Method.DELETE, "/v1/api/expedition/equipment/:id", this::deleteEquipment),
                restCall(Method.GET, "/v1/api/expedition/equipment/:id", this::getEquipment),
                restCall(Method.GET, "/v1/api/expedition/equipment?pageNo&pageSize", this::getEquipmentSummaries)
        ).withAutoAcl(true)
                .withServiceAcls(
                        ServiceAcl.methodAndPath(Method.OPTIONS, "\\*")
                );

    }
}