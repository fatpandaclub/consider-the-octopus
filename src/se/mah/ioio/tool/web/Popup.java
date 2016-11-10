package se.mah.ioio.tool.web;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.web.PopupFeatures;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class Popup extends Stage implements Callback<PopupFeatures, WebEngine> {
	private Stage primaryStage;
	
	Popup(Stage primStage) {
		initStyle(StageStyle.UTILITY);
		setTitle("Popup!");
		primaryStage = primStage;
	}
	
	@Override public WebEngine call(PopupFeatures p) {
		WebView wv = new WebView();
		setScene(new Scene(wv, 480, 320));
		show();
		double x = (primaryStage.getX() + (primaryStage.getWidth()/2) - (getWidth()/2));
		double y = (primaryStage.getY() + (primaryStage.getHeight()/2) - (getHeight()/2));
		setX(x);
		setY(y);
		wv.getEngine().setOnVisibilityChanged(new EventHandler<WebEvent<Boolean>>() {
			@Override public void handle(WebEvent<Boolean> event) {
				if(event.getData().booleanValue()) {
					show();
				} else {
					hide();
				}
			}
		});
		return wv.getEngine();
	}
}