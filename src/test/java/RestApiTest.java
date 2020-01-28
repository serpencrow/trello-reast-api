import api.BoardApi;
import beans.Board;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;

public class RestApiTest {

    @Test
    public void createNewBoardsTest() {
        Board board1 = BoardApi.createBoard();
        Board board2 = BoardApi.createBoard();

        assertThat(board1.closed, equalTo(false));
        assertThat(board2.closed, equalTo(false));

        BoardApi.deleteBoard(board1.id);
        BoardApi.deleteBoard(board2.id);
    }

    @Test
    public void closeBoardTest() {
        Board board1 = BoardApi.createBoard();
        board1 = BoardApi.boardApiBuilder()
                .closed(true)
                .updateBoard(board1.id);

        assertThat(board1.closed, equalTo(true));
    }

    @Test
    public void deleteBoardTest() {
        Board board1 = BoardApi.createBoard();

        board1 = BoardApi.boardApiBuilder()
                .closed(true)
                .updateBoard(board1.id);

        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void closeNonexistentBoardTest() {
        Board board1 = BoardApi.createBoard();
        board1 = BoardApi.boardApiBuilder()
                .closed(true)
                .updateBoard(board1.id);

        BoardApi.deleteBoard(board1.id);

        board1 = BoardApi.boardApiBuilder()
                .closed(true)
                .updateBoard(board1.id);

        assertThat(board1, is(nullValue()));
    }

    @Test
    public void deleteNonexistentBoardTest() {
        Board board1 = BoardApi.createBoard();

        board1 = BoardApi.boardApiBuilder()
                .closed(true)
                .updateBoard(board1.id);

        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_NOT_FOUND));
    }

    @Test
    public void getExistingBoardTest() {
        Board board1 = BoardApi.createBoard();
        String boardDescription = "testDescription";

        board1 = BoardApi.boardApiBuilder()
                .desc(boardDescription)
                .updateBoard(board1.id);

        Board board2 = BoardApi.getBoard(board1.id);

        assertThat(board2.desc, allOf(startsWith("test"), endsWith("Description")));
        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
    }

    @Test
    public void getNonexistentBoardTest() {
        Board board1 = BoardApi.createBoard();
        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));

        Board board2 = BoardApi.getBoard(board1.id);
        assertThat(board2, is(nullValue()));
    }

    @Test
    public void updateBoardTest() {
        Board board1 = BoardApi.createBoard();
        Board board2 = BoardApi.createBoard();
        Board board3 = BoardApi.createBoard();
        String boardDescription1 = "7test";
        String boardDescription2 = "7test123";
        String boardDescription3 = "7test225";

        board1 = BoardApi.boardApiBuilder()
                .desc(boardDescription1)
                .updateBoard(board1.id);
        board2 = BoardApi.boardApiBuilder()
                .desc(boardDescription2)
                .updateBoard(board2.id);
        board3 = BoardApi.boardApiBuilder()
                .desc(boardDescription3)
                .updateBoard(board3.id);

        assertThat(
                Arrays.asList(board1.desc, board2.desc, board3.desc),
                containsInAnyOrder(boardDescription2, boardDescription3, boardDescription1)
        );

        assertThat(BoardApi.deleteBoard(board1.id), equalTo(HttpStatus.SC_OK));
        assertThat(BoardApi.deleteBoard(board2.id), equalTo(HttpStatus.SC_OK));
        assertThat(BoardApi.deleteBoard(board3.id), equalTo(HttpStatus.SC_OK));
    }
}

