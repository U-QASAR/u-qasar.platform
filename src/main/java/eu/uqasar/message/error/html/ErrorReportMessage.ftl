<#assign title = "New U-QASAR error reported">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td colspan="2">
			<p>Somebody found a bug on the <a href="${homepage}">U-QASAR</a> platform and submitted an issue with the following details:</p>
		</td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Report:</b></td>
		<td>${report}</td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Reporter:</b></td>
		<td><em>${reporter}</em></td>
	</tr>
	<tr>
		<td valign="top" style="vertical-align: top"><b>Error Message:</b></td>
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