<#assign title = "Confirm Registration">
<#include "*/html/UQasarMessageHeader.ftl">
<table>
	<tr>
		<td>
			<p>Dear ${user.fullName},</p>
			<p>Welcome to <a href="${homepage}">U-QASAR</a>!</p>
			<p>To complete your registration please confirm your email address by clicking on the following link (or copy paste the link into your browser's address bar).</p>
			<div>
				<ul>
					<li><a href="${confirmation_link}">Please click here to COMPLETE your registration.</a></li>
				</ul>
			</div>
			<p>If you did not intend to register at <a href="${homepage}">U-QASAR</a> please cancel your registration by clicking on the following link (or copy paste the link into your browser's address bar) or simply disregard this message.</p>
			<div>
				<ul>
					<li><a href="${cancellation_link}">Please click here to CANCEL your registration.</a></li>
				</ul>
			</div>
			<p>Sincerely,</p>
			<p>the U-QASAR team</p>
		</td>
	</tr>
</table>
<#include "*/html/UQasarMessageFooter.ftl">