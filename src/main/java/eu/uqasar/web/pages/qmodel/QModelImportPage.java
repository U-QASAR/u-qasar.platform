package eu.uqasar.web.pages.qmodel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBException;

import org.apache.wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.file.File;
import org.apache.wicket.util.lang.Bytes;
import org.codehaus.jackson.JsonProcessingException;
import org.jboss.solder.logging.Logger;
import org.joda.time.DateTime;

import eu.uqasar.adapter.exception.uQasarException;
import eu.uqasar.model.company.Company;
import eu.uqasar.model.qmtree.QModel;
import eu.uqasar.model.user.User;
import eu.uqasar.service.QMTreeNodeService;
import eu.uqasar.service.company.CompanyService;
import eu.uqasar.util.io.importer.QModelJsonParser;
import eu.uqasar.util.io.importer.QModelXmlDomParser;
import eu.uqasar.web.UQasar;
import eu.uqasar.web.pages.BasePage;
import eu.uqasar.web.pages.qmtree.qmodels.QModelViewPage;

public class QModelImportPage extends BasePage {

	/**
	 *
	 */
	private static final long serialVersionUID = -5813192421848847482L;

	private static final Logger logger = Logger.getLogger(QModelImportPage.class);

	private static final String XML_CONTENT = "text/xml";
	private static final String OCT_CONTENT = "application/octet-stream";
	private static final String JSON_CONTENT = "application/json";
	private static final int MAX_SIZE = 100;

	private String errorMessage = "";

	private final FileUploadField file;

	private QModel qm = null;
	private Company company;    
	private User user;    

	@Inject
	private QMTreeNodeService qmodelService;
	
	@Inject
	private CompanyService companyService;

