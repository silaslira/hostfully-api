package com.hostfully.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.controller.dtos.BlockDto;
import com.hostfully.controller.dtos.ErrorResponseDto;
import com.hostfully.controller.dtos.PersistBlockDto;
import com.hostfully.controller.dtos.PersistPropertyDto;
import com.hostfully.controller.dtos.PersistReservationDto;
import com.hostfully.controller.dtos.PropertyDto;
import com.hostfully.controller.dtos.ReservationDto;
import com.hostfully.model.Reservation;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservationControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testSuccessfulReservationCreation() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ReservationDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ReservationDto.class);

    // Then
    Assertions.assertThat(response.id()).isNotNull();
    Assertions.assertThat(response.start()).isEqualTo(LocalDate.now());
    Assertions.assertThat(response.finish()).isEqualTo(LocalDate.now().plusDays(1));
    Assertions.assertThat(response.guestName()).isEqualTo("Guest name");
  }

  @Test
  public void testReservationCreationWithInvalidDate() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().minusDays(1), "Guest name");

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message()).isEqualTo("Start cannot be after the finish");
  }

  @Test
  public void testReservationCreationWithoutMandatoryField() throws Exception {
    // Given
    PersistReservationDto createReservationDto = new PersistReservationDto(null, null, null, null);

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message()).isEqualTo("Invalid object");
    Assertions.assertThat(response.errors())
        .contains(
            "guestName is mandatory",
            "propertyId is mandatory",
            "start is mandatory",
            "finish is mandatory");
  }

  @Test
  public void testReservationCreationDuringBlockPeriod() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    BlockDto blockDto = getBlockDto(propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(5));
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo(
            String.format(
                "Selected range is overlapping with previously defined block(s): (%s: %s until %s)",
                blockDto.id(), blockDto.start(), blockDto.finish()));
  }

  @Test
  public void testReservationCreationDuringAnotherReservationPeriod() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");
    MvcResult firstResult =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andReturn();
    ReservationDto previousReservation =
        objectMapper.readValue(
            firstResult.getResponse().getContentAsString(), ReservationDto.class);

    // When
    MvcResult secondResult =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(
            secondResult.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo(
            String.format(
                "Selected range is overlapping with previously defined reservation(s): (%s: %s until %s)",
                previousReservation.id(),
                previousReservation.start(),
                previousReservation.finish()));
  }

  @Test
  public void testSuccessfulUpdate() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");
    MvcResult createResult =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andReturn();
    ReservationDto reservationDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(), ReservationDto.class);

    // When
    MvcResult result =
        mockMvc
            .perform(
                put("/reservation/" + reservationDto.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistReservationDto(
                                propertyDto.id(),
                                LocalDate.now().plusDays(2),
                                LocalDate.now().plusDays(3),
                                "New guest name"))))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ReservationDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ReservationDto.class);

    // Then
    Assertions.assertThat(response.start()).isEqualTo(LocalDate.now().plusDays(2));
    Assertions.assertThat(response.finish()).isEqualTo(LocalDate.now().plusDays(3));
    Assertions.assertThat(response.guestName()).isEqualTo("New guest name");
  }

  @Test
  public void testUpdateWithoutMandatoryFields() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");
    MvcResult createResult =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andReturn();
    ReservationDto reservationDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(), ReservationDto.class);

    // When
    MvcResult result =
        mockMvc
            .perform(
                put("/reservation/" + reservationDto.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistReservationDto(null, null, null, null))))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message()).isEqualTo("Invalid object");
    Assertions.assertThat(response.errors())
        .contains(
            "guestName is mandatory",
            "propertyId is mandatory",
            "start is mandatory",
            "finish is mandatory");
  }

  @Test
  public void testSuccessfulCancellation() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");
    MvcResult createResult =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andReturn();
    ReservationDto reservationDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(), ReservationDto.class);

    // When
    mockMvc
        .perform(put("/reservation/" + reservationDto.id() + "/cancel"))
        .andExpect(status().isNoContent());

    MvcResult result =
        mockMvc
            .perform(
                get("/reservation/" + reservationDto.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ReservationDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ReservationDto.class);

    // Then
    Assertions.assertThat(response.status())
        .isEqualTo(Reservation.ReservationStatus.CANCELLED.toString());
  }

  @Test
  public void testSuccessfulRebook() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistReservationDto createReservationDto =
        new PersistReservationDto(
            propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1), "Guest name");
    MvcResult createResult =
        mockMvc
            .perform(
                post("/reservation")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andReturn();
    ReservationDto reservationDto =
        objectMapper.readValue(
            createResult.getResponse().getContentAsString(), ReservationDto.class);
    mockMvc
        .perform(put("/reservation/" + reservationDto.id() + "/cancel"))
        .andExpect(status().isNoContent());

    // When
    mockMvc
        .perform(put("/reservation/" + reservationDto.id() + "/rebook"))
        .andExpect(status().isNoContent());

    MvcResult result =
        mockMvc
            .perform(
                get("/reservation/" + reservationDto.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createReservationDto)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ReservationDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ReservationDto.class);

    // Then
    Assertions.assertThat(response.status())
        .isEqualTo(Reservation.ReservationStatus.ACTIVE.toString());
  }

  @Test
  public void testFailedFindById() throws Exception {
    // Given
    // No reservation crated

    // When
    MvcResult result =
        mockMvc
            .perform(get("/reservation/it-does-not-exist"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo("Could not find reservation for the id: it-does-not-exist");
  }

  private PropertyDto getPropertyDto() throws Exception {
    String request = objectMapper.writeValueAsString(new PersistPropertyDto("Property name"));
    MvcResult creationResult =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andReturn();
    return objectMapper.readValue(
        creationResult.getResponse().getContentAsString(), PropertyDto.class);
  }

  private BlockDto getBlockDto(String propertyId, LocalDate start, LocalDate finish)
      throws Exception {
    String request =
        objectMapper.writeValueAsString(new PersistBlockDto(propertyId, start, finish));
    MvcResult creationResult =
        mockMvc
            .perform(post("/block").contentType(MediaType.APPLICATION_JSON).content(request))
            .andReturn();
    return objectMapper.readValue(
        creationResult.getResponse().getContentAsString(), BlockDto.class);
  }
}
