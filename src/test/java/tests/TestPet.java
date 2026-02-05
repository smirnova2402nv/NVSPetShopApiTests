package tests;

import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.Pet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestPet {
    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Удаление несуществующего питомца")
    public void testDeletedNonexistentPet() {
        Response response = step("Отправка DELETE запроса на удаление несуществующего питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .when()
                        .delete(BASE_URL + "/pet/9999"));

        String responseBody = response.getBody().asString();
        step("Проверка что статус-код ответа = 200", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверка что текст ответа 'Pet deleted'", () ->
                assertEquals("Pet deleted", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody)
        );
    }

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Обновление несуществующего питомца")
    public void testUpdateNonexistentPet() {
        Pet pet = new Pet();
        pet.setId(9999);
        pet.setName("Non-existent Pet");
        pet.setStatus("available");
        Response response = step("Отправка PUT запроса на обновление несуществующего питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .put(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверка что статус-код ответа = 404", () ->
                assertEquals(404, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверка что текст ответа 'Pet not found'", () ->
                assertEquals("Pet not found", responseBody,
                        "Текст ошибки не совпал с ожидаемым. Получен: " + responseBody)
        );
    }

    @ParameterizedTest(name = "Добавление питомца со статусом: {2}")
    @CsvSource({
            "200, Li-li, available",
            "201, Buddy, pending",
            "202, Molly, sold"
    })
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    @DisplayName("Добавление питомца")
    public void testAddNewPet(int id, String name, String status) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setStatus(status);
        Response response = step("Отправка PUT запроса на добавление питомца", () ->
                given()
                        .contentType(ContentType.JSON)
                        .header("Accept", "application/json")
                        .body(pet)
                        .when()
                        .post(BASE_URL + "/pet"));

        String responseBody = response.getBody().asString();

        step("Проверка что статус-код ответа = 404", () ->
                assertEquals(200, response.getStatusCode(),
                        "Код ответа не совпал с ожидаемым. Ответ: " + responseBody)
        );

        step("Проверка параметров созданного питомца", () -> {
                    Pet createdPet = response.as(Pet.class);
                    assertEquals(pet.getId(), createdPet.getId(), "id питомца не совпадает с ожидаемым");
                    assertEquals(pet.getName(), createdPet.getName(), "name питомца не совпадает с ожидаемым");
                    assertEquals(pet.getStatus(), createdPet.getStatus(), "status питомца не совпадает с ожидаемым");
                }
        );
    }
}