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
			<LINK href="/STYLE/cart.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/cart.js"/>
			<TITLE>柴燒原之鄉 &#187; 我的購物車</TITLE>
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
								<xsl:choose>
									<xsl:when test="stores">
										<xsl:for-each select="stores/*">
											<DIV class="store">
												<TABLE>
													<CAPTION>
														<H1>我的購物車：<xsl:value-of select="@name"/></H1>
													</CAPTION>
													<THEAD>
														<TR>
															<TH>產品名稱</TH>
															<TH>規格</TH>
															<TH>單價</TH>
															<TH>數量</TH>
															<TH>小計</TH>
															<TH/>
														</TR>
													</THEAD>
													<TFOOT>
														<TR>
															<TD colspan="4">合計</TD>
															<TD>
																<xsl:value-of select="format-number(@total,'###,###')"/>
																<SPAN>元</SPAN>
															</TD>
															<TD>
																<FORM class="ajax" action="checkOut.json" method="POST">
																	<INPUT name="store" type="hidden" value="{@id}"/>
																	<INPUT type="submit" value="結帳付款"/>
																</FORM>
															</TD>
														</TR>
													</TFOOT>
													<TBODY>
														<xsl:for-each select="items/*">
															<TR>
																<TH>
																	<IMG alt="{@name}" width="77" height="77">
																		<xsl:attribute name="src">
																			<xsl:choose>
																				<xsl:when test="@imageId">/seventySeven/<xsl:value-of select="@imageId"/>.png</xsl:when>
																				<xsl:otherwise>/IMG/productWithoutImage/77.png</xsl:otherwise>
																			</xsl:choose>
																		</xsl:attribute>
																	</IMG>
																</TH>
																<TD rowspan="2">
																	<xsl:value-of select="@specificationName"/>
																</TD>
																<TD rowspan="2">
																	<xsl:value-of select="format-number(@price,'###,###')"/>
																	<SPAN>元</SPAN>
																</TD>
																<TD rowspan="2">
																	<FORM class="ajax" action="quantity.json" method="POST">
																		<INPUT name="merchandise" type="hidden" value="{@id}"/>
																		<INPUT name="specification" type="hidden" value="{@specificationId}"/>
																		<INPUT class="monospace" maxlength="2" name="quantity" type="text" value="{@quantity}"/>
																		<INPUT class="hidden" type="submit"/>
																	</FORM>
																</TD>
																<TD rowspan="2">
																	<xsl:value-of select="format-number(@subTotal,'###,###')"/>
																	<SPAN>元</SPAN>
																</TD>
																<TD rowspan="2">
																	<FORM class="ajax" action="quantity.json" method="POST">
																		<INPUT name="merchandise" type="hidden" value="{@id}"/>
																		<INPUT name="specification" type="hidden" value="{@specificationId}"/>
																		<INPUT name="quantity" type="hidden" value="0"/>
																		<INPUT class="hidden" type="submit"/>
																		<A class="fa fa-trash">刪除</A>
																	</FORM>
																</TD>
															</TR>
															<TR>
																<TD>
																	<xsl:value-of select="."/>
																</TD>
															</TR>
														</xsl:for-each>
													</TBODY>
												</TABLE>
											</DIV>
										</xsl:for-each>
									</xsl:when>
									<xsl:otherwise>
										<P id="emptyCart">購物車是空的或您的登入週期已經逾時！</P>
									</xsl:otherwise>
								</xsl:choose>
							</DIV>
							<xsl:call-template name="footer"/>
						</NAV>
					</DIV>
				</DIV>
			</DIV>
		</BODY>
	</xsl:template>

</xsl:stylesheet>