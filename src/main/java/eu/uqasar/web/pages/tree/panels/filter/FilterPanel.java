package eu.uqasar.web.pages.tree.panels.filter;

import java.util.Arrays;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextFieldConfig;
import eu.uqasar.model.lifecycle.LifeCycleStage;
import eu.uqasar.model.tree.QualityStatus;
import eu.uqasar.web.i18n.Language;
import java.util.Date;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public abstract class FilterPanel extends Panel {

	private static final long serialVersionUID = 7542340690884580790L;

	private DateTextField startDateField, endDateField;
	private final DropDownChoice<LifeCycleStage> stageChoice;
	private final DropDownChoice<QualityStatus> statusChoice;
	private final Form<Void> form;
	private final AjaxSubmitLink apply, reset;

	private QualityStatus color;
	private LifeCycleStage stage;
	private Date startDate, endDate;

	public FilterPanel(String id) {
		super(id);
		form = new Form<>("form");
		form.add(stageChoice = new DropDownChoice<>("filter.lcStage.choice",
				new PropertyModel<LifeCycleStage>(this, "stage"),
				Arrays.asList(LifeCycleStage.values())));

		form.add(statusChoice = new DropDownChoice<>("filter.color.choice",
				new PropertyModel<QualityStatus>(this, "color"),
				Arrays.asList(QualityStatus.values()))
		);

		form.add(apply = new AjaxSubmitLink("apply") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FilterPanel.this.applyClicked(target, form);
			}
		});
		form.add(reset = new AjaxSubmitLink("reset") {
			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				FilterPanel.this.resetClicked(target, form);
				resetForm();
				target.add(form);
			}
		});
		add(form);
	}

	public TreeFilterStructure getFilter() {
		return new TreeFilterStructure(this);
	}

	private void resetForm() {
		stageChoice.clearInput();
		statusChoice.clearInput();
		startDateField.clearInput();
		endDateField.clearInput();
		stageChoice.setModelObject(null);
		statusChoice.setModelObject(null);
		startDateField.setModelObject(null);
		endDateField.setModelObject(null);
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		form.addOrReplace(startDateField = newDateTextField("filter.startDate",
				new PropertyModel<Date>(this, "startDate")));
		form.addOrReplace(endDateField = newDateTextField("filter.endDate",
				new PropertyModel<Date>(this, "endDate")));
	}

	private DateTextField newDateTextField(final String id, IModel<Date> model) {
		Language language = Language.fromSession();
		DateTextFieldConfig config = new DateTextFieldConfig()
				.withFormat(language.getDatePattern())
				.withLanguage(language.getLocale().getLanguage())
				.allowKeyboardNavigation(true).autoClose(false)
				.highlightToday(true).showTodayButton(true);
		DateTextField dateTextField = new DateTextField(id, model, config);
		dateTextField.add(new AttributeAppender("placeHolder", language
				.getLocalizedDatePattern()));
		return dateTextField;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public LifeCycleStage getLCStage() {
		return stage;
	}

	public QualityStatus getQualityStatus() {
		return color;
	}

	public abstract void applyClicked(AjaxRequestTarget target, Form<?> form);

	public abstract void resetClicked(AjaxRequestTarget target, Form<?> form);
}
