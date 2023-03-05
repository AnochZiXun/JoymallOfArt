/**
 * @author	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	$("UL#lightSlider").lightSlider({
		gallery: true,
		item: 1,
		loop: false,
		slideMargin: 0,
		thumbItem: 5
	});
	$('INPUT#quantity').keyup(function () {
		$(this).val($(this).val().replace(/\D/gi, ''));
	});
	$('FORM.ajax').submit(function (e) {
		e.preventDefault();
		var f = this;
		$.post($(f).attr('action'), $(f).serialize(), function (r) {
			if (r.reason) {
				alert(r.reason);
			}
			if (r.response) {
				location.reload();
			}
		}, 'json');
		return false;
	});
	$('DIV#otherMerchandises>DIV.owl-carousel').owlCarousel({
		items: 4,
		navigation: true,
		navigationText: ['上一頁', '下一頁'],
		rewindNav: false,
		scrollPerPage: true,
		pagination: false
	});
});