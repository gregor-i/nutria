/*
 * Copyright (C) 2016  Gregor Ihmor & Merlin Göttlinger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import javafx.scene.{Scene => jScene}

import scalafx.application.JFXApp
import scalafx.scene.Scene
import scalafx.scene.Parent
import scalafxml.core.{FXMLView, NoDependencyResolver}

object Viewer extends JFXApp {
  stage = new JFXApp.PrimaryStage {
    title.value = "Nutria Viewer"
    width = 800
    height = 600
    scene = new Scene(new jScene(FXMLView(getClass.getResource("gui.fxml"), NoDependencyResolver)))
    //val model = Model.defaultModel
    //new View(model)
  }
}


