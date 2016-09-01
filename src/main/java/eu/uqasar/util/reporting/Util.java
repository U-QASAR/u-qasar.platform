package eu.uqasar.util.reporting;

/*
 * #%L
 * U-QASAR
 * %%
 * Copyright (C) 2012 - 2015 U-QASAR Consortium
 * %%
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
 * #L%
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 *
 */
public class Util {

    public static void main(String[] args) {
        Util util = new Util();
        System.out.println(Util.createExpressionEditor(null,"http://uqasar.pythonanywhere.com"));

    }//EoM      

    public static String createExpressionEditor(String cubeurl,String uqasarOLAPDNS) {
        String script = "";

        if (cubeurl == null || cubeurl.equalsIgnoreCase("")) {
            
            List cubes = retrieveCubes(uqasarOLAPDNS + "/cubes");
            cubeurl = uqasarOLAPDNS + "/cube/"+ cubes.get(0);
        }

        ArrayList dimensions = new ArrayList();
        // get the dimension through /model
        dimensions = (ArrayList) Util.retrieveDimensions(cubeurl + "/model");

        script = "                 "        //<script>
                + "       "
                + "   var filters = [";

        script += "{\n"
                + "                    id: \"drilldown\",\n"
                + "                    label: \"drilldown\",\n"
                + "                    type: \"string\",\n"
                + "                    input: \"select\",\n"
                + "                        values: {\n";

        for (int j = 0; j < dimensions.size(); j++) {
            String retvalue = (String) dimensions.get(j);
            if (j > 0) {
                script += ",";
            }
            script += " \"" + retvalue + "\" : \"" + retvalue + "\" ";
        }

        script += "                         },\n"
                + "                    operators: [\"equal\"]\n"
                + "                }";

        for (Object dimension1 : dimensions) {
            //get dimensions
            String dimension = (String) dimension1;
            ArrayList retvalues = (ArrayList) retrieveValues(cubeurl + "/members/" + dimension, dimension);

            script += ", {\n"
                    + "                    id: \"" + dimension + "\",\n"
                    + "                    label: \"" + dimension + "\",\n"
                    + "                    type: \"string\",\n"
                    + "                    input: \"select\",\n"
                    + "                        values: {\n";
            for (int j = 0; j < retvalues.size(); j++) {
                String retvalue = (String) retvalues.get(j);
                if (j > 0) {
                    script += ",";
                }
                script += " \"" + retvalue + "\" : \"" + retvalue + "\" ";
            }

//                    + "                             1: 'Books',\n"
//                    + "                             2: 'Movies',\n"
//                    + "                             3: 'Music',\n"
//                    + "                             4: 'Goodies'\n"
            script += "                         },\n"
                    + "                    operators: [\"equal\"]\n"
                    + "                }";

        }//for

        script += "];    "
                + "            $(\"#builder-basic\").queryBuilder({\n"
                + "                plugins: [\"sortable\", \"bt-tooltip-errors\"],\n"
                + "                filters: filters\n"
                + "            });\n"
                + "            \n"
                + "             //$.unblockUI();"
                + "                ";           //</script>

        return script;
    }//EoM

    public static List retrieveDimensions(String url) {
        ArrayList objToReturn = new ArrayList();
        int maxretries = 10;
        boolean wellexecuted = false;
        while (!wellexecuted && maxretries > 0) {
            try {
                maxretries--;
                JSONObject json = readJsonFromUrl(url);
                if (json.has("error")) {
                    System.out.println("Error during dimension retrieval");
                    objToReturn = null;
                } else {
                    JSONObject values = new JSONObject();
                    JSONArray arr = json.getJSONArray("dimensions");
                    for (int i = 0; i < arr.length(); i++) {
                        String value = arr.getJSONObject(i).getString("name");
                        System.out.println("Dimension: " + value);
                        objToReturn.add(value);
                    }//for
                    wellexecuted = true;
                }//else
            } catch (JSONException ex) {
                ex.printStackTrace();
                wellexecuted = false;
            }//catch            
        }//while

        return objToReturn;
    }//EoM   

    private static List retrieveCubes(String url) {
        ArrayList objToReturn = new ArrayList();
        int maxretries = 10;
        boolean wellexecuted = false;
        while (!wellexecuted && maxretries > 0) {
            try {
                maxretries--;
                System.out.println("to url pou prospatho na xtipitso" + url);
                JSONArray jsonArray = readJsonArrayFromUrl(url);

                if (jsonArray.length() == 0) {
                    System.out.println("Error during cubes retrieval");
                    objToReturn = null;
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String value = jsonArray.getJSONObject(i).getString("name");
                        System.out.println("Retrieved cubes: " + value);
                        objToReturn.add(value);
                    }//for
                    wellexecuted = true;
                }//else
            } catch (JSONException ex) {
                ex.printStackTrace();
                wellexecuted = false;
            }//catch            
        }//while

