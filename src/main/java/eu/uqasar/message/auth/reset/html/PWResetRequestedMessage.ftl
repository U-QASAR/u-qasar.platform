<#--
 #%L
 U-QASAR
 %%
 Copyright (C) 2012 - 2015 U-QASAR Consortium
 %%
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
      http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 #L%
-->
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