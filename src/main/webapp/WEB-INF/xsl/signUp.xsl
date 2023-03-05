<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<xsl:import href="/import.xsl"/>

	<xsl:template match="/">
		<xsl:text disable-output-escaping="yes">&#60;!DOCTYPE HTML&#62;</xsl:text>
		<HTML dir="ltr" lang="zh-TW">
			<xsl:apply-templates/>
		</HTML>
	</xsl:template>

	<xsl:template match="document">
		<HEAD>
			<META charset="UTF-8"/>
			<META name="viewport" content="width=device-width, initial-scale=1.0"/>
			<LINK href="http://fonts.googleapis.com/earlyaccess/cwtexyen.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/sunny/jquery-ui.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/signUp.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/i18n/jquery-ui-i18n.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/signUp.js"/>
			<TITLE>柴燒原之鄉加入會員</TITLE>
		</HEAD>
		<xsl:comment>
			<xsl:value-of select="system-property('xsl:version')"/>
		</xsl:comment>
		<BODY>
			<DIV>
				<DIV>
					<HEADER>
						<DIV>
							<xsl:call-template name="header"/>
						</DIV>
					</HEADER>
				</DIV>
				<DIV>
					<DIV>
						<NAV>
							<xsl:call-template name="navigator"/>
							<DIV>
								<FORM id="signUp" action="/signUp.asp" method="POST">
									<BR/>
									<DIV>柴燒原之鄉加入會員</DIV>
									<BR/>
									<TABLE class="form">
										<CAPTION>
											<xsl:value-of select="form/@error"/>
										</CAPTION>
										<TBODY>
											<TR>
												<TH class="must">
													<LABEL for="email">帳號</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="email" maxlength="256" name="email" placeholder="電子郵件" required="" type="text" value="{form/email}"/>
												</TD>
											</TR>
											<TR>
												<TH class="must">
													<LABEL for="shadow">密碼</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="shadow" name="shadow" placeholder="八碼以上英數混雜" required="" type="password"/>
												</TD>
											</TR>
											<TR>
												<TH class="must">
													<LABEL for="lastname">姓氏</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="lastname" maxlength="16" name="lastname" required="" type="text" value="{form/lastname}"/>
												</TD>
											</TR>
											<TR>
												<TH class="must">
													<LABEL for="firstname">名字</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="firstname" maxlength="16" name="firstname" required="" type="text" value="{form/firstname}"/>
												</TD>
											</TR>
											<TR>
												<TH class="must">
													<LABEL for="birth">生日</LABEL>
												</TH>
												<TD>
													<INPUT class="dP monospace" id="birth" maxlength="10" name="birth" readonly="" required="" type="text" value="{form/birth}"/>
												</TD>
											</TR>
											<TR>
												<TH class="must">性別</TH>
												<TD>
													<LABEL>
														<INPUT name="gender" required="" type="radio" value="false">
															<xsl:if test="form/gender='false'">
																<xsl:attribute name="checked"/>
															</xsl:if>
														</INPUT>
														<SPAN>女性</SPAN>
													</LABEL>
													<SPAN>、</SPAN>
													<LABEL>
														<INPUT name="gender" required="" type="radio" value="true">
															<xsl:if test="form/gender='true'">
																<xsl:attribute name="checked"/>
															</xsl:if>
														</INPUT>
														<SPAN>男性</SPAN>
													</LABEL>
												</TD>
											</TR>
											<TR>
												<TH>
													<LABEL for="phone">聯絡電話</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="phone" maxlength="16" name="phone" type="text" value="{form/phone}"/>
												</TD>
											</TR>
											<TR>
												<TH>
													<LABEL for="address">預設地址</LABEL>
												</TH>
												<TD>
													<INPUT id="address" style="width:100%" maxlength="32" name="address" type="text" value="{form/address}"/>
												</TD>
											</TR>
											<TR>
												<TD class="textAlignRite" colspan="2">
													<LABEL>
														<INPUT name="agree" required="" type="checkbox"/>
														<SPAN>勾選表示同意 </SPAN>
														<A id="agreement">柴燒原之鄉會員交易的法律條款及合約</A>
														<SPAN>！</SPAN>
													</LABEL>
													<DIV id="jDialog" title="柴燒原之鄉會員交易的法律條款及合約">
														<xsl:value-of select="jDialog" disable-output-escaping="yes"/>
													</DIV>
												</TD>
											</TR>
											<TR>
												<TD colspan="2">
													<INPUT class="fL" style="background-color:#000" type="reset" value="取消重填"/>
													<INPUT class="fR" style="background-color:#F39019" type="submit" value="確認送出"/>
												</TD>
											</TR>
										</TBODY>
									</TABLE>
									<BR/>
								</FORM>
							</DIV>
							<xsl:call-template name="footer"/>
						</NAV>
					</DIV>
				</DIV>
			</DIV>
		</BODY>
	</xsl:template>

</xsl:stylesheet>