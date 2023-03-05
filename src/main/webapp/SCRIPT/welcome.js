/**
 * @author	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	$('DIV#banner>DIV').owlCarousel({
		singleItem: true,
		autoPlay: 3000
	});
	$('DIV#topSales>DIV').owlCarousel({
		items: 4,
		navigation: true,
		navigationText: ['上一頁', '下一頁'],
		rewindNav: false,
		scrollPerPage: true,
		pagination: false
	});
});