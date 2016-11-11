package se.mah.ioio.tool.web;

import java.util.concurrent.CountDownLatch;

import javax.swing.SwingUtilities;

//import processing.app.Editor;
import netscape.javascript.JSObject;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import processing.app.Base;
import processing.app.tools.Tool;
import processing.app.ui.Editor;
import processing.app.Messages;

/*
 * This class is an interface for web processing via JavaFX.
 * It has a singleton instance since I couldn't find another way of getting a reference
 * to the object created by JavaFX after launching it.
 * It has an reference to a Browser which is explained below.
 */
public class WebInterface extends Application {
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static WebInterface instance = null;
	public Browser browser;
	private Scene scene;
	private Stage stage;
	private Editor editor;
	  Base base;

	public static WebInterface waitForInitialization(Editor edit) {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		instance.editor = edit;
		return instance;
	}

	public static void setInstance(WebInterface ins) {
		instance = ins;
		latch.countDown();
	}

	public WebInterface() {
		setInstance(this);
	}

	@Override
	public void start(Stage s) throws Exception {
		// create the scene
		stage = s;
		stage.setTitle("U P G R A D E D   G U A C A M O L E");
		browser = new Browser(stage, editor);
		scene = new Scene(browser, 960, 640, Color.web("#56DCF2"));
		stage.setScene(scene);
		scene.getStylesheets().add(this.getClass().getResource("/data/style.css").toExternalForm()); //XXX
		stage.show();
	}

	public void showOrHide() {
		if(stage.isShowing()) {
			stage.hide();
		} else {
			stage.show();
		}
	}
}

/*
 * Our Browser class is a class for controlling the WebView from JavaFX.
 * It allows us to communicate with the web page we are viewing.
 */
class Browser extends Region {
	final protected Editor editor;
	final protected WebView browser = new WebView();
	final protected WebEngine webEngine = browser.getEngine();
	final protected Stage primaryStage;
	final protected Button showPrevDoc = new Button("Toggle Previous Docs");
	final protected ComboBox<String> comboBox = new ComboBox<String>();
	private HBox toolBar;
	protected boolean isLoaded;

