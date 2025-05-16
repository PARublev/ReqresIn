package testApiWithReqresIn;

import org.junit.Test;
import testApiWithReqresIn.lombok.CreateUserRequest;
import testApiWithReqresIn.lombok.CreateUserResponse;
import testApiWithReqresIn.lombok.Response;
import testApiWithReqresIn.lombok.UserData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.*;

public class ReqresTestWithLombok extends Specification {

   public static final String EXP_MESSAGE_ERROR = "Missing password";

    @Test
    public void checkAvatarAndIdTest() {
        List<UserData> user = given()
                .spec(requestSpecification)
                .when()
                .queryParam("page", "2")
                .get("/users")
                .then().log().all()
                .spec(responseSpecUnique(200))
                .extract().jsonPath().getList("data", UserData.class);
        user.forEach(x -> assertTrue(x.getAvatar().contains(x.getId().toString())));

//        assertTrue(user.stream().allMatch(x->x.getAvatar().contains(x.getId().toString())));

        assertTrue(user.stream().allMatch(X -> X.getEmail().endsWith("@reqres.in")));
        List<String> avatars = user.stream().map(UserData::getAvatar).collect(Collectors.toList());
        List<String> id = user.stream().map(x -> x.getId().toString()).collect(Collectors.toList());
        for (int i = 0; i < avatars.size(); i++) {
            assertTrue(avatars.get(i).contains(id.get(i)));
        }
    }

    @Test
    public void userRegistrSuccess() {
        String expId = "4";
        String expToken = "QpwL5tke4Pnpja7X4";

        Register userRegistr = new Register("eve.holt@reqres.in", "pistol");
        SuccessReg successReg = given()
                .spec(requestSpecification)
                .body(userRegistr)
                .when()
                .post("/register")
                .then().log().all()
                .spec(responseSpec)
                .extract().as(SuccessReg.class);

        assertEquals(expId, successReg.getId());
        assertEquals(expToken, successReg.getToken());

    }

    @Test
    public void checkEmailFieldInResponse() {
        Response response = given()
                .spec(requestSpecification)
                .queryParam("page", "2")
                .when()
                .get("/users")
                .then().log().all()
                .spec(responseSpec)
                .extract().as(Response.class);

        assertThat("Список пользователей не пустой", response.getUserData(), is(not(empty())));

        for (String property : new String[]{"id", "email", "first_name", "last_name", "avatar"}) {
            if (property.equals("id")) {
                assertThat(
                        "Наличие числового поля \"id\"" + property,
                        response.getUserData(),
                        everyItem(hasProperty(property, allOf(notNullValue(), greaterThan(0))))
                );
            } else {
                assertThat(
                        "Наличие строковых полей \"email\",\"first_name\",\"last_name\",\"avatar\" " + property,
                        response.getUserData(),
                        everyItem(hasProperty(property, allOf(notNullValue(), not(emptyString()))))
                );
            }
        }
    }

    @Test
    public void checkEmailFormat() {
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        List<UserData> users = given()
                .spec(requestSpecification)
                .queryParam("page", "2")
                .when()
                .get("users/")
                .then().log().all()
                .spec(responseSpec)
                .extract().jsonPath()
                .getList("data", UserData.class);

        for (UserData user : users) {
            assertThat("Проверка формата Email ", user.getEmail(), matchesPattern(emailRegex));
        }
    }

    @Test
    public void verifyUserInResponse() {
        String expFirst_Name = "Lindsay";
        String expLast_Name = "Ferguson";

        List<UserData> users = given()
                .spec(requestSpecification)
                .queryParam("page", "2")
                .when()
                .get("users")
                .then().log().all()
                .extract()
                .jsonPath().getList("data", UserData.class);

        UserData user = users.stream()
                .filter(u -> u.getId() == 8)
                .findFirst()
                .orElseThrow(() -> new AssertionError("Не найден пользователь с id = " + 8));

        assertEquals("Имя не совпадает:", expFirst_Name, user.getFirst_name());
        assertEquals("Фамилия не совпадает:", expLast_Name, user.getLast_name());
        System.out.println("Проверяем пользователя: " + user);
    }

    @Test
    public void verifyResponseStructure() {
        given()
                .spec(requestSpecification)
                .queryParam("page", "2")
                .when()
                .get("users/")
                .then().log().all()
                .statusCode(200)
                .body("data[0]", hasKey("id"))
                .body("data[0]", hasKey("email"))
                .body("data[0]", hasKey("first_name"))
                .body("data[0]", hasKey("last_name"))
                .body("data[0]", hasKey("avatar"))

                .body("support", allOf(
                        hasKey("url"),
                        hasKey("text")
                ));

    }

    @Test
    public void checkTextInSupport() {
        given()
                .spec(requestSpecification)
                .queryParam("page", "2")
                .when()
                .get("users/")
                .then().log().all()
                .statusCode(200)
                .body("support.text", equalTo("Tired of writing endless social media content? Let Content Caddy generate it for you."));
    }