        return objToReturn;
    }//EoM 

    public static JSONObject readJsonFromUrl(String url) throws JSONException {
        InputStream is = null;
        JSONObject json = null;

        int maxretries = 10;
        boolean wellexecuted = false;
        while (!wellexecuted && maxretries > 0) {
            try {
                maxretries--;
                is = new URL(url).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                json = new JSONObject(jsonText);
                is.close();
                wellexecuted = true;
            } catch (IOException ex) {
                ex.printStackTrace();
                json = new JSONObject();
                json.put("error", ex);
                wellexecuted = false;
            }
        }//while

        return json;
    }//EoM    

    private static JSONArray readJsonArrayFromUrl(String url) throws JSONException {
        InputStream is = null;
        JSONArray json = null;

        int maxretries = 10;
        boolean wellexecuted = false;
        while (!wellexecuted && maxretries > 0) {
            try {
                maxretries--;
                is = new URL(url).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                json = new JSONArray(jsonText);
                is.close();
                wellexecuted = true;
            } catch (IOException ex) {
                ex.printStackTrace();
                json = new JSONArray();

                wellexecuted = false;
            }
        }//while

        return json;
    }//EoM 

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }//EoM        

    private static List retrieveValues(String url, String filtername) {
        ArrayList ret = new ArrayList();
        int maxretries = 10;
        boolean wellexecuted = false;
        while (!wellexecuted && maxretries > 0) {
            try {
                JSONObject json = Util.readJsonFromUrl(url);
                if (json.has("error")) {
                    return ret;
                } else {
                    JSONArray arr = json.getJSONArray("data");
                    JSONObject values = new JSONObject();
                    for (int i = 0; i < arr.length(); i++) {
                        String value = "null";
                        if (!arr.getJSONObject(i).isNull(filtername)) {
                            value = arr.getJSONObject(i).getString(filtername);
                        }
                        values.put(value, value);
                        ret.add(value);
                    }//for
                }//else
                wellexecuted = true;
            } catch (JSONException ex) {
                ex.printStackTrace();
                wellexecuted = false;
            }
        }//while
        return ret;
    }//EoM

    public static String createExpressionEditorInputExperiment() {
        return "                 "                    //<script>
                + "       "
                + "   var filters = ["
                + "{\n"
                + "                    id: \"drilldown\",\n"
                + "                    label: \"drilldown\",\n"
                + "                    type: \"string\",\n"
                + "                    input: \"select\",\n"
                + "                        values: {\n"
                + "                             1: 'Books',\n"
                + "                             2: 'Movies',\n"
                + "                             3: 'Music',\n"
                + "                             4: 'Goodies'\n"
                + "                         },\n"
                + "                    operators: [\"equal\"]\n"
                + "                }"
                + "];    "
                + "            $(\"#builder-basic\").queryBuilder({\n"
                + "                plugins: [\"sortable\", \"bt-tooltip-errors\"],\n"
                + "                filters: filters\n"
                + "            });\n"
                + "            \n"
                + "             //$.unblockUI();"
                + "                ";
    }//EoM    

    public static String createSummaryTable(JSONObject cuberesponse) {
        //create table with summary

        String summary_table = "<table cellpadding='10'>";

        JSONObject summaryjson = cuberesponse.getJSONObject("summary");
        Iterator iter = summaryjson.keys();
        while (iter.hasNext()) {
            String key = (String) iter.next();
            String value = summaryjson.get(key).toString();

            summary_table += "<tr>";
            summary_table += "<td>" + key + "</td>";
            summary_table += "<td>" + value + "</td>";
            summary_table += "</tr>";
        }

        summary_table += "</table>";
        System.out.println("summary_table:  " + summary_table);
        return summary_table;
    }//EoM 

    public static String getAvailableCubes(String uqasarOLAPDNS) {

        List cubes = retrieveCubes(uqasarOLAPDNS + "/cubes");

        String select = " <form id=\"cubeSelector\" method=\"POST\" action=\"./eu.uqasar.web.pages.reporting.ReportingPage\"><select>";

        for (Object cube : cubes) {

            select += "  <option value=\"" + uqasarOLAPDNS + "/cube/" + cube + "\">" + cube + "</option>";
        }
        select += "</select>\n";

        select += "<input style=\"display:none;\" type=\"text\" name=\"cubeToLoad\" id=\"cubeToLoad\" value=\"" + uqasarOLAPDNS + "/cube/" + cubes.get(0).toString() + "\">\n";

        select += "<input style=\"display:none;\" type=\"submit\" value=\"Submit\"></form>";

        return select;
    }//EoM 


    public static String constructCubeRetrieverURL(String jsonToParse, String cubeendpoint) { 
        
        String firstdrilldown = "";
        String urlToLoad = cubeendpoint+"/aggregate?";

        JSONObject obj = new JSONObject(jsonToParse);
        JSONArray arr = obj.getJSONArray("rules");
        for (int i = 0; i < arr.length(); i++) {
            String id = arr.getJSONObject(i).getString("id");

            if (id.equalsIgnoreCase("drilldown")) {

                String value = arr.getJSONObject(i).getString("value");
                urlToLoad += "&drilldown=" + value;

                if (firstdrilldown.equalsIgnoreCase("")) {
                    firstdrilldown = value;
                }
            } else {
                urlToLoad += "&cut=" + arr.getJSONObject(i).getString("id") + ":" + arr.getJSONObject(i).getString("value");
            }
        }
        
        System.out.println("urlToLoad: "+urlToLoad);
        return urlToLoad;
    }//EoM

    
    public static String constructCubeRetrieverURLDifferently(String selectedRule, String selectedAdditionalRule,
            String cubeendpoint) {

            String firstdrilldown = "";
            String urlToLoad = cubeendpoint + "/aggregate?";

            if (selectedRule.equalsIgnoreCase("drilldown")) {

                urlToLoad += "&drilldown=" + selectedAdditionalRule;

                if (firstdrilldown.equalsIgnoreCase("")) {
                    firstdrilldown = selectedAdditionalRule;

                }
            } else {
                urlToLoad += "&cut=" + selectedRule + ":" + selectedAdditionalRule;
            }

            return urlToLoad;
        }// EoM
}//EoC
