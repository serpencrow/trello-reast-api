package api;

import beans.Board;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.BoardConstant;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpStatus;
import utils.PropertiesSingleton;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;

import static constants.BoardApiConstants.*;
import static io.restassured.http.ContentType.*;
import static org.hamcrest.Matchers.lessThan;

public class BoardApi {

    private Properties properties = PropertiesSingleton.get();;
    private ContentType contentType = URLENC;
    private HashMap<String, String> params = new HashMap<>();

    private BoardApi() {
    }

    public static class BoardApiBuilder {

        private BoardApi boardApi;

        private BoardApiBuilder(BoardApi boardApi) {
            this.boardApi = boardApi;
        }

        /*
        public BoardApiBuilder id(String id) {
            boardApi.params.put(BoardConstant.ID.getConstantName(), id);
            return this;
        }*/

        public BoardApiBuilder name(String name) {
            boardApi.params.put(BoardConstant.NAME.getConstantName(), name);
            return this;
        }

        public BoardApiBuilder desc(String desc) {
            boardApi.params.put(BoardConstant.DESCRIPTION.getConstantName(), desc);
            return this;
        }

        public BoardApiBuilder closed(Boolean closingState) {
            boardApi.params.put(BoardConstant.CLOSED.getConstantName(), closingState.toString());
            return this;
        }

        public Board createBoard() {
            return formBoardFromResponse(
                    RestAssured.with()
                        .spec(baseRequestConfiguration())
                        .contentType(boardApi.contentType
                                .withCharset(Charset.defaultCharset()))
                        .params(boardApi.params)
                        .log().all()
                        .post(boardApi.properties.getProperty(ROOT_PATH)
                                + boardApi.properties.getProperty(BOARDS_PATH)
                        ).prettyPeek()
            );
        }

        public Board getBoard(String id) {
            return formBoardFromResponse(
                    RestAssured.with()
                        .spec(baseRequestConfiguration())
                        .contentType(boardApi.contentType
                                .withCharset(Charset.defaultCharset()))
                        .log().all()
                        .get(boardApi.properties.getProperty(ROOT_PATH)
                                + boardApi.properties.getProperty(BOARDS_PATH)
                                + id
                        ).prettyPeek()
            );
        }

        public int deleteBoard(String id) {
            return RestAssured.with()
                    .spec(baseRequestConfiguration())
                    .contentType(boardApi.contentType
                            .withCharset(Charset.defaultCharset()))
                    .log().all()
                    .delete(boardApi.properties.getProperty(ROOT_PATH)
                            + boardApi.properties.getProperty(BOARDS_PATH)
                            + id
                    ).prettyPeek()
                    .then()
                    .extract()
                    .statusCode();
        }

        public Board updateBoard(String id) {
            return formBoardFromResponse(
                    RestAssured.with()
                        .spec(baseRequestConfiguration())
                        .contentType(boardApi.contentType
                                .withCharset(Charset.defaultCharset()))
                        .params(boardApi.params)
                        .log().all()
                        .put(boardApi.properties.getProperty(ROOT_PATH)
                                + boardApi.properties.getProperty(BOARDS_PATH)
                                + id
                        ).prettyPeek()
            );
        }
    }

    public static BoardApiBuilder boardApiBuilder() {
        return new BoardApiBuilder(new BoardApi());
    }

    public static Board createBoard() {
        BoardApi api = new BoardApi();
        api.params.put(BoardConstant.NAME.getConstantName(),
                RandomStringUtils.randomAlphabetic(40));
        return new BoardApiBuilder(api).createBoard();
    }

    public static Board getBoard(final String id) {
        BoardApi api = new BoardApi();
        return new BoardApiBuilder(api).getBoard(id);
    }

    public static int deleteBoard(final String id) {
        BoardApi api = new BoardApi();
        return new BoardApiBuilder(api).deleteBoard(id);
    }

    private static Board formBoardFromResponse(Response response) {
        if (HttpStatus.SC_OK == response.then().extract().statusCode()) {
            return new Gson().
                    fromJson(response.asString().trim(),
                            new TypeToken<Board>() {}.getType());
        } else {
            return null;
        }
    }

    private static ResponseSpecification successResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(JSON)
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    private static ResponseSpecification notFoundResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(TEXT)
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .build();
    }

    private static RequestSpecification baseRequestConfiguration() {
        return new RequestSpecBuilder()
                .setRelaxedHTTPSValidation()
                .setBaseUri(PropertiesSingleton.get().getProperty(ROOT_PATH))
                .addParam(KEY, PropertiesSingleton.get().getProperty(KEY))
                .addParam(TOKEN, PropertiesSingleton.get().getProperty(TOKEN))
                .build();
    }
}
