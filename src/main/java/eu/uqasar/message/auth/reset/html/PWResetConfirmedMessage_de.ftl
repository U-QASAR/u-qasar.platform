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
<#assign title = "Passwort erfolgreich zurückgesetzt">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Sehr geehrte(r) ${user.fullName},</p>
			<p>Ihr Passwort für die U-QASAR Plattform wurde entsprechend Ihren Eingaben geändert. </p>
			<p>Um sich mit denen neuen Zugangsdaten an der Plattform anzumelden, klicken Sie bitte auf folgenden Link (oder fügen Sie die Adresse in die Adressleiste Ihres Browser ein).</p>
			<div>
				<ul>
					<li><a href="${link}">Klicken Sie hier, um sich mit Ihrem neuen Passwort ANZUMELDEN</a></li>
				</ul>
			</div>
			<p>Mit freundlichen Grüßen,</p>
			<p>das U-QASAR Team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">