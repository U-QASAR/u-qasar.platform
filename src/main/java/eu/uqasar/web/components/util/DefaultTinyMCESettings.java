package eu.uqasar.web.components.util;

import org.apache.wicket.Session;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.UrlResourceReference;

import wicket.contrib.tinymce.settings.AdvListPlugin;
import wicket.contrib.tinymce.settings.Button;
import wicket.contrib.tinymce.settings.ContextMenuPlugin;
import wicket.contrib.tinymce.settings.DateTimePlugin;
import wicket.contrib.tinymce.settings.FullScreenPlugin;
import wicket.contrib.tinymce.settings.PastePlugin;
import wicket.contrib.tinymce.settings.SearchReplacePlugin;
import wicket.contrib.tinymce.settings.TablePlugin;
import wicket.contrib.tinymce.settings.TinyMCESettings;
import wicket.contrib.tinymce.settings.WordcountPlugin;

public class DefaultTinyMCESettings extends TinyMCESettings {

	private static final long serialVersionUID = 3696448415421321646L;

	private DefaultTinyMCESettings() {
		super(TinyMCESettings.Theme.advanced, Language.valueOf(Session.get()
				.getLocale().getLanguage()));
		// Register non-buttuon plugins
		register(new ContextMenuPlugin());
		register(new WordcountPlugin());
		register(new AdvListPlugin());

		add(Button.fontselect, TinyMCESettings.Toolbar.first,
				TinyMCESettings.Position.after);
		add(Button.fontsizeselect, TinyMCESettings.Toolbar.first,
				TinyMCESettings.Position.after);

		// second toolbar
		PastePlugin pastePlugin = new PastePlugin();
		SearchReplacePlugin searchReplacePlugin = new SearchReplacePlugin();
		DateTimePlugin dateTimePlugin = new DateTimePlugin();
		dateTimePlugin.setDateFormat("%m-%d-%Y");
		dateTimePlugin.setTimeFormat("%H:%M");

		add(Button.cut, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(Button.copy, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(pastePlugin.getPasteButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(pastePlugin.getPasteTextButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(pastePlugin.getPasteWordButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(Button.separator, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(searchReplacePlugin.getSearchButton(),
				TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		add(searchReplacePlugin.getReplaceButton(),
				TinyMCESettings.Toolbar.second, TinyMCESettings.Position.before);
		add(Button.separator, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.before);
		add(dateTimePlugin.getDateButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.after);
		add(dateTimePlugin.getTimeButton(), TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.after);
		add(Button.separator, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.after);
		add(Button.forecolor, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.after);
		add(Button.backcolor, TinyMCESettings.Toolbar.second,
				TinyMCESettings.Position.after);

		// third toolbar
		TablePlugin tablePlugin = new TablePlugin();
		// EmotionsPlugin emotionsPlugin = new EmotionsPlugin();
		// IESpellPlugin iespellPlugin = new IESpellPlugin();
		FullScreenPlugin fullScreenPlugin = new FullScreenPlugin();
		// DirectionalityPlugin directionalityPlugin = new
		// DirectionalityPlugin();
		add(tablePlugin.getTableControls(), TinyMCESettings.Toolbar.third,
				TinyMCESettings.Position.before);
		add(Button.separator, TinyMCESettings.Toolbar.third,
				TinyMCESettings.Position.after);
		// add(emotionsPlugin.getEmotionsButton(),
		// TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		// add(iespellPlugin.getIespellButton(),
		// TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		// add(Button.separator, TinyMCESettings.Toolbar.third,
		// TinyMCESettings.Position.after);
		// add(directionalityPlugin.getLtrButton(),
		// TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		// add(directionalityPlugin.getRtlButton(),
		// TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);
		// add(Button.separator, TinyMCESettings.Toolbar.third,
		// TinyMCESettings.Position.after);
		add(fullScreenPlugin.getFullscreenButton(),
				TinyMCESettings.Toolbar.third, TinyMCESettings.Position.after);

		// other settings
		setToolbarAlign(TinyMCESettings.Align.left);
		setToolbarLocation(TinyMCESettings.Location.top);
		setStatusbarLocation(TinyMCESettings.Location.bottom);
		setResizing(true);
		disableButton(Button.styleselect);

		// custom settings
		// String styleFormats = "style_formats : [" +
		// "{title : 'Bold text', inline : 'b'},"
		// +
		// "{title : 'Red text', inline : 'span', styles : {color : '#ff0000'}},"
		// +
		// "{title : 'Red header', block : 'h1', styles : {color : '#ff0000'}},"
		// + "{title : 'Example 1', inline : 'span', classes : 'example1'},"
		// + "{title : 'Example 2', inline : 'span', classes : 'example2'}," +
		// "{title : 'Table styles'},"
		// + "{title : 'Table row 1', selector : 'tr', classes : 'tablerow1'}]";

		String renderContextRelativeUrl = RequestCycle.get().getUrlRenderer()
				.renderContextRelativeUrl("assets/css/tinymce-adjustments.css");
		setContentCss(new UrlResourceReference(Url
				.parse(renderContextRelativeUrl)));
	}

	public static DefaultTinyMCESettings get() {
		return new DefaultTinyMCESettings();
	}

}
