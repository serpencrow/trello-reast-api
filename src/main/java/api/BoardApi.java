package api;

import beans.Board;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.BoardApiConstants;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import utils.PropertiesSingleton;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Properties;

import static constants.BoardApiConstants.*;
import static io.restassured.http.ContentType.*;
import static org.hamcrest.Matchers.lessThan;

public class BoardApi {

    private Properties properties;
    private ContentType contentType = URLENC;
    private HashMap<String, String> params = new HashMap<>();

    private BoardApi() {
    }

    public static class BoardApiBuilder {

        private BoardApi boardApi;

        private BoardApiBuilder(BoardApi boardApi) {
            this.boardApi = boardApi;
        }

        public BoardApiBuilder id(String id) {
            boardApi.params.put(BoardApiConstants.ID, id);
            return this;
        }

        public BoardApiBuilder name(String name) {
            boardApi.params.put(BoardApiConstants.NAME, name);
            return this;
        }

        public BoardApiBuilder desc(String desc) {
            boardApi.params.put(BoardApiConstants.DESCRIPTION, desc);
            return this;
        }

        public BoardApiBuilder closed(Boolean closingState) {
            boardApi.params.put(BoardApiConstants.CLOSED, closingState.toString());
            return this;
        }

        public Response createBoard() {
            return RestAssured.with()
                    .spec(baseRequestConfiguration())
                    .contentType(boardApi.contentType
                            .withCharset(Charset.defaultCharset()))
                    .params(boardApi.params)
                    .log().all()
                    .post(boardApi.properties.getProperty(ROOT_PATH)
                            + boardApi.properties.getProperty(BOARDS_PATH)
                    ).prettyPeek();
        }

        public Response getBoard(String id) {
            return RestAssured.with()
                    .spec(baseRequestConfiguration())
                    .contentType(boardApi.contentType
                            .withCharset(Charset.defaultCharset()))
                    .log().all()
                    .get(boardApi.properties.getProperty(ROOT_PATH)
                            + boardApi.properties.getProperty(BOARDS_PATH)
                            + id
                    ).prettyPeek();
        }

        public Response deleteBoard(String id) {
            return RestAssured.with()
                    .spec(baseRequestConfiguration())
                    .contentType(boardApi.contentType
                            .withCharset(Charset.defaultCharset()))
                    .log().all()
                    .delete(boardApi.properties.getProperty(ROOT_PATH)
                            + boardApi.properties.getProperty(BOARDS_PATH)
                            + id
                    ).prettyPeek();
        }

        public Response updateBoard(String id) {
            return RestAssured.with()
                    .spec(baseRequestConfiguration())
                    .contentType(boardApi.contentType
                            .withCharset(Charset.defaultCharset()))
                    .params(boardApi.params)
                    .log().all()
                    .put(boardApi.properties.getProperty(ROOT_PATH)
                            + boardApi.properties.getProperty(BOARDS_PATH)
                            + id
                    ).prettyPeek();
        }
    }

    public static BoardApiBuilder with() {
        BoardApi api = new BoardApi();
        api.properties = PropertiesSingleton.get();
        return new BoardApiBuilder(api);
    }

    public static Board getBoard(Response response) {
        return new Gson().
                fromJson(response.asString().trim(),
                        new TypeToken<Board>() {}.getType());
    }

    public static ResponseSpecification successResponse() {
        return new ResponseSpecBuilder()
                .expectContentType(JSON)
                .expectResponseTime(lessThan(20000L))
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification notFoundResponse() {
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
