package com.hostfully.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hostfully.controller.dtos.ErrorResponseDto;
import com.hostfully.controller.dtos.PersistBlockDto;
import com.hostfully.controller.dtos.PersistPropertyDto;
import com.hostfully.controller.dtos.PersistReservationDto;
import com.hostfully.controller.dtos.PropertyDto;
import java.time.LocalDate;
import java.util.List;
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
public class PropertyControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Test
  public void testSuccessfulPropertyCreation() throws Exception {
    // Given
    String request = objectMapper.writeValueAsString(new PersistPropertyDto("Property name"));

    // When
    MvcResult result =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").exists())
            .andReturn();
    PropertyDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), PropertyDto.class);

    // Then
    Assertions.assertThat(response.name()).isEqualTo("Property name");
    Assertions.assertThat(response.name()).isNotEmpty();
  }

  @Test
  public void testPropertyCreationWithNameMissing() throws Exception {
    // Given
    String request = objectMapper.writeValueAsString(new PersistPropertyDto(null));

    // When
    MvcResult result =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").doesNotExist())
            .andExpect(jsonPath("$.name").doesNotExist())
            .andExpect(jsonPath("$.errors").exists())
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message()).contains("Invalid object");
    Assertions.assertThat(response.errors()).contains("Name is mandatory");
  }

  @Test
  public void testSuccessfulFindAll() throws Exception {
    // Given
    for (int i = 0; i < 10; i++) {
      String request =
          objectMapper.writeValueAsString(new PersistPropertyDto("Property number " + i));
      MvcResult creationResult =
          mockMvc
              .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
              .andReturn();
      PropertyDto created =
          objectMapper.readValue(
              creationResult.getResponse().getContentAsString(), PropertyDto.class);

      mockMvc.perform(
          post("/reservation")
              .contentType(MediaType.APPLICATION_JSON)
              .content(
                  objectMapper.writeValueAsString(
                      new PersistReservationDto(
                          created.id(),
                          LocalDate.now(),
                          LocalDate.now().plusDays(1),
                          "guestName"))));

      mockMvc.perform(
          post("/block")
              .contentType(MediaType.APPLICATION_JSON)
              .content(
                  objectMapper.writeValueAsString(
                      new PersistBlockDto(
                          created.id(),
                          LocalDate.now().plusDays(2),
                          LocalDate.now().plusDays(3)))));
    }

    // When
    MvcResult result =
        mockMvc
            .perform(get("/property"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    String jsonResponse = result.getResponse().getContentAsString();
    List<PropertyDto> response = objectMapper.readValue(jsonResponse, new TypeReference<>() {});

    // Then
    Assertions.assertThat(response.size())
        .isGreaterThanOrEqualTo(10); // other tests might have created more properties
    for (int i = 0; i < 10; i++) {
      int index = i;
      PropertyDto propertyDto =
          response.stream()
              .filter(property -> property.name().equals("Property number " + index))
              .findFirst()
              .orElseThrow();
      Assertions.assertThat(propertyDto.id()).isNotNull();
      Assertions.assertThat(propertyDto.reservations()).hasSize(1);
      Assertions.assertThat(propertyDto.blocks()).hasSize(1);
    }
  }

  @Test
  public void testSuccessfulFindById() throws Exception {
    // Given
    String request = objectMapper.writeValueAsString(new PersistPropertyDto("Property name"));
    MvcResult creationResult =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andReturn();
    PropertyDto created =
        objectMapper.readValue(
            creationResult.getResponse().getContentAsString(), PropertyDto.class);

    // When
    MvcResult result =
        mockMvc
            .perform(get("/property/" + created.id()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    PropertyDto foundById =
        objectMapper.readValue(result.getResponse().getContentAsString(), PropertyDto.class);

    // Then
    Assertions.assertThat(foundById).isEqualTo(created);
  }

  @Test
  public void testFindByIdNotFound() throws Exception {
    // Given
    // Non-existing property

    // When
    MvcResult result =
        mockMvc
            .perform(get("/property/no-property-whatsoever"))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo("Could not find property for the id: no-property-whatsoever");
  }

  @Test
  public void testSuccessfulUpdate() throws Exception {
    // Given
    String request = objectMapper.writeValueAsString(new PersistPropertyDto("Property name"));
    MvcResult creationResult =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andReturn();
    PropertyDto created =
        objectMapper.readValue(
            creationResult.getResponse().getContentAsString(), PropertyDto.class);
    PersistPropertyDto toBeUpdate = new PersistPropertyDto("Updated property name");

    // When
    MvcResult result =
        mockMvc
            .perform(
                put("/property/" + created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(toBeUpdate)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    PropertyDto updated =
        objectMapper.readValue(result.getResponse().getContentAsString(), PropertyDto.class);

    MvcResult findResult =
        mockMvc
            .perform(get("/property/" + created.id()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    PropertyDto foundById =
        objectMapper.readValue(findResult.getResponse().getContentAsString(), PropertyDto.class);

    // Then
    Assertions.assertThat(updated.name()).isEqualTo("Updated property name");
    Assertions.assertThat(foundById.name()).isEqualTo("Updated property name");
  }

  @Test
  public void testInvalidUpdate() throws Exception {
    // Given
    String request =
        objectMapper.writeValueAsString(new PersistPropertyDto("Original property name"));
    MvcResult creationResult =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andReturn();
    PropertyDto created =
        objectMapper.readValue(
            creationResult.getResponse().getContentAsString(), PropertyDto.class);
    PersistPropertyDto toBeUpdate = new PersistPropertyDto("");

    // When
    MvcResult result =
        mockMvc
            .perform(
                put("/property/" + created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(toBeUpdate)))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    MvcResult findResult =
        mockMvc
            .perform(get("/property/" + created.id()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    PropertyDto foundById =
        objectMapper.readValue(findResult.getResponse().getContentAsString(), PropertyDto.class);

    // Then
    Assertions.assertThat(response.message()).contains("Invalid object");
    Assertions.assertThat(response.errors()).contains("Name is mandatory");
    Assertions.assertThat(foundById.name()).isEqualTo("Original property name");
  }

  @Test
  public void testUpdateNotFound() throws Exception {
    // Given
    // No entity created

    // When
    MvcResult result =
        mockMvc
            .perform(
                put("/property/non-existing-property")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(
                        objectMapper.writeValueAsString(
                            new PersistPropertyDto("Updated property name"))))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo("Could not find property for the id: non-existing-property");
  }

  @Test
  public void testSuccessfulDelete() throws Exception {
    // Given
    String request = objectMapper.writeValueAsString(new PersistPropertyDto("Property name"));
    MvcResult creationResult =
        mockMvc
            .perform(post("/property").contentType(MediaType.APPLICATION_JSON).content(request))
            .andReturn();
    PropertyDto created =
        objectMapper.readValue(
            creationResult.getResponse().getContentAsString(), PropertyDto.class);

    // When
    mockMvc
        .perform(delete("/property/" + created.id()))
        .andExpect(status().isNoContent())
        .andReturn();

    MvcResult result =
        mockMvc
            .perform(get("/property/" + created.id()))
            .andExpect(status().isNotFound())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn();
    ErrorResponseDto response =
        objectMapper.readValue(result.getResponse().getContentAsString(), ErrorResponseDto.class);

    // Then
    Assertions.assertThat(response.message())
        .isEqualTo("Could not find property for the id: " + created.id());
  }

  @Test
  public void testDeleteNotFound() throws Exception {
    // Given
    // No entity created previously

    // When
    MvcResult result =
        mockMvc
            .perform(delete("/property/it-does-not-exist"))
            .andExpect(status().isNoContent()) // Silently ignore not found when deleting
            .andReturn();

    // Then
    Assertions.assertThat(result.getResponse().getContentLength()).isZero();
  }
}
