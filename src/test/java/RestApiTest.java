import api.BoardApi;
import beans.Board;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.List;

import static constants.TestConstants.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class RestApiTest {

    private List<Board> boardList = new LinkedList<>();

    @Test(priority = 1)
    public void createNewBoardsTest() {
        boardList.add(0, BoardApi.getBoard(BoardApi.with()
                .name(FIRST_BOARD_NAME)
                .desc(OLD_BOARD_DESCRIPTION)
                .createBoard().then().specification(BoardApi.successResponse()).extract().response())
        );

        boardList.add(1, BoardApi.getBoard(BoardApi.with()
                .name(SECOND_BOARD_NAME)
                .desc(OLD_BOARD_DESCRIPTION)
                .createBoard().then().specification(BoardApi.successResponse()).extract().response())
        );

        assertThat(boardList.get(0).name, equalTo(FIRST_BOARD_NAME));
        assertThat(boardList.get(1).name, equalTo(SECOND_BOARD_NAME));
    }

    @Test(priority = 2)
    public void closeBoardTest() {
        boardList.set(1, BoardApi.getBoard(BoardApi.with()
                .closed(true)
                .updateBoard(boardList.get(1).id)
                .then()
                .specification(BoardApi.successResponse()).extract().response())
        );

        assertThat(boardList.get(1).closed, equalTo(true));
    }

    @Test(priority = 3)
    public void deleteBoardTest() {
        BoardApi.with()
                .deleteBoard(boardList.get(1).id)
                .then()
                .specification(BoardApi.successResponse());

        BoardApi.with()
                .getBoard(boardList.get(1).id)
                .then()
                .specification(BoardApi.notFoundResponse());
    }

    @Test(priority = 4)
    public void closeNonexistentBoardTest() {
       BoardApi.with()
                .closed(true)
                .updateBoard(boardList.get(1).id)
                .then()
                .specification(BoardApi.notFoundResponse());
    }

    @Test(priority = 5)
    public void deleteNonexistentBoardTest() {
        BoardApi.with()
                .deleteBoard(boardList.get(1).id)
                .then()
                .specification(BoardApi.notFoundResponse());
    }

    @Test(priority = 6)
    public void getExistingBoardTest() {
        Board exBoard = BoardApi.getBoard(BoardApi.with()
                .getBoard(boardList.get(0).id)
                .then()
                .specification(BoardApi.successResponse()).extract().response());

        assertThat(exBoard.name, equalTo(boardList.get(0).name));
    }

    @Test(priority = 7)
    public void getNonexistentBoardTest() {
        BoardApi.with()
                .getBoard(boardList.get(1).id)
                .then()
                .specification(BoardApi.notFoundResponse());
    }

    @Test(priority = 8)
    public void updateBoardTest() {
        assertThat(boardList.get(0).name, equalTo(FIRST_BOARD_NAME));
        assertThat(boardList.get(0).desc, equalTo(OLD_BOARD_DESCRIPTION));

        boardList.set(0, BoardApi.getBoard(BoardApi.with()
                .name(NEW_BOARD_NAME)
                .desc(NEW_BOARD_DESCRIPTION)
                .updateBoard(boardList.get(0).id)
                .then()
                .specification(BoardApi.successResponse()).extract().response())
        );

        assertThat(boardList.get(0).name, equalTo(NEW_BOARD_NAME));
        assertThat(boardList.get(0).desc, equalTo(NEW_BOARD_DESCRIPTION));
    }

    @AfterSuite
    public void tearDown() {
        boardList.forEach(board -> BoardApi.with().deleteBoard(board.id));
    }
}

