/**
 * @author	P-C Lin (a.k.a 高科技黑手)
 */
$(document).ready(function () {
	/**
	 * 手風琴
	 */
	$('ASIDE').accordion({
		heightStyle: 'content'
	});

	/**
	 * AJAX via DELETE
	 */
	$('TABLE.list A.delete').click(function (e) {
		e.preventDefault();
		var that = this;
		if (window.confirm('確定要刪除嗎？')) {
			$.ajax({
				dataType: 'json',
				method: 'DELETE',
				success: function (d) {
					if (d.reason) {
						alert(d.reason);
					}
					if (d.redirect) {
						location.href = d.redirect;
					}
					if (d.response) {
						location.reload();
					}
				},
				url: $(that).attr('href')
			});
		}
		return false;
	});

	/**
	 * AJAX via GET
	 */
	$('TABLE.list A.get').click(function (e) {
		e.preventDefault();
		$.get($(this).attr('href'), function (d) {
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

	/**
	 * AJAX via POST
	 */
	$('TABLE.list A.post').click(function (e) {
		e.preventDefault();
		$.post($(this).attr('href'), function (d) {
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

	/**
	 * 翻頁器
	 */
	$('FORM#pagination A.paginate').click(function (e) {
		e.preventDefault();
		var p = $(this).attr('tabindex');
		$(this).parents('FORM').find('SELECT[name="p"] OPTION').each(function () {
			if ($(this).val() === p) {
				$(this).siblings('OPTION:selected').removeAttr('selected');
				$(this).attr({selected: true});
				this.form.submit();
			}
		});
		return false;
	});

	/**
	 * 跳頁器
	 */
	$('FORM#pagination SELECT[name="p"]').change(function () {
		this.form.submit();
	});

	/**
	 * 僅數字
	 */
	$('INPUT[type="text"].numeric').keyup(function () {
		$(this).val($(this).val().replace(/\D/g, ''));
	});

	/**
	 * 日期選擇器
	 */
	if (typeof jQuery.ui !== 'undefined' && /[1-9]\.[1-9]{1,2}.[1-9]{1,2}/.test($.ui.version)) {
		$('INPUT.dP').datepicker({
			changeMonth: true,
			changeYear: true,
			dateFormat: 'yy-mm-dd'
		}).datepicker($.datepicker.regional['zh-TW']);
	}

	/**
	 * 所見即所得編輯器
	 */
	if (typeof CKEDITOR !== 'undefined') {
		var editor = CKEDITOR.replace('html', {
			toolbar: [
				{name: 'basicstyles', groups: ['basicstyles', 'cleanup'], items: ['Bold', 'Italic', 'Underline', 'Strike', 'Subscript', 'Superscript', '-', 'RemoveFormat']},
				{name: 'paragraph', groups: ['list', 'indent', 'blocks', 'align', 'bidi'], items: ['NumberedList', 'BulletedList', '-', 'Outdent', 'Indent', '-', 'Blockquote', 'CreateDiv', '-', 'JustifyLeft', 'JustifyCenter', 'JustifyRight', 'JustifyBlock', '-', 'BidiLtr', 'BidiRtl']},
				{name: 'links', items: ['Link', 'Unlink', 'Anchor']},
				{name: 'insert', items: ['Image', 'Table', 'HorizontalRule', 'SpecialChar']},
				{name: 'insert', items: ['Table', 'HorizontalRule', 'SpecialChar']},
				'/',
				{name: 'styles', items: ['Styles', 'Format', 'Font', 'FontSize']},
				{name: 'colors', items: ['TextColor', 'BGColor']},
				{name: 'tools', items: ['Maximize', 'ShowBlocks']},
				{name: 'others', items: ['-']},
				{name: 'about', items: ['Source', 'About']}
			]
		});
		CKFinder.setupCKEditor(editor, '/ckfinder/');
		CKEDITOR.on('instanceReady', function (ev) {
			ev.editor.dataProcessor.writer.selfClosingEnd = '>';
		});
	}
});