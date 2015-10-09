<#assign title = "Reset Your Password">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Dear ${user.fullName},</p>
			<p>You are receiving this email because someone requested to reset your password at the <a href="${homepage}">U-QASAR</a> platform.</p>
			<p>If you did not intend to reset your password simply disregard this message.</p>
			<p>In order to access the U-QASAR platform for resetting your password, click on the following link (or copy paste the link into your browser's address bar).</p>
			<div>
				<ul>
					<li><a href="${link}">Please click here to RESET your password.</a></li>
				</ul>
			</div>
			<p>Sincerely,</p>
			<p>the U-QASAR team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">