	public Browser(Stage stage, Editor edit) {
		getStyleClass().add("browser");
		primaryStage = stage; //Save the stage reference
		editor = edit; //Save the editor reference
		isLoaded = false;
		
		comboBox.setPrefWidth(60);
		
		//Set up toolbar
		toolBar = new HBox();
		toolBar.setAlignment(Pos.CENTER);
		toolBar.getStyleClass().add("browser-toolbar");
		toolBar.getChildren().add(showPrevDoc);
        toolBar.getChildren().add(comboBox);
        toolBar.getChildren().add(createSpacer());
        

        //Set up the previous docs button
        showPrevDoc.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent t) {
                webEngine.executeScript("window.history.back()");
            }
        });
        
        //process history
        final WebHistory history = webEngine.getHistory();
        history.getEntries().addListener(new 
            ListChangeListener<WebHistory.Entry>(){
                @Override public void onChanged(Change<? extends Entry> c) {
                    c.next();
                    for (Entry e : c.getRemoved()) {
                        comboBox.getItems().remove(e.getUrl());
                    }
                    for (Entry e : c.getAddedSubList()) {
                        comboBox.getItems().add(e.getUrl());
                    }
                }
        });
 
        //set the behavior for the history combobox               
        comboBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent ev) {
                int offset = comboBox.getSelectionModel().getSelectedIndex() - history.getCurrentIndex();
                history.go(offset);
            }
        });
        
		//Set up the confirm handler
		webEngine.setConfirmHandler(new Confirm(primaryStage));
		//Set up the alert handler.
		webEngine.setOnAlert(new Alert(primaryStage));
		//Set up the popup handler.
		webEngine.setCreatePopupHandler(new Popup(primaryStage));
		//Set up the visibility handler.
		webEngine.setOnVisibilityChanged(new EventHandler<WebEvent<Boolean>>() {
			@Override public void handle(WebEvent<Boolean> event) {
				if(event.getData().booleanValue()) {
					primaryStage.show();
				} else {
					primaryStage.hide();
				}
			}
		});
		
		//Set up the load worker to change state of the program.
		webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				if (newState == State.SUCCEEDED) {
					JSObject win = (JSObject) webEngine.executeScript("window");
					win.setMember("app", new JSToJava(webEngine));
					isLoaded = true;
				} else {
					isLoaded = false;
				}
			}
		});
		webEngine.load(this.getClass().getResource("/data/main.css").toExternalForm());
		webEngine.load(this.getClass().getResource("/data/jquery.min.js").toExternalForm());
		webEngine.load(this.getClass().getResource("/data/main.js").toExternalForm());
		webEngine.load(this.getClass().getResource("/data/index.html").toExternalForm());
		//getChildren().add(toolBar); //XXX: this will add a toolbar to your browser
		getChildren().add(browser);
	}

	// JavaScript to Java interface
	/* XXX: write here whatever functions you want to be calling from your Javascript
	 * XXX: if you have in your Javascript something like "app.textInput(txt)" you need to have a
	 * XXX: callback method in this class called "textInput" in order to capture the Javascript event
	 */
	public class JSToJava {		
		
		WebEngine webEngine;
		
		JSToJava(WebEngine webEngine) {
			this.webEngine = webEngine;
		}
		
		//XXX: this first example is sending text from your Javascript application to the IDE
		//XXX: uncomment the line you prefer to either erase everything or to append some text at bottom
		public void textInput(final String txt) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
					//XXX: to erase and just add text
					editor.setText(txt);
					
					//XXX: to append text at the bottom
					//editor.setText(editor.getSketch().getCode().toString()+"\n"+txt);
				}
			});
		}
		
		//XXX: this second example is collecting some information about your code and sending it back to Javascript
		public void getTab(final String txt) {
			
					//XXX: get the sketch name
					String name = editor.getSketch().getName();
					
					//XXX: get the whole code in the window
					String code = editor.getText();
					
					//XXX: split the code into an array using SPACE, TAB and EOL as separators
					String[] symbols = code.split("\\s+");
					
					//XXX: get the amount of items in the array, a.k.a. the amount of words
					int words = symbols.length;
					
					//XXX: get the amount of lines used in the editor
					int lines = editor.getLineCount();
					
					//XXX: send the information back to the Javascript
					webEngine.executeScript("changeTab('"+name+"','"+words+"','"+lines+"')");
		}
		
		public void generateSwitch(String switchCountTxt, String varName) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override public void run() {
			
					int switchCount = Integer.parseInt(switchCountTxt);
					 
					if(switchCount > 0) {
						String newSwitch = "switch (int " + varName + ") {\n";
						for(int i = 0; i < switchCount; i++) {
							newSwitch += "\tcase " + i + ":\n";
							newSwitch += "\t\t // Some code goes here\n";
							newSwitch += "\t\tbreak;\n";
						}
						newSwitch += "\tdefault:\n";
						newSwitch += "\t\t // Some code goes here\n";
						newSwitch += "\t\tbreak;\n";
						newSwitch += "}";
						 
						editor.insertText(newSwitch);
					}
					
					webEngine.executeScript("confirmReception()");
				}
			});
		 }
	}
	
    private Node createSpacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    @Override protected void layoutChildren() {
        double w = getWidth();
        double h = getHeight();
        double tbHeight = toolBar.prefHeight(w);
        layoutInArea(browser,0,tbHeight,w,h,0,HPos.CENTER,VPos.CENTER);
        layoutInArea(toolBar,0,0,w,tbHeight,0,HPos.CENTER,VPos.CENTER);
    }

	@Override protected double computePrefWidth(double height) {
		return 960;
	}

	@Override protected double computePrefHeight(double width) {
		return 640;
	}
}