<#--
 #%L
 U-QASAR
 %%
 Copyright (C) 2012 - 2015 U-QASAR Consortium
 %%
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 #L%
-->
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