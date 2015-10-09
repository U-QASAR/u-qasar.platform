<#assign title = "Neuer U-QASAR Fehlerbericht">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td colspan="2">
			<p>Joku on löytänyt virheen/ongelman kohteessa <a href="${homepage}">U-QASAR</a> ja lähettänyt seuraavat tiedot:</p>
		</td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Viesti:</b></td>
		<td>${report}</td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Lähettäjä:</b></td>
		<td><em>${reporter}</em></td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Virheilmoitus:</b></td>
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