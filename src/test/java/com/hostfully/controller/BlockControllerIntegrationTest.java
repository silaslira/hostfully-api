package com.hostfully.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import com.hostfully.controller.dtos.PropertyDto;
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
public class BlockControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testSuccessfulBlockCreation() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistBlockDto persistBlockDto =
        new PersistBlockDto(propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1));

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(persistBlockDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    BlockDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), BlockDto.class);

    // Then
    Assertions.assertThat(response.id()).isNotNull();
    Assertions.assertThat(response.start()).isEqualTo(LocalDate.now());
    Assertions.assertThat(response.finish()).isEqualTo(LocalDate.now().plusDays(1));
  }

  @Test
  public void testBlockInvalidDate() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistBlockDto persistBlockDto =
        new PersistBlockDto(propertyDto.id(), LocalDate.now(), LocalDate.now().minusDays(1));

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(persistBlockDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message()).isEqualTo("Start cannot be after the finish");
  }

  @Test
  public void testTwoBlockCreationWithSameDate() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();
    PersistBlockDto persistBlockDto =
        new PersistBlockDto(propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1));

    // When
    MvcResult firstResult =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(persistBlockDto)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    BlockDto firstResponse =
        objectMapper.readValue(firstResult.getResponse().getContentAsString(), BlockDto.class);

    MvcResult secondResult =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(persistBlockDto)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto secondResponse =
        objectMapper.readValue(
            secondResult.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(firstResponse.id()).isNotNull();
    Assertions.assertThat(firstResponse.start()).isEqualTo(LocalDate.now());
    Assertions.assertThat(firstResponse.finish()).isEqualTo(LocalDate.now().plusDays(1));
    Assertions.assertThat(secondResponse.message())
        .isEqualTo(
            String.format(
                "Selected range is overlapping with previously defined block(s): (%s: %s until %s)",
                firstResponse.id(), firstResponse.start(), firstResponse.finish()));
  }

  @Test
  public void testBlockCreationWithInvalidPropertyId() throws Exception {
    // Given
    PersistBlockDto persistBlockDto =
        new PersistBlockDto("it-does-not-exist", LocalDate.now(), LocalDate.now().plusDays(1));

    // When
    MvcResult result =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(persistBlockDto)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo("Could not find property for the id: it-does-not-exist");
  }

  @Test
  public void testSuccessfulUpdate() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();

    MvcResult blockResult =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistBlockDto(
                                propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1)))))
            .andReturn();
    BlockDto create =
        objectMapper.readValue(blockResult.getResponse().getContentAsString(), BlockDto.class);

    // When
    MvcResult result =
        mockMvc
            .perform(
                put("/block/" + create.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistBlockDto(
                                propertyDto.id(),
                                LocalDate.now().plusDays(2),
                                LocalDate.now().plusDays(3)))))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    BlockDto updated =
        objectMapper.readValue(result.getResponse().getContentAsString(), BlockDto.class);

    // Then
    Assertions.assertThat(updated.start()).isEqualTo(LocalDate.now().plusDays(2));
    Assertions.assertThat(updated.finish()).isEqualTo(LocalDate.now().plusDays(3));
  }

  @Test
  public void testSuccessfulDelete() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();

    MvcResult blockResult =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistBlockDto(
                                propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1)))))
            .andReturn();
    BlockDto toBeDeleted =
        objectMapper.readValue(blockResult.getResponse().getContentAsString(), BlockDto.class);

    // When
    mockMvc
        .perform(delete("/block/" + toBeDeleted.id()))
        .andExpect(status().isNoContent())
        .andReturn();

    MvcResult result =
        mockMvc
            .perform(get("/block/" + toBeDeleted.id()))
            .andExpect(status().isNotFound())
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo("Could not find block for the id: " + toBeDeleted.id());
    ;
  }

  @Test
  public void testSuccessfulFindById() throws Exception {
    // Given
    PropertyDto propertyDto = getPropertyDto();

    MvcResult blockResult =
        mockMvc
            .perform(
                post("/block")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistBlockDto(
                                propertyDto.id(), LocalDate.now(), LocalDate.now().plusDays(1)))))
            .andReturn();
    BlockDto toBeFound =
        objectMapper.readValue(blockResult.getResponse().getContentAsString(), BlockDto.class);

    // When

    MvcResult result =
        mockMvc.perform(get("/block/" + toBeFound.id())).andExpect(status().isOk()).andReturn();
    BlockDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), BlockDto.class);

    // Then
    Assertions.assertThat(response.id()).isNotNull();
    Assertions.assertThat(response.start()).isEqualTo(LocalDate.now());
    Assertions.assertThat(response.finish()).isEqualTo(LocalDate.now().plusDays(1));
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
}
