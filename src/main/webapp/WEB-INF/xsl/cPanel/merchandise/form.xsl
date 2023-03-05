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
			<LINK href="/STYLE/default.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/cPanel.css" rel="stylesheet" media="all" type="text/css"/>
			<STYLE><![CDATA[TABLE.form A.fa{line-height:24px;font-size:140%;text-decoration:none}]]></STYLE>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"/>
			<SCRIPT src="//cdn.ckeditor.com/4.5.6/full/ckeditor.js"/>
			<SCRIPT src="/ckfinder/ckfinder.js"/>
			<SCRIPT src="/SCRIPT/cPanel.js"/>
			<TITLE>控制臺 &#187; 商品</TITLE>
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
										<xsl:if test="form/shelfs">
											<TR>
												<TH class="must">
													<LABEL for="shelf">商品分類</LABEL>
												</TH>
												<TD>
													<SELECT id="shelf" name="shelf" required="">
														<xsl:apply-templates select="form/shelfs/*"/>
													</SELECT>
												</TD>
												<TD>必選。</TD>
											</TR>
										</xsl:if>
										<TR>
											<TR>
												<TH class="must">
													<LABEL for="name">商品名稱</LABEL>
												</TH>
												<TD>
													<INPUT id="name" maxlength="16" name="name" required="" type="text" value="{form/name}"/>
												</TD>
												<TD>必填；最多16個字。</TD>
											</TR>
										</TR>
										<TR>
											<TR>
												<TH class="must">
													<LABEL for="price">單價</LABEL>
												</TH>
												<TD>
													<INPUT id="price" name="price" required="" type="text" value="{form/price}"/>
												</TD>
												<TD>必填。</TD>
											</TR>
										</TR>
										<TR>
											<TH class="must">
												<LABEL for="html">描述</LABEL>
											</TH>
											<TD>
												<TEXTAREA id="html" cols="1" name="html" required="" rows="1">
													<xsl:value-of select="form/html"/>
												</TEXTAREA>
											</TD>
											<TD>必填。</TD>
										</TR>
										<TR style="display:none">
											<TD colspan="3">
												<INPUT name="carrying" type="hidden" value="{form/carrying}"/>
												<INPUT name="inStock" type="hidden" value="{form/inStock}"/>
												<INPUT name="recommended" type="hidden" value="{form/recommended}"/>
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
													<B class="blank">&#160;</B>
													<A class="button fa fa-list-alt fa-3x" title="商品圖片" href="{form/id}/merchandiseImage/">&#160;</A>
													<B class="blank">&#160;</B>
													<A class="button fa fa-cogs fa-3x" title="商品規格" href="{form/id}/merchandiseSpecification/">&#160;</A>
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
					<A href="{@requestURI}">商品</A>
				</DIV>
				<xsl:call-template name="topRite"/>
			</HEADER>
		</BODY>
	</xsl:template>

</xsl:stylesheet>