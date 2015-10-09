<#assign title = "Registrierung bestätigen">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Sehr geehrte(r) ${user.fullName},</p>
			<p>Willkommen bei <a href="${homepage}">U-QASAR</a>!</p>
			<p>Um die Registrierung vollständig abzuschließen und die E-Mail Adresse zu bestätigen, klicken Sie bitte auf folgenden Link (oder fügen Sie die Adresse in die Adressleiste Ihres Browser ein).</p>
			<div>
				<ul>
					<li><a href="${confirmation_link}">Klicken Sie hier, um Ihre Registrierung zu BESTÄTIGEN</a></li>
				</ul>
			</div>
			<p>Sollten nicht Sie sich persönlich ein Benutzerkonteo bei <a href="${homepage}">U-QASAR</a> angelegt haben, klicken Sie bitte auf den folgenden Link (oder fügen Sie die Adresse in die Adressleiste Ihres Browser ein) oder Sie ignorieren diese E-Mail.</p>
			<div>
				<ul>
					<li><a href="${cancellation_link}">Klicken Sie hier, wenn Sie Ihre Registrierung ABBRECHEN möchten.</a></li>
				</ul>
			</div>
			<p>Mit freundlichen Grüßen,</p>
			<p>das U-QASAR Team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">