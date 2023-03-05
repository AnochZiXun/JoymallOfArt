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
			<LINK href="/STYLE/register.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/i18n/jquery-ui-i18n.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/register.js"/>
			<TITLE>柴燒原之鄉 &#187; 忘記並重設店家密碼</TITLE>
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
								<FORM id="register" action="{form/@action}" method="POST">
									<BR/>
									<DIV>柴燒原之鄉 忘記並重設店家密碼</DIV>
									<BR/>
									<TABLE class="form">
										<CAPTION>
											<xsl:value-of select="form/@error"/>
										</CAPTION>
										<TBODY>
											<TR>
												<TH class="must">
													<LABEL for="login">店家帳號</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="login" maxlength="256" name="login" placeholder="電子郵件" required="" type="text" value="{form/login}"/>
												</TD>
											</TR>
											<TR>
												<TH class="must">
													<LABEL for="shadow">新密碼</LABEL>
												</TH>
												<TD>
													<INPUT class="monospace" id="shadow" name="shadow" placeholder="八碼以上英數混雜" required="" type="password"/>
												</TD>
											</TR>
											<TR>
												<TD colspan="2">
													<xsl:if test="form/code">
														<INPUT name="code" type="hidden" value="{form/code}"/>
													</xsl:if>
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