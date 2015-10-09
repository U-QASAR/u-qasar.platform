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