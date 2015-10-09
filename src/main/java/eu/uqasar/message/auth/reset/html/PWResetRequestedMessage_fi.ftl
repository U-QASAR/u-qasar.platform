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