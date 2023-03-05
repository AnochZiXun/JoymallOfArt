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
			<!--<LINK href="//ajax.googleapis.com/ajax/libs/jqueryui/1.11.4/themes/sunny/jquery-ui.css" rel="stylesheet" media="all" type="text/css"/>-->
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/store.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<TITLE>柴燒原之鄉 &#187; <xsl:value-of select="booth/name"/></TITLE>
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
							<DIV class="cF">
								<ASIDE>
									<A id="aside1">
										<xsl:value-of select="booth/name"/>
									</A>
									<A id="aside2" href="/store/{booth/@id}/">店家簡介</A>
									<A id="aside3">產品分類</A>
									<xsl:for-each select="booth/shelves/*">
										<A href="/category/{@id}/">
											<xsl:value-of select="."/>
										</A>
									</xsl:for-each>
								</ASIDE>
								<DIV>
									<H1 id="boothName">
										<xsl:value-of select="booth/name"/>
									</H1>
									<xsl:if test="string-length(booth/html)&gt;'0'">
										<xsl:value-of select="booth/html" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="booth/internalFrames">
										<xsl:for-each select="booth/internalFrames/*">
											<P>
												<IFRAME src="{.}">
													<xsl:choose>
														<xsl:when test="@width">
															<xsl:attribute name="width">
																<xsl:value-of select="@width"/>
															</xsl:attribute>
														</xsl:when>
														<xsl:otherwise>
															<xsl:attribute name="style">width&#58;100&#37;</xsl:attribute>
														</xsl:otherwise>
													</xsl:choose>
													<xsl:if test="@height">
														<xsl:attribute name="height">
															<xsl:value-of select="@height"/>
														</xsl:attribute>
													</xsl:if>
												</IFRAME>
											</P>
										</xsl:for-each>
									</xsl:if>
								</DIV>
							</DIV>
							<xsl:call-template name="footer"/>
						</NAV>
					</DIV>
				</DIV>
			</DIV>
		</BODY>
	</xsl:template>

</xsl:stylesheet>