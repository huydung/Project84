/**
 * Confirm plugin 1.3
 *
 * Copyright (c) 2007 Nadia Alramli (http://nadiana.com/)
 * Dual licensed under the MIT (MIT-LICENSE.txt)
 * and GPL (GPL-LICENSE.txt) licenses.
 */

/**
 * For more docs and examples visit:
 * http://nadiana.com/jquery-confirm-plugin
 * For comments, suggestions or bug reporting,
 * email me at: http://nadiana.com/contact/
 */

jQuery.fn.confirm = function(options) {
  options = jQuery.extend({
    msg: 'Really Delete? ',
    stopAfter: 'never',
    wrapper: '<span></span>',
    eventType: 'click',
    dialogShow: 'show',
    dialogSpeed: '',
    timeout: 0
  }, options);
  options.stopAfter = options.stopAfter.toLowerCase();
  if (!options.stopAfter in ['never', 'once', 'ok', 'cancel']) {
    options.stopAfter = 'never';
  }
  options.buttons = jQuery.extend({
    ok: ' Yes ',
    cancel: ' No ',
    wrapper:'<a href="#"></a>',
    separator: '/'
  }, options.buttons);

  // Shortcut to eventType.
  var type = options.eventType;

  return this.each(function() {
    var target = this;
    var $target = jQuery(target);
    var timer;
    var saveHandlers = function() {
      var events = jQuery.data(target, 'events');
      if (!events && target.href) {
        // No handlers but we have href
        $target.bind('click', function() {document.location = target.href});
        events = jQuery.data(target, 'events');
      } else if (!events) {
        // There are no handlers to save.
        return;
      }
      target._handlers = new Array();
      for (var i in events[type]) {
        target._handlers.push(events[type][i]);
      }
    }
    
    // Create ok button, and bind in to a click handler.
    var $ok = jQuery(options.buttons.wrapper)
      .append(options.buttons.ok)
      .click(function() {
      // Check if timeout is set.
      if (options.timeout != 0) {
        clearTimeout(timer);
      }
      $target.unbind(type, handler);
      $target.show();
      $dialog.hide();
      // Rebind the saved handlers.
      if (target._handlers != undefined) {
        jQuery.each(target._handlers, function() {
          $target.click(this.handler);
        });
      }
      // Trigger click event.
      $target.click();
      if (options.stopAfter != 'ok' && options.stopAfter != 'once') {
        $target.unbind(type);
        // Rebind the confirmation handler.
        $target.one(type, handler);
      }
      return false;
    })

    var $cancel = jQuery(options.buttons.wrapper).append(options.buttons.cancel).click(function() {
      // Check if timeout is set.
      if (options.timeout != 0) {
        clearTimeout(timer);
      }
      if (options.stopAfter != 'cancel' && options.stopAfter != 'once') {
        $target.one(type, handler);
      }
      $target.show();
      $dialog.hide();
      return false;
    });

    if (options.buttons.cls) {
      $ok.addClass(options.buttons.cls);
      $cancel.addClass(options.buttons.cls);
    }

    var $dialog = jQuery(options.wrapper)
    .append(options.msg)
    .append($ok)
    .append(options.buttons.separator)
    .append($cancel);

    var handler = function() {
      jQuery(this).hide();

      // Do this check because of a jQuery bug
      if (options.dialogShow != 'show') {
        $dialog.hide();
      }

      $dialog.insertBefore(this);
      // Display the dialog.
      $dialog[options.dialogShow](options.dialogSpeed);
      if (options.timeout != 0) {
        // Set timeout
        clearTimeout(timer);
        timer = setTimeout(function() {$cancel.click(); $target.one(type, handler);}, options.timeout);
      }
      return false;
    };

    saveHandlers();
    $target.unbind(type);
    target._confirm = handler
    target._confirmEvent = type;
    $target.one(type, handler);
  });
};

