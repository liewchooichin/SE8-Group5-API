package sg.edu.ntu.bus_api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import sg.edu.ntu.bus_api.entity.UserAccess;
import sg.edu.ntu.bus_api.repository.UserAccessRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Rollback
public class UserAccessControllerTest {

    @Autowired
    private UserAccessRepository userAccessRepository;

    @BeforeEach
    void cleanUp() {
        // Cleanup the database before each test to ensure no conflicts
        userAccessRepository.deleteAll();
    }

    // Create random username and password
    String randomUsername = "TestUser" + UUID.randomUUID().toString();
    String randomPassword = "TestPassword" + UUID.randomUUID().toString();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateUserAccess() throws Exception {
        // Arrange
        UserAccess userAccess = new UserAccess();
        userAccess.setUsername(randomUsername);
        userAccess.setPassword("TestPassword123" + UUID.randomUUID().toString()); // Ensure unique password
        userAccess.setAccessLevel("Administrator");

        // Act
        ResultActions response = mockMvc.perform(post("/api/user-access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userAccess)));

        // Assert
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.username", is(randomUsername)))
                .andExpect(jsonPath("$.password", is(userAccess.getPassword()))) // Expect the generated password
                .andExpect(jsonPath("$.accessLevel", is("Administrator")))
                .andExpect(jsonPath("$.created_At", notNullValue()))
                .andExpect(jsonPath("$.updated_At", is((Object) null)));
    }

    @Test
    void testGetUserAccessById() throws Exception {
        UserAccess userAccess = new UserAccess();
        userAccess.setUsername(randomUsername);
        userAccess.setPassword("TestPassword123" + UUID.randomUUID().toString()); // Set a valid password
        userAccess.setAccessLevel("USER");
        UserAccess savedUserAccess = userAccessRepository.save(userAccess);

        ResultActions response = mockMvc.perform(get("/api/user-access/{id}", savedUserAccess.getId()));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUserAccess.getId().intValue())))
                .andExpect(jsonPath("$.username", is(randomUsername))) // Use random username
                .andExpect(jsonPath("$.accessLevel", is("USER")));
    }

    @Test
    void testGetAllUserAccess() throws Exception {
        UserAccess user1 = new UserAccess();
        user1.setUsername("user1");
        user1.setPassword("TestPassword123" + UUID.randomUUID().toString()); // Ensure password is set
        user1.setAccessLevel("ADMIN");

        UserAccess user2 = new UserAccess();
        user2.setUsername("user2");
        user2.setPassword("TestPassword123" + UUID.randomUUID().toString()); // Ensure password is set
        user2.setAccessLevel("USER");

        userAccessRepository.save(user1);
        userAccessRepository.save(user2);

        ResultActions response = mockMvc.perform(get("/api/user-access"));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].username", is("user1")))
                .andExpect(jsonPath("$[1].username", is("user2")));
    }

    @Test
    void testUpdateUserAccess() throws Exception {
        UserAccess userAccess = new UserAccess();
        userAccess.setUsername("testuser");
        userAccess.setPassword("TestPassword123" + UUID.randomUUID().toString()); // Ensure password is set
        userAccess.setAccessLevel("USER");
        UserAccess savedUserAccess = userAccessRepository.save(userAccess);

        UserAccess updatedUserAccess = new UserAccess();
        updatedUserAccess.setUsername("updateduser");
        updatedUserAccess.setPassword("UpdatedPassword123" + UUID.randomUUID().toString()); // Ensure password is set
        updatedUserAccess.setAccessLevel("ADMIN");

        ResultActions response = mockMvc.perform(put("/api/user-access/{id}", savedUserAccess.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUserAccess)));

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedUserAccess.getId().intValue())))
                .andExpect(jsonPath("$.username", is("updateduser")))
                .andExpect(jsonPath("$.accessLevel", is("ADMIN")));
    }

    @Test
    void testDeleteUserAccess() throws Exception {
        UserAccess userAccess = new UserAccess();
        userAccess.setUsername("testuser");
        userAccess.setPassword("TestPassword123" + UUID.randomUUID().toString()); // Ensure password is set
        userAccess.setAccessLevel("USER");
        UserAccess savedUserAccess = userAccessRepository.save(userAccess);

        ResultActions response = mockMvc.perform(delete("/api/user-access/{id}", savedUserAccess.getId()));

        response.andExpect(status().isNoContent());
    }
}
