package se.mah.ioio.tool.web;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Alert extends Stage implements EventHandler<WebEvent<String>> {
	private Stage primaryStage;
	Alert(Stage primStage) {
		primaryStage = primStage;
	}
	@Override public void handle(WebEvent<String> event) {
		//Stage popup = new Stage();
		initOwner(primaryStage);
		initStyle(StageStyle.UTILITY);
		initModality(Modality.WINDOW_MODAL);
		setTitle("Alert!");

		StackPane content = new StackPane();
		content.setPadding(new Insets(10));
		content.getChildren().setAll(new Label(event.getData()));
		content.setMinWidth(75);
		
		setScene(new Scene(content));
		sizeToScene();
		
		show(); //Show dialog to be able to retrieve width and height.
		//Get x and y as center of primary stage.
		double y = (primaryStage.getY() + (primaryStage.getHeight()/2) - (getHeight()/2));
		double x = (primaryStage.getX() + (primaryStage.getWidth()/2) - (getWidth()/2));
		hide();
		setY(y);
        setX(x);
		
		showAndWait();
	}
}