$(document).ready(function(){
	var processPage = function(){
		/** Default gritter options **/
		$.gritter.options = {		
			position: 'bottom-right',
			time: 15000
		};
		
		/** Date Picker **/
		$('input.date').each(function(e){
			var format = $(this).attr("data-format");
			var $dp = $(this);
			$dp.datepicker({
				changeMonth: true,
				changeYear: true,
				dateFormat: format,
				minDate: "+1d"
			})
		})
		
		/** Accordion & Tabs **/
		var stop = false;
		$( "#accordions h3.header" ).click(function( event ) {
			if ( stop ) {
				event.stopImmediatePropagation();
				event.preventDefault();
				stop = false;
			}
		});
		$('#accordions').accordion({
			header: 'h3.header',
			collapsible: true,
			active: false
		});
		$('.project_listings').sortable({
			axis: "y",
			handle: "h3.header",
			stop: function() {
				stop = true;
				var ids = [];
				var nav_els = [];
				$('.project_listings .listing-panel').each(function(){
					var id = $(this).attr('data-id');
					ids.push( id );
					var nav_el = $('.listing_nav[data-id='+ id +']').clone();
					nav_els.push(nav_el);
				});
				var project_id = $(this).attr('data-id');
				$.post(
					'/project/'+project_id+'/listings/orderings/'+ids.join(','),
					null,
					function(){	
						$('.listing_nav').remove();
						$firstNav = $('#menu_group_main .first');
						for( var i = nav_els.length - 1; i > -1; i-- ){
							$firstNav.after(nav_els[i]);
						};					
					}
				);
			}
		});
		$('#tabs').tabs();
		
		/** Hide elements that will be enhanced by Javascript **/
		$('.enhanced').hide();
		
		
		/** Allow to click on label to select **/
		$('label,.label').css({cursor:'pointer'}).click(function(){
			$(this).next('input[type="checkbox"]').click().end()
					.prev('input[type="checkbox"]').click();
			return false;
		});
		
		/** Convert delete action link to have confirmation **/
		$('.icon-link-delete')
			.click(function(){
				window.location.href = $(this).attr('href');
				return false;
			})
			.confirm({
				timeout:5000,
				wrapper:'<span class="confirmation fright"></span>'
			});
		
		/** Convert modal actions in widget to use AJAX **/
		$('.modal').click(function(){
			$this = $(this);
			$('body').append('<div id="modal-dialog"></div>');
			$('#modal-dialog')
				.load($this.attr('href'), function(response, status, xhr){
					if(status != "error"){			
						$('#modal-dialog').dialog({
							modal: true,
							title: $this.text(),
							close: function(){
								$('#modal-dialog').remove()
							},
							width: 600
						});
					}else{
						//See documentation: http://boedesign.com/blog/2009/07/11/growl-for-jquery-gritter/
						$.gritter.add({
							title: 'Oops',
							text: response,
							class_name: 'error',						
						});
					}	
				});
			return false;			
		});
		
		/** Icon Chooser Components **/
		//Fills Icons in the modal-dialog
		var $modal = $('#modal-dialog');
		if($modal.length <= 0){
			$('body').append('<div id="modal-dialog"></div>');
			$modal = $('#modal-dialog');
		}
		var $iconChoosers = $('.iconChooser select');
		if( $iconChoosers.length > 0 ){
			var str = '<ul class="grid-items">';
			$iconChoosers.eq(0).find('option').each(function(){
				str += '<li><img src="'+ $(this).val() +'" width="32" height="32" /></li>';
				
			});
			str +='</div>';
			$modal.html(str);
			$modal.dialog({
				autoOpen: false,
				title: "Icon Chooser",
				width: 600,
				height: 420,
				modal: true
			});
			$iconChoosers.parents('a').click(function(){
				$modal.data('caller_id', $(this).attr('id')).dialog('open');
				return false;
			});
			$modal.find('li').click(function(){
				var id = $modal.data('caller_id');
				var src = $(this).find('img').attr('src');
				$('#'+id)
					.find('select').val( src )
					.end()
					.find('img').attr('src', src);
				$modal.dialog('close');
			});
		};
		
		/** Checkbox interactivity in Project Configuration page **/
		$('.listing-fields-configuration input[type=checkbox]').click(function(){
			var $this = $(this);
			if( $this.is(':checked') ){
				$this.parents('tr')
				.addClass('used')
					.find('input[type=text]').each(function(){
						$(this).val($(this).attr('placeholder'));
					});
			}else{
				$this.parents('tr').removeClass('used');
			}
		});
		
		/** sortable **/
		var fixHelper = function(e, ui) {
		    ui.children().each(function() {
		        $(this).width($(this).width());
		    });
		    return ui;
		};
		//alert('Hey');
		$('.sortable tbody,ul.sortable').sortable({
			'handle': '.drag-handler',
			helper: fixHelper,
			placeholder: 'ui-sortable-placeholder'
		}).disableSelection();
		
		
		/** Ajaxify Form **/
		/*
		$('form.ajaxify button[type=submit], form.ajaxify input[type=submit]').click(function(){
			$form = $(this).parent('form');
			$.ajax({
				type: $form.attr('method'),
				url: $form.attr('action'),
				dataType: html,
				success: function(data, textStatus, jqXHR){
					$form.parent('.accordion-panel').html(data);
				},
				error: function(){
					alert('Error');
				}
			});
			return false;
		});*/
		
		/** LISTING FILTERS **/
		$('.filter-field').change(function(){
			$this = $(this);
			console.log( $this.attr('name') + ': ' + $this.val() );
			$this.parents('form').submit();
			
		});
	};
	processPage();
});

