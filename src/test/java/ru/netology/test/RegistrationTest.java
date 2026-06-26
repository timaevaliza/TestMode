package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.data.DataGenerator;
import ru.netology.data.RegistrationDto;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class RegistrationTest {

    @BeforeAll
    static void setUpAll() {

    }

    @BeforeEach
    void setup() {
        // ИСПРАВЛЕНО: Теперь Selenide открывает локально запущенное приложение
        open("http://localhost:9999");
        $("[data-test-id='login'] input").shouldBe(Condition.visible, Duration.ofSeconds(10));
    }

    @Test
    void shouldLoginSuccessfullyIfRegisteredActiveUser() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();
        $("h2").shouldHave(Condition.text("Личный кабинет"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfRegisteredBlockedUser() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("blocked");
        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.exactText("Ошибка! Пользователь заблокирован"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfInvalidLogin() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String invalidLogin = registeredUser.getLogin() + "_invalid_user_999";

        $("[data-test-id='login'] input").setValue(invalidLogin);
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfInvalidPassword() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String invalidPassword = registeredUser.getPassword() + "_wrong_pass";

        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(invalidPassword);
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfUserNotRegistered() {
        RegistrationDto notRegisteredUser = DataGenerator.Registration.getUser("active");

        $("[data-test-id='login'] input").setValue(notRegisteredUser.getLogin());
        $("[data-test-id='password'] input").setValue(notRegisteredUser.getPassword());
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10));
    }
}
