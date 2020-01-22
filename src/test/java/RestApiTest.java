import api.BoardApi;
import beans.Board;
import enums.BoardConstant;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class RestApiTest {

    @Test
    public void createNewBoardsTest() {
        String boardName1 = "boardName1";
        String boardName2 = "boardName2";

        Board board1 = BoardApi.createBoard(boardName1);
        Board board2 = BoardApi.createBoard(boardName2);

        assertThat(board1.name, equalTo(boardName1));
        assertThat(board2.name, equalTo(boardName2));

        BoardApi.deleteBoard(board1.id);
        BoardApi.deleteBoard(board2.id);
    }

    @Test
    public void closeBoardTest() {
        String boardName1 = "boardName3";

        Board board1 = BoardApi.createBoard(boardName1);
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.CLOSED, "true");

        assertThat(board1.closed, equalTo(true));
    }

    @Test
    public void deleteBoardTest() {
        String boardName1 = "boardName4";

        Board board1 = BoardApi.createBoard(boardName1);
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.CLOSED, "true");

        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void closeNonexistentBoardTest() {
        String boardName1 = "boardName5";

        Board board1 = BoardApi.createBoard(boardName1);
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.CLOSED, "true");

        BoardApi.deleteBoard(board1.id);

        board1 = BoardApi.updateBoard(board1.id, BoardConstant.CLOSED, "true");
        assertThat(board1, is(nullValue()));
    }

    @Test
    public void deleteNonexistentBoardTest() {
        String boardName1 = "boardName6";

        Board board1 = BoardApi.createBoard(boardName1);
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.CLOSED, "true");

        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void getExistingBoardTest() {
        String boardName1 = "boardName7";
        Board board1 = BoardApi.createBoard(boardName1);
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.DESCRIPTION, "sas123");

        Board board2 = BoardApi.getBoard(board1.id);

        assertThat(board2.desc, allOf(startsWith("sas"), endsWith("123")));
        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void getNonexistentBoardTest() {
        String boardName1 = "boardName8";
        Board board1 = BoardApi.createBoard(boardName1);
        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));

        Board board2 = BoardApi.getBoard(board1.id);
        assertThat(board2, is(nullValue()));
    }

    @Test
    public void updateBoardTest() {
        String boardName1 = "boardName9";
        Board board1 = BoardApi.createBoard(boardName1);
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.DESCRIPTION, "sas123");
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.DESCRIPTION, "sas124");
        board1 = BoardApi.updateBoard(board1.id, BoardConstant.DESCRIPTION, "sas126");

        assertThat(board1.desc,
                anyOf(
                        containsString("123"),
                        containsString("124"),
                        containsString("126")
                ));

        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
    }
}

