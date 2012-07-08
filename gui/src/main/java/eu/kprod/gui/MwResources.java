/**
 * Copyright (C) 2012 @author treym (Trey Marc)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.kprod.gui;

import java.util.HashMap;

/**
 * for everythings related to resources path
 * @author treym
 *
 */
public class MwResources {

    private static final String FONT_PREFIX = "/fonts/";
    private static final String THEME_PREFIXE = "/images/";

    private static final String DEFAULT_FONT = FONT_PREFIX + "Liberation/LiberationMono-Regular.ttf";
    private static final String DEFAULT_THEME = THEME_PREFIXE + "default/";

    HashMap<Integer, String> ressources;

    MwResources() {
        ressources = initDefaultMap();

    }

    MwResources(HashMap<Integer, String>  r) {
        ressources = r;
    }


    boolean setResources(HashMap<Integer, String> ressources1) {
        if (ressources1 != null) {
            ressources = ressources1;
            return true;
        } else {
            return false;
        }
    }

   private HashMap<Integer, String> initDefaultMap() {

       HashMap<Integer, String> ress =  new  HashMap<Integer, String>();
       ress.put(MwConfiguration.FONT, DEFAULT_FONT);
       ress.put(MwConfiguration.THEME, DEFAULT_THEME);
       return ress;

    }

   String get(int key) {

       String r = ressources.get(key);
       if (r==null){
           return "";
       }else{
           return r;
       }

   }
}
