package client.view;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.ResourceBundle;

public class GameView implements Initializable {
    private static final int BOARD_SIZE = 450, DIM = 10;

    @FXML
    private GridPane gridpaneP1, grifpaneP2;

    @FXML
    private Label lblP2, lblP1, lblHead;

    @FXML
    private Label lblActiveP1, lblActiveP11;

    private Node getGridCell(GridPane gridPane, int row, int col) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            //if(GridPane.getRowIndex(node) == row && GridPane.getColumnIndex(node) == col) {
                System.out.println(GridPane.getRowIndex(node));
                System.out.println(GridPane.getColumnIndex(node));
            //}
        }
        return null;
    }

    public GridPane getGrid(int id) {
        if (id == 0)
            return gridpaneP1;
        return grifpaneP2;
    }

    public void cellClicked(int gridId, int r, int col) {
        System.out.println(gridId + " " + r + " " + col);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (int k = 0; k < 2; k++)
            for (int i = 0; i < DIM; i++)
                for (int j = 0; j < DIM; j++) {
                    Rectangle tile = new Rectangle(BOARD_SIZE / DIM, BOARD_SIZE / DIM);
                    tile.setFill(Color.TRANSPARENT);
                    getGrid(k).add(new StackPane(tile), j, i);
                    int ii = i, jj = j, kk = k;
                    tile.setOnMouseClicked((event) -> {
                        cellClicked(kk, ii, jj);
                    });
                }
    }
}
