/*
 * Copyright © ${project.inceptionYear} Instituto Superior Técnico
 *
 * This file is part of Fenix IST.
 *
 * Fenix IST is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Fenix IST is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Fenix IST.  If not, see <http://www.gnu.org/licenses/>.
 */
function jsonHandler(hostJson) {
	
	if(hostJson.menu.length === 0) {
		hostJson.hasMenuItems = false;
	} else {
		hostJson.hasMenuItems = true;
	}

	$(hostJson.locales).each(function() {
		if (this.tag === hostJson.locale.tag) {
			this.selected = true;
		} else {
			this.selected = false;
		}
	});

	hostJson.manager = false;
	$(hostJson.groups).each(function() {
		if (this["expression"] === "#managers") {
			hostJson.manager = true;
		}
	});

	var path = location.href.substring(location.origin.length).substring(contextPath.length + 1);
	
	var selectedMenu = false;
	
	var resetMenu = function() {
		var parent = this;
		var child = parent.menu;
		//deselect all childs
		$(child).each(function() {
			this.selected = false;
			this.parentId = parent.id;
			this.parent = parent;
		});
	};
	
	var tryToSelectParentMenu = function() {

		if (this.path === path || this.path + '/' === path) {
			this.selected = true;
			selectedMenu = this;
			return false;
		}
		
	};
	
	var tryToSelectChildMenu = function() {
		var parent = this;
		var child = parent.menu;
		
		//if no parentMenu was selected and has child try to select one of the childs.
		//If founds one child selects the parentMenu too.
			$(child).each(function() {
				this.selected = this.fullPath === '/' + path;
				if (this.selected) {
					selectedMenu = this;
					this.selected = true;
					parent.selected = true;
					return false;
				}
			});
	};
	
	$(hostJson.menu).each(resetMenu);
	
	$(hostJson.menu).each(tryToSelectParentMenu);
	
	if (!selectedMenu) {
		$(hostJson.menu).each(tryToSelectChildMenu);
	}
	
	return hostJson;
}