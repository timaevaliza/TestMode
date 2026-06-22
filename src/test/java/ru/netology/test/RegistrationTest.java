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
        Configuration.headless = true;
        // Принудительно выставляем глобальный таймаут 10 секунд
        Configuration.timeout = 10000;

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        Configuration.browserCapabilities = options;
    }

    @BeforeEach
    void setup() {
        open("https://netology.ru");
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
                .shouldHave(Condition.text("Пользователь заблокирован"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfInvalidLogin() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String invalidLogin = registeredUser.getLogin() + "_invalid_user_999";

        $("[data-test-id='login'] input").setValue(invalidLogin);
        $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfInvalidPassword() {
        RegistrationDto registeredUser = DataGenerator.Registration.getRegisteredUser("active");
        String invalidPassword = registeredUser.getPassword() + "_wrong_pass";

        $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
        $("[data-test-id='password'] input").setValue(invalidPassword);
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(10));
    }

    @Test
    void shouldGetErrorIfUserNotRegistered() {
        RegistrationDto notRegisteredUser = DataGenerator.Registration.getUser("active");

        $("[data-test-id='login'] input").setValue(notRegisteredUser.getLogin());
        $("[data-test-id='password'] input").setValue(notRegisteredUser.getPassword());
        $("button[data-test-id='action-login']").click();
        $("[data-test-id='error-notification'] .notification__content")
                .shouldHave(Condition.text("Неверно указан логин или пароль"), Duration.ofSeconds(10));
    }
}

