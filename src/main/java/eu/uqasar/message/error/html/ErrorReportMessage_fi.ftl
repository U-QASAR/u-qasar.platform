<#assign title = "Uusi U-QASAR-virheraportti">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td colspan="2">
			<p>Jemand hat ein Fehler/Problem auf der <a href="${homepage}">U-QASAR</a> Plattform gefunden und folgende Informationen übermittelt:</p>
		</td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Mitteilung:</b></td>
		<td>${report}</td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Absender:</b></td>
		<td><em>${reporter}</em></td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Fehlernachricht:</b></td>
		<td><pre>${errormessage}</pre></td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Stacktrace:</b></td>
		<td><pre>${stacktrace}</pre></td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Markup:</b></td>
		<td><pre>${markup}</pre></td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">