    @Test
    public void findIdByNameAndLastName() {
        given()
                .spec(requestSpecification)
                .queryParam("page", "2")
                .when()
                .get("users/")
                .then().log().all()
                .statusCode(200)
                .body("data.find {it.first_name == 'Byron' && it.last_name == 'Fields'}.id", equalTo(10));
    }

    @Test
    public void checkSortYear() {
        List<Integer> years = given()
                .spec(requestSpecification)
                .when()
                .get("/unknown")
                .then().log().all()
                .extract().jsonPath().getList("data.year", Integer.class);
        System.out.println(years + " года");

        List<Integer> sortedYears = new ArrayList<>(years);
        sortedYears.sort(Integer::compareTo);

        assertThat("Годы не отсортированы по возрастанию", sortedYears, equalTo(years));
    }

    @Test
    public void singleUserNotFound() {
        given()
                .spec(requestSpecification)
                .when()
                .get("users/23")
                .then().log().all()
                .spec(responseSpecUnique(404))
                .body(equalTo("{}"));
    }

    @Test
    public void createUserSuccess() {
        String expName = "morpheus";
        String expJob = "leader";

        CreateUserRequest userRequest = new CreateUserRequest("morpheus", "leader");
        CreateUserResponse userResponse = given()
                .spec(requestSpecification)
                .body(userRequest)
                .when()
                .post("/users")
                .then().log().all()
                .spec(responseSpecUnique(201))
                .extract().as(CreateUserResponse.class);

        assertEquals(expName, userResponse.getName());
        assertEquals(expJob, userResponse.getJob());
        assertNotNull(userResponse.getCreatedAt());
        assertTrue(!userResponse.getCreatedAt().isEmpty());
    }

    @Test
    public void updateUser() {
        String expName = "morpheus";
        String expJob = "zion resident";

        CreateUserRequest updateUserRequest = new CreateUserRequest("morpheus", "zion resident");
        CreateUserResponse updateUserResponse = given()
                .spec(requestSpecification)
                .body(updateUserRequest)
                .when()
                .put("users/2")
                .then().log().all()
                .spec(responseSpecОК200())
                .extract().as(CreateUserResponse.class);
        System.out.println(updateUserResponse);

        assertEquals(expName, updateUserResponse.getName());
        assertEquals(expJob, updateUserResponse.getJob());
        assertThat(updateUserResponse.getUpdatedAt(), not(emptyOrNullString()));

    }

    @Test
    public void deleteUser() {
        given()
                .spec(requestSpecification)
                .when()
                .delete("users/2")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    public void registerSuccess() {
        String expId = "4";
        String expToken = "QpwL5tke4Pnpja7X4";

        UserData userRegister = UserData.builder()
                .email("eve.holt@reqres.in")
                .password("pistol").build();

        SuccessReg userSuccessRegister = given()
                .spec(requestSpecification)
                .body(userRegister)
                .when()
                .post("register")
                .then().log().all()
                .spec(responseSpecОК200())
                .extract().as(SuccessReg.class);
        if (userSuccessRegister.getError() != null) {
            fail("Регистрация не выполнена" + userSuccessRegister.getError());
        } else {
            assertEquals(expId, userSuccessRegister.getId());
            assertEquals(expToken, userSuccessRegister.getToken());
        }
    }

    @Test
    public void registerUnSuccess() {


        UserData userRegister = UserData.builder()
                .email("sydney@fife")
                .build();

        UnSuccessReg userUnSuccessReg = given()
                .spec(requestSpecification)
                .body(userRegister)
                .when()
                .post("register")
                .then().log().all()
                .spec(responseSpecErr400())
                .extract().as(UnSuccessReg.class);

        assertEquals(EXP_MESSAGE_ERROR, userUnSuccessReg.getError());
    }

    @Test
    public void successLogin() {
        String expToken = "QpwL5tke4Pnpja7X4";

        LoginSuccessful successLogin = LoginSuccessful.builder()
                .email("eve.holt@reqres.in")
                .password("cityslicka")
                .build();

        SuccessReg successLoginResponse = given()
                .spec(requestSpecification)
                .body(successLogin)
                .when()
                .post("login")
                .then().log().all()
                .extract()
                .as(SuccessReg.class);

        assertEquals(expToken, successLoginResponse.getToken());

    }

    @Test
    public void unSuccessLogin() {

        LoginUnSuccessful unSuccessLogin = LoginUnSuccessful.builder()
                .email("peter@klaven")
                .build();

        UnSuccessReg unSuccessLoginResponse = given()
                .spec(requestSpecification)
                .body(unSuccessLogin)
                .when()
                .post("login")
                .then().log().all()
                .spec(responseSpecErr400())
                .extract().as(UnSuccessReg.class);

        assertEquals(EXP_MESSAGE_ERROR, unSuccessLoginResponse.getError());

    }
}

