import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TestPet {
    private static final String BASE_URL = "http://5.181.109.28:9090/api/v3";

    @Test
    @Feature("Pet")
    @Severity(SeverityLevel.CRITICAL)
    @Owner("Nadejda Smirnova")
    public void testDeletedNonexistentPet() {
        Response response = step("Проверка что текст ответа 'Pet deleted'", () ->
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
}