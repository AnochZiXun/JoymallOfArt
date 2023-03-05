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
			<STYLE><![CDATA[TABLE.form A.fa{line-height:24px;font-size:140%;text-decoration:none}]]></STYLE>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/i18n/jquery-ui-i18n.min.js"/>
			<SCRIPT src="//cdn.ckeditor.com/4.5.6/full/ckeditor.js"/>
			<SCRIPT src="/ckfinder/ckfinder.js"/>
			<SCRIPT src="/SCRIPT/cPanel.js"/>
			<TITLE>控制臺 &#187; 最新消息</TITLE>
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
										<xsl:value-of select="form/@legend"/>
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
													<LABEL for="subject">主旨</LABEL>
												</TH>
												<TD>
													<INPUT id="subject" name="subject" required="" type="text" value="{form/subject}"/>
												</TD>
												<TD>必填。</TD>
											</TR>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="html">內容</LABEL>
											</TH>
											<TD>
												<TEXTAREA id="html" cols="1" name="html" required="" rows="1">
													<xsl:value-of select="form/html"/>
												</TEXTAREA>
											</TD>
											<TD>必填。</TD>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="when">發佈日期</LABEL>
											</TH>
											<TD>
												<INPUT class="dP monospace" id="when" name="when" readonly="" required="" type="text" value="{form/when}"/>
											</TD>
										</TR>
										<TR>
											<TD class="cF" colspan="3">
												<INPUT class="fL" type="reset" value="復原"/>
												<INPUT class="fR" type="submit" value="送出"/>
											</TD>
										</TR>
										<xsl:if test="form/id">
											<TR>
												<TD class="textAlignCenter" colspan="3">
													<HR/>
													<A class="button fa fa-object-group fa-3x" title="nested browsing context (&#60;IFRAME&#47;&#62;)" href="{form/id}/internalFrame/">&#160;</A>
												</TD>
											</TR>
										</xsl:if>
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
					<A href="{@requestURI}">最新消息</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>