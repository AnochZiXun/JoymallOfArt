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
			<META name="viewport" content="width=device-width, initial-scale=1.0"/>
			<LINK href="http://fonts.googleapis.com/earlyaccess/cwtexyen.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/font-awesome.min.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/common.css" rel="stylesheet" media="all" type="text/css"/>
			<LINK href="/STYLE/category.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<TITLE>柴燒原之鄉 &#187; <xsl:value-of select="booth/shelf"/></TITLE>
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
									<H1 id="shelfName">
										<xsl:value-of select="booth/shelf"/>
									</H1>
									<P>&#160;</P>
									<UL class="cF" id="merchandises">
										<xsl:for-each select="booth/merchandises/*">
											<LI>
												<A class="figure" href="/product/{@id}/">
													<FIGURE>
														<IMG alt="產品{position()}" width="190" height="190">
															<xsl:attribute name="src">
																<xsl:choose>
																	<xsl:when test="@merchandiseImageId">/oneNinety/<xsl:value-of select="@merchandiseImageId"/>.png</xsl:when>
																	<xsl:otherwise>/IMG/productWithoutImage/190.png</xsl:otherwise>
																</xsl:choose>
															</xsl:attribute>
														</IMG>
														<FIGCAPTION>
															<P>
																<xsl:value-of select="."/>
															</P>
															<P class="price">
																<xsl:value-of select="format-number(@price,'###,###')"/>
															</P>
														</FIGCAPTION>
													</FIGURE>
												</A>
											</LI>
										</xsl:for-each>
									</UL>
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