<#assign title = "Your Password was reset">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Dear ${user.fullName},</p>
			<p>Your password for the U-QASAR platform has been reset according to your inputs.</p>
			<p>In order to access the U-QASAR platform with your new credentials, click on the following link (or copy paste the link into your browser's address bar).</p>
			<div>
				<ul>
					<li><a href="${link}">Please click here to LOGIN at U-QASAR with your new credentials.</a></li>
				</ul>
			</div>
			<p>Sincerely,</p>
			<p>the U-QASAR team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">