package eu.uqasar.web.pages.tree.projects;

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


import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Bytes;
import org.jboss.solder.logging.Logger;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.tree.Project;
import eu.uqasar.model.user.User;
import eu.uqasar.service.tree.TreeNodeService;
import eu.uqasar.service.user.UserService;
import eu.uqasar.util.io.importer.QProjectJsonParser;
import eu.uqasar.util.io.importer.QProjectXmlDomParser;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.qmodel.QModelImportPage;

public class ProjectImportPage extends BasePage {

    @Inject
    private TreeNodeService treeNodeService;
    @Inject
    private Logger logger;
    @Inject
    private UserService userService;
    private User user;

    private static final long serialVersionUID = -5446363200890193417L;
    private static final int MAX_SIZE = 100;
    private static final String XML_CONTENT = "text/xml";
    // private static final String JSON_CONTENT = "application/octet-stream";

    private boolean error = false;

    private final FileUploadField upload;
    
    private String errorMessage = "";

    public ProjectImportPage(PageParameters parameters) {
        super(parameters);

        upload = new FileUploadField("file");

        final Form<?> form = new Form<Void>("form") {

            private static final long serialVersionUID = -8541751079487127243L;

            @Override
            protected void onSubmit() {
                Project p = null;
                try {
                    FileUpload file = upload.getFileUpload();
                    if (file == null) {
                        logger.info("File upload failed");
                        error = true;
                    } else {
                        if (file.getSize() > Bytes.kilobytes(MAX_SIZE).bytes()) {
                            logger.info("File is too big");
                            error = true;
                        } else {
                            String content = file.getContentType();
                            if (content != null) {
                                logger.info("Parser called");
                                p = parseProject(file, content);
                                p = (Project) treeNodeService.create(p);

                                // set company to qmodel
                                user = UQasar.getSession().getLoggedInUser();
                                if (user != null && user.getCompany() != null) {
                                   	p.setCompany(user.getCompany());
                                }
                            } else {
                                logger.info("Upload file is invalid");
                                error = true;
                            }
                        }
                    }
                } catch (uQasarException ex) { 
                	if (ex.getMessage().contains("nodeKey")){
                		error = true;
                		logger.info("duplicated nodeKey");
                		errorMessage = "import.key.unique";
					}
            	} catch (Exception ex) {
                    error = true;
                    logger.error(ex.getMessage(), ex);
                } finally {
                    PageParameters parameters = new PageParameters();
                    
                    
                    if (error) {
                        parameters.add(BasePage.LEVEL_PARAM, FeedbackMessage.ERROR);
                        
                        if (errorMessage != null && errorMessage.contains("key")) {
                        	parameters.add(QModelImportPage.MESSAGE_PARAM, getLocalizer().getString(errorMessage, this));
                        } else {
                        	parameters.add(BasePage.MESSAGE_PARAM, getLocalizer().getString("import.importError", this));
                        }
                        setResponsePage(ProjectImportPage.class, parameters);
                    } else {
                        parameters.add(BasePage.LEVEL_PARAM, FeedbackMessage.SUCCESS);
                        parameters.add(BasePage.MESSAGE_PARAM, getLocalizer().getString("import.importMessage", this));
                        // TODO what if p == null?
                        parameters.add("project-key", p != null ? p.getNodeKey() : "?");
                        setResponsePage(ProjectViewPage.class, parameters);
                    }
                }
            }
        };
        form.add(upload);
        form.setMaxSize(Bytes.kilobytes(MAX_SIZE));
        form.add(new Label("max", new AbstractReadOnlyModel<String>() {

            private static final long serialVersionUID = 3532428309651830468L;

            @Override
            public String getObject() {
                return form.getMaxSize().toString();
            }
        }));

        form.add(new UploadProgressBar("progress", form, upload));
        form.add(new Button("uploadButton", new StringResourceModel("upload.button", this, null)));

        add(form);

    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssReferenceHeaderItem.forUrl("assets/css/user/user-panel.css"));
    }

    @Override
    protected IModel<String> getPageTitleModel() {
        return new StringResourceModel("page.title", this, null);
    }

    private Project parseProject(FileUpload upload, String content) throws uQasarException, Exception {
        File file = new File(upload.getClientFileName());
        if (file.exists()) {
            file.delete();
        }
        file.createNewFile();
        upload.writeTo(file);

        Project p = content.equals(XML_CONTENT) ? QProjectXmlDomParser.parseFile(file) : QProjectJsonParser.parseFile(file);

        
        List<String> nodeKeyList = treeNodeService.getAllNodeKeys();
		
		
		if (Collections.frequency(nodeKeyList, p.getNodeKey()) > 0){
			throw new uQasarException("nodeKey");
		}
		
        
        file.delete();
        return p;
    }

}
