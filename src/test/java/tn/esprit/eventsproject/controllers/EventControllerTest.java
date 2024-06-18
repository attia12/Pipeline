package tn.esprit.eventsproject.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit.eventsproject.entities.Event;
import tn.esprit.eventsproject.entities.Logistics;
import tn.esprit.eventsproject.entities.Participant;
import tn.esprit.eventsproject.entities.Tache;
import tn.esprit.eventsproject.services.IEventServices;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventRestController.class)
public class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEventServices eventService;

    @Autowired
    private ObjectMapper objectMapper;

    private Participant participant;
    private Event event;

    private List<Logistics> logisticsList;



    @BeforeEach
    void setUp() {
        participant = new Participant();
        participant.setIdPart(2);
        participant.setNom("John Doe");
        participant.setPrenom("John");
        participant.setTache(Tache.INVITE);

        event = new Event();
        event.setIdEvent(1);
        event.setDescription("Sample Event");
        event.setDateDebut(LocalDate.now());
        event.setDateFin(LocalDate.now().plusDays(1));
        event.setCout(100.0f);




        Logistics logistics = new Logistics();
        logistics.setIdLog(1);
        logistics.setDescription("Sample Logistics");
        logistics.setReserve(true);
        logistics.setPrixUnit(50.0f);
        logistics.setQuantite(10);
        logisticsList = Collections.singletonList(logistics);



    }

    @Test
    void addParticipantTest() throws Exception {
        given(eventService.addParticipant(any(Participant.class))).willReturn(participant);

        mockMvc.perform(post("/event/addPart")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(participant)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPart").value(2))
                .andExpect(jsonPath("$.nom").value("John Doe"))
                .andExpect(jsonPath("$.prenom").value("John"))
                .andExpect(jsonPath("$.tache").value("INVITE"));
    }
    @Test
    void addEventTest() throws Exception {
        given(eventService.addAffectEvenParticipant(any(Event.class))).willReturn(event);

        mockMvc.perform(post("/event/addEvent")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvent").exists())
                .andExpect(jsonPath("$.description").value("Sample Event"))
                .andExpect(jsonPath("$.dateDebut").exists())
                .andExpect(jsonPath("$.dateFin").exists())
                .andExpect(jsonPath("$.cout").value(100.0));
    }
    @Test
    void addEventPartTest() throws Exception {
        int idPart = 1;

        given(eventService.addAffectEvenParticipant(any(Event.class), any(Integer.class))).willReturn(event);

        mockMvc.perform(post("/event/addEvent/{id}", idPart)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEvent").exists())
                .andExpect(jsonPath("$.description").value("Sample Event"))
                .andExpect(jsonPath("$.dateDebut").exists())
                .andExpect(jsonPath("$.dateFin").exists())
                .andExpect(jsonPath("$.cout").value(100.0));
    }

    @Test
    void getLogistiquesDatesTest() throws Exception {
        LocalDate date1 = LocalDate.now();
        LocalDate date2 = LocalDate.now().plusDays(1);

        given(eventService.getLogisticsDates(date1, date2)).willReturn(logisticsList);

        mockMvc.perform(get("/event/getLogs/{d1}/{d2}", date1, date2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idLog").value(1))
                .andExpect(jsonPath("$[0].description").value("Sample Logistics"))
                .andExpect(jsonPath("$[0].reserve").value(true))
                .andExpect(jsonPath("$[0].prixUnit").value(50.0))
                .andExpect(jsonPath("$[0].quantite").value(10));
    }



}
