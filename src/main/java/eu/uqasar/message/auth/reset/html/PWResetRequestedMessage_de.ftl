<#assign title = "Passwort zurücksetzen">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Sehr geehrte(r) ${user.fullName},</p>
			<p>Sie erhalten diese e-Mail, weil jemand eine Anforderung für das Zurücksetzen Ihres Passworts für die <a href="${homepage}">U-QASAR</a> Plattform gestellt hat.</p>
			<p>Sollten das nicht Sie persönlich gewesen sein, ignorieren Sie diese Nachricht einfach.</p>
			<p>Um Ihr Passwort zurückzusetzen und abschließend ein neues zu vergeben, klicken Sie bitte auf folgenden Link (oder fügen Sie die Adresse in die Adressleiste Ihres Browser ein).</p>
			<div>
				<ul>
					<li><a href="${link}">Klicken Sie hier, um Ihr Passwort ZURÜCKZUSETZEN</a></li>
				</ul>
			</div>
			<p>Mit freundlichen Grüßen,</p>
			<p>das U-QASAR Team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">