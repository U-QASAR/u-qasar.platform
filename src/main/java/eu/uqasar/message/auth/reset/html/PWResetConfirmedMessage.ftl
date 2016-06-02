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