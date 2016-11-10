package se.mah.ioio.tool.web;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;


public class Confirm extends Stage implements Callback<String, Boolean> {
	private final Stage primaryStage;
    private VBox layout = new VBox();

    private ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();
    public boolean isSelected() {
        return selected.get();
    }
    public ReadOnlyBooleanProperty selectedProperty() {
        return selected.getReadOnlyProperty();
    }
    
    public Confirm(final Stage primStage) {
    	primaryStage = primStage;
    }
    
    public void initilize(String question) {
    	setTitle("Comfirm!");
        initStyle(StageStyle.UTILITY);
        initModality(Modality.APPLICATION_MODAL);

        layout.setSpacing(10);
        layout.setPadding(new Insets(10));

        createControls();

        layout.getChildren().addAll(
                new Label(question),
                createControls()
        );

        setScene(new Scene(layout));
        sizeToScene();  // workaround because utility stages aren't automatically sized correctly to their scene.
    }

    private HBox createControls() {
        final Button ok = new Button("OK");
        ok.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent arg0) {
	            selected.set(true);
	            close();
			}
        });

        final Button cancel = new Button("Cancel");
        cancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent arg0) {
				selected.set(false);
	            close();
			}
        });

        final HBox controls = new HBox(10, ok, cancel);
        controls.setAlignment(Pos.CENTER_RIGHT);

        return controls;
    }
    
    @Override public Boolean call(final String msg) {
    	initilize(msg);
		show(); //Show dialog to be able to retrieve width and height.
		//Get x and y as center of primary stage.
		double y = (primaryStage.getY() + (primaryStage.getHeight()/2) - (getHeight()/2));
		double x = (primaryStage.getX() + (primaryStage.getWidth()/2) - (getWidth()/2));
		hide();
        setY(y);
        setX(x);
        showAndWait();
        return isSelected();
	}
}