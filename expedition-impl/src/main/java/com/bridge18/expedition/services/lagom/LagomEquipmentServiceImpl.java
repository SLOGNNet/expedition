package com.bridge18.expedition.services.lagom;

import akka.Done;
import akka.NotUsed;
import com.bridge18.exception.LagomException;
import com.bridge18.expedition.api.LagomEquipmentService;
import com.bridge18.expedition.dto.v1.EquipmentDTO;
import com.bridge18.expedition.dto.v1.MileageRecordDTO;
import com.bridge18.expedition.dto.v1.PaginatedSequence;
import com.bridge18.expedition.entities.equipment.EquipmentState;
import com.bridge18.expedition.entities.equipment.MileageRecord;
import com.bridge18.expedition.repository.EquipmentRepository;
import com.bridge18.expedition.services.objects.EquipmentService;
import com.google.common.collect.Lists;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.xml.ws.WebServiceException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.bridge18.expedition.services.lagom.LagomDriverServiceImpl.PAGE_SIZE;

public class LagomEquipmentServiceImpl implements LagomEquipmentService {

    private final Validator validator;
    private EquipmentService equipmentService;
    private EquipmentRepository equipmentRepository;

    @Inject
    public LagomEquipmentServiceImpl(EquipmentService equipmentService, EquipmentRepository equipmentRepository, Validator validator) {
        this.equipmentService = equipmentService;
        this.equipmentRepository = equipmentRepository;
        this.validator = validator;
    }

    @Override
    public ServiceCall<EquipmentDTO, EquipmentDTO> createNewEquipment() {
        return request -> {
            validate(request);
            PVector<MileageRecord> inMileageRecords = Optional.ofNullable(request.mileageRecords).isPresent() ?
                    TreePVector.from(
                            Lists.transform(request.mileageRecords, mileageRecordDTO -> MileageRecord.builder()
                                    .miles(Optional.ofNullable(mileageRecordDTO.miles))
                                    .takenAt(Optional.ofNullable(mileageRecordDTO.takenAt))
                                    .build()
                            )
                    ) : null;
            return equipmentService.createEquipment(Optional.ofNullable(request.vin),
                    Optional.ofNullable(request.ownership),
                    Optional.ofNullable(Optional.ofNullable(request.type).<WebServiceException>orElseThrow(
                            () -> new WebServiceException("Type field is mandatory"))),
                    Optional.ofNullable(Optional.ofNullable(request.subType).<WebServiceException>orElseThrow(
                            () -> new WebServiceException("Subtype field is mandatory"))),
                    Optional.ofNullable(request.operatingMode),
                    Optional.ofNullable(request.make),
                    Optional.ofNullable(request.model),
                    Optional.ofNullable(request.colour),
                    Optional.ofNullable(request.isSleeperBerthAvailable),
                    Optional.ofNullable(request.number),
                    Optional.ofNullable(request.licensePlateState),
                    Optional.ofNullable(request.licensePlateNumber),
                    Optional.ofNullable(request.licensePlateExpiration),
                    Optional.ofNullable(request.notes),
                    Optional.ofNullable(inMileageRecords))

                    .thenApply(this::convertEquipmentStateToEquipmentDTO);
        };

    }

