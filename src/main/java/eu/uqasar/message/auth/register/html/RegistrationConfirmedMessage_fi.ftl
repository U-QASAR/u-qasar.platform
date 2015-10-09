<#assign title = "Rekisteröinti vahvistettu">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Hyvä ${user.fullName},</p>
			<p>Rekisteröintisi on vahvistettu ja siten päätetty. Päästäksesi U-QASAR-alustalle, klikkaa seuraavaa linkkiä (tai liitä linkki selaimesi osoiteriville).</p>
			<div>
				<ul>
					<li><a href="${link}">Klikkaa tästä kirjautuaksesi U-QASAR-alustalle.</a></li>
				</ul>
			</div>
			<p>Terveisin,</p>
			<p>U-QASAR-tiimi</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">