<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output
		method="html"
		encoding="UTF-8"
		omit-xml-declaration="yes"
		indent="no"
		media-type="text/html"
	/>

	<xsl:import href="/cPanel/import.xsl"/>

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
			<LINK href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/dark-hive/jquery-ui.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/cPanel.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="/SCRIPT/cPanel.js"/>
			<TITLE>
				<xsl:choose>
					<xsl:when test="@title">
						<xsl:value-of select="@title" disable-output-escaping="yes"/>
					</xsl:when>
					<xsl:otherwise>控制臺 &#187; 變更密碼</xsl:otherwise>
				</xsl:choose>
			</TITLE>
		</HEAD>
		<xsl:comment>
			<xsl:value-of select="system-property('xsl:version')"/>
		</xsl:comment>
		<BODY>
			<TABLE id="cPanel">
				<TBODY>
					<TR>
						<TD>
							<xsl:apply-templates select="aside"/>
						</TD>
						<TD>
							<FORM action="{form/@action}" method="POST">
								<FIELDSET>
									<LEGEND>
										<xsl:choose>
											<xsl:when test="form/@legend">
												<xsl:value-of select="form/@legend"/>
											</xsl:when>
											<xsl:otherwise>變更密碼</xsl:otherwise>
										</xsl:choose>
									</LEGEND>
									<TABLE class="form">
										<xsl:if test="form/@error">
											<CAPTION>
												<xsl:value-of select="form/@error"/>
											</CAPTION>
										</xsl:if>
										<TR>
											<TR>
												<TH class="must">
													<LABEL for="shadow1">新密碼</LABEL>
												</TH>
												<TD>
													<INPUT id="shadow1" name="shadow1" required="" type="password"/>
												</TD>
												<TD>必填。</TD>
											</TR>
										</TR>
										<TR>
											<TR>
												<TH class="must">
													<LABEL for="shadow2">確認新密碼</LABEL>
												</TH>
												<TD>
													<INPUT id="shadow2" name="shadow2" required="" type="password"/>
												</TD>
												<TD>必填。</TD>
											</TR>
										</TR>
										<TR>
											<TD class="cF" colspan="3">
												<INPUT class="fL" type="reset" value="復原"/>
												<INPUT class="fR" type="submit" value="送出"/>
											</TD>
										</TR>
									</TABLE>
								</FIELDSET>
							</FORM>
						</TD>
					</TR>
				</TBODY>
			</TABLE>
			<HEADER class="cF">
				<DIV class="fL">
					<A href="/cPanel/">控制臺首頁</A>
					<SPAN>&#187;</SPAN>
					<A href="{@requestURI}">變更密碼</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>