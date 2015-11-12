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
<#assign title = "Vahvista rekisteröinti">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Hyvä ${user.fullName},</p>
			<p>Tervetuloa <a href="${homepage}">U-QASAR:iin</a>!</p>
			<p>Päättääksesi rekisteröinnin vahvista sähköpostiosoitteesi klikkaamalla seuraavaa linkkiä (tai liitä linkki selaimesi osoiteriville).</p>
			<div>
				<ul>
					<li><a href="${confirmation_link}">Klikkaa tästä PÄÄTTÄÄKSESI rekisteröintisi.</a></li>
				</ul>
			</div>
			<p>Mikäli et aikonut rekisteröityä <a href="${homepage}">U-QASAR:iin</a> peruuta rekisteröintisi klikkaamalla seuraavaa linkkiä (tai liitä linkki selaimen osoiteriville) tai yksinkertaisesti jätä tämä viesti huomiotta.</p>
			<div>
				<ul>
					<li><a href="${cancellation_link}">Klikkaa tästä PERUUTTAAKSESI rekisteröitymisesi.</a></li>
				</ul>
			</div>
			<p>Terveisin,</p>
			<p>U-QASAR-tiimi</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">