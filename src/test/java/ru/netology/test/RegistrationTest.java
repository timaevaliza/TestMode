package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;
import ru.netology.data.RegistrationDto;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class RegistrationTest {

    @BeforeAll
    static void setUpAll() {
        // СТРОГО ПО ДЗ: Включаем листенер Allure
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        // СТРОГО ПО ДЗ: Удаляем листенер после всех тестов
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        Allure.step("Открыть главную страницу приложения", () -> {
            open("http://localhost:9999");
            $("[data-test-id='login'] input").shouldBe(Condition.visible, Duration.ofSeconds(10));
        });
    }

    @Test
    void shouldLoginSuccessfullyIfRegisteredActiveUser() {
        RegistrationDto registeredUser = Allure.step("Зарегистрировать активного пользователя", () ->
                DataGenerator.Registration.getRegisteredUser("active")
        );

        Allure.step("Заполнить форму авторизации и войти", () -> {
            $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
            $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
            $("button[data-test-id='action-login']").click();
        });

        Allure.step("Проверить успешный переход в личный кабинет", () -> {
            $("h2").shouldHave(Condition.text("Личный кабинет"), Duration.ofSeconds(10));
        });
    }

    @Test
    void shouldGetErrorIfRegisteredBlockedUser() {
        RegistrationDto registeredUser = Allure.step("Зарегистрировать заблокированного пользователя", () ->
                DataGenerator.Registration.getRegisteredUser("blocked")
        );

        Allure.step("Заполнить форму авторизации и войти", () -> {
            $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
            $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
            $("button[data-test-id='action-login']").click();
        });

        Allure.step("Проверить ошибку заблокированного пользователя", () -> {
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldHave(Condition.exactText("Ошибка! Пользователь заблокирован"), Duration.ofSeconds(10));
        });
    }

    @Test
    void shouldGetErrorIfInvalidLogin() {
        RegistrationDto registeredUser = Allure.step("Зарегистрировать активного пользователя", () ->
                DataGenerator.Registration.getRegisteredUser("active")
        );
        String invalidLogin = registeredUser.getLogin() + "_invalid_user_999";

        Allure.step("Ввести неверный логин и войти", () -> {
            $("[data-test-id='login'] input").setValue(invalidLogin);
            $("[data-test-id='password'] input").setValue(registeredUser.getPassword());
            $("button[data-test-id='action-login']").click();
        });

        Allure.step("Проверить ошибку авторизации", () -> {
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10));
        });
    }

    @Test
    void shouldGetErrorIfInvalidPassword() {
        RegistrationDto registeredUser = Allure.step("Зарегистрировать активного пользователя", () ->
                DataGenerator.Registration.getRegisteredUser("active")
        );
        String invalidPassword = registeredUser.getPassword() + "_wrong_pass";

        Allure.step("Ввести неверный пароль и войти", () -> {
            $("[data-test-id='login'] input").setValue(registeredUser.getLogin());
            $("[data-test-id='password'] input").setValue(invalidPassword);
            $("button[data-test-id='action-login']").click();
        });

        Allure.step("Проверить ошибку авторизации", () -> {
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10));
        });
    }

    @Test
    void shouldGetErrorIfUserNotRegistered() {
        RegistrationDto notRegisteredUser = Allure.step("Сгенерировать случайного пользователя без регистрации", () ->
                DataGenerator.Registration.getUser("active")
        );

        Allure.step("Ввести данные незарегистрированного пользователя и войти", () -> {
            $("[data-test-id='login'] input").setValue(notRegisteredUser.getLogin());
            $("[data-test-id='password'] input").setValue(notRegisteredUser.getPassword());
            $("button[data-test-id='action-login']").click();
        });

        Allure.step("Проверить ошибку авторизации", () -> {
            $("[data-test-id='error-notification'] .notification__content")
                    .shouldHave(Condition.exactText("Ошибка! Неверно указан логин или пароль"), Duration.ofSeconds(10));
        });
    }
}