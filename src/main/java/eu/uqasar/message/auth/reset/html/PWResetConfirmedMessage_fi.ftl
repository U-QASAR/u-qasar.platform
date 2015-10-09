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