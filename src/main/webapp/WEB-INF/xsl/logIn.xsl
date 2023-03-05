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
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/signUp.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<TITLE>柴燒原之鄉會員登入</TITLE>
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
								<FORM id="logIn" action="/logIn.asp" method="POST">
									<BR/>
									<DIV>柴燒原之鄉會員登入</DIV>
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
													<INPUT class="monospace" id="shadow" name="shadow" required="" type="password"/>
												</TD>
											</TR>
											<TR>
												<TD style="text-align:right" colspan="2">
													<A id="forgot" href="/forgotAndResetPassword.asp">忘記密碼重設</A>
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