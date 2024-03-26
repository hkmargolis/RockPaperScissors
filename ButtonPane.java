package com.example.demo2;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * The ButtonPane class extends GridPane and creates the buttons for the client window in an HBox.
 */
public class ButtonPane extends GridPane {
    //Variables
    private Button rockButton;
    private Button paperButton;
    private Button scissorsButton;
    private Button newGameButton;
    private Button quitButton;

    /**
     * Constructor creates the buttons, adds them to an HBox and then adds them to the ButtonPane (GridPane)
     */
    public ButtonPane(){
        rockButton = createIconButton("https://icons.iconarchive.com/icons/microsoft/fluentui-emoji-flat/512/Rock-Flat-icon.png");
        paperButton = createIconButton("https://en.wiki.forgeofempires.com/images/9/96/Paper_icon.png");
        scissorsButton = createIconButton("https://cdn-icons-png.flaticon.com/512/5418/5418004.png");
        newGameButton = createTextButton("New Game");
        quitButton = createTextButton("Quit");
        quitButton.setVisible(false);
        setStyle("-fx-background-color: black; -fx-background-color: black; -fx-background-radius: 15px; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10 10 10 10;");
        HBox hBox = new HBox(20, newGameButton, rockButton, paperButton, scissorsButton, quitButton);
        getChildren().addAll(hBox);
    }
    /**
     * Creates the rock, paper and scissors buttons.
     * @param url image url
     * @return button
     */
    private Button createIconButton(String url){
        Image img = new Image(url);
        ImageView view = new ImageView(img);
        view.setFitHeight(80);
        view.setPreserveRatio(true);
        //Creating a Button
        Button button = new Button();
        button.setStyle("-fx-background-color: black; -fx-background-color: black; -fx-background-radius: 15px; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10 10 10 10;");
        //Setting the location of the button
        button.setTranslateX(25);
        button.setTranslateY(100);
        //Setting the size of the button
        button.setPrefSize(80, 80);
        //Setting a graphic to the button
        button.setGraphic(view);
        return button;
    }


    /**
     * Creates the new game button and the quit button.
     * @param text displayed on button
     * @return button.
     */
    private Button createTextButton(String text){
        Button button = new Button();
        button.setText(text);
        button.setTextFill(Color.WHITE);
        button.setFont(new Font("Arial", 14));
        button.setStyle("-fx-background-color: black; -fx-background-color: black; -fx-background-radius: 15px; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 10 10 10 10;");
        //Setting the location of the button
        button.setTranslateX(25);
        button.setTranslateY(100);
        //Setting the size of the button
        button.setPrefSize(100, 80);
        return button;
    }

    /**
     * This method gets the rock button.
     * @return rockButton
     */
    public Button getRockButton(){
        return rockButton;
    }

    /**
     * This method gets the paper button
     * @return paperButton
     */
    public Button getPaperButton(){
        return paperButton;
    }

    /**
     * This method gets the scissors button.
     * @return scissorsButton
     */
    public Button getScissorsButton(){
        return scissorsButton;
    }

    /**
     * This method gets the new game button.
     * @return newGameButton
     */
    public Button getNewGameButton() { return newGameButton; }

    /**
     * This method gets the quit button
     * @return quitButton
     */
    public Button getQuitButton() { return quitButton; }
}
