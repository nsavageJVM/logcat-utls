/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fishbeans.app;

import com.fishbeans.app.ui.controllers.ViewController;
import com.fishbeans.util.ResourceUtils;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.stage.Screen;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.net.URL;

/**
 *
 * hijack the fxml classloader to allow sprng cdi in controllers
 */
public abstract class FXMLComponentBase implements ApplicationContextAware {

	private static final String FXML_DIR = "fxml/";

	protected FXMLLoader fxmlLoader;
	protected URL fxmlView;
	private ApplicationContext applicationContext;
	protected Rectangle2D bounds;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

		if (this.applicationContext != null) {
			return;
		}

		this.applicationContext = applicationContext;
	}

	public FXMLComponentBase() {
		String firstPath =  getClass().getSimpleName().toLowerCase();
		firstPath = firstPath+".fxml";
		String fullPath = FXML_DIR + firstPath;

		//System.out.println(fullPath);
		//Gets resource from classpath.
		this.fxmlView = ResourceUtils.getResource(fullPath);

	}


	public Parent getRootNode() {

		setupFxmlReloader();

		Parent parent = fxmlLoader.getRoot();
		Screen screen = Screen.getPrimary();
		Rectangle2D bounds = screen.getVisualBounds();
		ViewController controller = (ViewController) fxmlLoader.getController();
		controller.set2DBounds(bounds);

		return parent;
	}




	private Object createControllerAsBean(Class<?> type) {
		return this.applicationContext.getBean(type);
	}

	private FXMLLoader doLoad(URL fxml ) throws IllegalStateException {

		FXMLLoader loader = new FXMLLoader(fxml );
		loader.setControllerFactory(this::createControllerAsBean);

		try {
			loader.load();
		} catch (IOException ex) {
			throw new IllegalStateException( ex);
		}

		return loader;
	}

	private void setupFxmlReloader() {

		if (this.fxmlLoader != null) {
			return;
		}

		this.fxmlLoader = doLoad(fxmlView);

	}

	public void set2DBounds(Rectangle2D bounds) {
		this.bounds = bounds;
	}
}
