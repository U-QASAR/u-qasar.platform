<#assign title = "Registrierung abgeschlossen">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Sehr geehrte(r) ${user.fullName},</p>
			<p>Ihre Registrierung für U-QASAR wurde erfolgreich bestätigt und ist nun abgeschlossen. Um auf die U-QASAR Plattform zuzugreifen, klicken Sie bitte auf folgenden Link (oder fügen Sie die Adresse in die Adressleiste Ihres Browser ein).</p>
			<div>
				<ul>
					<li><a href="${link}">Klicken Sie hier, um sich an der U-QASAR Plattform anzumelden.</a></li>
				</ul>
			</div>
			<p>Mit freundlichen Grüßen,</p>
			<p>das U-QASAR Team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">