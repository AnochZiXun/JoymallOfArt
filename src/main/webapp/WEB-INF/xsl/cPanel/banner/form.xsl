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
			<TITLE>控制臺 &#187; 連播橫幅</TITLE>
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
							<FORM action="{form/@action}" method="POST" enctype="multipart/form-data">
								<FIELDSET>
									<LEGEND>
										<xsl:value-of select="form/@legend" disable-output-escaping="yes"/>
									</LEGEND>
									<TABLE class="form">
										<xsl:if test="form/@error">
											<CAPTION>
												<xsl:value-of select="form/@error"/>
											</CAPTION>
										</xsl:if>
										<xsl:if test="not(form/id)">
											<TR>
												<TH class="must">
													<LABEL for="content">圖檔</LABEL>
												</TH>
												<TD>
													<INPUT id="content" accept="image/bmp,image/gif,image/jpeg,image/png" name="content" required="" type="file"/>
												</TD>
												<TD>必選。</TD>
											</TR>
										</xsl:if>
										<TR>
											<TH>
												<LABEL for="href">連結</LABEL>
											</TH>
											<TD>
												<INPUT class="monospace" id="href" name="href" type="text" value="{form/href}"/>
											</TD>
											<TD>非必填。</TD>
										</TR>
										<TR>
											<TD colspan="2">
												<LABEL>
													<INPUT name="external" type="radio" value="false">
														<xsl:if test="form/external='false'or not(form/external)">
															<xsl:attribute name="checked"/>
														</xsl:if>
													</INPUT>
													<SPAN>非外部連結</SPAN>
												</LABEL>
												<SPAN>、</SPAN>
												<LABEL>
													<INPUT name="external" type="radio" value="true">
														<xsl:if test="form/external='true'">
															<xsl:attribute name="checked"/>
														</xsl:if>
													</INPUT>
													<SPAN>外部連結</SPAN>
												</LABEL>
											</TD>
											<TD>必選。</TD>
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
					<A href="{@requestURI}">連播橫幅</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>