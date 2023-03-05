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
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/i18n/jquery-ui-i18n.min.js"/>
			<SCRIPT src="//cdn.ckeditor.com/4.5.6/full/ckeditor.js"/>
			<SCRIPT src="/ckfinder/ckfinder.js"/>
			<SCRIPT src="/SCRIPT/cPanel.js"/>
			<TITLE>控制臺 &#187; 工作人員基本資料</TITLE>
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
									<LEGEND>修改工作人員基本資料</LEGEND>
									<TABLE class="form">
										<xsl:if test="form/@error">
											<CAPTION>
												<xsl:value-of select="form/@error"/>
											</CAPTION>
										</xsl:if>
										<TR>
											<TH class="must">
												<LABEL for="login">帳號</LABEL>
											</TH>
											<TD>
												<INPUT id="login" name="login" placeholder="電子郵件" required="" type="text" value="{form/login}"/>
											</TD>
											<TD>必填且不可重複。</TD>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="name">工作人員暱稱</LABEL>
											</TH>
											<TD>
												<INPUT id="name" name="name" required="" type="text" value="{form/name}"/>
											</TD>
											<TD>必填。</TD>
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
					<A>
						<xsl:attribute name="href">
							<xsl:choose>
								<xsl:when test="@internal='false'">/cPanel/booth.asp</xsl:when>
								<xsl:otherwise>/cPanel/staff.asp</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<SPAN>基本資料</SPAN>
					</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>