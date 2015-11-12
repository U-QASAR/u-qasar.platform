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
<#assign title = "Salasanasi on nollattu">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Hyvä ${user.fullName},</p>
			<p>Salasanasi U-QASAR-alustalle on nollattu syötteidesi mukaan.</p>
			<p>Päästäksesi U-QASAR-alustalle uusilla tunnuksilla, klikkaa seuraavaa linkkiä (tai liitä linkki selaimesi osoiteriville).</p>
			<div>
				<ul>
					<li><a href="${link}">Klikkaa tästä KIRJAUTUAKSESI SISÄÄN U-QASAR:iin uusilla tunnuksillasi.</a></li>
				</ul>
			</div>
			<p>Terveisin,</p>
			<p>U-QASAR-tiimi</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">