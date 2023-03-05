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
			<TITLE>控制臺 &#187; 店家基本資料</TITLE>
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
									<LEGEND>修改店家基本資料</LEGEND>
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
												<LABEL for="name">店家抬頭</LABEL>
											</TH>
											<TD>
												<INPUT id="name" name="name" required="" type="text" value="{form/name}"/>
											</TD>
											<TD>必填。</TD>
										</TR>
										<TR>
											<TH>
												<xsl:if test="form/logo='true'">
													<xsl:attribute name="class">must</xsl:attribute>
												</xsl:if>
												<LABEL for="logo">店家圖像</LABEL>
											</TH>
											<TD>
												<INPUT id="logo" name="logo" type="file">
													<xsl:if test="form/logo='true'">
														<xsl:attribute name="required"/>
													</xsl:if>
												</INPUT>
											</TD>
											<TD>
												<xsl:choose>
													<xsl:when test="form/logo='true'">必選。</xsl:when>
													<xsl:otherwise>非必選。</xsl:otherwise>
												</xsl:choose>
											</TD>
										</TR>
										<TR>
											<TH>
												<LABEL for="html">簡介</LABEL>
											</TH>
											<TD>
												<TEXTAREA id="html" cols="1" name="html" rows="1">
													<xsl:value-of select="form/html"/>
												</TEXTAREA>
											</TD>
											<TD>非必填。</TD>
										</TR>
										<TR>
											<TH>
												<LABEL for="address">實體地址</LABEL>
											</TH>
											<TD>
												<INPUT id="address" name="address" type="text" value="{form/address}"/>
											</TD>
											<TD>非必填。</TD>
										</TR>
										<TR>
											<TH>
												<LABEL for="cellular">手機號碼</LABEL>
											</TH>
											<TD>
												<INPUT id="cellular" name="cellular" type="text" value="{form/cellular}"/>
											</TD>
											<TD>非必填。</TD>
										</TR>
										<TR>
											<TH>
												<LABEL for="phone">市內電話</LABEL>
											</TH>
											<TD>
												<INPUT id="phone" name="phone" type="text" value="{form/phone}"/>
											</TD>
											<TD>非必填。</TD>
										</TR>
										<TR>
											<TH>
												<LABEL for="representative">聯絡代表</LABEL>
											</TH>
											<TD>
												<INPUT id="representative" name="representative" type="text" value="{form/representative}"/>
											</TD>
											<TD>非必填。</TD>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="merchantID">歐付寶特店編號</LABEL>
											</TH>
											<TD>
												<INPUT class="monospace" id="merchantID" name="merchantID" type="text" value="{form/merchantID}"/>
											</TD>
											<TD>必填。</TD>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="hashKey">歐付寶AIO介接的HashKey</LABEL>
											</TH>
											<TD>
												<INPUT class="monospace" id="hashKey" name="hashKey" type="text" value="{form/hashKey}"/>
											</TD>
											<TD>必填。</TD>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="hashIV">歐付寶AIO介接的HashIV</LABEL>
											</TH>
											<TD>
												<INPUT class="monospace" id="hashIV" name="hashIV" type="text" value="{form/hashIV}"/>
											</TD>
											<TD>必填。</TD>
										</TR>
										<TR>
											<TD class="cF textAlignCenter" colspan="3">
												<INPUT class="fL" type="reset" value="復原"/>
												<A class="button fa fa-object-group fa-2x" title="nested browsing context (&#60;IFRAME&#47;&#62;)" href="/cPanel/booth/internalFrame/">&#160;</A>
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
					<A href="/cPanel/booth.asp">基本資料</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>