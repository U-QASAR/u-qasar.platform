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