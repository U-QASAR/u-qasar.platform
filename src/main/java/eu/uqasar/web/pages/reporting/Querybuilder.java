package eu.uqasar.web.pages.reporting;

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


import eu.uqasar.util.reporting.Util;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 *
 */
public class Querybuilder extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        JSONObject objToReturn = new JSONObject();
        response.setContentType("text/html;charset=UTF-8");

        String cubeurl = request.getParameter("data[cube]");
        String urlToLoad = Util.constructCubeRetrieverURL(request.getParameter("data[rules]"), cubeurl);
        String urlToLoadAsLink = "<a href='" + urlToLoad + "'>" + urlToLoad + "</a>";

        JSONObject cuberesponse = Util.readJsonFromUrl(urlToLoad);
        JSONObject donutchartJSON = new JSONObject();

        if (cuberesponse.has("error")) {
            System.out.println("Exception during retrieval !");
            objToReturn.put("error", cuberesponse.get("error"));
        } else {

            JSONArray cuberesponse_arr = cuberesponse.getJSONArray("cells");

            if (cuberesponse_arr.length() > 0) {

                System.out.println("cuberesponse_arr");
                System.out.println(cuberesponse_arr);

                List<String> facts = Util.retrieveDimensions(cubeurl + "/model");

                String cube_table = "<table cellpadding='10'>";

                cube_table += "<tr>";
                for (String fact : facts) {

                    if (cuberesponse_arr.optJSONObject(0).has(fact)) {
                        cube_table += "<th>" + fact + "</th>";
                    }

                }
                cube_table += "<th>count</th>";
                cube_table += "</tr>";

                int[] barchartvalues = new int[cuberesponse_arr.length()];

                for (int i = 0; i < cuberesponse_arr.length(); i++) {

                    String factsDescription = "";
                    cube_table += "<tr>";
                    for (String fact : facts) {

                        if (cuberesponse_arr.optJSONObject(i).has(fact)) {

                            String factid = cuberesponse_arr.getJSONObject(i).get(fact).toString();
                            System.out.println(fact + " : " + factid + "\n\n");
                            cube_table += "<td>" + factid + "</td>";
                            factsDescription += fact + ":" + factid + " ";
                        }

                    }

                    if (cuberesponse_arr.getJSONObject(i).has("count")) {
                        int countID = cuberesponse_arr.getJSONObject(i).getInt("count");
                        System.out.println("count : " + countID + "\n\n");
                        cube_table += "<td>" + countID + "</td>";
                        barchartvalues[i] = countID;
                        donutchartJSON.put(factsDescription, countID);
                    }

                    cube_table += "</tr>";

                }

                cube_table += "</table>";

               
                objToReturn.put("cubetable", cube_table);
                objToReturn.put("donutchart", donutchartJSON.toString());

            }
            objToReturn.put("totalcuberesponse", cuberesponse);
            objToReturn.put("cubeurl", urlToLoadAsLink);
            objToReturn.put("summary", Util.createSummaryTable(cuberesponse));
        }

        response.setContentType("text/x-json;charset=UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setStatus(HttpServletResponse.SC_OK);
        System.out.println("objToReturn" + objToReturn);
        response.getWriter().print(objToReturn.toString());
        response.getWriter().flush();
        response.getWriter().close();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
