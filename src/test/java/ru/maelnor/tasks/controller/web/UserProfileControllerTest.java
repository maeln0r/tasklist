package ru.maelnor.tasks.controller.web;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.maelnor.tasks.UserAbstractTest;

import java.text.MessageFormat;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
@ActiveProfiles("test")
class UserProfileControllerTest extends UserAbstractTest {
    @Test
    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldDisplayOwnProfile() throws Exception {
        mockMvc.perform(get("/user/profile/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("pageTitle", "Профиль пользователя " + user.getUsername()))
                .andExpect(view().name("user"));
    }

    @Test
    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldNotDisplayAdminProfileForUser() throws Exception {
        mockMvc.perform(get("/user/profile/" + admin.getId()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Access Denied")));
    }

    @Test
    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldThrowUserNotFoundException() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(get("/user/profile/" + uuid))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Access Denied")));
    }

    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldDisplayUserProfileForManager() throws Exception {
        mockMvc.perform(get("/user/profile/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("pageTitle", "Профиль пользователя " + user.getUsername()))
                .andExpect(view().name("user"));
    }

    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldDisplayUserProfileForAdmin() throws Exception {
        mockMvc.perform(get("/user/profile/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("request"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("pageTitle", "Профиль пользователя " + user.getUsername()))
                .andExpect(view().name("user"));
    }

    @Test
    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldAllowUserToChangeOwnPassword() throws Exception {
        mockMvc.perform(post("/user/change-password")
                        .param("user_id", user.getId().toString())
                        .param("password", "newpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(MessageFormat.format("/user/profile/{0}?passwordChanged=true", user.getId())));
    }

    @Test
    @WithUserDetails(value = "admin", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldAllowAdminToChangeUserPassword() throws Exception {
        mockMvc.perform(post("/user/change-password")
                        .param("user_id", user.getId().toString())
                        .param("password", "newpassword"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(MessageFormat.format("/user/profile/{0}?passwordChanged=true", user.getId())));
    }

    @Test
    @WithUserDetails(value = "user", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldDisallowUserToChangeManagerPassword() throws Exception {
        mockMvc.perform(post("/user/change-password")
                        .param("user_id", manager.getId().toString())
                        .param("password", "newpassword"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Access Denied")));
    }

    @Test
    @WithUserDetails(value = "manager", setupBefore = TestExecutionEvent.TEST_EXECUTION, userDetailsServiceBeanName = "userDetailsServiceImpl")
    void shouldDisallowManagerToChangeUserPassword() throws Exception {
        mockMvc.perform(post("/user/change-password")
                        .param("user_id", user.getId().toString())
                        .param("password", "newpassword"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Access Denied")));
    }


}