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
/**
 ** ***************************************
 ** U-QASAR TOURS
 ** based on Bootstrap Tour, http://bootstraptour.com/
 ** *************************************** 
 *
**/


// INITS
var welcome, project, qmodel, search, notify, languageContent, admin, user = ""; 
var titleWelcome, titleProject, titleQmodel, titleSearch, titleNotify, titleLanguage, titleAdmin, titleUser = "";
var projectTree, treeOptions, qualityStatus, projectInformation = "";
var titleTree, titleOptions, titleStatus, titleInformation= "";
var titleAdd, titleUpdate, titleShare, titleDelete, titleReset="";
var addWidget, updateDashboard, deleteDashboard, shareDashboard, resetDashboard, exportDashboard = "";
var titleQMTree, titleQMOptions = "";
var qmodelTree, qmtreeOptions = "";
var prev, next, end = "";
// URLS
var serverUrl = window.location.origin;
var url = window.location.href;
var basicUrl = serverUrl+"/uqasar/?";
var projectUrl = serverUrl+"/uqasar/projects/";
var dashboardurl = "http://localhost:8080/uqasar/dashboards";
var qmodelUrl = "http://localhost:8080/uqasar/qmodels/Quality%20Model%20A,%20U-QASAR";


/**
 ** ***************************************
 ** jQUERY Begin
 ** *************************************** 
**/
$(document).ready(function() {
		
	/**
	 ** ***************************************
	 ** INTERNATIONALIZATION
	 ** *************************************** 
	**/
	var language = getCurrentLanguage();
	
	switch(language){
		case "de": 	setGermanText(); 	break;
		case "en": 	setEnglishText();	break;	
		case "fi": 	setFinnishText();	break;
		default:	setEnglishText();
	}
	
	/**
	 ** ***************************************
	 ** HTML TEMPLATE FOR POPOVER
	 ** *************************************** 
	**/
	var tourTemplate = "<div class='popover tour mypopover' > " +
							"<div class='arrow'></div>" +
							"<h3 class='popover-title mypopover underlined' ></h3>" +
							"<div class='popover-content'></div>" +
							"<div class='popover-navigation'>" +
								"<div class='btn-group'>" +
									"<button class='btn btn-sm btn-success' data-role='prev'>" + prev + "</button>" +
									"<span data-role='separator'>|</span>" +
									"<button class='btn btn-sm btn-success' data-role='next'>" + next + "</button>" +
								"</div>" +
									"<button class='btn btn-sm btn-success' data-role='end'>" + end + "</button>" +
						"</div>";
	
	/**
	 ** ***************************************
	 ** TOUR 1: BASIC TOUR ON MAINPAGE
	 ** *************************************** 
	**/
	var tour = new Tour({
	  debug: true,
	  storage: window.localStorage,
	  template: tourTemplate,
  	   steps: [
				  { element: "#brand",  		title: titleWelcome,   	content: welcome,   		placement: "bottom"	  },
				  { element: "#projectsMenu ", 	title: titleProject,	content: project,			placement: "bottom"	  },
				  {	element: "#qmodelMenu",		title: titleQmodel,		content: qmodel,			placement: "bottom"	  },
				  { element: "#searchMenu", 	title: titleSearch,		content: search,			placement: "bottom"	  },
				  { element: "#notifyMenu",	  	title: titleNotify,	  	content: notify,			placement: "bottom"   },
				  { element: "#languagesMenu",  title: titleLanguage,   content: languageContent, 	placement: "bottom"   },
				  { element: "#adminsMenu",	    title: titleAdmin,		content: admin,	    		placement: "bottom"	  },
				  {	element: "#usersMenu",		title: titleUser,		content: user,				placement: "bottom"	  }
			  ]
	});
	
	// load only on mainpage
	
	if(url.indexOf(basicUrl) != -1){
		tour.init();
		tour.start();	
	}
	
	
	
	
	
	
	
	/**
	 ** ***************************************
	 ** TOUR 2: PROJECT TOUR ON U-QASAR DETAILS PAGE
	 ** *************************************** 
	**/
	var projectTour = new Tour({
		debug: true,
		storage: window.localStorage,
		template: tourTemplate,
		steps:  [
			        { element: $('#projectTree div').get(0),  	title: titleTree,  			content: projectTree,  			placement: "bottom"	},
			        { element: "#projectTree",  			 	title: titleOptions,   		content: treeOptions,   		placement: "bottom"	},
		        	{ element: "#qualityStatus",  				title: titleStatus,   		content: qualityStatus, 		placement: "bottom"	},
		        	{ element: "#team",	
		        	  // change template for this step and remove "arrow"
		        	  template: "<div class='popover tour mypopover' > " +
									"<h3 class='popover-title mypopover underlined' ></h3>" +
									"<div class='popover-content'></div>" +
									"<div class='popover-navigation'>" +
										"<div class='btn-group'>" +
											"<button class='btn btn-sm btn-success' data-role='prev'>" + prev + "</button>" +
											"<span data-role='separator'>|</span>" +
											"<button class='btn btn-sm btn-success' data-role='next'>" + next + "</button>" +
										"</div>" +
											"<button class='btn btn-sm btn-success' data-role='end'>" + end + "</button>" +
								"</div>",	
		        	  title: titleInformation,	
		        	  content: projectInformation,  	
		        	  placement: 'bottom'} 
		        ]
	});
	
	// load only on default project page of U-QASAR, but not(!) on the edit page
	if((url.indexOf(projectUrl) !== -1) && (url.indexOf("edit") === -1)){
		projectTour.init();
		projectTour.start();	
	}
	
	
	
	/**
	 ** ***************************************
	 ** TOUR 3: dashboard TOUR ON U-QASAR defult dashboard page 
	 ** *************************************** 
	**/
	var dashboardTour = new Tour({
		debug: true,
		storage: window.localStorage,
		template: tourTemplate,
		steps:  [
			        { element: "#addwidget",  			 	title: titleAdd,   			content: addWidget,   			placement: "bottom"	},
			        { element: "#updatedashboard",  		title: titleUpdate,   		content: updateDashboard,   	placement: "bottom"	},
		        	{ element: "#sharedashboard",  			title: titleShare,   		content: shareDashboard, 		placement: "bottom"	},
		        	{ element: "#resetdashboard",	        title: titleReset,   		content: resetDashboard, 		placement: "bottom"	},
		        	{ element: "#deletedashboard",	        title: titleDelete,   		content: deleteDashboard, 		placement: "bottom"	}, 
		        	{ element: "#exportdashboard",	        title: titleExport,   		content: exportDashboard, 		placement: "bottom"	}, 
		        ]
	});
	
	// load only on default project page of U-QASAR, but not(!) on the add page
	if((url.indexOf(dashboardurl) !== -1) && (url.indexOf("create") === -1)){
		dashboardTour.init();
		dashboardTour.start();	
	}
	
	
	
	
	
	
	/**
	 ** ***************************************
	 **TOUR 4: QMODEL TOUR ON U-QASAR DETAILS PAGE
	 
	 ** *************************************** 
	**/
	var qmodelTour = new Tour({
		debug: true,
		storage: window.localStorage,
		template: tourTemplate,
		steps:  [
			        { element: $('#qmodeltree div').get(0),  	title: titleQMTree,  			content: qmodelTree,  			placement: "bottom"	},
			        { element: "#qmodeltree",  			 	title: titleQMOptions,   		content: qmtreeOptions,   		placement: "bottom"	}
		       
		        ]
	});
	
	// load only on default project page of U-QASAR, but not(!) on the edit page
	if((url.indexOf(qmodelUrl) !== -1) && (url.indexOf("edit") === -1)){
		qmodelTour.init();
		qmodelTour.start();	
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 ** ***************************************
	 ** REACTIVATION OF THE TOUR
	 ** *************************************** 
	**/
	$('#takeTour').css("cursor", "pointer");
	$('#takeTour').on('click', function(){
		if((url.indexOf(projectUrl) !== -1) && (url.indexOf("edit") === -1)){
			projectTour.restart();
		}
		else if((url.indexOf(dashboardurl) !== -1) && (url.indexOf("create") === -1)){
			dashboardTour.restart();
		}
		else if((url.indexOf(qmodelUrl) !== -1) && (url.indexOf("edit") === -1)){
			qmodelTour.restart();
		}
		else{
			tour.restart();
		}
	});
	
});


/**
 ** ***************************************
 ** HELPER FUNCTIONS
 ** *************************************** 
**/

/**
 * getCurrentLanguage()
 * retrieves the current language of the platform
 */
function  getCurrentLanguage(){
	var language = "";
	var nrOfLangs = $(".languages").children().length;

	for (var lan=0; lan < nrOfLangs; lan++){	
		var className = $(".languages a").get(lan).className;
		if (className.match("active")  != null){
			var active =  className.match("active").input;
			if (active.indexOf("de") != -1){
				language = "de";
			} else if (active.indexOf("en") != -1) {
				language = "en";
			} else {
				language = "fi";
			}
		}
	}
	return language;
}


/**
 ** ***************************************
 ** Set text for different languages
 ** *************************************** 
**/
/**
 * setFinnishText()
 */
function setFinnishText(){
	/**
	 * BASIC TOUR
	 */
	welcome = "Moi! <br/> Tämä kierros opastaa perusnavigointiin U-QASAR-alustalla.";
	project = "Projektit-valikossa voit tarkastella ja editoida käynnissäolevia projekteja, luoda uusia ja tuoda/viedä projekteja.";
	qmodel = "Laatumallit-valikossa voit tarkastella ja editoida nykyisiä laatumalleja, luoda uusia ja tuoda/viedä niitä.";
	search = "Jos haluat etsiä tietoja alustalta, käytä tätä hakukenttää.";
	notify = "Jos sinulle on saapunut ilmoituksia, sinulle näytetään ilmoitus täällä!";
	languageContent = "Vaihda alustan kieli tässä.";
	admin = "Koska olet tällä hetkellä kirjautuneena <i>Järjestelmänvalvojana</i>, löydät vaihtoehtoja U-QASAR-alustan konfigurointiin ja kustomointiin, kuten käyttäjä, yritys ja metadata-tietojen hallintaan ja paljon muuta.";
	user = "Tässä valikossa löydät käyttäjäprofiilit ja voit editoida niitä ja luoda ja editoida personoituja näkymiä (kojelautoja).  <br/> <br/> " +
			"Jos haluat tehdä U-QASAR-kierroksen, tällä hetkellä on useita kierroksia valittavana: <br/>" +
			"<ul class='tour'>" +
				"<li><i class='icon-home'/><span>U-QASAR-perusteet (tällä hetkellä aktiivinen) </span></li>" +
				"<li><i class='icon-sitemap'/><span>Projektikierros</span></li>" +
			"</ul>" +
			"Voit aloittaa tai toistaan sivukohtaisen kierroksen milloin vain, klikkaa vain yksinkertaisesti tässä menussa <i class='icon-info-sign'/><b>"+ $('#takeTour').text().trim() +"</b> ollessasi halutulla sivulla.";

	// TITLES		
	titleWelcome = "TERVETULOA U-QASAR:iin";
	titleProject = "PROJEKTIT";
	titleQmodel = "LAATUMALLIT";
	titleSearch = "ETSI";
	titleNotify = "ILMOITUKSET";
	titleLanguage = "KIELI";
	titleAdmin = "JÄRJESTELMÄVALVOJA";
	titleUser = "KÄYTTÄJÄ";
	/**
	 * PROJECT TOUR
	 */
	projectTree = "Tämän avattavan/suljettavan puurakenteen ymmärtämiseksi huomioi seuraava selitys:" +
					"<table>" +
						"<tr><td><i class='icon-sitemap'/> Laatuprojekti ('Quality Model')</td></tr>" +
						"<tr><td style='text-indent:10px'><i class='icon-tasks'/> Laatutavoite</td></tr>" +
						"<tr><td style='text-indent:20px'><i class='icon-dashboard'/> Laatuindikaattori</td></tr>" +
						"<tr><td style='text-indent:30px'><i class='icon-signal'/> Metriikka</td></tr>" +
					"</table><br/>"+
					"Kukin puun solmu on avattavissa/suljettavissa kolmioikonin kautta ja se edustaa klikattavaa linkkiä vastaavalle puun solmun sisältösivulle.";
	treeOptions = "Käytä näitä pikatoimintopainikkeita " +
					"<ul style='list-style:none'>" +
						"<li> <i class='icon-plus'/> uusien projektien luomiseen, </li> " +
						"<li> <i class='icon-pencil'/> nykyisen projektin muokkaukseen,</li>" +
						"<li> <i class='icon-chevron-sign-down'/> puun solmujen siirtämiseen puun sisällä,</li>" +
						"<li> <i class='icon-trash'/> nykyisen projektin poistamiseen.</li>" +
					"</ul>";
	qualityStatus = "Huomioi laatuprojektin nykyinen arvo tässä. Väri indikoi laatutasoa (punainen/keltainen/vihreä).";
	projectInformation = "Huomioi projektille spefinen tieto, johon mm. sisältyvät osallistuvat tiimin jäsenet, käytetty metadata ja laatutrendikaaviot tällä alueella.";
	titleTree = "PROJEKTIPUU";
	titleOptions = "VAIHTOEHDOT";
	titleStatus = "LAATUSTATUS";
	titleInformation= "PROJEKTITIEDOT";

	/**
	 * dashboard TOUR
	 */
	addWidget = "Tästä menusta voit lisätä erilaisia widgettejä nykyiselle kojelaudalle. Voit valita seuraavista widgeteistä:" +
					"<table>" +
						"<tr><td> Projektin laatu -widget</td></tr>" +
						"<tr><td> JIRA-widget</td></tr>" +
						"<tr><td> Sonar-widget</td></tr>" +
						"<tr><td> TestLink-widget</td></tr>" +
						"<tr><td> Projektipuu-widget</td></tr>" +
						"<tr><td> Poikkeamat datassa -widget</td></tr>" +
						"<tr><td> JIRA tekninen velka -widget</td></tr>" +
				  "</table><br/>";
	updateDashboard = "Valitse Päivitä päivittääksesi nykyisen kojelaudan ja sillä olevat widgetit."; 
	shareDashboard = "Valitse Jaa jakaaksesi nykyisen kojelaudan muiden käyttäjien kanssa.";
	resetDashboard = "Valitse Nollaa nollataksesi nykyisen kojelaudan (tyhjentää kojelaudan kaikista widgeteistä).";
	deleteDashboard = "Valitse Poista tuhotaksesi nykyisen kojelaudan.";
	exportDashboard = "Valitse Vie luodaksesi PDF-raportin kojelaudan sisällöstä."
	titleAdd = "LISÄÄ WIDGET";
	titleUpdate = "PÄIVITÄ";
	titleShare = "JAA KOJELAUTA";
	titleReset = "NOLLAA KOJELAUTA";
	titleDelete = "POISTA KOJELAUTA";
	titleExport = "VIE KOJELAUTA";
	
	/**
	 * QMODEL TOUR
	 */
	qmodelTree = "Tämän avattavan/suljettavan laatumallin puurakenteen ymmärtämiseksi huomioi seuraavat:" +
					"<table>" +
						"<tr><td><i class='icon-sitemap'/> Laatumalli</td></tr>" +
						"<tr><td style='text-indent:10px'><i class='icon-tasks'/> Laatutavoite</td></tr>" +
						"<tr><td style='text-indent:20px'><i class='icon-dashboard'/> Laatuindikaattori</td></tr>" +
						"<tr><td style='text-indent:30px'><i class='icon-signal'/> Laatumetriikka</td></tr>" +
				  "</table><br/>"+
				  "Kukin laatumallipuun solmu on avattavissa/suljettavissa kolmioikonin kautta ja solmu on klikattava linkki, joka johtaa vastaavalle puun solmun sisältösivulle.";
	qmtreeOptions = "Käytä näitä pikatoimintopainikkeita " +
					"<ul style='list-style:none'>" +
						"<li> <i class='icon-plus'/> uuden Laatumallin luomiseen, </li> " +
						"<li> <i class='icon-pencil'/> olemassaolevan Laatumallin muokkaamiseen,</li>" +
						"<li> <i class='icon-chevron-sign-down'/> Laatumallipuun solmujen siirtämiseen puun sisällä,</li>" +
						"<li> <i class='icon-trash'/> nykyisen Laatumallin poistamiseen.</li>" +
					"</ul>";
	
	titleQMTree = "LAATUMALLIPUU";
	titleQMOptions = "VALINNAT";
	
	// NAVI
	prev = "« Edellinen";
	next = "Seuraava »";
	end = "Lopeta kierros";	
}

/**
 * setEnglishText()
 */
function setEnglishText(){
	/**
	 * BASIC TOUR
	 */
	// TEXT
	welcome = "Hi there! <br/> This tour guides you through the basic navigation of the U-QASAR platform.";
	project = "In the project menu you can observe and edit your currently running projects, create new projects and import/export projects.";
	qmodel = "In the quality model menu you can observe and edit your current quality models, create new ones and import/export quality models.";
	search = "If you want to search the platform, use this search input field to look up your request.";
	notify = "If news have arrived for you, you will get notified here!";
	languageContent = "Change the platform's language here.";
	admin = "Since you are currently logged in as an <i>Administrator</i>, you can find here possibilities for the configuration and customization of the U-QASAR platform, like user, company and metadata management and many more.";
	user = "Under this menu item you can find and edit your user profile and create and edit personalized views (dashboards). <br/><br/> " +
			"If you want to take a U-QASAR tour, there are several tours available at the moment: <br/>" +
			"<ul class='tour'>" +
				"<li><i class='icon-home'/><span>U-QASAR Basics (currently active)</span></li>" +
				"<li><i class='icon-sitemap'/><span>Project Tour</span></li>" +
			"</ul>" +
			"You can start or repeat a page specific tour at any time, just simply click under this menu on <i class='icon-info-sign'/><b>"+ $('#takeTour').text().trim() +"</b> on the page you are at the moment.";
	// TITLES
	titleWelcome = "WELCOME TO U-QASAR";	
	titleProject = "PROJECTS";
	titleQmodel = "QUALITYMODELS";
	titleSearch = "SEARCH";
	titleNotify = "NOTIFICATIONS";
	titleLanguage = "LANGUAGE";
	titleAdmin = "ADMINISTRATOR";
	titleUser = "USER";
	/**
	 * PROJECT TOUR
	 */
	projectTree = "For a better understanding of this foldable tree structure, observe the following general explanation of the tree structure:" +
					"<table>" +
						"<tr><td><i class='icon-sitemap'/> Quality Project ('Quality Model')</td></tr>" +
						"<tr><td style='text-indent:10px'><i class='icon-tasks'/> Quality Objective</td></tr>" +
						"<tr><td style='text-indent:20px'><i class='icon-dashboard'/> Quality Indicator</td></tr>" +
						"<tr><td style='text-indent:30px'><i class='icon-signal'/> Software Metric</td></tr>" +
				  "</table><br/>"+
				  "Each tree node of the tree structure is foldable with the help of the caret sign and represents a clickable link to the corresponding tree node details page.";
	treeOptions = "Use this quick action buttons to " +
					"<ul style='list-style:none'>" +
						"<li> <i class='icon-plus'/> create new projects, </li> " +
						"<li> <i class='icon-pencil'/> edit the current project,</li>" +
						"<li> <i class='icon-chevron-sign-down'/> move tree nodes within the tree,</li>" +
						"<li> <i class='icon-trash'/> delete the current project.</li>" +
					"</ul>";
	qualityStatus = "Observe the current value of the Quality Project here. The color indicates the quality's level (red/yellow/green).";
	projectInformation = "Observe project specific information of your selected project including e.g. the participating team members, used metadata and quality trend charts in this area.";
	titleTree = "PROJECT TREE";
	titleOptions = "OPTIONS";
	titleStatus = "QUALITY STATUS";
	titleInformation= "PROJECT INFORMATION";
	
	/**
	 * dashboard TOUR
	 */
	addWidget = "In this menu you can add many different widgets to the current dashboard. You may add one of the following widgets:" +
					"<table>" +
						"<tr><td> Project Quality Widget</td></tr>" +
						"<tr><td> JIRA Widget</td></tr>" +
						"<tr><td> Sonar Quality Widget</td></tr>" +
						"<tr><td> TestLink Widget</td></tr>" +
						"<tr><td> Project Tree Widget</td></tr>" +
						"<tr><td> Data Deviation  Widget</td></tr>" +
						"<tr><td> JIRA tech Debt Widget</td></tr>" +
				  "</table><br/>";
	updateDashboard = "Use Refresh to update the current Dashboard and the content on it."; 
	shareDashboard = "Use Share to share the current Dashboard with another user(s).";
	resetDashboard = "Use Reset to reset the current Dashboard (empty the Dashboard from all widgets).";
	deleteDashboard = "Use Delete to remove the current Dashboard.";
	exportDashboard = "Use Export to create a PDF report from the Dashboard content."
	titleAdd = "ADD WIDGET";
	titleUpdate = "REFRESH";
	titleShare = "SHARE DASHBOARD";
	titleReset = "RESET DASHBOARD";
	titleDelete = "DELETE DASHBOARD";
	titleExport = "EXPORT DASHBOARD";
	
	/**
	 * QMODEL TOUR
	 */
	qmodelTree = "For a better understanding of this foldable qmodel tree structure, observe the following general explanation of the qmodel tree structure:" +
					"<table>" +
						"<tr><td><i class='icon-sitemap'/> Quality Model</td></tr>" +
						"<tr><td style='text-indent:10px'><i class='icon-tasks'/> Quality Objective</td></tr>" +
						"<tr><td style='text-indent:20px'><i class='icon-dashboard'/> Quality Indicator</td></tr>" +
						"<tr><td style='text-indent:30px'><i class='icon-signal'/> Software Metric</td></tr>" +
				  "</table><br/>"+
				  "Each qmtree node of the qmtree structure is foldable with the help of the caret sign and represents a clickable link to the corresponding tree node details page.";
	qmtreeOptions = "Use the quick action buttons to " +
					"<ul style='list-style:none'>" +
						"<li> <i class='icon-plus'/> create a new Quality Model, </li> " +
						"<li> <i class='icon-pencil'/> edit the current Quality Model,</li>" +
						"<li> <i class='icon-chevron-sign-down'/> move QModel tree nodes within the QModel tree,</li>" +
						"<li> <i class='icon-trash'/> delete the current Quality Model.</li>" +
					"</ul>";
	
	titleQMTree = "QMODEL TREE";
	titleQMOptions = "OPTIONS";
	
	
	
	// NAVI
	prev = "« Prev";
	next = "Next »";
	end = "End Tour";
}

/**
 * setGermanText()
 */
function setGermanText(){
	/**
	 * BASIC TOUR
	 */
	// TEXT
	welcome = "Hallo! <br/> Diese Tour soll dir die grundlegenden Funktionalitäten hinter den Navigationspunkten auf der U-QASAR Plattform vorstellen. ";
	project = "Im Projektmenü kannst Du deine aktuell laufenden Projekte sehen und bearbeiten, neue Projekte erstellen sowie Projekte importieren und exportieren.";		
	qmodel = "Im Menü zu den Qualitätsmodellen kannst Du deine aktuellen Qualitätsmodelle sehen und bearbeiten, neue erstellen sowie Qualitätsmodelle importieren und exportieren.";
	search = "Nutze diese Eingabemaske, um die Plattform zu durchsuchen.";
	notify = "Wenn Neuigkeiten für dich eintreffen, wirst du hier benachrichtigt.";
	languageContent = "Ändere hier die Sprache der Platfform.";
	admin = "Da du zur Zeit als <i>Administrator</i> angemeldet bist, findest du hier Möglichkeiten zur Konfiguration und weiteren Anpassung der U-QASAR Plattform, wie z.B. Benutzer-, Firmen- und Metadatenverwaltung und vieles Weitere.";
	user = "Unter diesem Punkt kannst du dein Benutzerprofil finden und editieren sowie personalisierte Ansichten (Dashboards) erstellen und bearbeiten. <br/><br/> " +
			"Falls du einen U-QASAR Rundgang unternehmen willst, stehen Dir zur Zeit verschiedene Rundgänge zur Verfügung: <br/>" +
			"<ul class='tour'>" +
				"<li><i class='icon-home'/><span>U-QASAR Basics (zur Zeit aktiv)</span></li>" +
				"<li><i class='icon-sitemap'/><span>Projekt: 'U-QASAR' Rundgang</span></li>" +
			"</ul>" +
			"Du kannst zu jeder Zeit eine seitenbezogene Tour starten und oder wiederholen. Dazu klicke einfach unter diesem Menupunkt auf <i class='icon-info-sign'/><b>"+ $('#takeTour').text().trim() +"</b> auf der Seite, auf der du dich gerade befindest.";
	// TITLES
	titleWelcome = "WILLKOMMEN BEI U-QASAR";
	titleProject = "PROJEKTE";
	titleQmodel = "QUALITÄTSMODELLE";
	titleSearch = "SUCHE";
	titleNotify = "BENACHRICHTIGUNGEN";
	titleLanguage = "SPRACHE";
	titleAdmin = "ADMINISTRATOR";
	titleUser = "BENUTZER";
	/**
	 * PROJECT TOUR
	 */
	projectTree = "Zum besseren Verständnis der ein- und ausklappbaren Baumstruktur für ein Qualitätsprojekt, siehe folgende allgemeine Beschreibung des Aufbaus:" +
					"<table>" +
						"<tr><td><i class='icon-sitemap'/> Qualitätsprojekt ('Qualitätsmodell')</td></tr>" +
						"<tr><td style='text-indent:10px'> <i class='icon-tasks'/> Qualitätsziel</td></tr>" +
						"<tr><td style='text-indent:20px'> <i class='icon-dashboard'/> Qualitätsindikator</td></tr>" +
						"<tr><td style='text-indent:30px'> <i class='icon-signal'/> Softwaremetrik </td></tr>" +
				  "</table><br/>"+
				  "Jeder Knoten der Baumstruktur ist mithilfe des Pfeils ein- und aufklappbar, sowie ein klickbarer Link zur jeweiligen Detailseite des Baumknotens. ";
	treeOptions = "Nutze diese Buttons um " +
					"<ul style='list-style:none'>" +
						"<li> <i class='icon-plus'/> neue Projekte zu erstellen, </li>" +
						"<li> <i class='icon-pencil'/> das aktuelle Projekt zu bearbeiten,</li>" +
						"<li> <i class='icon-chevron-sign-down'/> Baumknoten zu verschieben,</li>" +
						"<li> <i class='icon-trash'/> das aktuelle Projekt zu löschen.</li>" +
					"</ul>";
	qualityStatus = "Hier wird dir der aktuelle Wert der Softwarequalität angezeigt. Die Farbe zeigt dir dabei die aktuelle Güte deiner Software an (rot/gelb/grün).";
	projectInformation = "Du kannst projektbezogene Informationen wie z.B. die teilnehmenden Teammitglieder, die verwendeten Metadaten und Visualisierungen von Qualitätstrends deines Projekts in diesem Bereich ansehen.";
	titleTree = "PROJEKTBAUM";
	titleOptions = "OPTIONEN";
	titleStatus = "QUALITÄTSSTATUS";
	titleInformation= "PROJEKTINFORMATIONEN";
	
	/**
	 * dashboard TOUR
	 */
	addWidget = "Unter diesem Menüpunkt kannst einige verschiedene Widgets zu deinem akutellen Dashboard hinzufügen, wie zum Beispiel:" +
					"<table>" +
						"<tr><td> Project Quality Widget</td></tr>" +
						"<tr><td> JIRA Widget</td></tr>" +
						"<tr><td> Sonar Widget</td></tr>" +
						"<tr><td> TestLink Widget</td></tr>" +
						"<tr><td> Projektbaum Widget</td></tr>" +
						"<tr><td> Datenunterschiede Widget</td></tr>" +
						"<tr><td> JIRA TechDebt Widget</td></tr>" +
				  "</table><br/>";
	updateDashboard = "Klicke auf Aktualisieren, um dein aktuelles Dashboard und dessen Inhalt zu aktualisiren."; 
	shareDashboard = "Klicke auf Teilen, um dein aktuelles Dashboard mit anderen Nutzern zu teilen.";
	resetDashboard = "Klicke auf Reset, um dein aktuelles Dashboard zurückzusetzen (alle Widgets des Dashboards entfernen).";
	deleteDashboard = "Klicke auf Löschen, um dein aktuelles Dashboard vollständig zu entfernen.";
	exportDashboard = "Klicke auf Exportieren, um einen Report deines akutellen Dashboards im PDF Format zu erstellen und herunterzuladen.";
	titleAdd = "WIDGET HINZUFÜGEN";
	titleUpdate = "AKTUALISIEREN";
	titleShare = "DASHBOARD TEILEN";
	titleReset = "DASHBOARD ZURÜCKSETZEN";
	titleDelete = "DASHBOARD LÖSCHEN";
	titleExport = "DASHBOARD EXPORTIEREN";
	
	/**
	 * QMODEL TOUR
	 */
	qmodelTree = "Zum besseren Verständnis aus-und einklappbaren Struktur des Qualitätsmodellbaumes, findest du nachfolgend ein paar allgemeine Erklärungen dazu:" +
					"<table>" +
						"<tr><td><i class='icon-sitemap'/> Qualitätsmodell</td></tr>" +
						"<tr><td style='text-indent:10px'><i class='icon-tasks'/> Qualitätsziel</td></tr>" +
						"<tr><td style='text-indent:20px'><i class='icon-dashboard'/> Qualitätsindicator</td></tr>" +
						"<tr><td style='text-indent:30px'><i class='icon-signal'/> Software Metrik</td></tr>" +
				  "</table><br/>"+
				  "Jeder Baumknoten des Qualitätsmodells ist mit Hilfe des Pfeil-Symbols aus- und einklappbar und steht für klickbaren Link zu der Detailsseite des Knotens.";
	qmtreeOptions = "Nutze die Quick-Actions, um" +
					"<ul style='list-style:none'>" +
						"<li> <i class='icon-plus'/> ein Qualitätsmodell zu erstellen, </li> " +
						"<li> <i class='icon-pencil'/> das aktuelle Qualitätsmodell zu bearbeiten,</li>" +
						"<li> <i class='icon-chevron-sign-down'/> Baumknoten innerhalb des Baums zu verschieben,</li>" +
						"<li> <i class='icon-trash'/> oder um das aktuelle Qualitätsmodell zu löschen.</li>" +
					"</ul>";
	
	titleQMTree = "Qualitätsmodellbaum";
	titleQMOptions = "OPTIONEN";

	// NAVI
	prev = "« Zurück";
	next = "Weiter »";
	end = "Tour beenden";
}
