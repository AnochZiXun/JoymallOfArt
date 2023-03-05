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
			<LINK href="/STYLE/checkout.css" rel="stylesheet" media="all" type="text/css"/>
			<SCRIPT src="//ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"/>
			<SCRIPT src="/SCRIPT/gcse.js"/>
			<SCRIPT src="/SCRIPT/checkout.js"/>
			<TITLE>柴燒原之鄉 &#187; 結帳付款：<xsl:value-of select="store"/></TITLE>
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
								<DIV id="booth">
									<xsl:choose>
										<xsl:when test="store">
											<TABLE id="store">
												<CAPTION>
													<H1>結帳付款：<xsl:value-of select="store"/></H1>
												</CAPTION>
												<THEAD>
													<TR>
														<TH>產品名稱</TH>
														<TH>規格</TH>
														<TH>單價</TH>
														<TH>數量</TH>
														<TH>小計</TH>
													</TR>
												</THEAD>
												<TFOOT>
													<TR>
														<TD colspan="4">合計</TD>
														<TD>
															<xsl:value-of select="format-number(store/@totalAmount,'###,###')"/>
															<SPAN>元</SPAN>
														</TD>
													</TR>
													<TR>
														<TD colspan="5">
															<FORM action="https://payment.allpay.com.tw/Cashier/AioCheckOut" method="POST">
															<!--<FORM action="http://payment-stage.allpay.com.tw/Cashier/AioCheckOut" method="POST">-->
																<INPUT name="MerchantID" type="hidden"/>
																<INPUT name="MerchantTradeNo" type="hidden"/>
																<INPUT name="MerchantTradeDate" type="hidden"/>
																<INPUT name="PaymentType" type="hidden" value="aio"/>
																<INPUT name="TotalAmount" type="hidden" value="{store/@totalAmount}"/>
																<INPUT name="TradeDesc" type="hidden" value="易購物商城：{store}"/>
																<INPUT name="ItemName" type="hidden" value="{store/@itemName}"/>
																<INPUT name="ReturnURL" type="hidden" value="http://www.e95.com.tw/receivable.asp"/>
																<INPUT name="ChoosePayment" type="hidden" value="ALL"/>
																<INPUT name="CheckMacValue" type="hidden"/>
																<INPUT name="ClientBackURL" type="hidden" value="http://www.e95.com.tw/cart/"/>
																<TABLE id="checkout">
																	<CAPTION>收件人資料</CAPTION>
																	<TBODY>
																		<TR>
																			<TH>
																				<LABEL for="recipient">姓名</LABEL>
																			</TH>
																			<TD>
																				<INPUT id="recipient" name="recipient" required="" type="text" value="{regular}"/>
																			</TD>
																		</TR>
																		<TR>
																			<TH>
																				<LABEL for="phone">聯絡電話</LABEL>
																			</TH>
																			<TD>
																				<INPUT id="phone" name="phone" required="" type="text" value="{regular/@phone}"/>
																			</TD>
																		</TR>
																		<TR>
																			<TH>
																				<LABEL for="address">寄送地址</LABEL>
																			</TH>
																			<TD>
																				<INPUT id="address" name="address" required="" type="text" value="{regular/@address}"/>
																			</TD>
																		</TR>
																	</TBODY>
																</TABLE>
																<INPUT type="button" value="結帳付款"/>
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
																<xsl:value-of select="@quantity"/>
															</TD>
															<TD rowspan="2">
																<xsl:value-of select="format-number(@subTotal,'###,###')"/>
																<SPAN>元</SPAN>
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
										</xsl:when>
										<xsl:otherwise>
											<P id="emptyCart">購物車是空的或您的登入週期已經逾時！</P>
										</xsl:otherwise>
									</xsl:choose>
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