    @Override
    public ServiceCall<EquipmentDTO, EquipmentDTO> updateEquipmentInformation(String id) {
        return request -> {
            PVector<MileageRecord> inMileageRecords = Optional.ofNullable(request.mileageRecords).isPresent() ?
                    TreePVector.from(
                            Lists.transform(request.mileageRecords, mileageRecordDTO -> MileageRecord.builder()
                                    .miles(Optional.ofNullable(mileageRecordDTO.miles))
                                    .takenAt(Optional.ofNullable(mileageRecordDTO.takenAt))
                                    .build()
                            )
                    ) : null;
            return equipmentService.updateEquipment(id,
                    Optional.ofNullable(request.vin),
                    Optional.ofNullable(request.ownership),
                    Optional.ofNullable(Optional.ofNullable(request.type).<WebServiceException>orElseThrow(
                            () -> new WebServiceException("Type field is mandatory"))),
                    Optional.ofNullable(Optional.ofNullable(request.subType).<WebServiceException>orElseThrow(
                            () -> new WebServiceException("Subtype field is mandatory"))),
                    Optional.ofNullable(request.operatingMode),
                    Optional.ofNullable(request.make),
                    Optional.ofNullable(request.model),
                    Optional.ofNullable(request.colour),
                    Optional.ofNullable(request.isSleeperBerthAvailable),
                    Optional.ofNullable(request.number),
                    Optional.ofNullable(request.licensePlateState),
                    Optional.ofNullable(request.licensePlateNumber),
                    Optional.ofNullable(request.licensePlateExpiration),
                    Optional.ofNullable(request.notes),
                    Optional.ofNullable(inMileageRecords))

                    .thenApply(this::convertEquipmentStateToEquipmentDTO);
        };
    }

    @Override
    public ServiceCall<NotUsed, Done> deleteEquipment(String id) {
        return request ->
                equipmentService.deleteEquipment(id);
    }

    @Override
    public ServiceCall<NotUsed, EquipmentDTO> getEquipment(String id) {
        return request ->
                equipmentService.getEquipment(id)

                        .thenApply(this::convertEquipmentStateToEquipmentDTO);
    }

    @Override
    public ServiceCall<NotUsed, PaginatedSequence<EquipmentDTO>> getEquipmentSummaries(Optional<Integer> pageNumber, Optional<Integer> pageSize) {
        return request -> {
            PaginatedSequence<EquipmentState> equipments = equipmentRepository.getEquipments(pageNumber.orElse(1), pageSize.orElse(PAGE_SIZE));
            return CompletableFuture.completedFuture(
                    new PaginatedSequence<>(
                            TreePVector.from(
                                    equipments.getValues().stream().map(this::convertEquipmentStateToEquipmentDTO).collect(Collectors.toList())
                            ),
                            equipments.getPageSize(),
                            equipments.getCount()
                    )
            );
        };
    }

    private EquipmentDTO convertEquipmentStateToEquipmentDTO(EquipmentState equipmentState) {
        List<MileageRecordDTO> mileageRecordDTOList = equipmentState.getMileageRecords().isPresent() ?
                Lists.transform(equipmentState.getMileageRecords().get(), mileageRecord ->
                        new MileageRecordDTO(mileageRecord.getMiles().orElse(null),
                                mileageRecord.getTakenAt().orElse(null))
                ) : null;
        return new EquipmentDTO(equipmentState.getId(),
                equipmentState.getVin().orElse(null),
                equipmentState.getOwnership().orElse(null),
                equipmentState.getType().orElseThrow(() -> new WebServiceException("Type field is mandatory")),
                equipmentState.getSubType().orElseThrow(() -> new WebServiceException("Subtype field is mandatory")),
                equipmentState.getOperatingMode().orElse(null),
                equipmentState.getMake().orElse(null),
                equipmentState.getModel().orElse(null),
                equipmentState.getColour().orElse(null),
                equipmentState.getIsSleeperBerthAvailable().orElse(null),
                equipmentState.getNumber().orElse(null),
                equipmentState.getLicensePlateState().orElse(null),
                equipmentState.getLicensePlateNumber().orElse(null),
                equipmentState.getLicensePlateExpiration().orElse(null),
                equipmentState.getNotes().orElse(null),
                mileageRecordDTOList);
    }

    private void validate(EquipmentDTO request) {
        Set<ConstraintViolation<EquipmentDTO>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if (equipmentRepository.findByNumber(request.number) != null) {
            throw new LagomException("NUMBER_IN_USE", 400, "Number in use. Please enter unique number", "number");
        }
    }
}
