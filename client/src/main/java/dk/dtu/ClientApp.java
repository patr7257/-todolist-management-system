package dk.dtu;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

/**
 * JavaFX client
 *  - Scenes + controllers (login, lists overview, tasks view)
 *  - Navigation (in SceneNavigator)
 *  - UI states (current user, selected list etc)
 */
public class ClientApp extends Application {

    public static void main(String[] args) throws InterruptedException {
        Space inbox = new SequentialSpace();
        inbox.put("Hello World!");
        Object[] tuple = inbox.get(new FormalField(String.class));
        System.out.println(tuple[0]);

        launch(args);
    }

    private int counter = 0;
    private final Button button = new Button();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");

        button.setText("Im a counter! Click ME!!!");
        button.setOnAction(this::handleClick);

        StackPane root = new StackPane(button);
        Scene scene = new Scene(root, 300, 250);
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKey);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleClick(ActionEvent event) {
        counter++;
        button.setText(String.valueOf(counter));
    }

    private void handleKey(KeyEvent event) {
        if (event.getCode() == KeyCode.UP) {
            counter++;
        } else if (event.getCode() == KeyCode.DOWN) {
            counter--;
        } else {
            return;
        }
        button.setText(String.valueOf(counter));
    }
}
