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
<#assign title = "Nollaa salasanasi">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Hyvä ${user.fullName},</p>
			<p>Vastaanotat tämän sähköpostin, koska joku vaati salasanasi nollaamista <a href="${homepage}">U-QASAR-</a>alustalla.</p>
			<p>Ellet aikonut nollata salasanaasi, jätä tämä viesti yksinkertaisesti huomiotta.</p>
			<p>Päästäksesi U-QASAR-alustalle nollataksesi salasanasi, klikkaa seuraavaa linkkiä (tai liitä linkki selaimesi osoiteriville).</p>
			<div>
				<ul>
					<li><a href="${link}">Klikkaa tästä NOLLATAKSESI salasanasi.</a></li>
				</ul>
			</div>
			<p>Terveisin,</p>
			<p>U-QASAR-tiimi</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">