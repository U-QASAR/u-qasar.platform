<#assign title = "Registration confirmed">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Dear ${user.fullName},</p>
			<p>Your registration has been confirmed and thus your registration is completed. In order to access the U-QASAR platform, click on the following link (or copy paste the link into your browser's address bar).</p>
			<div>
				<ul>
					<li><a href="${link}">Please click here to sign into the U-QASAR platform.</a></li>
				</ul>
			</div>
			<p>Sincerely,</p>
			<p>the U-QASAR team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">