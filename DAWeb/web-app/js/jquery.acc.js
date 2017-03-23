/**
 * jQuery Acc js
 *
 * Accordion plugin for jQuery
 *
 * @category    jQuery Plugin
 * @license     http://www.opensource.org/licenses/mit-license.html  MIT License
 * @copyright   2014, Daiki Sato
 * @author      Daiki Sato <sato.dik@gmail.com>
 * @link        http://orememo-v2.tumblr.com
 * @version     1.0
 * @since       2014.06.23
 */

;(function($, window, document, undefined) {

		var pluginName = 'acc',
				defaults = {
					accToggleBtn: null,
					accToggleTarget: null,
					easingOpen: 'swing',
					easingClose: 'swing',
					slideSpeedOpen: 400,
					slideSpeedClose: 400
				};

		function Plugin(element, options) {
			this.element = element;
			this.settings = $.extend({}, defaults, options);
			this._defaults = defaults;
			this._name = pluginName;

			this.init();
		}

		Plugin.prototype.init = function() {
			var self = this;

			self.$element = $(self.element);
			self.$toggleBtn = self.$element.find(self.settings.accToggleBtn);
			self.$toggleTarget = self.$element.find(self.settings.accToggleTarget);
			self.hiddenFlg = false;

			self.$toggleBtn.on('click keypress', function(e) {
				e.preventDefault()

				self.toggleElem();
			});
		}

		Plugin.prototype.toggleElem = function() {
			var self = this;

			if(self.hiddenFlg) {
				self.showElem();
			} else {
				self.hideElem();
			}
		}

		Plugin.prototype.showElem = function() {
			var self = this;

			self.$toggleTarget
				.stop(false, true)
				.slideDown(self.settings.slideSpeedOpen, self.settings.easingOpen);
			self.hiddenFlg = false;
		}

		Plugin.prototype.hideElem = function() {
			var self = this;

			self.$toggleTarget
				.stop(false, true)
				.slideUp(self.settings.slideSpeedClose, self.settings.easingClose);
			self.hiddenFlg = true;
		}

		$.fn[pluginName] = function(options) {
			this.each(function() {
				if(!$.data(this, 'plugin_' + pluginName)) {
					$.data(this, 'plugin_' + pluginName, new Plugin(this, options));
				}
			});

			return this;
		}

})(jQuery, window, document, undefined);
