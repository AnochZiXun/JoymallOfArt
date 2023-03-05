/**
 * 結帳付款
 *
 * @author:	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	$('TABLE#store INPUT[type="button"]').click(function () {
		var f = this.form;
		$.post('./', $(f).serialize(), function (d) {
			if (d.reason) {
				alert(d.reason);
			}
			if (d.response && d.result) {
				$(f['recipient']).attr({disabled: true});
				$(f['phone']).attr({disabled: true});
				$(f['address']).attr({disabled: true});
				var r = d.result;
				$(f['MerchantID']).val(r.merchantID);
				$(f['MerchantTradeNo']).val(r.merchantTradeNo);
				$(f['MerchantTradeDate']).val(r.merchantTradeDate);
				$(f['CheckMacValue']).val(r.checkMacValue);
				$(f).submit();
			}
		}, 'json');
	});
});