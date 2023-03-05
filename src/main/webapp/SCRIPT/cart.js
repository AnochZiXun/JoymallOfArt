/**
 * 我的購物車
 *
 * @author:	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	$('INPUT[name="quantity"]').keyup(function () {
		$(this).val($(this).val().replace(/\D/gi, ''));
	});
	$('FORM.ajax').submit(function (e) {
		e.preventDefault();
		var f = this;
		$.post($(f).attr('action'), $(f).serialize(), function (d) {
			if (d.reason) {
				alert(d.reason);
			}
			if (d.redirect) {
				location.href = d.redirect;
			}
			if (d.response) {
				location.reload();
			}
		}, 'json');
		return false;
	});
	$('A.fa.fa-trash').click(function () {
		$(this.parentNode).submit();
	});
//	$('FORM.checkout>INPUT[type="button"]').click(function () {
//		var f = this.parentNode;
//		$.post('checkout.json', $(f).serialize(), function (d) {
//			if (d.reason) {
//				alert(d.reason);
//			}
//			if (d.response) {
//				var r = d.result;
//				$(f['MerchantID']).val(r.merchantID);
//				$(f['MerchantTradeNo']).val(r.merchantTradeNo);
//				$(f['MerchantTradeDate']).val(r.merchantTradeDate);
//				$(f['CheckMacValue']).val(r.checkMacValue);
//				$(f).submit();
//			}
//		}, 'json');
//	});
});