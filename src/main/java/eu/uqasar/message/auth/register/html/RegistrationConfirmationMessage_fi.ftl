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