	public QModelImportPage(PageParameters parameters) {
		super(parameters);
		logger.info("QModelImportPage::QModelImportPage start");
		final Form<?> form = new Form<Void>("form") {

			/**
			 *
			 */
			private static final long serialVersionUID = 4949407424211758709L;

			@Override
			protected void onSubmit() {
				logger.info("QModelImportPage::onSubmit starts");
				errorMessage = "";

				try {

					FileUpload upload = file.getFileUpload();
					if (upload == null) {
						errorMessage = "qmodel.empty.file.error";
						logger.info("QModelImportPage::onSubmit no file uploaded");
					} else {
						logger.info("QModelImportPage::onSubmit some file uploaded");

						if (upload.getSize() > Bytes.kilobytes(MAX_SIZE).bytes()) {
							errorMessage = "qmodel.max.file.error";
							logger.info("QModelImportPage::onSubmit MAX_SIZE size" + upload.getSize());
						} else {
							logger.info("QModelImportPage::onSubmit file name " + upload.getClientFileName()
									+ " File-Size: " + Bytes.bytes(upload.getSize()).toString()
									+ "content-type " + upload.getContentType());

							user = UQasar.getSession().getLoggedInUser();    
							if (user.getCompany() != null) {
								company = companyService.getById(user.getCompany().getId());
							}
							
						    if (upload.getContentType() != null && upload.getContentType().equals(XML_CONTENT)) {
								//parsing
								qm = parse(upload, true);
							} else if (upload.getContentType() != null && (upload.getContentType().equals(JSON_CONTENT)||upload.getContentType().equals(OCT_CONTENT))) {
								//json candidate
								qm = parse(upload, false);
							} else {
								//file not valid
								errorMessage = "qmodel.type.file.error";
							}
						}
					}
					
					if (qm != null) {
						qm.setUpdateDate(DateTime.now().toDate());
						if (qm.getCompanyId()!=0){
							qm.setCompany(companyService.getById(qm.getCompanyId()));
						} else {
							if (company != null){
								qm.setCompany(company);
								qm.setCompanyId(company.getId());
							}
						}
						qm = (QModel) qmodelService.create(qm);
					}
				} catch (uQasarException ex) {
					if (ex.getMessage().contains("nodeKey")){
						errorMessage = "qmodel.key.unique";
					}
				} catch (JsonProcessingException ex) {
					logger.info("JsonProcessingException----------------------------------------");
					if (ex.getMessage().contains("expecting comma to separate ARRAY entries")) {
						errorMessage = "qmodel.json.parse.error";
					} else if (ex.getMessage().contains("Unexpected character")) {
						errorMessage = "qmodel.json.char.error";
					} else if(ex.getMessage().contains("Can not construct instance")) {
						errorMessage = "qmodel.json.enum.error";
					} else {
						logger.info("JsonProcessingException----------------------------");
						errorMessage = "qmodel.xml.parse.error";						
					}
				} catch (JAXBException ex) {
					logger.info("JAXBException----------------------------");
					errorMessage = "qmodel.xml.parse.error";
				} catch (Exception ex) {
					logger.info("IOException----------------------------");
					errorMessage = "qmodel.import.importError";
				} finally {
					PageParameters parameters = new PageParameters();
					if (null != errorMessage && !errorMessage.equals("")) {
						logger.info("Attaching error message");
						parameters.add(QModelImportPage.LEVEL_PARAM, FeedbackMessage.ERROR);
						parameters.add(QModelImportPage.MESSAGE_PARAM, getLocalizer().getString(errorMessage, this));
						setResponsePage(QModelImportPage.class, parameters);
					} else {
						
						
						logger.info("qmodel successfully created: redirection");
						//qmodel successfully created: redirection
						parameters.add(BasePage.LEVEL_PARAM, FeedbackMessage.SUCCESS);
						parameters.add(BasePage.MESSAGE_PARAM, getLocalizer().getString("treenode.imported.message", this));
						parameters.add("qmodel-key", qm.getNodeKey());
						parameters.add("name", qm.getName());
						setResponsePage(QModelViewPage.class, parameters);
					}
				}
			}
		};

		// create the file upload field
		file = new FileUploadField("file");
		form.addOrReplace(file);

		form.add(new Label("max", new AbstractReadOnlyModel<String>() {
			/**
			 *
			 */
			private static final long serialVersionUID = 3532428309651830468L;

			@Override
			public String getObject() {
				return (Bytes.kilobytes(MAX_SIZE)).toString();
			}
		}));

		// add progress bar
		form.add(new UploadProgressBar("progress", form, file));

		ServletContext context = ((WebApplication) getApplication()).getServletContext();
		// Download xml example
		File filexml = new File(context.getRealPath("/assets/files/qmodel.xml"));
		DownloadLink xmlLink = new DownloadLink("xmlLink", filexml);
		form.add(xmlLink);

		// Download json example
		File filejson = new File(context.getRealPath("/assets/files/qmodel.json"));
		DownloadLink jsonLink = new DownloadLink("jsonLink", filejson);
		form.add(jsonLink);

		add(form);

		logger.info("QModelImportPage::QModelImportPage ends");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem
				.forUrl("assets/css/user/user-panel.css"));
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.uqasar.web.BasePage#getPageTitleModel()
	 */
	@Override
	protected IModel<String> getPageTitleModel() {
		return new StringResourceModel("page.title", this, null);
	}

	/**
	 * Method to invoke parser xml instance.
	 *
	 * @Param upload File uploaded ready to parse
	 * @return QModel created
	 * @throws IOException 
	 * @throws JAXBException
	 * @throws uQasarException
	 */
	private QModel parse(FileUpload upload, boolean xml) throws IOException, JAXBException, uQasarException {

		QModel qmodel;
		File newFile = new File(upload.getClientFileName());
		if (newFile.exists()) {
			newFile.delete();
		}

		newFile.createNewFile();
		upload.writeTo(newFile);

		//Parse file and save info
		if (xml) {
			qmodel = QModelXmlDomParser.parseFile(newFile);
		} else {
			qmodel = QModelJsonParser.parseFile(newFile);
		}
		
		List<String> nodeKeyList = qmodelService.getAllNodeKeys();
		
			
		if (Collections.frequency(nodeKeyList, qmodel.getNodeKey()) > 0){
			throw new uQasarException("nodeKey");
		}
		
		newFile.delete();
		return qmodel;
	}

}
