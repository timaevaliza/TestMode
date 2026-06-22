package ru.netology.test;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.data.RegistrationDto;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class RegistrationTest {
    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    @Test
    void shouldLoginSuccessfullyIfRegisteredActiveUser() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");

        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();


        $("h2").shouldHave(Condition.text("Личный кабинет"), Duration.ofSeconds(5));
    }

    @Test
    void shouldGetErrorIfRegisteredBlockedUser() {

        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("blocked");

        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();


        $("[data-test-id='error-notification']")
                .shouldHave(Condition.text("Пользователь заблокирован"), Duration.ofSeconds(5));
    }

    @Test
    void shouldGetErrorIfInvalidLogin() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String invalidLogin = DataGenerator.getRandomLogin(); // берем случайный другой логин

        $("[data-test-id='login'] input").setValue(invalidLogin); // подменяем логин
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();

        $("[data-test-id='error-notification']")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5));
    }

    @Test
    void shouldGetErrorIfInvalidPassword() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String invalidPassword = DataGenerator.getRandomPassword(); // берем случайный другой пароль

        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(invalidPassword);
        $("button[data-test-id='action-login']").click();

        $("[data-test-id='error-notification']")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5));
    }

    @Test
    void shouldGetErrorIfUserNotRegistered() {

        RegistrationDto notRegisteredUser = DataGenerator.Registration.getUser("active");

        $("[data-test-id='login'] input").setValue(notRegisteredUser.getLogin());
        $("[data-test-id='password'] input").setValue(notRegisteredUser.getPassword());
        $("button[data-test-id='action-login']").click();

        $("[data-test-id='error-notification']")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(5));
    }
}
